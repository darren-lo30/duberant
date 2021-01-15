package duber.game.networking;

import duber.engine.entities.components.Vision;
import duber.engine.entities.components.Transform;
import duber.game.gameobjects.Player;

public class PlayerPositionPacket extends Packet {
    public int playerId;
    public Transform playerTransform;
    public Transform cameraTransform;

    public PlayerPositionPacket(int playerId, Player player) {
        this.playerId = playerId;
        playerTransform = player.getComponent(Transform.class);
        cameraTransform = player.getComponent(Vision.class)
                                .getCamera()
                                .getComponent(Transform.class);
    }

    @SuppressWarnings("unused")
    private PlayerPositionPacket() {}
}