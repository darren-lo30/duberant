package duber.game.server;

import org.joml.Vector2f;
import org.joml.Vector3f;

import duber.engine.KeyboardInput;
import duber.engine.MouseInput;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.Transform;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Player.MovementState;
import duber.game.gameobjects.Player.PlayerData;
import duber.game.networking.GunFirePacket;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;




public class Controls {
    private static float mouseSensitivity = 0.0006f;

    private static final int FORWARD = GLFW_KEY_W;
    private static final int BACKWARD = GLFW_KEY_S;
    private static final int STRAFE_LEFT = GLFW_KEY_A;
    private static final int STRAFE_RIGHT = GLFW_KEY_D;
    private static final int WALK = GLFW_KEY_LEFT_SHIFT;
    private static final int JUMP = GLFW_KEY_SPACE;

    private Controls() {}
    
    private static void addControlVelocity(Vector3f playerVelocity, Vector3f controlRotation, Vector3f controlVelocity) {
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

    public static Vector3f calculateControlVelocity(PlayerData playerData, KeyboardInput keyboardInput) {
        Vector3f controlVelocity = new Vector3f();

        boolean walking = keyboardInput.isKeyPressed(WALK);

        float moveSpeed = walking ? playerData.getWalkingSpeed() : playerData.getRunningSpeed();

        if(keyboardInput.isKeyPressed(FORWARD)) {
            controlVelocity.add(0, 0, -moveSpeed);
        } else if(keyboardInput.isKeyPressed(BACKWARD)) {
            controlVelocity.add(0, 0, moveSpeed);
        }

        if(keyboardInput.isKeyPressed(STRAFE_LEFT)) {
            controlVelocity.add(-moveSpeed, 0, 0);
        } else if(keyboardInput.isKeyPressed(STRAFE_RIGHT)) {
            controlVelocity.add(moveSpeed, 0, 0);
        }

        //Jumping
        if (!playerData.isJumping() && keyboardInput.isKeyPressed(JUMP)) {
            controlVelocity.add(0, playerData.getJumpingSpeed(), 0);
        }

        return controlVelocity;
    }


    public static void updatePlayer(MatchManager match, Player player, MouseInput mouseInput, KeyboardInput keyboardInput) {
        PlayerData playerData = player.getPlayerData();

        //Update player position
        Vector3f controlVelocity = calculateControlVelocity(playerData, keyboardInput);
        Vector3f playerVelocity = player.getComponent(RigidBody.class).getVelocity();
        addControlVelocity(playerVelocity, player.getComponent(Transform.class).getRotation(), controlVelocity);
        
        //Update player rotation
        Vector2f controlRotation = mouseInput.getCursorDisplacement();
        player.getComponent(RigidBody.class).getAngularVelocity().add(
            controlRotation.y * mouseSensitivity, controlRotation.x * mouseSensitivity, 0.0f);

        
        //Make player shoot
        if(mouseInput.leftButtonIsPressed() && player.canShoot()) {
            match.getGameWorld().simulateShot(player);
            GunFirePacket gunFirePacket = new GunFirePacket(player.getId());

            match.sendAllTCP(gunFirePacket);
        }

        if(keyboardInput.isKeyPressed(JUMP)) {
            playerData.setState(MovementState.JUMPING);
        }
        
        //Set player movement state
        if(!playerData.isJumping()) {
            if(controlVelocity.equals(0, 0, 0)) {
                playerData.setState(MovementState.STOP);
            } else if(keyboardInput.isKeyPressed(WALK)) {
                playerData.setState(MovementState.WALKING);
            } else {
                playerData.setState(MovementState.RUNNING);
            }
        }

        
    }
}