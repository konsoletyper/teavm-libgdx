package org.teavm.libgdx.controllers.support;

/**
 *
 * @author Alexey Andreev
 */
public interface GamepadSupportListener {
    public void onGamepadConnected(int index);

    public void onGamepadDisconnected(int index);

    public void onGamepadUpdated(int index);
}
