package duber.game.server;

import org.joml.Vector2f;
import org.joml.Vector3f;

import duber.engine.KeyboardInput;
import duber.engine.MouseInput;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.Transform;
import duber.game.gameobjects.Player;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;




public class Controls {
    private float mouseSensitivity = 0.0006f;
    private MatchManager matchManager;

    public Controls(MatchManager matchManager) {
        this.matchManager = matchManager;
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

    public void update(Player player, MouseInput mouseInput, KeyboardInput keyboardInput) {
        Vector2f controlRotation = mouseInput.getCursorDisplacement();
        Vector3f controlVelocity = new Vector3f();

        float moveSpeed = keyboardInput.isKeyPressed(GLFW_KEY_LEFT_SHIFT) ? 
            player.getPlayerData().getWalkingSpeed() : 
            player.getPlayerData().getRunningSpeed();

        if(keyboardInput.isKeyPressed(GLFW_KEY_W)) {
            controlVelocity.add(0, 0, -moveSpeed);
        } else if(keyboardInput.isKeyPressed(GLFW_KEY_S)) {
            controlVelocity.add(0, 0, moveSpeed);
        }

        if(keyboardInput.isKeyPressed(GLFW_KEY_A)) {
            controlVelocity.add(-moveSpeed, 0, 0);
        } else if(keyboardInput.isKeyPressed(GLFW_KEY_D)) {
            controlVelocity.add(moveSpeed, 0, 0);
        }

        if(mouseInput.leftButtonIsPressed() && player.canShoot()) {
            matchManager.getGameWorld().simulateShot(player);
        }

        Vector3f playerVelocity = player.getComponent(RigidBody.class).getVelocity();
        
        if (!player.getPlayerData().isJumping() && keyboardInput.isKeyPressed(GLFW_KEY_SPACE)) {
            player.getPlayerData().setJumping(true);
            controlVelocity.add(0, player.getPlayerData().getJumpingSpeed(), 0);
        }
        
        addControlVelocity(playerVelocity, player.getComponent(Transform.class).getRotation(), controlVelocity);
        
        player.getComponent(RigidBody.class).getAngularVelocity().add(
            controlRotation.y * mouseSensitivity, controlRotation.x * mouseSensitivity, 0.0f);
    }
}