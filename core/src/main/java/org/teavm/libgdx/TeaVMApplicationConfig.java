package org.teavm.libgdx;

import org.teavm.jso.dom.html.HTMLCanvasElement;

public class TeaVMApplicationConfig {
    private HTMLCanvasElement canvas;
    private boolean antialiasEnabled;
    private boolean stencilEnabled;
    private boolean alphaEnabled;
    private boolean premultipliedAlpha;
    private boolean drawingBufferPreserved;

    public HTMLCanvasElement getCanvas() {
        return canvas;
    }

    public void setCanvas(HTMLCanvasElement canvas) {
        this.canvas = canvas;
    }

    public boolean isAntialiasEnabled() {
        return antialiasEnabled;
    }

    public void setAntialiasEnabled(boolean antialiasEnabled) {
        this.antialiasEnabled = antialiasEnabled;
    }

    public boolean isStencilEnabled() {
        return stencilEnabled;
    }

    public void setStencilEnabled(boolean stencilEnabled) {
        this.stencilEnabled = stencilEnabled;
    }

    public boolean isAlphaEnabled() {
        return alphaEnabled;
    }

    public void setAlphaEnabled(boolean alphaEnabled) {
        this.alphaEnabled = alphaEnabled;
    }

    public boolean isPremultipliedAlpha() {
        return premultipliedAlpha;
    }

    public void setPremultipliedAlpha(boolean premultipliedAlpha) {
        this.premultipliedAlpha = premultipliedAlpha;
    }

    public boolean isDrawingBufferPreserved() {
        return drawingBufferPreserved;
    }

    public void setDrawingBufferPreserved(boolean drawingBufferPreserved) {
        this.drawingBufferPreserved = drawingBufferPreserved;
    }
}
