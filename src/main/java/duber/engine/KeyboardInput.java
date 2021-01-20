package duber.engine;

import java.util.Arrays;

/**
 * KeyboardInput
 */
public class KeyboardInput {
    private boolean[] keyPressed = new boolean[501];

    public void setKeyPressed(int keyCode, boolean pressed) {
        if(keyCode >= 0) {
            keyPressed[keyCode] = pressed;
        }
    }

    public boolean isKeyPressed(int keyCode) {
        if(keyCode >= 0) {
            return keyPressed[keyCode];
        }
        return false;
    }

    public void clear() {
        Arrays.fill(keyPressed, false);
    }
}