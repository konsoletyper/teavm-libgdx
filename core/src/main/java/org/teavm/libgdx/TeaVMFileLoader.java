package org.teavm.libgdx;

import java.util.ArrayDeque;
import java.util.Queue;
import org.teavm.jso.ajax.ReadyStateChangeHandler;
import org.teavm.jso.ajax.XMLHttpRequest;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.dom.events.Event;
import org.teavm.jso.dom.events.EventListener;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.html.HTMLImageElement;
import org.teavm.jso.json.JSON;
import org.teavm.libgdx.TeaVMFileHandle.FSEntry;

/**
 *
 * @author Alexey Andreev
 */
public class TeaVMFileLoader {
    private static final XMLHttpRequest xhr = XMLHttpRequest.create();

    public static void loadFiles(final TeaVMFilePreloadListener listener) {
        xhr.setOnReadyStateChange(new ReadyStateChangeHandler() {
            @Override
            public void stateChanged() {
                if (xhr.getReadyState() != XMLHttpRequest.DONE) {
                    return;
                }
                if (xhr.getStatus() != 200) {
                    listener.error();
                    return;
                }
                loadAll(listener);
            }
        });
        xhr.open("GET", "filesystem.json");
        xhr.send();
    }

    private static void loadAll(final TeaVMFilePreloadListener listener) {
        final Queue<Task> tasks = new ArrayDeque<>();
        loadDescription(tasks);
        new QueuedTaskExecutor(tasks, new TaskHandler() {
            @Override public void complete() {
                listener.complete();
            }
            @Override public void error() {
                listener.error();
            }
        }).complete();
    }

    static class QueuedTaskExecutor implements TaskHandler {
        Queue<Task> tasks;
        TaskHandler handler;
        public QueuedTaskExecutor(Queue<Task> tasks, TaskHandler handler) {
            this.tasks = tasks;
            this.handler = handler;
        }
        @Override
        public void complete() {
            Task next = tasks.poll();
            if (next == null) {
                handler.complete();
            } else {
                next.run(this);
            }
        }
        @Override
        public void error() {
            handler.error();
        }
    }

    private static void loadDescription(Queue<Task> tasks) {
        @SuppressWarnings("unchecked")
        JSArrayReader<FileDescriptor> rootFiles = (JSArrayReader<FileDescriptor>) JSON.parse(xhr.getResponseText());
        initEntry(TeaVMFileHandle.root, rootFiles, "assets", tasks);
    }

    private static void initEntry(FSEntry parent, JSArrayReader<FileDescriptor> descList, String fullPath,
            Queue<Task> tasks) {
        for (int i = 0; i < descList.getLength(); ++i) {
            FileDescriptor fileDesc = descList.get(i);
            final FSEntry entry = new FSEntry();
            String name = fileDesc.getName();
            entry.directory = fileDesc.isDirectory();
            parent.childEntries.put(name, entry);
            final String entryPath = fullPath + "/" + name;
            if (entry.directory) {
                initEntry(entry, fileDesc.getChildFiles(), entryPath, tasks);
            } else {
                tasks.add(new LoadFileTask(entry, entryPath));
                if (name.endsWith(".png") || name.endsWith("jpeg") || name.endsWith("jpg") ||
                        name.endsWith("gif") || name.endsWith("bmp")) {
                    tasks.add(new LoadImageTask(entry, entryPath));
                }
            }
        }
    }

    private static class LoadFileTask implements Task, ReadyStateChangeHandler {
        FSEntry entry;
        String path;
        TaskHandler handler;
        public LoadFileTask(FSEntry entry, String path) {
            this.entry = entry;
            this.path = path;
        }
        @Override
        public void run(final TaskHandler handler) {
            this.handler = handler;
            xhr.setOnReadyStateChange(this);
            xhr.open("GET", path);
            xhr.overrideMimeType("text/plain; charset=x-user-defined");
            xhr.send();
        }
        @Override
        public void stateChanged() {
            if (xhr.getReadyState() != XMLHttpRequest.DONE) {
                return;
            }
            if (xhr.getStatus() != 200) {
                handler.error();
                return;
            }
            String text = xhr.getResponseText();
            entry.data = new byte[text.length()];
            for (int i = 0; i < text.length(); ++i) {
                entry.data[i] = (byte)text.charAt(i);
            }
            handler.complete();
        }
    }

    private static class LoadImageTask implements Task, EventListener<Event> {
        FSEntry entry;
        String path;
        TaskHandler handler;
        public LoadImageTask(FSEntry entry, String path) {
            this.entry = entry;
            this.path = path;
        }
        @Override
        public void run(TaskHandler handler) {
            this.handler = handler;
            HTMLDocument document = Window.current().getDocument();
            HTMLImageElement image = (HTMLImageElement) document.createElement("img");
            document.getBody().appendChild(image);
            image.getStyle().setProperty("display", "none");
            image.setSrc(path);
            image.addEventListener("load", this);
            entry.imageElem = image;
        }
        @Override
        public void handleEvent(Event evt) {
            handler.complete();
        }
    }

    interface Task {
        void run(TaskHandler handler);
    }
    interface TaskHandler {
        void complete();

        void error();
    }
}
