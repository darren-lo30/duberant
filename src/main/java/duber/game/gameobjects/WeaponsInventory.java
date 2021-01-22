package duber.game.gameobjects;

import duber.engine.entities.components.Component;
import duber.engine.entities.components.Named;

public class WeaponsInventory extends Component {
    private static final int PRIMARY_GUN_IDX = 0;
    private static final int SECONDARY_GUN_IDX = 1;
    
    Gun[] guns = new Gun[2];
    private int equippedGunIdx = 0;

    public void updateData(WeaponsInventory updatedInventory) {
        if(!updateGunData(getPrimaryGun(), updatedInventory.getPrimaryGun())) {
            setPrimaryGun(updatedInventory.getPrimaryGun());
        }

        if(!updateGunData(getSecondaryGun(), updatedInventory.getSecondaryGun())) {
            setSecondaryGun(updatedInventory.getSecondaryGun());
        }

        //Update equipped index
        equippedGunIdx = updatedInventory.getEquippedGunIdx();
    }    

    private boolean updateGunData(Gun gun, Gun newGun) {
        if(gun == null || newGun == null) {
            return false;
        }
        
        String gunType = gun.getComponent(Named.class).getName();
        String newGunType = newGun.getComponent(Named.class).getName();
        if(!gunType.equals(newGunType)) {
            return false;
        } 
        
        gun.getGunData().set(newGun.getGunData());
        return true;
    }
    
    public void clear() {
        guns[0] = null;
        guns[1] = null;
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
        guns[PRIMARY_GUN_IDX] = primaryGun;
    }

    public Gun getSecondaryGun() {
        return guns[SECONDARY_GUN_IDX];
    }

    public void setSecondaryGun(Gun secondaryGun) {
        guns[SECONDARY_GUN_IDX] = secondaryGun;
    }

    public void equipPrimaryGun() {
        equippedGunIdx = PRIMARY_GUN_IDX;
    }

    public void equipSecondaryGun() {
        equippedGunIdx = SECONDARY_GUN_IDX;
    }
}