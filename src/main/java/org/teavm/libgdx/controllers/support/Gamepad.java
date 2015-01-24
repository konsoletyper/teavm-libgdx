package org.teavm.libgdx.controllers.support;

import org.teavm.jso.JSFloatArray;
import org.teavm.jso.JSIntArray;
import org.teavm.jso.JSObject;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public interface Gamepad extends JSObject {
    String getId();

    int getIndex();

    double getTimestamp();

    JSFloatArray getAxes();

    JSIntArray getButtons();

    double getPreviousTimestamp();

    void setPreviousTimestamp(double previousTimestamp);
}
