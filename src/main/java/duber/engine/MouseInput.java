package duber.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final long windowHandle;

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f cursorDisplacement;

    private boolean leftButtonIsPressed = false;

    private boolean rightButtonIsPressed = false;

    public MouseInput(long windowHandle) {
        this.windowHandle = windowHandle;

        previousPos = new Vector2d(0, 0);
        currentPos = new Vector2d(0, 0);
        cursorDisplacement = new Vector2f();
        
        init();
    }
    
    private void init() {
        glfwSetCursorPos(windowHandle, 0, 0);
        glfwSetCursorPosCallback(windowHandle, (window, xPos, yPos) -> {
            currentPos.x = xPos;
            currentPos.y = yPos;
        });

        glfwSetMouseButtonCallback(windowHandle, (window, button, action, mode) -> {
            leftButtonIsPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonIsPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public Vector2f getCursorDisplacement() {
        return cursorDisplacement;
    }

    public void updateCursorDisplacement() {
        cursorDisplacement.x = (float) (currentPos.x - previousPos.x);
        cursorDisplacement.y = (float) (currentPos.y - previousPos.y);
        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public void setCursorPosition(float xPos, float yPos) {
        glfwSetCursorPos(windowHandle, xPos, yPos);
    }

    public boolean leftButtonIsPressed() {
        return leftButtonIsPressed;
    }

    public boolean rightButtonIsPressed() {
        return rightButtonIsPressed;
    }
}