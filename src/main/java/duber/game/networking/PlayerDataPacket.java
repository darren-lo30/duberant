package duber.game.networking;

import duber.engine.entities.components.Vision;
import duber.engine.entities.components.Transform;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Player.PlayerData;

public class PlayerDataPacket extends Packet {
    public int playerId;
    public Transform playerTransform;
    public Transform cameraTransform;
    public PlayerData playerData;

    public PlayerDataPacket(Player player) {
        this.playerId = player.getId();
        playerTransform = player.getComponent(Transform.class);
        cameraTransform = player.getComponent(Vision.class)
                                .getCamera()
                                .getComponent(Transform.class);
        playerData = player.getPlayerData();
    }

    @SuppressWarnings("unused")
    private PlayerDataPacket() {}
}