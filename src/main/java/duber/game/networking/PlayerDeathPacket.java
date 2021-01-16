package duber.game.networking;

public class PlayerDeathPacket extends Packet {
    public int playerId;

    public PlayerDeathPacket(int playerId) {
        this.playerId = playerId;
    }

    @SuppressWarnings("unused")
    private PlayerDeathPacket() {}
}