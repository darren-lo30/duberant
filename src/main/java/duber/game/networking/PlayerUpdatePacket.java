package duber.game.networking;

import duber.engine.entities.components.Vision;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Transform;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Score;
import duber.game.gameobjects.Player.PlayerData;
import duber.game.gameobjects.Player.WeaponsInventory;

public class PlayerUpdatePacket extends Packet {
    public int playerId;
    public Transform playerTransform;
    public Transform cameraTransform;
    public PlayerData playerData;
    public Score playerScore;
    public WeaponsInventory weaponsInventory;
    public boolean visible;

    public PlayerUpdatePacket(Player player) {
        this.playerId = player.getId();
        playerTransform = player.getComponent(Transform.class);
        cameraTransform = player.getComponent(Vision.class)
                                .getCamera()
                                .getComponent(Transform.class);
        playerData = player.getPlayerData();
        playerScore = player.getScore();
        weaponsInventory = player.getWeaponsInventory();
        visible = player.getComponent(MeshBody.class).isVisible();
    }

    @SuppressWarnings("unused")
    private PlayerUpdatePacket() {}
}