package org.teavm.libgdx.controllers.support;

import org.teavm.jso.JSDoubleArray;
import org.teavm.jso.JSIntArray;
import org.teavm.jso.JSObject;

/**
 *
 * @author Alexey Andreev
 */
public interface Gamepad extends JSObject {
    String getId();

    int getIndex();

    double getTimestamp();

    JSDoubleArray getAxes();

    JSIntArray getButtons();

    double getPreviousTimestamp();

    void setPreviousTimestamp(double previousTimestamp);
}
