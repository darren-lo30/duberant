package duber.game;

import org.joml.Vector2f;
import org.joml.Vector3f;

import duber.engine.MouseInput;
import duber.engine.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;


public class Controls {
    private float mouseSensitivity;
    
    private final Window window;
    private Player player;

    public Controls(Window window, Player player) {
        this.window = window;
        this.player = player;

        mouseSensitivity = 0.02f;
    }

    public void input(MouseInput mouseInput) {
        Vector3f playerVelocity = player.getPlayerBody().getVelocity();
        Vector3f playerAngularVelocity = player.getPlayerBody().getAngularVelocity();

        playerVelocity.set(0, 0, 0);
        
        if(window.isKeyPressed(GLFW_KEY_W)) {
            playerVelocity.add(0, 0, -player.getSpeed());
        } else if(window.isKeyPressed(GLFW_KEY_S)) {
            playerVelocity.add(0, 0, player.getSpeed());
        }

        if(window.isKeyPressed(GLFW_KEY_A)) {
            playerVelocity.add(-player.getSpeed(), 0, 0);
        } else if(window.isKeyPressed(GLFW_KEY_D)) {
            playerVelocity.add(player.getSpeed(), 0, 0);
        }

        if(window.isKeyPressed(GLFW_KEY_Z)) {
            playerVelocity.add(0, -player.getSpeed(), 0);
        } else if(window.isKeyPressed(GLFW_KEY_X)) {
            playerVelocity.add(0, player.getSpeed(), 0);
        }

        Vector2f playerRotation = mouseInput.getDisplacementVec();
        playerAngularVelocity.add(playerRotation.y * mouseSensitivity, playerRotation.x * mouseSensitivity, 0.0f);
    }
}