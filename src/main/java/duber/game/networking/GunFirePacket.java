package duber.game.networking;

/**
 * A Packet used by Kryonet that notifies the client that someone shot.
 * @author Darren Lo
 * @version 1.0
 */
public class GunFirePacket extends Packet {
    /** The player id of the shooter */
    public int shooterId;   

    /**
     * Constructs a GunFirePacket
     * @param shooterId the id of the shooter
     */
    public GunFirePacket(int shooterId) {
        this.shooterId = shooterId;
    }

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private GunFirePacket() {}
}