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
import org.teavm.dom.browser.Screen;
import org.teavm.jso.browser.Window;
import org.teavm.jso.dom.html.HTMLCanvasElement;
import org.teavm.dom.webgl.WebGLContextAttributes;
import org.teavm.dom.webgl.WebGLContextAttributesFactory;
import org.teavm.dom.webgl.WebGLRenderingContext;
import org.teavm.jso.JS;
import org.teavm.jso.JSStringArrayReader;

/**
 *
 * @author Alexey Andreev
 */
public class TeaVMGraphics implements Graphics {
    private HTMLCanvasElement element;
    private TeaVMApplicationConfig config;
    private WebGLRenderingContext context;
    long frameId = -1;
    float deltaTime;
    long lastTimeStamp;
    long time;
    int frames;
    float fps;
    private TeaVMGL20 gl20;

    public TeaVMGraphics(HTMLCanvasElement element, TeaVMApplicationConfig config) {
        this.element = element;
        this.config = config;

        WebGLContextAttributesFactory attrFactory = (WebGLContextAttributesFactory)JS.getGlobal();
        WebGLContextAttributes attr = attrFactory.createWebGLContextAttributes();
        attr.setAlpha(config.isAlphaEnabled());
        attr.setAntialias(config.isAntialiasEnabled());
        attr.setStencil(config.isStencilEnabled());
        attr.setPremultipliedAlpha(config.isPremultipliedAlpha());
        attr.setPreserveDrawingBuffer(config.isDrawingBufferPreserved());

        context = (WebGLRenderingContext)element.getContext("webgl");
        context.viewport(0, 0, element.getWidth(), element.getHeight());
        gl20 = new TeaVMGL20(context);
    }

    @Override
    public boolean isGL30Available() {
        return false;
    }

    @Override
    public GL20 getGL20() {
        return gl20;
    }

    @Override
    public GL30 getGL30() {
        return null;
    }

    @Override
    public int getWidth() {
        return element.getWidth();
    }

    @Override
    public int getHeight() {
        return element.getHeight();
    }

    @Override
    public long getFrameId() {
        return frameId;
    }

    @Override
    public float getDeltaTime() {
        return deltaTime;
    }

    @Override
    public float getRawDeltaTime() {
        return deltaTime;
    }

    @Override
    public int getFramesPerSecond() {
        return (int)fps;
    }

    @Override
    public GraphicsType getType() {
        return GraphicsType.WebGL;
    }

    @Override
    public float getPpiX() {
        return 96;
    }

    @Override
    public float getPpiY() {
        return 96;
    }

    @Override
    public float getPpcX() {
        return 96 / 2.54f;
    }

    @Override
    public float getPpcY() {
        return 96 / 2.54f;
    }

    @Override
    public float getDensity() {
        return 0;
    }

    @Override
    public boolean supportsDisplayModeChange() {
        return true;
    }

    @Override
    public DisplayMode[] getDisplayModes() {
        Window window = Window.current();
        Screen screen = window.getScreen();
        return new DisplayMode[] { new DisplayMode(screen.getWidth(), screen.getHeight(), 60, 8) {}};
    }

    @Override
    public DisplayMode getDesktopDisplayMode() {
        return getDisplayModes()[0];
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
        return new BufferFormat(8, 8, 8, 0, 16, config.isStencilEnabled() ? 8 : 0, 0, false);
    }

    @Override
    public boolean supportsExtension(String extension) {
        JSStringArrayReader array = context.getSupportedExtensions();
        for (int i = 0; i < array.getLength(); ++i) {
            if (array.get(i).equals(extension)) {
                return true;
            }
        }
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

    public void update () {
        long currTimeStamp = System.currentTimeMillis();
        deltaTime = (currTimeStamp - lastTimeStamp) / 1000.0f;
        lastTimeStamp = currTimeStamp;
        time += deltaTime;
        frames++;
        if (time > 1) {
            this.fps = frames;
            time = 0;
            frames = 0;
        }
    }
}
