package duber.game.gameobjects;

import duber.engine.entities.components.Named;
import duber.engine.exceptions.LWJGLException;
import duber.engine.loaders.MeshLoader;
import duber.engine.loaders.MeshResource;

import java.util.EnumMap;
import java.util.Map;

import duber.engine.entities.components.MeshBody;
import duber.game.gameobjects.Gun.GunData;

import static duber.game.gameobjects.Gun.GunData.PRIMARY_GUN;
import static duber.game.gameobjects.Gun.GunData.SECONDARY_GUN;;

public class GunBuilder {
    private Map<GunType, Gun> guns;
    private Map<GunType, MeshBody> gunMeshes;

    private static GunBuilder instance;

    public static GunBuilder getInstance() {
        if(instance == null) {
            instance = new GunBuilder();
        }
        return instance;
    }
    
    private GunBuilder() {
        guns = new EnumMap<>(GunType.class);
        gunMeshes = new EnumMap<>(GunType.class);
        
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
        GunData gunData = new GunData(PRIMARY_GUN, totalBullets, bulletsPerSecond, bullet);

        Gun rifle = new Gun(name, gunData, 2500);
        guns.put(GunType.RIFLE, rifle);
    }

    private void setLmg() {
        String name = GunType.LMG.toString();
        
        int totalBullets = 50;
        float bulletsPerSecond = 14;

        int damagePerBullet = 22;

        Bullet bullet = new Bullet(damagePerBullet);
        GunData gunData = new GunData(PRIMARY_GUN, totalBullets, bulletsPerSecond, bullet);

        Gun lmg = new Gun(name, gunData, 2000);
        guns.put(GunType.LMG, lmg);
    }

    private void setPistol() {
        String name = GunType.PISTOL.toString();
        
        int totalBullets = 20;
        float bulletsPerSecond = 3.3f;

        int damagePerBullet = 20;

        Bullet bullet = new Bullet(damagePerBullet);
        GunData gunData = new GunData(SECONDARY_GUN, totalBullets, bulletsPerSecond, bullet);

        Gun pistol = new Gun(name, gunData, 500);
        guns.put(GunType.PISTOL, pistol);
    }

    public <T extends Gun> T buildGunInstance(T gun, T gunInstance) {
        String name = gun.getComponent(Named.class).getName();

        //Copy important gun's fields over while leaving others untouched
        gunInstance.getComponent(Named.class).setName(name);
        gunInstance.getComponent(GunData.class).set(gun.getGunData());
        gunInstance.getComponent(MeshBody.class).set(gun.getComponent(MeshBody.class));

        return gunInstance;
    }

    public Gun buildGun(GunType gunType) {
        return buildGunInstance(guns.get(gunType), new Gun());
    }

    public void loadGunMesh(Gun gun) throws LWJGLException {
        if(gun == null) {
            return;
        }
        
        GunType gunType = GunType.getGunType(gun);
        if(gunType == null) {
            throw new IllegalArgumentException("The gun is not registered with a mesh");
        }

        if(!gunMeshes.containsKey(gunType)) {
            MeshResource gunMeshResource = gunType.getGunMeshResource();
            gunMeshes.put(gunType, new MeshBody(MeshLoader.load(gunMeshResource)));
        }
        
        MeshBody gunMesh = new MeshBody(gunMeshes.get(gunType));
        gunMesh.setVisible(true);

        gun.addComponent(gunMesh);
    }

}