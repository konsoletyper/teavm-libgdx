package org.teavm.libgdx;

import org.teavm.dom.events.*;
import org.teavm.dom.html.HTMLCanvasElement;
import org.teavm.jso.dom.html.HTMLDocument;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.IntMap;
import com.badlogic.gdx.utils.IntSet;

/**
 *
 * @author Alexey Andreev
 */
public class TeaVMInput implements Input, EventListener {
    static final int MAX_TOUCHES = 20;
    boolean justTouched = false;
    private IntMap<Integer> touchMap = new IntMap<>(20);
    private boolean[] touched = new boolean[MAX_TOUCHES];
    private int[] touchX = new int[MAX_TOUCHES];
    private int[] touchY = new int[MAX_TOUCHES];
    private int[] deltaX = new int[MAX_TOUCHES];
    private int[] deltaY = new int[MAX_TOUCHES];
    IntSet pressedButtons = new IntSet();
    int pressedKeyCount = 0;
    boolean[] pressedKeys = new boolean[256];
    boolean keyJustPressed = false;
    boolean[] justPressedKeys = new boolean[256];
    InputProcessor processor;
    char lastKeyCharPressed;
    float keyRepeatTimer;
    long currentEventTimeStamp;
    final HTMLCanvasElement canvas;
    boolean hasFocus = true;

    public TeaVMInput(HTMLCanvasElement canvas) {
        this.canvas = canvas;
        hookEvents();
    }

    void reset() {
        justTouched = false;
        if (keyJustPressed) {
            keyJustPressed = false;
            for (int i = 0; i < justPressedKeys.length; i++) {
                justPressedKeys[i] = false;
            }
        }
    }

    @Override
    public float getAccelerometerX() {
        return 0;
    }

    @Override
    public float getAccelerometerY() {
        return 0;
    }

    @Override
    public float getAccelerometerZ() {
        return 0;
    }

    @Override
    public int getX() {
        return touchX[0];
    }

    @Override
    public int getX(int pointer) {
        return touchX[pointer];
    }

    @Override
    public int getDeltaX() {
        return deltaX[0];
    }

    @Override
    public int getDeltaX(int pointer) {
        return deltaX[pointer];
    }

    @Override
    public int getY() {
        return touchY[0];
    }

    @Override
    public int getY(int pointer) {
        return touchY[pointer];
    }

    @Override
    public int getDeltaY() {
        return deltaY[0];
    }

    @Override
    public int getDeltaY(int pointer) {
        return deltaY[pointer];
    }

    @Override
    public boolean isTouched() {
        for (int pointer = 0; pointer < MAX_TOUCHES; pointer++) {
            if (touched[pointer]) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean justTouched() {
        return justTouched;
    }

    @Override
    public boolean isTouched(int pointer) {
        return touched[pointer];
    }

    @Override
    public boolean isButtonPressed(int button) {
        return button == Buttons.LEFT && touched[0];
    }

    @Override
    public boolean isKeyPressed(int key) {
        if (key == Keys.ANY_KEY) {
            return pressedKeyCount > 0;
        }
        if (key < 0 || key > 255) {
            return false;
        }
        return pressedKeys[key];
    }

    @Override
    public boolean isKeyJustPressed(int key) {
        if (key == Keys.ANY_KEY) {
            return keyJustPressed;
        }
        if (key < 0 || key > 255) {
            return false;
        }
        return justPressedKeys[key];
    }

    /*public void getTextInput(TextInputListener listener, String title, String text, String hint) {
        TextInputDialogBox dialog = new TextInputDialogBox(title, text, hint);
        final TextInputListener capturedListener = listener;
        dialog.setListener(new TextInputDialogListener() {
            @Override
            public void onPositive(String text) {
                if (capturedListener != null) {
                    capturedListener.input(text);
                }
            }

            @Override
            public void onNegative() {
                if (capturedListener != null) {
                    capturedListener.canceled();
                }
            }
        });
    }*/

    @Override
    public void setOnscreenKeyboardVisible(boolean visible) {
    }

    @Override
    public void vibrate(int milliseconds) {
    }

    @Override
    public void vibrate(long[] pattern, int repeat) {
    }

    @Override
    public void cancelVibrate() {
    }

    @Override
    public float getAzimuth() {
        return 0;
    }

    @Override
    public float getPitch() {
        return 0;
    }

    @Override
    public float getRoll() {
        return 0;
    }

    @Override
    public void getRotationMatrix(float[] matrix) {
    }

    @Override
    public long getCurrentEventTime() {
        return currentEventTimeStamp;
    }

    @Override
    public void setCatchBackKey(boolean catchBack) {
    }

    @Override
    public boolean isCatchBackKey() {
        return false;
    }

    @Override
    public void setCatchMenuKey(boolean catchMenu) {
    }

    @Override
    public void setInputProcessor(InputProcessor processor) {
        this.processor = processor;
    }

    @Override
    public InputProcessor getInputProcessor() {
        return processor;
    }

    @Override
    public boolean isPeripheralAvailable(Peripheral peripheral) {
        switch (peripheral) {
            case Accelerometer:
            case Compass:
            case OnscreenKeyboard:
            case Vibrator:
                return false;
            case HardwareKeyboard:
                return true;
            case MultitouchScreen:
                return isTouchScreen();
        }
        return false;
    }

    @Override
    public int getRotation() {
        return 0;
    }

    @Override
    public Orientation getNativeOrientation() {
        return Orientation.Landscape;
    }

    private  boolean isCursorCatchedJSNI() {
        // TODO: implement
        return false;
    }

    private void setCursorCatchedJSNI(HTMLCanvasElement element) {
        // TODO: implement
    }

    private void exitCursorCatchedJSNI() {
        // TODO: implement
    }

    private float getMovementXJSNI(Event event) {
        // TODO: implement
        return 0;
    }

    private float getMovementYJSNI(Event event) {
        // TODO: implement
        return 0;
    }

    private static boolean isTouchScreen() {
        // TODO: implement
        return false;
    }

    /**
     * works only for Chrome > Version 18 with enabled Mouse Lock enable in
     * about:flags or start Chrome with the --enable-pointer-lock flag
     */
    @Override
    public void setCursorCatched(boolean catched) {
        if (catched) {
            setCursorCatchedJSNI(canvas);
        } else {
            exitCursorCatchedJSNI();
        }
    }

    @Override
    public boolean isCursorCatched() {
        return isCursorCatchedJSNI();
    }

    @Override
    public void setCursorPosition(int x, int y) {
        // FIXME??
    }

    private static float getMouseWheelVelocity(Event evt) {
        // TODO: implement
        return 0;
    }

    protected static String getMouseWheelEvent() {
        // TODO: implement
        return null;
    }

    /** Kindly borrowed from PlayN. **/
    protected int getRelativeX(MouseEvent e, HTMLCanvasElement target) {
        float xScaleRatio = target.getWidth() * 1f / target.getClientWidth();
        return Math.round(xScaleRatio *
                (e.getClientX() - target.getAbsoluteLeft() + target.getScrollLeft() + target.getOwnerDocument()
                        .getScrollLeft()));
    }

    /** Kindly borrowed from PlayN. **/
    protected int getRelativeY(MouseEvent e, HTMLCanvasElement target) {
        float yScaleRatio = target.getHeight() * 1f / target.getClientHeight();
        return Math.round(yScaleRatio *
                (e.getClientY() - target.getAbsoluteTop() + target.getScrollTop() + target.getOwnerDocument()
                        .getScrollTop()));
    }
/*
    protected int getRelativeX(Touch touch, HTMLCanvasElement target) {
        float xScaleRatio = target.getWidth() * 1f / target.getClientWidth(); // Correct
                                                                              // for
                                                                              // canvas
                                                                              // CSS
                                                                              // scaling
        return Math.round(xScaleRatio * touch.getRelativeX(target));
    }

    protected int getRelativeY(Touch touch, CanvasElement target) {
        float yScaleRatio = target.getHeight() * 1f / target.getClientHeight(); // Correct
                                                                                // for
                                                                                // canvas
                                                                                // CSS
                                                                                // scaling
        return Math.round(yScaleRatio * touch.getRelativeY(target));
    }
*/
    private void hookEvents() {
        HTMLDocument document = canvas.getOwnerDocument();
        canvas.addEventListener("mousedown", this, true);
        document.addEventListener("mousedown", this, true);
        canvas.addEventListener("mouseup", this, true);
        document.addEventListener("mouseup", this, true);
        canvas.addEventListener("mousemove", this, true);
        document.addEventListener("mousemove", this, true);
        canvas.addEventListener("mousewheel", this, true);
        document.addEventListener("keydown", this, false);
        document.addEventListener("keyup", this, false);
        document.addEventListener("keypress", this, false);

        canvas.addEventListener("touchstart", this);
        canvas.addEventListener("touchmove", this);
        canvas.addEventListener("touchcancel", this);
        canvas.addEventListener("touchend", this);

    }

    private int getButton(int button) {
        if (button == MouseEvent.LEFT_BUTTON) {
            return Buttons.LEFT;
        }
        if (button == MouseEvent.RIGHT_BUTTON) {
            return Buttons.RIGHT;
        }
        if (button == MouseEvent.MIDDLE_BUTTON) {
            return Buttons.MIDDLE;
        }
        return Buttons.LEFT;
    }

    @Override
    public void handleEvent(Event e) {
        if (e.getType().equals("mousedown")) {
            MouseEvent mouseEvent = (MouseEvent)e;
            if (e.getTarget() != canvas || touched[0]) {
                float mouseX = getRelativeX(mouseEvent, canvas);
                float mouseY = getRelativeY(mouseEvent, canvas);
                if (mouseX < 0 || mouseX > Gdx.graphics.getWidth() || mouseY < 0 ||mouseY > Gdx.graphics.getHeight()) {

                    hasFocus = false;
                }
                return;
            }
            hasFocus = true;
            this.justTouched = true;
            this.touched[0] = true;
            this.pressedButtons.add(getButton(mouseEvent.getButton()));
            this.deltaX[0] = 0;
            this.deltaY[0] = 0;
            if (isCursorCatched()) {
                this.touchX[0] += getMovementXJSNI(e);
                this.touchY[0] += getMovementYJSNI(e);
            } else {
                this.touchX[0] = getRelativeX(mouseEvent, canvas);
                this.touchY[0] = getRelativeY(mouseEvent, canvas);
            }
            if (processor != null) {
                processor.touchDown(touchX[0], touchY[0], 0, getButton(mouseEvent.getButton()));
            }
        }

        if (e.getType().equals("mousemove")) {
            MouseEvent mouseEvent = (MouseEvent)e;
            if (isCursorCatched()) {
                this.deltaX[0] = (int)getMovementXJSNI(e);
                this.deltaY[0] = (int)getMovementYJSNI(e);
                this.touchX[0] += getMovementXJSNI(e);
                this.touchY[0] += getMovementYJSNI(e);
            } else {
                this.deltaX[0] = getRelativeX(mouseEvent, canvas) - touchX[0];
                this.deltaY[0] = getRelativeY(mouseEvent, canvas) - touchY[0];
                this.touchX[0] = getRelativeX(mouseEvent, canvas);
                this.touchY[0] = getRelativeY(mouseEvent, canvas);
            }
            if (processor != null) {
                if (touched[0]) {
                    processor.touchDragged(touchX[0], touchY[0], 0);
                } else {
                    processor.mouseMoved(touchX[0], touchY[0]);
                }
            }
        }

        if (e.getType().equals("mouseup")) {
            if (!touched[0]) {
                return;
            }
            MouseEvent mouseEvent = (MouseEvent)e;
            this.pressedButtons.remove(getButton(mouseEvent.getButton()));
            this.touched[0] = pressedButtons.size > 0;
            if (isCursorCatched()) {
                this.deltaX[0] = (int)getMovementXJSNI(e);
                this.deltaY[0] = (int)getMovementYJSNI(e);
                this.touchX[0] += getMovementXJSNI(e);
                this.touchY[0] += getMovementYJSNI(e);
            } else {
                this.deltaX[0] = getRelativeX(mouseEvent, canvas) - touchX[0];
                this.deltaY[0] = getRelativeY(mouseEvent, canvas) - touchY[0];
                this.touchX[0] = getRelativeX(mouseEvent, canvas);
                this.touchY[0] = getRelativeY(mouseEvent, canvas);
            }
            this.touched[0] = false;
            if (processor != null)
                processor.touchUp(touchX[0], touchY[0], 0, getButton(mouseEvent.getButton()));
        }
        if (e.getType().equals(getMouseWheelEvent())) {
            if (processor != null) {
                processor.scrolled((int)getMouseWheelVelocity(e));
            }
            e.preventDefault();
        }
        if (e.getType().equals("keydown") && hasFocus) {
            KeyboardEvent keyEvent = (KeyboardEvent)e;
            int code = keyForCode(keyEvent.getKeyCode());
            if (code == 67) {
                e.preventDefault();
                if (processor != null) {
                    processor.keyDown(code);
                    processor.keyTyped('\b');
                }
            } else {
                if (!pressedKeys[code]) {
                    pressedKeyCount++;
                    pressedKeys[code] = true;
                    keyJustPressed = true;
                    justPressedKeys[code] = true;
                    if (processor != null) {
                        processor.keyDown(code);
                    }
                }
            }
        }

        if (e.getType().equals("keypress") && hasFocus) {
            KeyboardEvent keyEvent = (KeyboardEvent)e;
            char c = (char)keyEvent.getCharCode();
            if (processor != null) {
                processor.keyTyped(c);
            }
        }

        if (e.getType().equals("keyup") && hasFocus) {
            // System.out.println("keyup");
            KeyboardEvent keyEvent = (KeyboardEvent)e;
            int code = keyForCode(keyEvent.getKeyCode());
            if (pressedKeys[code]) {
                pressedKeyCount--;
                pressedKeys[code] = false;
            }
            if (processor != null) {
                processor.keyUp(code);
            }
        }

        /*if (e.getType().equals("touchstart")) {
            this.justTouched = true;
            JSArrayReader<Touch> touches = e.getChangedTouches();
            for (int i = 0, j = touches.length(); i < j; i++) {
                Touch touch = touches.get(i);
                int real = touch.getIdentifier();
                int touchId;
                touchMap.put(real, touchId = getAvailablePointer());
                touched[touchId] = true;
                touchX[touchId] = getRelativeX(touch, canvas);
                touchY[touchId] = getRelativeY(touch, canvas);
                deltaX[touchId] = 0;
                deltaY[touchId] = 0;
                if (processor != null) {
                    processor.touchDown(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
                }
            }
            this.currentEventTimeStamp = TimeUtils.nanoTime();
            e.preventDefault();
        }
        if (e.getType().equals("touchmove")) {
            JsArray<Touch> touches = e.getChangedTouches();
            for (int i = 0, j = touches.length(); i < j; i++) {
                Touch touch = touches.get(i);
                int real = touch.getIdentifier();
                int touchId = touchMap.get(real);
                deltaX[touchId] = getRelativeX(touch, canvas) - touchX[touchId];
                deltaY[touchId] = getRelativeY(touch, canvas) - touchY[touchId];
                touchX[touchId] = getRelativeX(touch, canvas);
                touchY[touchId] = getRelativeY(touch, canvas);
                if (processor != null) {
                    processor.touchDragged(touchX[touchId], touchY[touchId], touchId);
                }
            }
            this.currentEventTimeStamp = TimeUtils.nanoTime();
            e.preventDefault();
        }
        if (e.getType().equals("touchcancel")) {
            JsArray<Touch> touches = e.getChangedTouches();
            for (int i = 0, j = touches.length(); i < j; i++) {
                Touch touch = touches.get(i);
                int real = touch.getIdentifier();
                int touchId = touchMap.get(real);
                touchMap.remove(real);
                touched[touchId] = false;
                deltaX[touchId] = getRelativeX(touch, canvas) - touchX[touchId];
                deltaY[touchId] = getRelativeY(touch, canvas) - touchY[touchId];
                touchX[touchId] = getRelativeX(touch, canvas);
                touchY[touchId] = getRelativeY(touch, canvas);
                if (processor != null) {
                    processor.touchUp(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
                }
            }
            this.currentEventTimeStamp = TimeUtils.nanoTime();
            e.preventDefault();
        }
        if (e.getType().equals("touchend")) {
            JsArray<Touch> touches = e.getChangedTouches();
            for (int i = 0, j = touches.length(); i < j; i++) {
                Touch touch = touches.get(i);
                int real = touch.getIdentifier();
                int touchId = touchMap.get(real);
                touchMap.remove(real);
                touched[touchId] = false;
                deltaX[touchId] = getRelativeX(touch, canvas) - touchX[touchId];
                deltaY[touchId] = getRelativeY(touch, canvas) - touchY[touchId];
                touchX[touchId] = getRelativeX(touch, canvas);
                touchY[touchId] = getRelativeY(touch, canvas);
                if (processor != null) {
                    processor.touchUp(touchX[touchId], touchY[touchId], touchId, Buttons.LEFT);
                }
            }
            this.currentEventTimeStamp = TimeUtils.nanoTime();
            e.preventDefault();
        }*/
        // if(hasFocus) e.preventDefault();
    }

    private int getAvailablePointer() {
        for (int i = 0; i < MAX_TOUCHES; i++) {
            if (!touchMap.containsValue(i, false))
                return i;
        }
        return -1;
    }

    /** borrowed from PlayN, thanks guys **/
    private static int keyForCode(int keyCode) {
        switch (keyCode) {
            case KeyCodes.KEY_ALT:
                return Keys.ALT_LEFT;
            case KeyCodes.KEY_BACKSPACE:
                return Keys.BACKSPACE;
            case KeyCodes.KEY_CTRL:
                return Keys.CONTROL_LEFT;
            case KeyCodes.KEY_DELETE:
                return Keys.DEL;
            case KeyCodes.KEY_DOWN:
                return Keys.DOWN;
            case KeyCodes.KEY_END:
                return Keys.END;
            case KeyCodes.KEY_ENTER:
                return Keys.ENTER;
            case KeyCodes.KEY_ESCAPE:
                return Keys.ESCAPE;
            case KeyCodes.KEY_HOME:
                return Keys.HOME;
            case KeyCodes.KEY_LEFT:
                return Keys.LEFT;
            case KeyCodes.KEY_PAGEDOWN:
                return Keys.PAGE_DOWN;
            case KeyCodes.KEY_PAGEUP:
                return Keys.PAGE_UP;
            case KeyCodes.KEY_RIGHT:
                return Keys.RIGHT;
            case KeyCodes.KEY_SHIFT:
                return Keys.SHIFT_LEFT;
            case KeyCodes.KEY_TAB:
                return Keys.TAB;
            case KeyCodes.KEY_UP:
                return Keys.UP;

            case KEY_PAUSE:
                return Keys.UNKNOWN; // FIXME
            case KEY_CAPS_LOCK:
                return Keys.UNKNOWN; // FIXME
            case KEY_SPACE:
                return Keys.SPACE;
            case KEY_INSERT:
                return Keys.INSERT;
            case KEY_0:
                return Keys.NUM_0;
            case KEY_1:
                return Keys.NUM_1;
            case KEY_2:
                return Keys.NUM_2;
            case KEY_3:
                return Keys.NUM_3;
            case KEY_4:
                return Keys.NUM_4;
            case KEY_5:
                return Keys.NUM_5;
            case KEY_6:
                return Keys.NUM_6;
            case KEY_7:
                return Keys.NUM_7;
            case KEY_8:
                return Keys.NUM_8;
            case KEY_9:
                return Keys.NUM_9;
            case KEY_A:
                return Keys.A;
            case KEY_B:
                return Keys.B;
            case KEY_C:
                return Keys.C;
            case KEY_D:
                return Keys.D;
            case KEY_E:
                return Keys.E;
            case KEY_F:
                return Keys.F;
            case KEY_G:
                return Keys.G;
            case KEY_H:
                return Keys.H;
            case KEY_I:
                return Keys.I;
            case KEY_J:
                return Keys.J;
            case KEY_K:
                return Keys.K;
            case KEY_L:
                return Keys.L;
            case KEY_M:
                return Keys.M;
            case KEY_N:
                return Keys.N;
            case KEY_O:
                return Keys.O;
            case KEY_P:
                return Keys.P;
            case KEY_Q:
                return Keys.Q;
            case KEY_R:
                return Keys.R;
            case KEY_S:
                return Keys.S;
            case KEY_T:
                return Keys.T;
            case KEY_U:
                return Keys.U;
            case KEY_V:
                return Keys.V;
            case KEY_W:
                return Keys.W;
            case KEY_X:
                return Keys.X;
            case KEY_Y:
                return Keys.Y;
            case KEY_Z:
                return Keys.Z;
            case KEY_LEFT_WINDOW_KEY:
                return Keys.UNKNOWN; // FIXME
            case KEY_RIGHT_WINDOW_KEY:
                return Keys.UNKNOWN; // FIXME
                // case KEY_SELECT_KEY: return Keys.SELECT_KEY;
            case KEY_NUMPAD0:
                return Keys.NUMPAD_0;
            case KEY_NUMPAD1:
                return Keys.NUMPAD_1;
            case KEY_NUMPAD2:
                return Keys.NUMPAD_2;
            case KEY_NUMPAD3:
                return Keys.NUMPAD_3;
            case KEY_NUMPAD4:
                return Keys.NUMPAD_4;
            case KEY_NUMPAD5:
                return Keys.NUMPAD_5;
            case KEY_NUMPAD6:
                return Keys.NUMPAD_6;
            case KEY_NUMPAD7:
                return Keys.NUMPAD_7;
            case KEY_NUMPAD8:
                return Keys.NUMPAD_8;
            case KEY_NUMPAD9:
                return Keys.NUMPAD_9;
            case KEY_MULTIPLY:
                return Keys.UNKNOWN; // FIXME
            case KEY_ADD:
                return Keys.PLUS;
            case KEY_SUBTRACT:
                return Keys.MINUS;
            case KEY_DECIMAL_POINT_KEY:
                return Keys.PERIOD;
            case KEY_DIVIDE:
                return Keys.UNKNOWN; // FIXME
            case KEY_F1:
                return Keys.F1;
            case KEY_F2:
                return Keys.F2;
            case KEY_F3:
                return Keys.F3;
            case KEY_F4:
                return Keys.F4;
            case KEY_F5:
                return Keys.F5;
            case KEY_F6:
                return Keys.F6;
            case KEY_F7:
                return Keys.F7;
            case KEY_F8:
                return Keys.F8;
            case KEY_F9:
                return Keys.F9;
            case KEY_F10:
                return Keys.F10;
            case KEY_F11:
                return Keys.F11;
            case KEY_F12:
                return Keys.F12;
            case KEY_NUM_LOCK:
                return Keys.NUM;
            case KEY_SCROLL_LOCK:
                return Keys.UNKNOWN; // FIXME
            case KEY_SEMICOLON:
                return Keys.SEMICOLON;
            case KEY_EQUALS:
                return Keys.EQUALS;
            case KEY_COMMA:
                return Keys.COMMA;
            case KEY_DASH:
                return Keys.MINUS;
            case KEY_PERIOD:
                return Keys.PERIOD;
            case KEY_FORWARD_SLASH:
                return Keys.SLASH;
            case KEY_GRAVE_ACCENT:
                return Keys.UNKNOWN; // FIXME
            case KEY_OPEN_BRACKET:
                return Keys.LEFT_BRACKET;
            case KEY_BACKSLASH:
                return Keys.BACKSLASH;
            case KEY_CLOSE_BRACKET:
                return Keys.RIGHT_BRACKET;
            case KEY_SINGLE_QUOTE:
                return Keys.APOSTROPHE;
            default:
                return Keys.UNKNOWN;
        }
    }

    // these are absent from KeyCodes; we know not why...
    private static final int KEY_PAUSE = 19;
    private static final int KEY_CAPS_LOCK = 20;
    private static final int KEY_SPACE = 32;
    private static final int KEY_INSERT = 45;
    private static final int KEY_0 = 48;
    private static final int KEY_1 = 49;
    private static final int KEY_2 = 50;
    private static final int KEY_3 = 51;
    private static final int KEY_4 = 52;
    private static final int KEY_5 = 53;
    private static final int KEY_6 = 54;
    private static final int KEY_7 = 55;
    private static final int KEY_8 = 56;
    private static final int KEY_9 = 57;
    private static final int KEY_A = 65;
    private static final int KEY_B = 66;
    private static final int KEY_C = 67;
    private static final int KEY_D = 68;
    private static final int KEY_E = 69;
    private static final int KEY_F = 70;
    private static final int KEY_G = 71;
    private static final int KEY_H = 72;
    private static final int KEY_I = 73;
    private static final int KEY_J = 74;
    private static final int KEY_K = 75;
    private static final int KEY_L = 76;
    private static final int KEY_M = 77;
    private static final int KEY_N = 78;
    private static final int KEY_O = 79;
    private static final int KEY_P = 80;
    private static final int KEY_Q = 81;
    private static final int KEY_R = 82;
    private static final int KEY_S = 83;
    private static final int KEY_T = 84;
    private static final int KEY_U = 85;
    private static final int KEY_V = 86;
    private static final int KEY_W = 87;
    private static final int KEY_X = 88;
    private static final int KEY_Y = 89;
    private static final int KEY_Z = 90;
    private static final int KEY_LEFT_WINDOW_KEY = 91;
    private static final int KEY_RIGHT_WINDOW_KEY = 92;
    private static final int KEY_SELECT_KEY = 93;
    private static final int KEY_NUMPAD0 = 96;
    private static final int KEY_NUMPAD1 = 97;
    private static final int KEY_NUMPAD2 = 98;
    private static final int KEY_NUMPAD3 = 99;
    private static final int KEY_NUMPAD4 = 100;
    private static final int KEY_NUMPAD5 = 101;
    private static final int KEY_NUMPAD6 = 102;
    private static final int KEY_NUMPAD7 = 103;
    private static final int KEY_NUMPAD8 = 104;
    private static final int KEY_NUMPAD9 = 105;
    private static final int KEY_MULTIPLY = 106;
    private static final int KEY_ADD = 107;
    private static final int KEY_SUBTRACT = 109;
    private static final int KEY_DECIMAL_POINT_KEY = 110;
    private static final int KEY_DIVIDE = 111;
    private static final int KEY_F1 = 112;
    private static final int KEY_F2 = 113;
    private static final int KEY_F3 = 114;
    private static final int KEY_F4 = 115;
    private static final int KEY_F5 = 116;
    private static final int KEY_F6 = 117;
    private static final int KEY_F7 = 118;
    private static final int KEY_F8 = 119;
    private static final int KEY_F9 = 120;
    private static final int KEY_F10 = 121;
    private static final int KEY_F11 = 122;
    private static final int KEY_F12 = 123;
    private static final int KEY_NUM_LOCK = 144;
    private static final int KEY_SCROLL_LOCK = 145;
    private static final int KEY_SEMICOLON = 186;
    private static final int KEY_EQUALS = 187;
    private static final int KEY_COMMA = 188;
    private static final int KEY_DASH = 189;
    private static final int KEY_PERIOD = 190;
    private static final int KEY_FORWARD_SLASH = 191;
    private static final int KEY_GRAVE_ACCENT = 192;
    private static final int KEY_OPEN_BRACKET = 219;
    private static final int KEY_BACKSLASH = 220;
    private static final int KEY_CLOSE_BRACKET = 221;
    private static final int KEY_SINGLE_QUOTE = 222;

    @Override
    public void setCursorImage(Pixmap pixmap, int xHotspot, int yHotspot) {
    }

    @Override
    public void getTextInput(TextInputListener listener, String title, String text, String hint) {
    }
}
