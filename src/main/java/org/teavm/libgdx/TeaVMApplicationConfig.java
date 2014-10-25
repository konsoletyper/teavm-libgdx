package org.teavm.libgdx;

import org.teavm.dom.html.HTMLCanvasElement;

public class TeaVMApplicationConfig {
    private HTMLCanvasElement canvas;

    public HTMLCanvasElement getCanvas() {
        return canvas;
    }

    public void setCanvas(HTMLCanvasElement canvas) {
        this.canvas = canvas;
    }
}
