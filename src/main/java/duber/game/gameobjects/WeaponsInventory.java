package duber.game.gameobjects;

import duber.engine.entities.components.Component;

import static duber.game.gameobjects.Gun.GunData.PRIMARY_GUN;
import static duber.game.gameobjects.Gun.GunData.SECONDARY_GUN;;

/**
 * The collection of weapons as a Component
 * @author Darren Lo
 * @version 1.0
 */
public class WeaponsInventory extends Component {
    /** The index storing the Primary Weapon */
    private static final int PRIMARY_GUN_IDX = 0;

    /** The index storing the Secondary Weapon */
    private static final int SECONDARY_GUN_IDX = 1;
    
    /** The guns that the WeaponsInventory stores */
    Gun[] guns = new Gun[2];


    /** The index of the current equipped gun */
    private int equippedGunIdx = 0;
    
    /** 
     * Clears the WeaponsInventory of all the Guns 
     */
    public void clear() {
        guns[0] = null;
        guns[1] = null;
    }

    /**
     * Sets the WeaponsInventory equal to another WeaponsInventory.
     * @param weaponsInventory the WeaponsInventory to copy
     */
    public void set(WeaponsInventory weaponsInventory) {
        setPrimaryGun(weaponsInventory.getPrimaryGun());
        setSecondaryGun(weaponsInventory.getSecondaryGun());

        equippedGunIdx = weaponsInventory.getEquippedGunIdx();
    }

    /**
     * Resets all the guns.
     */
    public void resetGuns() {
        if (getPrimaryGun() != null) {
            getPrimaryGun().getGunData().reset();
        }

        if (getSecondaryGun() != null) {
            getSecondaryGun().getGunData().reset();
        }
    }

    /**
     * Gets the equipped gun.
     * @return the equipped gun
     */
    public Gun getEquippedGun() {
        return guns[equippedGunIdx];
    }

    /**
     * Gets the index of the equipped gun.
     * @return the index of the equipped gun
     */
    public int getEquippedGunIdx() {
        return equippedGunIdx;
    }

    /**
     * Determines if there is an equipped gun.
     * @return whether or not there is an equipped gun.
     */
    public boolean hasEquippedGun() {
        return guns[equippedGunIdx] != null;
    }

    /**
     * Gets the primary gun.
     * @return the primary gun
     */
    public Gun getPrimaryGun() {
        return guns[PRIMARY_GUN_IDX];
    }

    /**
     * Sets the primary gun.
     * @param primaryGun the primary gun
     */
    public void setPrimaryGun(Gun primaryGun) {
        if (primaryGun != null  && primaryGun.getGunData().getCategory() != PRIMARY_GUN) {
            throw new IllegalArgumentException("Gun must be primary gun");
        }
        guns[PRIMARY_GUN_IDX] = primaryGun;
    }

    /**
     * Gets the secondary gun.
     * @return the secondary gun
     */
    public Gun getSecondaryGun() {
        return guns[SECONDARY_GUN_IDX];
    }

    /**
     * Sets the secondary gun.
     * @param secondaryGun the secondary gun
     */
    public void setSecondaryGun(Gun secondaryGun) {
        if (secondaryGun != null && secondaryGun.getGunData().getCategory() != SECONDARY_GUN) {
            throw new IllegalArgumentException("Gun must be secondary gun");
        }

        guns[SECONDARY_GUN_IDX] = secondaryGun;
    }

    /**
     * Equips the primary gun.
     */
    public void equipPrimaryGun() {
        equippedGunIdx = PRIMARY_GUN_IDX;
    }

    /**
     * Equips the secondary gun.
     */
    public void equipSecondaryGun() {
        equippedGunIdx = SECONDARY_GUN_IDX;
    }
}