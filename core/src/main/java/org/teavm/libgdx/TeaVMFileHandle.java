/*
 *  Copyright 2015 Alexey Andreev.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.teavm.libgdx;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.teavm.jso.dom.html.HTMLImageElement;

/**
 *
 * @author Alexey Andreev
 */
public class TeaVMFileHandle extends FileHandle {
    public static final FSEntry root = new FSEntry();
    private final String file;
    private final FileType type;

    public static class FSEntry {
        public final Map<String, FSEntry> childEntries = new HashMap<>();
        public byte[] data;
        public long lastModified;
        public boolean directory;
        public HTMLImageElement imageElem;
    }

    public TeaVMFileHandle(String fileName, FileType type) {
        if (type != FileType.Internal && type != FileType.Classpath) {
            throw new GdxRuntimeException("FileType '" + type + "' Not supported in GWT backend");
        }
        this.file = fixSlashes(fileName);
        this.type = type;
    }

    public TeaVMFileHandle(String path) {
        this.type = FileType.Internal;
        this.file = fixSlashes(path);
    }

    @Override
    public String path() {
        return file;
    }

    @Override
    public String name() {
        int index = file.lastIndexOf('/');
        if (index < 0) {
            return file;
        }
        return file.substring(index + 1);
    }

    @Override
    public String extension() {
        String name = name();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) {
            return "";
        }
        return name.substring(dotIndex + 1);
    }

    @Override
    public String nameWithoutExtension() {
        String name = name();
        int dotIndex = name.lastIndexOf('.');
        if (dotIndex == -1) {
            return name;
        }
        return name.substring(0, dotIndex);
    }

    @Override
    public String pathWithoutExtension() {
        String path = file;
        int dotIndex = path.lastIndexOf('.');
        if (dotIndex == -1) {
            return path;
        }
        return path.substring(0, dotIndex);
    }

    @Override
    public FileType type() {
        return type;
    }

    @Override
    public InputStream read() {
        FSEntry entry = entry();
        if (entry == null || entry.data == null) {
            throw new GdxRuntimeException(file + " does not exist");
        }
        return new ByteArrayInputStream(entry.data);
    }

    public FSEntry entry() {
        FSEntry entry = root;
        for (String part : split()) {
            entry = entry.childEntries.get(part);
            if (entry == null) {
                break;
            }
        }
        return entry;
    }

    private String[] split() {
        List<String> result = new ArrayList<>();
        int index = 0;
        while (index < file.length()) {
            int next = file.indexOf('/', index);
            if (next == -1) {
                break;
            }
            addPart(index, next, result);
            index = next + 1;
        }
        addPart(index, file.length(), result);
        return result.toArray(new String[result.size()]);
    }

    private void addPart(int index, int next, List<String> result) {
        String part = file.substring(index, next);
        if (!part.isEmpty() && !part.equals(".")) {
            if (part.equals("..")) {
                result.remove(result.size() - 1);
            } else {
                result.add(part);
            }
        }
    }

    @Override
    public BufferedInputStream read(int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(read());
    }

    @Override
    public String readString() {
        return new String(readBytes());
    }

    @Override
    public byte[] readBytes() {
        FSEntry entry = entry();
        if (entry == null || entry.data == null) {
            throw new GdxRuntimeException("File does not exist: " + file);
        }
        return Arrays.copyOf(entry.data, entry.data.length);
    }

    @Override
    public int readBytes(byte[] bytes, int offset, int size) {
        FSEntry entry = entry();
        if (entry == null || entry.data == null) {
            throw new GdxRuntimeException("File does not exist: " + file);
        }
        size = Math.min(size, entry.data.length);
        System.arraycopy(entry.data, 0, bytes, offset, size);
        return size;
    }

    @Override
    public FileHandle[] list() {
        FSEntry entry = entry();
        if (entry == null) {
            throw new GdxRuntimeException("File does not exist: " + file);
        }
        FileHandle[] result = new FileHandle[entry.childEntries.size()];
        int index = 0;
        for (String childName : entry.childEntries.keySet()) {
            result[index++] = new TeaVMFileHandle(file + "/" + childName, type);
        }
        return result;
    }

    @Override
    public FileHandle[] list(String suffix) {
        FSEntry entry = entry();
        if (entry == null) {
            throw new GdxRuntimeException("File does not exist: " + file);
        }
        FileHandle[] result = new FileHandle[entry.childEntries.size()];
        int index = 0;
        for (String childName : entry.childEntries.keySet()) {
            if (childName.endsWith(suffix)) {
                result[index++] = new TeaVMFileHandle(file + "/" + childName, type);
            }
        }
        return index == result.length ? result : Arrays.copyOf(result, index);
    }

    @Override
    public boolean isDirectory() {
        FSEntry entry = entry();
        return entry != null && entry.data == null;
    }

    @Override
    public FileHandle child(String name) {
        return new TeaVMFileHandle(file + "/" + fixSlashes(name), type);
    }

    @Override
    public FileHandle parent() {
        int index = file.lastIndexOf('/', file.endsWith("/") ? file.length() - 1 : file.length());
        return index > 1 ? new TeaVMFileHandle(file.substring(0, index), type) : null;
    }

    @Override
    public FileHandle sibling(String name) {
        return parent().child(fixSlashes(name));
    }

    @Override
    public boolean exists() {
        return entry() != null;
    }

    @Override
    public long length() {
        FSEntry entry = entry();
        return entry != null && entry.data != null ? entry.data.length : null;
    }

    @Override
    public long lastModified() {
        FSEntry entry = entry();
        return entry != null ? entry.lastModified : null;
    }

    @Override
    public String toString() {
        return file;
    }

    private static String fixSlashes(String path) {
        path = path.replace('\\', '/');
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
