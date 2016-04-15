package org.teavm.libgdx.controllers.support;

import com.badlogic.gdx.utils.IntMap;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSProperty;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.dom.events.Event;

/**
 *
 * @author Alexey Andreev
 */
public class GamepadSupport {
    private static GamepadSupportListener listener;
    private static IntMap<Gamepad> gamepads = new IntMap<>();
    private static IntMap<Gamepad> gamepadsTemp = new IntMap<>();
    private static int timerId = -1;

    public static void init(GamepadSupportListener listener) {
        GamepadSupport.listener = listener;
        if (isGamepardSupportAvailable()) {
            Window.current().addEventListener("MozGamepadConnected", evt -> {
                GamepadEvent gamepadEvent = (GamepadEvent)evt;
                handleGamepadConnect(gamepadEvent);
            });
            Window.current().addEventListener("MozGamepadDisconnected", evt -> {
                GamepadEvent gamepadEvent = (GamepadEvent)evt;
                handleGamepadDisconnect(gamepadEvent);
            });
            if (shouldStartPolling()) {
                startPolling();
            }
        }
    }

    public static void startPolling() {
        if (timerId >= 0) {
            return;
        }
        consoleLog("startPolling");
        timerId = Window.setInterval(() -> { }, 100);
    }

    public static void stopPolling() {
        if (timerId < 0) {
            Window.clearInterval(timerId);
            timerId = -1;
        }
    }

    public static void pollGamepads() {
        JSArray<Gamepad> currentGamepads = nativePollGamepads();
        if (currentGamepads != null) {
            gamepadsTemp.clear();
            gamepadsTemp.putAll(gamepads);
            for (int i = 0, j = currentGamepads.getLength(); i < j; i++) {
                Gamepad gamepad = currentGamepads.get(i);
                if (gamepad != null) {
                    if (!gamepadsTemp.containsKey(gamepad.getIndex())) {
                        onGamepadConnect(gamepad);
                    }
                    gamepadsTemp.remove(gamepad.getIndex());
                }
            }
            for (Gamepad gamepad : gamepadsTemp.values()) {
                onGamepadDisconnect(gamepad);
            }
        }
    }

    public static void pollGamepadsStatus() {
        for (Gamepad gamepad : gamepads.values()) {
            if (gamepad.getPreviousTimestamp() != gamepad.getTimestamp()) {
                fireGamepadUpdated(gamepad.getIndex());
            }
            gamepad.setPreviousTimestamp(gamepad.getTimestamp());
        }
    }

    public static Gamepad getGamepad(int index) {
        return gamepads.get(index);
    }

    private static void onGamepadConnect(Gamepad gamepad) {
        consoleLog("onGamepadConnect: " + gamepad.getId());
        gamepads.put(gamepad.getIndex(), gamepad);
        fireGamepadConnected(gamepad.getIndex());
    }

    private static void onGamepadDisconnect(Gamepad gamepad) {
        consoleLog("onGamepadDisconnect: " + gamepad.getId());
        gamepads.remove(gamepad.getIndex());
        fireGamepadDisconnected(gamepad.getIndex());
    }

    private static void fireGamepadConnected(int index) {
        if (listener != null) {
            listener.onGamepadConnected(index);
        }
    }

    private static void fireGamepadDisconnected(int index) {
        if (listener != null) {
            listener.onGamepadDisconnected(index);
        }
    }

    private static void fireGamepadUpdated(int index) {
        if (listener != null) {
            listener.onGamepadUpdated(index);
        }
    }

    private static void handleGamepadConnect(GamepadEvent event) {
        onGamepadConnect(event.getGamepad());
    }
    private static void handleGamepadDisconnect(GamepadEvent event) {
        onGamepadDisconnect(event.getGamepad());
    }

    @JSBody(params = {}, script =
            "return !!navigator.getGamepads || !!navigator.webkitGamepads || !!navigator.webkitGetGamepads;")
    private static native boolean shouldStartPolling();

    @JSBody(params = {}, script =
        "return !!navigator.getGamepads || !!navigator.webkitGetGamepads || " +
                "!!navigator.webkitGamepads || (navigator.userAgent.indexOf('Firefox/') != -1);")
    private static native boolean isGamepardSupportAvailable();

    @JSBody(params = {}, script =
        "return rawGamepads = (navigator.webkitGetGamepads && navigator.webkitGetGamepads()) || " +
                "navigator.webkitGamepads;")
    private static native JSArray<Gamepad> nativePollGamepads();

    @JSBody(params = { "message" }, script = "window.console.log(message);")
    public static native void consoleLog(String message);

    private interface GamepadEvent extends Event {
        @JSProperty
        Gamepad getGamepad();
    }
}
