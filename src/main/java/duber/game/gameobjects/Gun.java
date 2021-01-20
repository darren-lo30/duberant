package duber.game.gameobjects;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Component;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Named;

public abstract class Gun extends Entity {    
    protected Gun(){
        this(null, new GunData(), 0);
    }

    protected Gun(String name, GunData gunData, int cost) {
        if(gunData == null) {
            throw new IllegalArgumentException("Gun must have gun data");
        }

        addComponent(new MeshBody());
        addComponent(new Named(name));
        addComponent(new Buyable(cost));
        addComponent(gunData);
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

    public static class GunData extends Component {        
        
        private int totalBullets;
        private int remainingBullets;
        private float bulletsPerSecond;
        private long lastFiredTime;
        private Bullet gunBullets;

        public GunData() {
            this(0, 0, new Bullet());
        }

        public GunData(GunData gunData) {
            this(gunData.getTotalBullets(), gunData.getBulletsPerSecond(), new Bullet(gunData.getGunBullets()));
        }

        public GunData(int totalBullets, float bulletsPerSecond, Bullet gunBullets) {
            this.totalBullets = totalBullets;
            remainingBullets = totalBullets;

            this.bulletsPerSecond = bulletsPerSecond;
            lastFiredTime = System.currentTimeMillis();

            this.gunBullets = gunBullets;
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