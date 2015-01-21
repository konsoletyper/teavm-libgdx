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
package org.teavm.libgdx.emu;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Alexey Andreev
 */
public class TeaVMFileHandle extends FileHandle {
    public static final FSEntry root = new FSEntry();
    private final String file;
    private final FileType type;

    public static class FSEntry {
        Map<String, FSEntry> childEntries = new HashMap<>();
        public byte[] data;
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

    private FSEntry entry() {
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

        }
        return result.toArray(new String[result.size()]);
    }

    @Override
    public BufferedInputStream read(int bufferSize) {
        return new BufferedInputStream(read(), bufferSize);
    }

    @Override
    public Reader reader() {
        return new InputStreamReader(read());
    }

    /**
     * Returns a reader for reading this file as characters.
     *
     * @throw GdxRuntimeException if the file handle represents a directory,
     *        doesn't exist, or could not be read.
     */
    @Override
    public Reader reader(String charset) {
        try {
            return new InputStreamReader(read(), charset);
        } catch (UnsupportedEncodingException e) {
            throw new GdxRuntimeException("Encoding '" + charset + "' not supported", e);
        }
    }

    /**
     * Returns a buffered reader for reading this file as characters.
     *
     * @throw GdxRuntimeException if the file handle represents a directory,
     *        doesn't exist, or could not be read.
     */
    @Override
    public BufferedReader reader(int bufferSize) {
        return new BufferedReader(reader(), bufferSize);
    }

    /**
     * Returns a buffered reader for reading this file as characters.
     *
     * @throw GdxRuntimeException if the file handle represents a directory,
     *        doesn't exist, or could not be read.
     */
    @Override
    public BufferedReader reader(int bufferSize, String charset) {
        return new BufferedReader(reader(charset), bufferSize);
    }

    /**
     * Reads the entire file into a string using the platform's default charset.
     *
     * @throw GdxRuntimeException if the file handle represents a directory,
     *        doesn't exist, or could not be read.
     */
    @Override
    public String readString() {
        return new String(readBytes());
    }

    /**
     * Reads the entire file into a byte array.
     *
     * @throw GdxRuntimeException if the file handle represents a directory,
     *        doesn't exist, or could not be read.
     */
    @Override
    public byte[] readBytes() {
        int length = (int)length();
        if (length == 0)
            length = 512;
        byte[] buffer = new byte[length];
        int position = 0;
        InputStream input = read();
        try {
            while (true) {
                int count = input.read(buffer, position, buffer.length - position);
                if (count == -1)
                    break;
                position += count;
                if (position == buffer.length) {
                    // Grow buffer.
                    byte[] newBuffer = new byte[buffer.length * 2];
                    System.arraycopy(buffer, 0, newBuffer, 0, position);
                    buffer = newBuffer;
                }
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
        }
        if (position < buffer.length) {
            // Shrink buffer.
            byte[] newBuffer = new byte[position];
            System.arraycopy(buffer, 0, newBuffer, 0, position);
            buffer = newBuffer;
        }
        return buffer;
    }

    /**
     * Reads the entire file into the byte array. The byte array must be big
     * enough to hold the file's data.
     *
     * @param bytes
     *            the array to load the file into
     * @param offset
     *            the offset to start writing bytes
     * @param size
     *            the number of bytes to read, see {@link #length()}
     * @return the number of read bytes
     */
    @Override
    public int readBytes(byte[] bytes, int offset, int size) {
        InputStream input = read();
        int position = 0;
        try {
            while (true) {
                int count = input.read(bytes, offset + position, size - position);
                if (count <= 0)
                    break;
                position += count;
            }
        } catch (IOException ex) {
            throw new GdxRuntimeException("Error reading file: " + this, ex);
        } finally {
            try {
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }
        }
        return position - offset;
    }

    /**
     * Returns a stream for writing to this file. Parent directories will be
     * created if necessary.
     *
     * @param append
     *            If false, this file will be overwritten if it exists,
     *            otherwise it will be appended.
     * @throw GdxRuntimeException if this file handle represents a directory, if
     *        it is a {@link FileType#Classpath} or {@link FileType#Internal}
     *        file, or if it could not be written.
     */
    @Override
    public OutputStream write(boolean append) {
        throw new GdxRuntimeException("Cannot write to files in GWT backend");
    }

    /**
     * Reads the remaining bytes from the specified stream and writes them to
     * this file. The stream is closed. Parent directories will be created if
     * necessary.
     *
     * @param append
     *            If false, this file will be overwritten if it exists,
     *            otherwise it will be appended.
     * @throw GdxRuntimeException if this file handle represents a directory, if
     *        it is a {@link FileType#Classpath} or {@link FileType#Internal}
     *        file, or if it could not be written.
     */
    @Override
    public void write(InputStream input, boolean append) {
        throw new GdxRuntimeException("Cannot write to files in GWT backend");
    }

    /**
     * Returns a writer for writing to this file using the default charset.
     * Parent directories will be created if necessary.
     *
     * @param append
     *            If false, this file will be overwritten if it exists,
     *            otherwise it will be appended.
     * @throw GdxRuntimeException if this file handle represents a directory, if
     *        it is a {@link FileType#Classpath} or {@link FileType#Internal}
     *        file, or if it could not be written.
     */
    @Override
    public Writer writer(boolean append) {
        return writer(append, null);
    }

    /**
     * Returns a writer for writing to this file. Parent directories will be
     * created if necessary.
     *
     * @param append
     *            If false, this file will be overwritten if it exists,
     *            otherwise it will be appended.
     * @param charset
     *            May be null to use the default charset.
     * @throw GdxRuntimeException if this file handle represents a directory, if
     *        it is a {@link FileType#Classpath} or {@link FileType#Internal}
     *        file, or if it could not be written.
     */
    @Override
    public Writer writer(boolean append, String charset) {
        throw new GdxRuntimeException("Cannot write to files in GWT backend");
    }

    /**
     * Writes the specified string to the file using the default charset. Parent
     * directories will be created if necessary.
     *
     * @param append
     *            If false, this file will be overwritten if it exists,
     *            otherwise it will be appended.
     * @throw GdxRuntimeException if this file handle represents a directory, if
     *        it is a {@link FileType#Classpath} or {@link FileType#Internal}
     *        file, or if it could not be written.
     */
    @Override
    public void writeString(String string, boolean append) {
        writeString(string, append, null);
    }

    /**
     * Writes the specified string to the file as UTF-8. Parent directories will
     * be created if necessary.
     *
     * @param append
     *            If false, this file will be overwritten if it exists,
     *            otherwise it will be appended.
     * @param charset
     *            May be null to use the default charset.
     * @throw GdxRuntimeException if this file handle represents a directory, if
     *        it is a {@link FileType#Classpath} or {@link FileType#Internal}
     *        file, or if it could not be written.
     */
    @Override
    public void writeString(String string, boolean append, String charset) {
        throw new GdxRuntimeException("Cannot write to files in GWT backend");
    }

    /**
     * Writes the specified bytes to the file. Parent directories will be
     * created if necessary.
     *
     * @param append
     *            If false, this file will be overwritten if it exists,
     *            otherwise it will be appended.
     * @throw GdxRuntimeException if this file handle represents a directory, if
     *        it is a {@link FileType#Classpath} or {@link FileType#Internal}
     *        file, or if it could not be written.
     */
    @Override
    public void writeBytes(byte[] bytes, boolean append) {
        throw new GdxRuntimeException("Cannot write to files in GWT backend");
    }

    /**
     * Writes the specified bytes to the file. Parent directories will be
     * created if necessary.
     *
     * @param append
     *            If false, this file will be overwritten if it exists,
     *            otherwise it will be appended.
     * @throw GdxRuntimeException if this file handle represents a directory, if
     *        it is a {@link FileType#Classpath} or {@link FileType#Internal}
     *        file, or if it could not be written.
     */
    @Override
    public void writeBytes(byte[] bytes, int offset, int length, boolean append) {
        throw new GdxRuntimeException("Cannot write to files in GWT backend");
    }

    /**
     * Returns the paths to the children of this directory. Returns an empty
     * list if this file handle represents a file and not a directory. On the
     * desktop, an {@link FileType#Internal} handle to a directory on the
     * classpath will return a zero length array.
     *
     * @throw GdxRuntimeException if this file is an {@link FileType#Classpath}
     *        file.
     */
    @Override
    public FileHandle[] list() {
        return preloader.list(file);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the
     * specified filter. Returns an empty list if this file handle represents a
     * file and not a directory. On the desktop, an {@link FileType#Internal}
     * handle to a directory on the classpath will return a zero length array.
     *
     * @throw GdxRuntimeException if this file is an {@link FileType#Classpath}
     *        file.
     */
    @Override
    public FileHandle[] list(FileFilter filter) {
        return preloader.list(file, filter);
    }

    /**
     * Returns the paths to the children of this directory that satisfy the
     * specified filter. Returns an empty list if this file handle represents a
     * file and not a directory. On the desktop, an {@link FileType#Internal}
     * handle to a directory on the classpath will return a zero length array.
     *
     * @throw GdxRuntimeException if this file is an {@link FileType#Classpath}
     *        file.
     */
    @Override
    public FileHandle[] list(FilenameFilter filter) {
        return preloader.list(file, filter);
    }

    /**
     * Returns the paths to the children of this directory with the specified
     * suffix. Returns an empty list if this file handle represents a file and
     * not a directory. On the desktop, an {@link FileType#Internal} handle to a
     * directory on the classpath will return a zero length array.
     *
     * @throw GdxRuntimeException if this file is an {@link FileType#Classpath}
     *        file.
     */
    @Override
    public FileHandle[] list(String suffix) {
        return preloader.list(file, suffix);
    }

    /**
     * Returns true if this file is a directory. Always returns false for
     * classpath files. On Android, an {@link FileType#Internal} handle to an
     * empty directory will return false. On the desktop, an
     * {@link FileType#Internal} handle to a directory on the classpath will
     * return false.
     */
    @Override
    public boolean isDirectory() {
        return preloader.isDirectory(file);
    }

    /**
     * Returns a handle to the child with the specified name.
     *
     * @throw GdxRuntimeException if this file handle is a
     *        {@link FileType#Classpath} or {@link FileType#Internal} and the
     *        child doesn't exist.
     */
    @Override
    public FileHandle child(String name) {
        return new GwtFileHandle(preloader, (file.isEmpty() ? "" : (file + (file.endsWith("/") ? "" : "/"))) + name,
                FileType.Internal);
    }

    @Override
    public FileHandle parent() {
        int index = file.lastIndexOf("/");
        String dir = "";
        if (index > 0)
            dir = file.substring(0, index);
        return new GwtFileHandle(preloader, dir, type);
    }

    @Override
    public FileHandle sibling(String name) {
        return parent().child(fixSlashes(name));
    }

    /**
     * @throw GdxRuntimeException if this file handle is a
     *        {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    @Override
    public void mkdirs() {
        throw new GdxRuntimeException("Cannot mkdirs with an internal file: " + file);
    }

    /**
     * Returns true if the file exists. On Android, a {@link FileType#Classpath}
     * or {@link FileType#Internal} handle to a directory will always return
     * false.
     */
    @Override
    public boolean exists() {
        return preloader.contains(file);
    }

    /**
     * Deletes this file or empty directory and returns success. Will not delete
     * a directory that has children.
     *
     * @throw GdxRuntimeException if this file handle is a
     *        {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    @Override
    public boolean delete() {
        throw new GdxRuntimeException("Cannot delete an internal file: " + file);
    }

    /**
     * Deletes this file or directory and all children, recursively.
     *
     * @throw GdxRuntimeException if this file handle is a
     *        {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    @Override
    public boolean deleteDirectory() {
        throw new GdxRuntimeException("Cannot delete an internal file: " + file);
    }

    /**
     * Copies this file or directory to the specified file or directory. If this
     * handle is a file, then 1) if the destination is a file, it is
     * overwritten, or 2) if the destination is a directory, this file is copied
     * into it, or 3) if the destination doesn't exist, {@link #mkdirs()} is
     * called on the destination's parent and this file is copied into it with a
     * new name. If this handle is a directory, then 1) if the destination is a
     * file, GdxRuntimeException is thrown, or 2) if the destination is a
     * directory, this directory is copied into it recursively, overwriting
     * existing files, or 3) if the destination doesn't exist, {@link #mkdirs()}
     * is called on the destination and this directory is copied into it
     * recursively.
     *
     * @throw GdxRuntimeException if the destination file handle is a
     *        {@link FileType#Classpath} or {@link FileType#Internal} file, or
     *        copying failed.
     */
    @Override
    public void copyTo(FileHandle dest) {
        throw new GdxRuntimeException("Cannot copy to an internal file: " + dest);
    }

    /**
     * Moves this file to the specified file, overwriting the file if it already
     * exists.
     *
     * @throw GdxRuntimeException if the source or destination file handle is a
     *        {@link FileType#Classpath} or {@link FileType#Internal} file.
     */
    @Override
    public void moveTo(FileHandle dest) {
        throw new GdxRuntimeException("Cannot move an internal file: " + file);
    }

    /**
     * Returns the length in bytes of this file, or 0 if this file is a
     * directory, does not exist, or the size cannot otherwise be determined.
     */
    @Override
    public long length() {
        return preloader.length(file);
    }

    /**
     * Returns the last modified time in milliseconds for this file. Zero is
     * returned if the file doesn't exist. Zero is returned for
     * {@link FileType#Classpath} files. On Android, zero is returned for
     * {@link FileType#Internal} files. On the desktop, zero is returned for
     * {@link FileType#Internal} files on the classpath.
     */
    @Override
    public long lastModified() {
        return 0;
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
