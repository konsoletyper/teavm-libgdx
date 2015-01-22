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

    public TeaVMApplication(ApplicationListener listener, TeaVMApplicationConfig config) {
        this.listener = listener;
        this.config = config;
    }

    public void start() {
        canvas = config.getCanvas();
        Gdx.app = this;
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
        return null;
    }

    @Override
    public Audio getAudio() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Input getInput() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Files getFiles() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getVersion() {
        // TODO Auto-generated method stub
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
