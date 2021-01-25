package duber.game.gameobjects;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Component;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Named;

/**
 * A representation of a gun in the game
 * @author Darren Lo
 * @version 1.0
 */
public class Gun extends Entity {    
    /**
     * Constructs a gun.
     */
    public Gun(){
        this(null, new GunData(), 0);
    }

    /**
     * Constructs a gun with initialized data.
     * @param name the name of the gun
     * @param gunData the guns data
     * @param cost the cost of the gun
     */
    public Gun(String name, GunData gunData, int cost) {
        if (gunData == null) {
            throw new IllegalArgumentException("Gun must have gun data");
        }

        addComponent(new MeshBody());
        addComponent(new Named(name));
        addComponent(new Buyable(cost));
        addComponent(gunData);
    }

    /**
     * Constructs a copy of a gun.
     * @param gun the gun to copy
     */
    public Gun(Gun gun) {
        this(gun.getComponent(Named.class).getName(), 
             new GunData(gun.getGunData()), 
             gun.getComponent(Buyable.class).getCost());
    }

    /**
     * Gets the gun's datas
     */
    public GunData getGunData() {
        return getComponent(GunData.class);
    }

    /**
     * Determines whether or not the gun can fire.
     * @return whether or not the gun can fire
     */
    public boolean canFire() {
        GunData gunData = getGunData();        
        long currTime = System.currentTimeMillis();
        boolean notReloading = currTime - gunData.getLastFiredTime() >= gunData.getFireTime();

        return notReloading && gunData.hasBullets();
    }

    /**
     * Fires the gun.
     */
    public void fire() {
        GunData gunData = getGunData();

        //Decrement number of bullets by 1
        gunData.setRemainingBullets(gunData.getRemainingBullets() - 1);

        //Set fire time as curr time
        gunData.setLastFiredTime(System.currentTimeMillis());   
    }

    /**
     * Determines if the gun is a primary gun.
     * @return whether or not the gun is a primary gun
     */
    public boolean isPrimaryGun() {
        return getGunData().getCategory() == GunData.PRIMARY_GUN;
    }

    /**
     * Determines if the gun is a secondary gun.    
     * @return whether or not the gun is a secondary gun
     */
    public boolean isSecondaryGun() {
        return getGunData().getCategory() == GunData.SECONDARY_GUN;
    }

    /**
     * A component that stores all the gun's data
     */
    public static class GunData extends Component {          

        /** A gun with no type */
        public static final int NO_CATEGORY = 0;

        /** A primary gun */
        public static final int PRIMARY_GUN = 1;

        /** A secondary gun */
        public static final int SECONDARY_GUN = 2;

        /** The category of the gun */
        private int category;      

        /** The total bullets in the gun when initialized */
        private int totalBullets;

        /** The remaining bullets in the gun */
        private int remainingBullets;

        /** The number of bullets shot per second */
        private float bulletsPerSecond;

        /** The last time the gun was fired */
        private long lastFiredTime;

        /** The bullets used in the gun */
        private Bullet gunBullets;

        /**
         * Constructs an empty GunData.
         */
        public GunData() {
            this(NO_CATEGORY, 0, 0, new Bullet());
        }

        /**
         * Constructs GunData that is a copy of another GunData.
         * @param gunData the GunData to copy
         */
        public GunData(GunData gunData) {
            this(gunData.getCategory(), gunData.getTotalBullets(), gunData.getBulletsPerSecond(), new Bullet(gunData.getGunBullets()));
        }

        /**
         * Constructs a GunData with all data filled.
         * @param category the category of the gun
         * @param totalBullets the total bullets in the gun
         * @param bulletsPerSecond the number of bullets shot per second
         * @param gunBullets the bullets used in the gun
         */
        public GunData(int category, int totalBullets, float bulletsPerSecond, Bullet gunBullets) {
            this.category = category;
            this.totalBullets = totalBullets;
            remainingBullets = totalBullets;

            this.bulletsPerSecond = bulletsPerSecond;
            lastFiredTime = System.currentTimeMillis();

            this.gunBullets = gunBullets;
        }

        /**
         * Sets the GunData to be the same of that of another GunData.
         * @param gunData the GunData to copy
         */
        public void set(GunData gunData) {
            category = gunData.category;
            totalBullets = gunData.totalBullets;
            remainingBullets = gunData.remainingBullets;
            bulletsPerSecond = gunData.bulletsPerSecond;
            lastFiredTime = gunData.lastFiredTime;
            gunBullets = gunData.gunBullets;
        }

        /**
         * Gets the category of the gun.
         * @return the category of the gun
         */
        public int getCategory() {
            return category;
        }

        /**
         * Gets the total bullets in the gun.
         * @return the total bullets in the gun
         */
        public int getTotalBullets() {
            return totalBullets;
        }

        /**
         * Gets the remaining bullets in the gun.
         * @return the remaining bulets in the gun
         */
        public int getRemainingBullets() {
            return remainingBullets;
        }

        /**
         * Sets the remaining bullets in the gun.
         * @param remainingBullets the remaining bullets in the gun
         */
        public void setRemainingBullets(int remainingBullets) {
            this.remainingBullets = remainingBullets;
        }

        /**
         * Determines if the gun has bullets left.
         * @return whether or not the gun has bullets left
         */
        public boolean hasBullets() {
            return remainingBullets > 0;
        }

        /**
         * Gets the number of bullets the gun can fire per second.
         * @return the number of bullets shot per second
         */
        public float getBulletsPerSecond() {
            return bulletsPerSecond;
        }

        /**
         * Gets the amount of time it takes to fire the gun.
         * @return the gun's fire time
         */
        public long getFireTime() {
            return (long) (1000.0f/bulletsPerSecond);
        }

        /**
         * Gets the time that the gun was last fired.
         * @return the time that gun was last fired
         */
        public long getLastFiredTime() {
            return lastFiredTime;
        }

        /**
         * Sets the time that the gun was last fired.
         * @param lastFiredTime that time the gun was last fired
         */
        public void setLastFiredTime(long lastFiredTime) {
            this.lastFiredTime = lastFiredTime;
        }   

        /**
         * Gets the Bullets used in the gun
         * @return the Bullets used in the gun
         */
        public Bullet getGunBullets() {
            return gunBullets;
        }

        /**
         * Resets the gun by reloading it with all its bullets.
         */
        public void reset() {
            remainingBullets = totalBullets;
        }
    }    
}