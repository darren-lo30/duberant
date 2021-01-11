package duber.game.networking;

import duber.engine.entities.components.Transform;

public class PlayerPositionPacket extends Packet {
    public int userId;
    public Transform playerTransform;

    public PlayerPositionPacket(int userId, Transform playerTransform) {
        this.userId = userId;
        this.playerTransform = playerTransform;
    }

    @SuppressWarnings("unused")
    private PlayerPositionPacket() {}
}