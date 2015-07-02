package org.teavm.libgdx.controllers;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.IntFloatMap;
import org.teavm.libgdx.controllers.support.Gamepad;
import org.teavm.libgdx.controllers.support.GamepadSupport;

/**
 *
 * @author Alexey Andreev
 */
public class TeaVMController implements Controller{
    private int index;

    private String name;

    protected final float[] axes;

    final IntFloatMap buttons = new IntFloatMap();

    private final Array<ControllerListener> listeners = new Array<>();

    public TeaVMController(int index, String name) {
        this.index = index;
        this.name = name;

        Gamepad gamepad = GamepadSupport.getGamepad(index);
        axes = new float[gamepad.getAxes().getLength()];
    }

    public int getIndex() {
        return index;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean getButton(int buttonCode) {
        return buttons.get(buttonCode, 0) >= 0.5f;
    }

    public float getButtonAmount(int buttonCode) {
        return buttons.get(buttonCode, 0);
    }

    @Override
    public float getAxis(int axisIndex) {
        if(axisIndex < 0 || axisIndex >= axes.length) return 0;
        return axes[axisIndex];
    }

    @Override
    public PovDirection getPov(int povCode) {
        return PovDirection.center;
    }

    @Override
    public boolean getSliderX(int sliderCode) {
        return false;
    }

    @Override
    public boolean getSliderY(int sliderCode) {
        return false;
    }

    @Override
    public Vector3 getAccelerometer(int accelerometerCode) {
        return Vector3.Zero;
    }

    @Override
    public void setAccelerometerSensitivity(float sensitivity) {
        // Nope
    }

    @Override
    public void addListener(ControllerListener listener) {
        this.listeners.add(listener);
    }

    @Override
    public void removeListener(ControllerListener listener) {
        this.listeners.removeValue(listener, true);
    }

    public Array<ControllerListener> getListeners() {
        return listeners;
    }
}
