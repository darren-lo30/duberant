package duber.game.gameobjects;

import duber.engine.entities.components.Component;

import static duber.game.gameobjects.Gun.GunData.PRIMARY_GUN;
import static duber.game.gameobjects.Gun.GunData.SECONDARY_GUN;;

public class WeaponsInventory extends Component {
    private static final int PRIMARY_GUN_IDX = 0;
    private static final int SECONDARY_GUN_IDX = 1;
    
    Gun[] guns = new Gun[2];
    private int equippedGunIdx = 0;
    
    public void clear() {
        guns[0] = null;
        guns[1] = null;
    }

    public void set(WeaponsInventory weaponsInventory) {
        setPrimaryGun(weaponsInventory.getPrimaryGun());
        setSecondaryGun(weaponsInventory.getSecondaryGun());

        equippedGunIdx = weaponsInventory.getEquippedGunIdx();
    }

    public void resetGuns() {
        if(getPrimaryGun() != null) {
            getPrimaryGun().getGunData().reset();
        }

        if(getSecondaryGun() != null) {
            getSecondaryGun().getGunData().reset();
        }
    }

    public Gun getEquippedGun() {
        return guns[equippedGunIdx];
    }

    public int getEquippedGunIdx() {
        return equippedGunIdx;
    }

    public boolean hasEquippedGun() {
        return guns[equippedGunIdx] != null;
    }

    public Gun getPrimaryGun() {
        return guns[PRIMARY_GUN_IDX];
    }

    public void setPrimaryGun(Gun primaryGun) {
        if(primaryGun != null  && primaryGun.getGunData().getCategory() != PRIMARY_GUN) {
            throw new IllegalArgumentException("Gun must be primary gun");
        }
        guns[PRIMARY_GUN_IDX] = primaryGun;
    }

    public Gun getSecondaryGun() {
        return guns[SECONDARY_GUN_IDX];
    }

    public void setSecondaryGun(Gun secondaryGun) {
        if(secondaryGun != null && secondaryGun.getGunData().getCategory() != SECONDARY_GUN) {
            throw new IllegalArgumentException("Gun must be secondary gun");
        }

        guns[SECONDARY_GUN_IDX] = secondaryGun;
    }

    public void equipPrimaryGun() {
        equippedGunIdx = PRIMARY_GUN_IDX;
    }

    public void equipSecondaryGun() {
        equippedGunIdx = SECONDARY_GUN_IDX;
    }
}