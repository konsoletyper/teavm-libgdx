package org.teavm.libgdx;

import org.teavm.dom.browser.TimerHandler;
import org.teavm.dom.browser.Window;
import org.teavm.dom.html.HTMLCanvasElement;
import org.teavm.jso.JS;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Audio;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.LifecycleListener;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Clipboard;

public class TeaVMApplication implements Application {
    private Window window = (Window)JS.getGlobal();
    private ApplicationListener listener;
    private TeaVMApplicationConfig config;
    private HTMLCanvasElement canvas;
    private TeaVMGraphics graphics;
    private TeaVMFiles files;
    private TeaVMAudio audio;
    private TeaVMInput input;

    public TeaVMApplication(ApplicationListener listener, TeaVMApplicationConfig config) {
        this.listener = listener;
        this.config = config;
    }

    public void start() {
        TeaVMFileLoader.loadFiles(new TeaVMFilePreloadListener() {
            @Override
            public void error() {
            }
            @Override
            public void complete() {
                startGdx();
            }
        });
    }

    private void startGdx() {
        canvas = config.getCanvas();
        graphics = new TeaVMGraphics(canvas, config);
        files = new TeaVMFiles();
        audio = new TeaVMAudio();
        input = new TeaVMInput(canvas);
        Gdx.app = this;
        Gdx.graphics = graphics;
        Gdx.gl = graphics.getGL20();
        Gdx.gl20 = graphics.getGL20();
        Gdx.files = files;
        Gdx.audio = audio;
        Gdx.input = input;
        listener.create();
        listener.resize(canvas.getWidth(), canvas.getHeight());
        delayedStep();
    }

    private void delayedStep() {
        window.setTimeout(new TimerHandler() {
            @Override public void onTimer() {
                step();
            }
        }, 0);
    }

    private void step() {
        delayedStep();
    }

    @Override
    public ApplicationListener getApplicationListener() {
        return listener;
    }

    @Override
    public Graphics getGraphics() {
        return graphics;
    }

    @Override
    public Audio getAudio() {
        return audio;
    }

    @Override
    public Input getInput() {
        return input;
    }

    @Override
    public Files getFiles() {
        return files;
    }

    @Override
    public Net getNet() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void log(String tag, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void log(String tag, String message, Throwable exception) {
        // TODO Auto-generated method stub

    }

    @Override
    public void error(String tag, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void error(String tag, String message, Throwable exception) {
        // TODO Auto-generated method stub

    }

    @Override
    public void debug(String tag, String message) {
        // TODO Auto-generated method stub

    }

    @Override
    public void debug(String tag, String message, Throwable exception) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setLogLevel(int logLevel) {
        // TODO Auto-generated method stub

    }

    @Override
    public int getLogLevel() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ApplicationType getType() {
        return ApplicationType.WebGL;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    @Override
    public long getJavaHeap() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public long getNativeHeap() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public Preferences getPreferences(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Clipboard getClipboard() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void postRunnable(Runnable runnable) {
        // TODO Auto-generated method stub

    }

    @Override
    public void exit() {
        // TODO Auto-generated method stub

    }

    @Override
    public void addLifecycleListener(LifecycleListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void removeLifecycleListener(LifecycleListener listener) {
        // TODO Auto-generated method stub
    }
}
