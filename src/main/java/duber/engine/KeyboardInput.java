package duber.engine;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.*;

/**
 * KeyboardInput
 */
public class KeyboardInput {
    private long windowHandle;
    private boolean[] keyPressed;

    public KeyboardInput(long windowHandle) {
        this.windowHandle = windowHandle;
        keyPressed = new boolean[501];
        init();
    }

    private void init() {
        glfwSetKeyCallback(windowHandle, (window, keyCode, scanCode, action, mods) -> {
            if(action == GLFW_PRESS) {
                keyPressed[keyCode] = true;
            }            

            if(action == GLFW_RELEASE) {
                keyPressed[keyCode] = false;
            }
        });
    }

    public boolean isKeyPressed(int keyCode) {
        return keyPressed[keyCode];
    }

    @SuppressWarnings("unused")
    private KeyboardInput() {}
}