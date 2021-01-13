package duber.game.networking;

import duber.engine.entities.components.Follow;
import duber.engine.entities.components.Transform;
import duber.game.gameobjects.Player;

public class PlayerPositionPacket extends Packet {
    public int userId;
    public Transform playerTransform;
    public Transform cameTransform;

    public PlayerPositionPacket(int userId, Player player) {
        this.userId = userId;
        playerTransform = player.getComponent(Transform.class);
        cameTransform = player.getComponent(Follow.class)
                              .getCamera()
                              .getComponent(Transform.class);
    }

    @SuppressWarnings("unused")
    private PlayerPositionPacket() {}
}