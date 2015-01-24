package org.teavm.libgdx.controllers.support;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public interface GamepadSupportListener {
    public void onGamepadConnected(int index);

    public void onGamepadDisconnected(int index);

    public void onGamepadUpdated(int index);
}
