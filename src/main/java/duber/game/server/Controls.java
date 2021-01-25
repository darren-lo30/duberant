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
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;

/**
 * Stores all the data used to control the Player in a match
 * @author Darren Lo
 * @version 1.0
 */
public class Controls {
    /**
     * The sensitivity of the mouse.
     */
    private static float mouseSensitivity = 0.0006f;

    /**
     * The key used to move forward.
     */
    private static final int FORWARD = GLFW_KEY_W;

    /**
     * The key used to move backwards.
     */
    private static final int BACKWARD = GLFW_KEY_S;

    /**
     * The key used to move left.
     */
    private static final int STRAFE_LEFT = GLFW_KEY_A;

    /**
     * The key used to move right.
     */
    private static final int STRAFE_RIGHT = GLFW_KEY_D;

    /**
     * The key used to walk.
     */
    private static final int WALK = GLFW_KEY_LEFT_SHIFT;

    /**
     * The key used to jump.
     */
    private static final int JUMP = GLFW_KEY_SPACE;

    /**
     * The key used to switch to the primary weapon.
     */
    private static final int PRIMARY_WEAPON = GLFW_KEY_1;

    /**
     * The key used to switch to the secondary weapon.
     */
    private static final int SECONDARY_WEAPON = GLFW_KEY_2;
    
    private Controls() {}

    /**
     * Adds velocity resulting from player inputs to the Player.
     * @param playerVelocity the Players current velocity
     * @param controlRotation the Players current rotation
     * @param controlVelocity the velocity resulting from inputs
     */
    private static void addControlVelocity(Vector3f playerVelocity, Vector3f controlRotation, Vector3f controlVelocity) {
        if (controlVelocity.z() != 0) {
            playerVelocity.x += (float) Math.sin(controlRotation.y()) * -1.0f * controlVelocity.z();
            playerVelocity.z += (float) Math.cos(controlRotation.y()) * controlVelocity.z();
        }
        
        if (controlVelocity.x() != 0) {
            playerVelocity.x += (float) Math.sin(controlRotation.y() - Math.toRadians(90)) * -1.0f * controlVelocity.x();
            playerVelocity.z += (float) Math.cos(controlRotation.y() - Math.toRadians(90)) * controlVelocity.x();
        }
        playerVelocity.y += controlVelocity.y();
    }   

    /**
     * Calculates the velocity resulting from inputs.
     * @param playerData the Players data
     * @param keyboardInput the keyboard input from the client
     * @return the velocity resulting from the inputs
     */
    public static Vector3f calculateControlVelocity(PlayerData playerData, KeyboardInput keyboardInput) {
        Vector3f controlVelocity = new Vector3f();

        boolean walking = keyboardInput.isKeyPressed(WALK);

        float moveSpeed = walking ? playerData.getWalkingSpeed() : playerData.getRunningSpeed();

        if (keyboardInput.isKeyPressed(FORWARD)) {
            controlVelocity.add(0, 0, -moveSpeed);
        } else if (keyboardInput.isKeyPressed(BACKWARD)) {
            controlVelocity.add(0, 0, moveSpeed);
        }

        if (keyboardInput.isKeyPressed(STRAFE_LEFT)) {
            controlVelocity.add(-moveSpeed, 0, 0);
        } else if (keyboardInput.isKeyPressed(STRAFE_RIGHT)) {
            controlVelocity.add(moveSpeed, 0, 0);
        }

        //Jumping
        if (!playerData.isJumping() && keyboardInput.isKeyPressed(JUMP)) {
            controlVelocity.add(0, playerData.getJumpingSpeed(), 0);
        }

        return controlVelocity;
    }

    /**
     * Updates the players rotation.
     * @param player the Player to update
     * @param mouseInput the mouse input from the client
     */
    private static void updatePlayerRotation(Player player, MouseInput mouseInput) {
        //Update player rotation
        Vector2f controlRotation = mouseInput.getCursorDisplacement();
        player.getComponent(RigidBody.class).getAngularVelocity().add(
            0.0f, controlRotation.x * mouseSensitivity, 0.0f);

        Vector3f playerRotation = player.getComponent(Transform.class).getRotation();
        Transform cameraTransform = player.getView().getComponent(Transform.class);
        cameraTransform.getRotation().y = playerRotation.y;
        cameraTransform.getRotation().z = playerRotation.z;
        cameraTransform.rotate(controlRotation.y * mouseSensitivity, 0, 0);
    }


    /**
     * Updates a player in the match.
     * @param match the match
     * @param player the Player to update
     * @param mouseInput the mouse input data from the client
     * @param keyboardInput the keyboard data input from the client
     */
    public static void updatePlayer(MatchManager match, Player player, MouseInput mouseInput, KeyboardInput keyboardInput) {
        PlayerData playerData = player.getPlayerData();

        //Update player position
        Vector3f controlVelocity = calculateControlVelocity(playerData, keyboardInput);
        Vector3f playerVelocity = player.getComponent(RigidBody.class).getVelocity();
        addControlVelocity(playerVelocity, player.getComponent(Transform.class).getRotation(), controlVelocity);
        
        updatePlayerRotation(player, mouseInput);
        
        //Make player shoot
        if (mouseInput.leftButtonIsPressed() && player.canShoot()) {
            match.getGameWorld().simulateShot(player);
            GunFirePacket gunFirePacket = new GunFirePacket(player.getId());

            match.sendAllTCP(gunFirePacket);
        }

        if (keyboardInput.isKeyPressed(JUMP)) {
            playerData.setMovementState(MovementState.JUMPING);
        }
        
        //Set player movement state
        if (!playerData.isJumping()) {
            if (controlVelocity.equals(0, 0, 0)) {
                playerData.setMovementState(MovementState.STOP);
            } else if (keyboardInput.isKeyPressed(WALK)) {
                playerData.setMovementState(MovementState.WALKING);
            } else {
                playerData.setMovementState(MovementState.RUNNING);
            }
        }   

        //Switch weapons
        if (keyboardInput.isKeyPressed(PRIMARY_WEAPON)) {
            player.getWeaponsInventory().equipPrimaryGun();
        } else if (keyboardInput.isKeyPressed(SECONDARY_WEAPON)) {
            player.getWeaponsInventory().equipSecondaryGun();
        }

    }
}