package duber.engine;

import java.util.Arrays;

/**
 * Information about the keyboard
 * @author Darren Lo
 * @version 1.0
 */
public class KeyboardInput {
    /** Stores if a key with a given keycode is pressed */
    private boolean[] keyPressed = new boolean[501];

    /** 
     * Sets a key as pressed/not pressed.
     * @param keyCode the keyCode of the key
     * @param pressed if the key is pressed
     */
    public void setKeyPressed(int keyCode, boolean pressed) {
        if (keyCode >= 0) {
            keyPressed[keyCode] = pressed;
        }
    }

    /**
     * Determines if a key is pressed.
     * @param keyCode the key code of the key to check
     * @return if the key is pressed.
     */
    public boolean isKeyPressed(int keyCode) {
        if (keyCode >= 0) {
            return keyPressed[keyCode];
        }
        return false;
    }

    /**
     * Clears all data about keyboard input.
     */
    public void clear() {
        Arrays.fill(keyPressed, false);
    }
}