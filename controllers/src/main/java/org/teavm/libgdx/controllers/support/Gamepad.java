package org.teavm.libgdx.controllers.support;

import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSArray;
import org.teavm.jso.core.JSNumber;

/**
 *
 * @author Alexey Andreev
 */
public interface Gamepad extends JSObject {
    String getId();

    int getIndex();

    double getTimestamp();

    JSArray<JSNumber> getAxes();

    JSArray<JSNumber> getButtons();

    double getPreviousTimestamp();

    void setPreviousTimestamp(double previousTimestamp);
}
