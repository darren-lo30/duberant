package duber.game.networking;

import duber.game.gameobjects.GunType;


/**
 * A Packet used by Kryonet that notifies the server that someone puchase a gun.
 * @author Darren Lo
 * @version 1.0
 */
public class GunPurchasePacket {
    /**
     * The GunType as a String. This is because Kryo has issues serializing enums.
     */
    public String gunTypeString;

    /**
     * Constructs a GunPurchasePacket.
     * @param gunType the GunType to purchase
     */
    public GunPurchasePacket(GunType gunType) {
        gunTypeString = gunType.toString();
    }

    /**
     * Gets the GunType  of the gun purchased.
     * @return the GunType of gun purchased
     */
    public GunType getGunType() {
        return GunType.getGunType(gunTypeString);
    }
    
    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private GunPurchasePacket() {}
}