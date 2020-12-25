package duber.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import duber.engine.MouseInput;
import duber.engine.Window;
import duber.engine.graphics.Camera;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;


public class Controls {
    private float moveSpeed;
    private float mouseSensitivity;

    private final Window window;
    private final Vector3f cameraDisplacement;

    public Controls(Window window) {
        this.window = window;
        cameraDisplacement = new Vector3f();

        moveSpeed = 3.0f;
        mouseSensitivity = 0.020f;
    }

    public void input() {
        if(window.isKeyPressed(GLFW_KEY_W)){
            cameraDisplacement.z = -1;
        } else if(window.isKeyPressed(GLFW_KEY_S)){
            cameraDisplacement.z = 1;
        }

        if(window.isKeyPressed(GLFW_KEY_A)){
            cameraDisplacement.x = -1;
        } else if(window.isKeyPressed(GLFW_KEY_D)){
            cameraDisplacement.x = 1;
        }

        if(window.isKeyPressed(GLFW_KEY_Z)){
            cameraDisplacement.y = -1;
        } else if(window.isKeyPressed(GLFW_KEY_X)){
            cameraDisplacement.y = 1;
        }
    }


    public void updateCameraView(Camera camera, MouseInput mouseInput) {
        //Updates the position of the camera
        camera.movePosition(
            cameraDisplacement.x * moveSpeed, 
            cameraDisplacement.y * moveSpeed, 
            cameraDisplacement.z * moveSpeed);
        
        //Reset displacement
        cameraDisplacement.zero();

        //Rotate the way that is being viewed
        Vector2f cameraRotation = mouseInput.getDisplacementVec();
        camera.moveRotation(cameraRotation.y * mouseSensitivity, cameraRotation.x * mouseSensitivity, 0);        
    }

}