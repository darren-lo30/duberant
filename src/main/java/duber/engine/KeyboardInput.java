package duber.engine;

/**
 * KeyboardInput
 */
public class KeyboardInput {
    private boolean[] keyPressed = new boolean[501];

    public void setKeyPressed(int keyCode, boolean pressed) {
        keyPressed[keyCode] = pressed;
    }

    public boolean isKeyPressed(int keyCode) {
        return keyPressed[keyCode];
    }
}