package duber.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import duber.engine.MouseInput;
import duber.engine.Window;
import duber.game.entities.Player;

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
    private final Vector3f moveDisplacement;

    public Controls(Window window) {
        this.window = window;
        moveDisplacement = new Vector3f();

        moveSpeed = 3.0f;
        mouseSensitivity = 0.02f;
    }

    public void input() {
        if(window.isKeyPressed(GLFW_KEY_W)) {
            moveDisplacement.z = -1;
        } else if(window.isKeyPressed(GLFW_KEY_S)) {
            moveDisplacement.z = 1;
        }

        if(window.isKeyPressed(GLFW_KEY_A)) {
            moveDisplacement.x = -1;
        } else if(window.isKeyPressed(GLFW_KEY_D)) {
            moveDisplacement.x = 1;
        }

        if(window.isKeyPressed(GLFW_KEY_Z)) {
            moveDisplacement.y = -1;
        } else if(window.isKeyPressed(GLFW_KEY_X)) {
            moveDisplacement.y = 1;
        }
    }


    public void updatePlayer(Player player, MouseInput mouseInput) {
        //Updates the position of the camera
        player.move(
            moveDisplacement.x * moveSpeed, 
            moveDisplacement.y * moveSpeed, 
            moveDisplacement.z * moveSpeed);
            
        //Reset displacement
        moveDisplacement.zero();
            
        //Rotate the way that is being viewed
        Vector2f cameraRotation = mouseInput.getDisplacementVec();
        player.rotate(cameraRotation.y * mouseSensitivity, cameraRotation.x * mouseSensitivity, 0.0f);
    }

}