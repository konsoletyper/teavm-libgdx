package org.teavm.libgdx;

import org.teavm.jso.JSArrayReader;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public interface FileDescriptor extends JSObject {
    @JSProperty
    String getName();

    @JSProperty
    boolean isDirectory();

    @JSProperty
    JSArrayReader<FileDescriptor> getChildFiles();
}
