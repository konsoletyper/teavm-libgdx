package org.teavm.libgdx.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Alexey Andreev <konsoletyper@gmail.com>
 */
public class LibGDXFilesServlet extends HttpServlet {
    private static final long serialVersionUID = -4400662787888459754L;
    private FileDescriptor rootFileDescriptor = new FileDescriptor();
    private ObjectMapper mapper = new ObjectMapper();
    private ObjectWriter writer = mapper.writerFor(FileDescriptor.class);

    @Override
    public void init() throws ServletException {
        File rootFile = new File(getServletContext().getRealPath("assets"));
        processFile(rootFile, rootFileDescriptor);
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setHeader("Content-Type", "application/json");
        SequenceWriter seqWriter = writer.writeValues(resp.getOutputStream());
        boolean first = true;
        resp.getOutputStream().write((byte)'[');
        for (FileDescriptor desc : rootFileDescriptor.getChildFiles()) {
            if (!first) {
                resp.getOutputStream().write((byte)',');
            }
            first = false;
            seqWriter.write(desc);
        }
        resp.getOutputStream().write((byte)']');
        seqWriter.flush();
    }
}
