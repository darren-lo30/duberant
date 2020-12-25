package duber.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;
import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {
    
    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displacementVec;

    private boolean leftButtonIsPressed = false;

    private boolean rightButtonIsPressed = false;

    public MouseInput(){
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displacementVec = new Vector2f();
    }

    public void init(Window window){
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xPos, yPos) -> {
            currentPos.x = xPos;
            currentPos.y = yPos;
        });

        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftButtonIsPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonIsPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;
        });
    }

    public Vector2f getDisplacementVec(){
        return displacementVec;
    }

    public void updateDisplacementVec(){
        displacementVec.x = (float) (currentPos.x - previousPos.x);
        displacementVec.y = (float) (currentPos.y - previousPos.y);

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
    }

    public void setMousePosition(Window window, float xPos, float yPos){
        glfwSetCursorPos(window.getWindowHandle(), xPos, yPos);
    }

    public boolean leftButtonIsPressed(){
        return leftButtonIsPressed;
    }

    public boolean rightButtonIsPressed(){
        return rightButtonIsPressed;
    }
}