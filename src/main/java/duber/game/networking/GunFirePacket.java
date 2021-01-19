package duber.game.networking;

public class GunFirePacket extends Packet {
    public int shooterId;   

    public GunFirePacket(int shooterId) {
        this.shooterId = shooterId;
    }

    @SuppressWarnings("unused")
    private GunFirePacket() {}
}