package duber.game.client;

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

        mouseSensitivity = 0.0003f;
    }

    private void addControlVelocity(Vector3f playerVelocity, Vector3f controlRotation, Vector3f controlVelocity) {
        if(controlVelocity.z() != 0) {
            playerVelocity.x += (float)Math.sin(controlRotation.y()) * -1.0f * controlVelocity.z();
            playerVelocity.z += (float)Math.cos(controlRotation.y()) * controlVelocity.z();
        }
        
        if(controlVelocity.x() != 0) {
            playerVelocity.x += (float)Math.sin(controlRotation.y() - Math.toRadians(90)) * -1.0f * controlVelocity.x();
            playerVelocity.z += (float)Math.cos(controlRotation.y() - Math.toRadians(90)) * controlVelocity.x();
        }
        playerVelocity.y += controlVelocity.y();
    }

    public void update(MouseInput mouseInput) {
        Vector2f controlRotation = mouseInput.getDisplacementVec();
        Vector3f controlVelocity = new Vector3f();

        if(window.isKeyPressed(GLFW_KEY_W)) {
            controlVelocity.add(0, 0, -player.getSpeed());
        } else if(window.isKeyPressed(GLFW_KEY_S)) {
            controlVelocity.add(0, 0, player.getSpeed());
        }

        if(window.isKeyPressed(GLFW_KEY_A)) {
            controlVelocity.add(-player.getSpeed(), 0, 0);
        } else if(window.isKeyPressed(GLFW_KEY_D)) {
            controlVelocity.add(player.getSpeed(), 0, 0);
        }

        if(window.isKeyPressed(GLFW_KEY_Z)) {
            controlVelocity.add(0, -player.getSpeed(), 0);
        } else if(window.isKeyPressed(GLFW_KEY_X)) {
            controlVelocity.add(0, player.getSpeed(), 0);
        }
        Vector3f playerVelocity = player.getPlayerBody().getVelocity();
        addControlVelocity(playerVelocity, player.getModel().getTransform().getRotation(), controlVelocity);
        player.getPlayerBody().getAngularVelocity().add(
            controlRotation.y * mouseSensitivity, controlRotation.x * mouseSensitivity, 0.0f);
    }
}