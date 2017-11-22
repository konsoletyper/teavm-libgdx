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
package org.teavm.libgdx.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;
import org.teavm.backend.javascript.rendering.RenderingManager;
import org.teavm.vm.BuildTarget;
import org.teavm.vm.spi.RendererListener;

/**
 *
 * @author Alexey Andreev
 */
public class AssetsCopier implements RendererListener {
    private RenderingManager context;
    private FileDescriptor rootFileDescriptor = new FileDescriptor();
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectWriter writer = mapper.writerFor(FileDescriptor.class);

    @Override
    public void begin(RenderingManager context, BuildTarget buildTarget) throws IOException {
        this.context = context;
    }

    @Override
    public void complete() throws IOException {
        String dirName = context.getProperties().getProperty("teavm.libgdx.genAssetsDirectory", "");
        if (!dirName.isEmpty()) {
            File dir = new File(dirName);
            dir.mkdirs();
            copyClasspathAssets(dir);
            createFSDescriptor(dir);
        } else {
            createFSDescriptor(null);
        }
    }

    private void copyClasspathAssets(File dir) throws IOException {
        Enumeration<URL> resources = context.getClassLoader().getResources("META-INF/teavm-libgdx/classpath-assets");
        Set<String> resourcesToCopy = new HashSet<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            InputStream input = resource.openStream();
            if (input == null) {
                continue;
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    line = line.trim();
                    if (line.isEmpty() || line.startsWith("#")) {
                        continue;
                    }
                    resourcesToCopy.add(line);
                }
            }
        }

        for (String resourceToCopy : resourcesToCopy) {
            File resource = new File(dir, resourceToCopy);
            if (resource.exists()) {
                URL url = context.getClassLoader().getResource(resourceToCopy);
                if (url != null && url.getProtocol().equals("file")) {
                    try {
                        File sourceFile = new File(url.toURI());
                        if (sourceFile.exists() && sourceFile.length() == resource.length() &&
                                sourceFile.lastModified() == resource.lastModified()) {
                            continue;
                        }
                    } catch (URISyntaxException e) {
                        // fall back to usual resource copying
                    }
                }
            }
            InputStream input = context.getClassLoader().getResourceAsStream(resourceToCopy);
            if (input == null) {
                continue;
            }
            resource.getParentFile().mkdirs();
            IOUtils.copy(input, new FileOutputStream(resource));
        }
    }

    private void createFSDescriptor(File dir) throws IOException {
        String path = context.getProperties().getProperty("teavm.libgdx.fsJsonPath", "");
        if (path.isEmpty()) {
            return;
        }
        if (dir != null) {
            processFile(dir, rootFileDescriptor);
        }

        String dirName = context.getProperties().getProperty("teavm.libgdx.warAssetsDirectory", "");
        if (!dirName.isEmpty()) {
            dir = new File(dirName);
            processFile(dir, rootFileDescriptor);
        }

        try (FileOutputStream output = new FileOutputStream(new File(path))) {
            writeJsonFS(output);
        }
    }

    private void processFile(File file, FileDescriptor desc) {
        desc.setName(file.getName());
        desc.setDirectory(file.isDirectory());
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                FileDescriptor childDesc = new  FileDescriptor();
                processFile(child, childDesc);
                desc.getChildFiles().add(childDesc);
            }
        }
    }

    private void writeJsonFS(OutputStream output) throws IOException {
        SequenceWriter seqWriter = writer.writeValues(output);
        boolean first = true;
        output.write((byte)'[');
        for (FileDescriptor desc : rootFileDescriptor.getChildFiles()) {
            if (!first) {
                output.write((byte)',');
            }
            first = false;
            seqWriter.write(desc);
        }
        output.write((byte)']');
        seqWriter.flush();
    }
}
