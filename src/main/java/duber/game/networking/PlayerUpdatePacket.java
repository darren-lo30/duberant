package duber.game.networking;

import duber.engine.entities.components.Vision;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Transform;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Score;
import duber.game.gameobjects.WeaponsInventory;
import duber.game.gameobjects.Player.PlayerData;

/**
 * A Packet used by Kryonet that updates a Player for the client.
 * @author Darren Lo
 * @version 1.0
 */
public class PlayerUpdatePacket extends Packet {
    /** The id of the Player to update. */
    public int playerId;
    /** The Player's transform. */
    public Transform playerTransform;
    /** The Player's camera transform. */
    public Transform cameraTransform;
    /** The Player's data. */
    public PlayerData playerData;
    /** The Player's score. */
    public Score playerScore;
    /** The Player's weapon inventory. */
    public WeaponsInventory playerInventory;
    /** Whether or not the Player is visible */
    public boolean visible;

    /**
     * Constructs a PlayerUpdatePacket.
     * @param player the Player to build the packet for
     */
    public PlayerUpdatePacket(Player player) {
        this.playerId = player.getId();
        playerTransform = player.getComponent(Transform.class);
        cameraTransform = player.getComponent(Vision.class)
                                .getCamera()
                                .getComponent(Transform.class);
        playerData = player.getPlayerData();
        playerScore = player.getScore();
        playerInventory = player.getWeaponsInventory();
        visible = player.getComponent(MeshBody.class).isVisible();
    }

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private PlayerUpdatePacket() {}
}