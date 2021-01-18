package duber.game.gameobjects;

import duber.engine.entities.components.Named;
import duber.engine.entities.components.MeshBody;
import duber.game.gameobjects.Gun.GunData;

public class GunBuilder {
    private PrimaryGun rifle;
    private PrimaryGun lmg;
    private SecondaryGun pistol;

    private static GunBuilder instance;

    public static GunBuilder getInstance() {
        if(instance == null) {
            instance = new GunBuilder();
        }

        return instance;
    }
    
    private GunBuilder() {
        setRifle();
        setLmg();
        setPistol();
    }

    private void setRifle() {
        String name = "Rifle";
        
        int totalBullets = 90;
        float bulletsPerSecond = 10;

        int damagePerBullet = 40;

        Bullet bullet = new Bullet(damagePerBullet);
        GunData gunData = new GunData(totalBullets, bulletsPerSecond, bullet);

        rifle = new PrimaryGun(name, gunData, 2500);
    }

    private void setLmg() {
        String name = "LMG";
        
        int totalBullets = 50;
        float bulletsPerSecond = 14;

        int damagePerBullet = 22;

        Bullet bullet = new Bullet(damagePerBullet);
        GunData gunData = new GunData(totalBullets, bulletsPerSecond, bullet);

        lmg = new PrimaryGun(name, gunData, 2000);
    }

    private void setPistol() {
        String name = "Pistol";
        
        int totalBullets = 20;
        float bulletsPerSecond = 6.3f;

        int damagePerBullet = 20;

        Bullet bullet = new Bullet(damagePerBullet);
        GunData gunData = new GunData(totalBullets, bulletsPerSecond, bullet);

        pistol = new SecondaryGun(name, gunData, 500);
    }

    public <T extends Gun> T buildGunInstance(T gun, T gunInstance) {
        String name = gun.getComponent(Named.class).getName();

        //Copy important gun's fields over while leaving others untouched
        gunInstance.addComponent(new Named(name));
        gunInstance.addComponent(new GunData(gun.getGunData()));
        gunInstance.addComponent(new MeshBody(gun.getComponent(MeshBody.class)));

        return gunInstance;
    }
    

    public PrimaryGun buildRifle() { 
        return buildGunInstance(rifle, new PrimaryGun());
    }

    public PrimaryGun buildLmg() {
        return buildGunInstance(lmg, new PrimaryGun());
    }

    public SecondaryGun buildPistol() {
        return buildGunInstance(pistol, new SecondaryGun());
    }

}