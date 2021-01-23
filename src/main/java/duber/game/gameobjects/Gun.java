package duber.game.gameobjects;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Component;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Named;

public class Gun extends Entity {    
    public Gun(){
        this(null, new GunData(), 0);
    }

    public Gun(String name, GunData gunData, int cost) {
        if(gunData == null) {
            throw new IllegalArgumentException("Gun must have gun data");
        }

        addComponent(new MeshBody());
        addComponent(new Named(name));
        addComponent(new Buyable(cost));
        addComponent(gunData);
    }

    public Gun(Gun gun) {
        this(gun.getComponent(Named.class).getName(), 
             new GunData(gun.getGunData()), 
             gun.getComponent(Buyable.class).getCost());
    }

    public GunData getGunData() {
        return getComponent(GunData.class);
    }

    public boolean canFire() {
        GunData gunData = getGunData();        
        long currTime = System.currentTimeMillis();
        boolean notReloading = currTime - gunData.getLastFiredTime() >= gunData.getFireTime();

        return notReloading && gunData.hasBullets();
    }
    
    public void fire() {
        GunData gunData = getGunData();

        //Decrement number of bullets by 1
        gunData.setRemainingBullets(gunData.getRemainingBullets() - 1);

        //Set fire time as curr time
        gunData.setLastFiredTime(System.currentTimeMillis());   
    }

    public boolean isPrimaryGun() {
        return getGunData().getCategory() == GunData.PRIMARY_GUN;
    }

    public boolean isSecondaryGun() {
        return getGunData().getCategory() == GunData.SECONDARY_GUN;
    }

    public static class GunData extends Component {          
        public static final int NO_CATEGORY = 0;
        public static final int PRIMARY_GUN = 1;
        public static final int SECONDARY_GUN = 2;

        private int category;      
        private int totalBullets;
        private int remainingBullets;
        private float bulletsPerSecond;
        private long lastFiredTime;
        private Bullet gunBullets;

        public GunData() {
            this(NO_CATEGORY, 0, 0, new Bullet());
        }

        public GunData(GunData gunData) {
            this(gunData.getCategory(), gunData.getTotalBullets(), gunData.getBulletsPerSecond(), new Bullet(gunData.getGunBullets()));
        }

        public GunData(int category, int totalBullets, float bulletsPerSecond, Bullet gunBullets) {
            this.category = category;
            this.totalBullets = totalBullets;
            remainingBullets = totalBullets;

            this.bulletsPerSecond = bulletsPerSecond;
            lastFiredTime = System.currentTimeMillis();

            this.gunBullets = gunBullets;
        }

        public void set(GunData gunData) {
            category = gunData.category;
            totalBullets = gunData.totalBullets;
            remainingBullets = gunData.remainingBullets;
            bulletsPerSecond = gunData.bulletsPerSecond;
            lastFiredTime = gunData.lastFiredTime;
            gunBullets = gunData.gunBullets;
        }

        public int getCategory() {
            return category;
        }

        public int getTotalBullets() {
            return totalBullets;
        }

        public int getRemainingBullets() {
            return remainingBullets;
        }

        public void setRemainingBullets(int remainingBullets) {
            this.remainingBullets = remainingBullets;
        }

        public boolean hasBullets() {
            return remainingBullets > 0;
        }

        public float getBulletsPerSecond() {
            return bulletsPerSecond;
        }

        public long getFireTime() {
            return (long) (1000.0f/bulletsPerSecond);
        }

        public long getLastFiredTime() {
            return lastFiredTime;
        }
        
        public void setLastFiredTime(long lastFiredTime) {
            this.lastFiredTime = lastFiredTime;
        }

        public Bullet getGunBullets() {
            return gunBullets;
        }

        public void reset() {
            remainingBullets = totalBullets;
        }
    }    
}