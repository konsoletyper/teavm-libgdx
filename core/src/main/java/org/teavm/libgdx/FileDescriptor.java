package org.teavm.libgdx;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSArrayReader;

/**
 *
 * @author Alexey Andreev
 */
public interface FileDescriptor extends JSObject {
    @JSProperty
    String getName();

    @JSProperty
    boolean isDirectory();

    @JSProperty
    JSArrayReader<FileDescriptor> getChildFiles();
}
