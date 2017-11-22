package org.teavm.libgdx.invaders;

import org.teavm.jso.browser.Window;
import org.teavm.dom.core.Document;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.jso.JS;
import org.teavm.libgdx.TeaVMApplication;
import org.teavm.libgdx.TeaVMApplicationConfig;

import com.badlogic.invaders.Invaders;

public class TeaVMLauncher {
    public static void main(String[] args) {
        Window window = Window.current();
        Document document = window.getDocument();
        TeaVMApplicationConfig config = new TeaVMApplicationConfig();
        config.setCanvas((HTMLCanvasElement)document.getElementById("invaders-canvas"));
        new TeaVMApplication(new Invaders(), config).start();
    }
}
