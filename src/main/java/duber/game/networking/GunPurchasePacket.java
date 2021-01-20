package duber.game.networking;

import duber.game.gameobjects.GunType;

public class GunPurchasePacket {
    public GunType gunType;

    public GunPurchasePacket(GunType gunType) {
        this.gunType = gunType;
    }
}