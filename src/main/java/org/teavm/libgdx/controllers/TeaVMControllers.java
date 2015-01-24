package org.teavm.libgdx.controllers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.ControllerManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.Pool;
import org.teavm.jso.JSFloatArray;
import org.teavm.jso.JSIntArray;
import org.teavm.libgdx.controllers.support.Gamepad;
import org.teavm.libgdx.controllers.support.GamepadSupport;
import org.teavm.libgdx.controllers.support.GamepadSupportListener;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public class TeaVMControllers implements ControllerManager, GamepadSupportListener {
    private final IntMap<TeaVMController> controllerMap = new IntMap<>();
    private final Array<Controller> controllers = new Array<>();
    private final Array<ControllerListener> listeners = new Array<>();
    private final Array<TeaVMControllerEvent> eventQueue = new Array<>();
    private final Pool<TeaVMControllerEvent> eventPool = new Pool<TeaVMControllerEvent>() {
        @Override
        protected TeaVMControllerEvent newObject() {
            return new TeaVMControllerEvent();
        }
    };

    public TeaVMControllers() {
        GamepadSupport.init(this);
        setupEventQueue();
    }

    public void setupEventQueue() {
        new Runnable() {
            @SuppressWarnings("synthetic-access")
            @Override
            public void run() {
                for (TeaVMControllerEvent event : eventQueue) {
                    switch (event.type) {
                        case TeaVMControllerEvent.CONNECTED:
                            controllers.add(event.controller);
                            for (ControllerListener listener : listeners) {
                                listener.connected(event.controller);
                            }
                            break;
                        case TeaVMControllerEvent.DISCONNECTED:
                            controllers.removeValue(event.controller, true);
                            for (ControllerListener listener : listeners) {
                                listener.disconnected(event.controller);
                            }
                            for (ControllerListener listener : event.controller.getListeners()) {
                                listener.disconnected(event.controller);
                            }
                            break;
                        case TeaVMControllerEvent.BUTTON_DOWN:
                            event.controller.buttons.put(event.code, event.amount);
                            for (ControllerListener listener : listeners) {
                                if (listener.buttonDown(event.controller, event.code))
                                    break;
                            }
                            for (ControllerListener listener : event.controller.getListeners()) {
                                if (listener.buttonDown(event.controller, event.code))
                                    break;
                            }
                            break;
                        case TeaVMControllerEvent.BUTTON_UP:
                            event.controller.buttons.remove(event.code, event.amount);
                            for (ControllerListener listener : listeners) {
                                if (listener.buttonUp(event.controller, event.code))
                                    break;
                            }
                            for (ControllerListener listener : event.controller.getListeners()) {
                                if (listener.buttonUp(event.controller, event.code))
                                    break;
                            }
                            break;
                        case TeaVMControllerEvent.AXIS:
                            event.controller.axes[event.code] = event.amount;
                            for (ControllerListener listener : listeners) {
                                if (listener.axisMoved(event.controller, event.code, event.amount))
                                    break;
                            }
                            for (ControllerListener listener : event.controller.getListeners()) {
                                if (listener.axisMoved(event.controller, event.code, event.amount))
                                    break;
                            }
                            break;
                        default:
                    }
                }
                eventPool.freeAll(eventQueue);
                eventQueue.clear();
                Gdx.app.postRunnable(this);
            }
        }.run();
    }

    @Override
    public Array<Controller> getControllers() {
        return controllers;
    }

    @Override
    public void addListener(ControllerListener listener) {
        listeners.add(listener);

    }

    @Override
    public void removeListener(ControllerListener listener) {
        listeners.removeValue(listener, true);
    }

    @Override
    public void onGamepadConnected(int index) {
        Gamepad gamepad = GamepadSupport.getGamepad(index);
        TeaVMController controller = new TeaVMController(gamepad.getIndex(), gamepad.getId());
        controllerMap.put(index, controller);
        TeaVMControllerEvent event = eventPool.obtain();
        event.type = TeaVMControllerEvent.CONNECTED;
        event.controller = controller;
        eventQueue.add(event);
    }

    @Override
    public void onGamepadDisconnected(int index) {
        TeaVMController controller = controllerMap.remove(index);
        if (controller != null) {
            TeaVMControllerEvent event = eventPool.obtain();
            event.type = TeaVMControllerEvent.DISCONNECTED;
            event.controller = controller;
            eventQueue.add(event);
        }
    }

    @Override
    public void onGamepadUpdated(int index) {
        Gamepad gamepad = GamepadSupport.getGamepad(index);
        TeaVMController controller = controllerMap.get(index);
        if (gamepad != null && controller != null) {
            // Determine what changed
            JSFloatArray axes = gamepad.getAxes();
            JSIntArray buttons = gamepad.getButtons();
            synchronized (eventQueue) {
                for (int i = 0, j = axes.getLength(); i < j; i++) {
                    float oldAxis = controller.getAxis(i);
                    float newAxis = axes.get(i);
                    if (oldAxis != newAxis) {
                        TeaVMControllerEvent event = eventPool.obtain();
                        event.type = TeaVMControllerEvent.AXIS;
                        event.controller = controller;
                        event.code = i;
                        event.amount = newAxis;
                        eventQueue.add(event);
                    }
                }
                for (int i = 0, j = buttons.getLength(); i < j; i++) {
                    float oldButton = controller.getButtonAmount(i);
                    float newButton = buttons.get(i);
                    if (oldButton != newButton) {
                        if ((oldButton < 0.5f && newButton < 0.5f) || (oldButton >= 0.5f && newButton >= 0.5f)) {
                            controller.buttons.put(i, newButton);
                            continue;
                        }

                        TeaVMControllerEvent event = eventPool.obtain();
                        event.type = newButton >= 0.5f ? TeaVMControllerEvent.BUTTON_DOWN :
                                TeaVMControllerEvent.BUTTON_UP;
                        event.controller = controller;
                        event.code = i;
                        event.amount = newButton;
                        eventQueue.add(event);
                    }
                }
            }
        }
    }

    @Override
    public void clearListeners() {
        listeners.clear();
    }
}
