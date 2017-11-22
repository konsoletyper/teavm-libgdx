package org.teavm.libgdx.controllers.support;

import org.teavm.jso.JSObject;

/**
 *
 * @author Alexey Andreev
 */
public interface Gamepad extends JSObject {
    String getId();

    int getIndex();

    double getTimestamp();

    double[] getAxes();

    int[] getButtons();

    double getPreviousTimestamp();

    void setPreviousTimestamp(double previousTimestamp);
}
