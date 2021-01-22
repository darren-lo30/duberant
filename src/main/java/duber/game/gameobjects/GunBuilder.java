package duber.game.gameobjects;

import duber.engine.entities.components.Named;
import duber.engine.exceptions.LWJGLException;
import duber.engine.loaders.MeshLoader;
import duber.engine.loaders.MeshResource;
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
        String name = GunType.RIFLE.toString();
        
        int totalBullets = 90;
        float bulletsPerSecond = 10;

        int damagePerBullet = 40;

        Bullet bullet = new Bullet(damagePerBullet);
        GunData gunData = new GunData(totalBullets, bulletsPerSecond, bullet);

        rifle = new PrimaryGun(name, gunData, 2500);
    }

    private void setLmg() {
        String name = GunType.LMG.toString();
        
        int totalBullets = 50;
        float bulletsPerSecond = 14;

        int damagePerBullet = 22;

        Bullet bullet = new Bullet(damagePerBullet);
        GunData gunData = new GunData(totalBullets, bulletsPerSecond, bullet);

        lmg = new PrimaryGun(name, gunData, 2000);
    }

    private void setPistol() {
        String name = GunType.PISTOL.toString();
        
        int totalBullets = 20;
        float bulletsPerSecond = 3.3f;

        int damagePerBullet = 20;

        Bullet bullet = new Bullet(damagePerBullet);
        GunData gunData = new GunData(totalBullets, bulletsPerSecond, bullet);

        pistol = new SecondaryGun(name, gunData, 500);
    }


    public <T extends Gun> T buildGunInstance(T gun, T gunInstance) {
        String name = gun.getComponent(Named.class).getName();

        //Copy important gun's fields over while leaving others untouched
        gunInstance.getComponent(Named.class).setName(name);
        gunInstance.getComponent(GunData.class).set(gun.getGunData());

        return gunInstance;
    }

    public Gun buildGun(GunType gunType) {
        if(gunType == GunType.PISTOL) {
            return buildPistol();
        } else if(gunType == GunType.RIFLE) {
            return buildRifle();
        } else if(gunType == GunType.LMG) {
            return buildLmg();
        }

        return null;
    }

    public void loadGunMesh(Gun gun) throws LWJGLException {
        MeshResource gunMeshResource = GunType.getGunType(gun).getGunMeshResource();
        MeshBody gunMesh = new MeshBody(MeshLoader.load(gunMeshResource));
        gunMesh.setVisible(true);

        gun.addComponent(gunMesh);
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