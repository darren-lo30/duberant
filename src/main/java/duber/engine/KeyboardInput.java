package duber.engine;

import static org.lwjgl.glfw.GLFW.glfwGetKey;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;


/**
 * KeyboardInput
 */
public class KeyboardInput {
    private final long windowHandle;

    public KeyboardInput(long windowHandle) {
        this.windowHandle = windowHandle;
    }

    public boolean isKeyPressed(int keyCode) {
        return glfwGetKey(windowHandle, keyCode) == GLFW_PRESS;
    }
}