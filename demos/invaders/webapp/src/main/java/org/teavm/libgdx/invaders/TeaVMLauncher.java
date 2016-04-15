package org.teavm.libgdx.invaders;

import com.badlogic.invaders.Invaders;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import org.teavm.jso.dom.xml.Document;
import org.teavm.libgdx.TeaVMApplication;
import org.teavm.libgdx.TeaVMApplicationConfig;

public class TeaVMLauncher {
    public static void main(String[] args) {
        Document document = HTMLDocument.current();
        TeaVMApplicationConfig config = new TeaVMApplicationConfig();
        config.setCanvas((HTMLCanvasElement)document.getElementById("invaders-canvas"));
        new TeaVMApplication(new Invaders(), config).start();
    }
}
