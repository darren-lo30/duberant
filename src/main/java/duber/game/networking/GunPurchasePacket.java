package duber.game.networking;

import duber.game.gameobjects.GunType;

public class GunPurchasePacket {
    public String gunTypeString;

    public GunPurchasePacket(GunType gunType) {
        gunTypeString = gunType.toString();
    }

    public GunType getGunType() {
        return GunType.getGunType(gunTypeString);
    }
    
    @SuppressWarnings("unused")
    private GunPurchasePacket() {}
}