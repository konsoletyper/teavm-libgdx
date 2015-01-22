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

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import org.teavm.dom.html.HTMLCanvasElement;
import org.teavm.dom.webgl.WebGLContextAttributes;
import org.teavm.dom.webgl.WebGLContextAttributesFactory;
import org.teavm.jso.JS;

/**
 *
 * @author Alexey Andreev
 */
public class TeaVMGraphics implements Graphics {
    private HTMLCanvasElement element;

    public TeaVMGraphics(HTMLCanvasElement element, TeaVMApplicationConfig config) {
        this.element = element;
        WebGLContextAttributesFactory attrFactory = (WebGLContextAttributesFactory)JS.getGlobal();
        WebGLContextAttributes attr = attrFactory.createWebGLContextAttributes();
        attr.setAlpha(config.isAlphaEnabled());
        attr.setAntialias(config.isAntialiasEnabled());
        attr.setStencil(config.isStencilEnabled());
        attr.setPremultipliedAlpha(config.isPremultipliedAlpha());
        attr.setPreserveDrawingBuffer(config.isDrawingBufferPreserved());

        element.getContext("webgl", attr);
    }

    @Override
    public boolean isGL30Available() {
        return false;
    }

    @Override
    public GL20 getGL20() {
        return null;
    }

    @Override
    public GL30 getGL30() {
        return null;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public long getFrameId() {
        return 0;
    }

    @Override
    public float getDeltaTime() {
        return 0;
    }

    @Override
    public float getRawDeltaTime() {
        return 0;
    }

    @Override
    public int getFramesPerSecond() {
        return 0;
    }

    @Override
    public GraphicsType getType() {
        return null;
    }

    @Override
    public float getPpiX() {
        return 0;
    }

    @Override
    public float getPpiY() {
        return 0;
    }

    @Override
    public float getPpcX() {
        return 0;
    }

    @Override
    public float getPpcY() {
        return 0;
    }

    @Override
    public float getDensity() {
        return 0;
    }

    @Override
    public boolean supportsDisplayModeChange() {
        return false;
    }

    @Override
    public DisplayMode[] getDisplayModes() {
        return null;
    }

    @Override
    public DisplayMode getDesktopDisplayMode() {
        return null;
    }

    @Override
    public boolean setDisplayMode(DisplayMode displayMode) {
        return false;
    }

    @Override
    public boolean setDisplayMode(int width, int height, boolean fullscreen) {
        return false;
    }

    @Override
    public void setTitle(String title) {
    }

    @Override
    public void setVSync(boolean vsync) {
    }

    @Override
    public BufferFormat getBufferFormat() {
        return null;
    }

    @Override
    public boolean supportsExtension(String extension) {
        return false;
    }

    @Override
    public void setContinuousRendering(boolean isContinuous) {
    }

    @Override
    public boolean isContinuousRendering() {
        return false;
    }

    @Override
    public void requestRendering() {
    }

    @Override
    public boolean isFullscreen() {
        return false;
    }
}
