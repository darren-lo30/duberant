package duber.game.gameobjects;

import duber.engine.entities.components.Named;
import duber.engine.loaders.MeshResource;
import duber.game.gameobjects.Gun.GunData;
import static duber.game.gameobjects.Gun.GunData.PRIMARY_GUN;
import static duber.game.gameobjects.Gun.GunData.SECONDARY_GUN;


public enum GunType {
    RIFLE (new Gun("Rifle", new GunData(PRIMARY_GUN, 90, 10, new Bullet(40)), 1800),
           new MeshResource("models/rifle/rifle.obj", "models/rifle")) {
        @Override
        public String toString() {
            return "Rifle";
        }
    }, 
    LMG (new Gun("Light Machine Gun", new GunData(PRIMARY_GUN, 90, 10, new Bullet(22)), 1300), 
         new MeshResource("models/lmg/lmg.obj", "models/lmg")) {
        @Override
        public String toString() {
            return "Light Machine Gun";
        }
    }, 
    PISTOL (new Gun("Pistol", new GunData(SECONDARY_GUN, 20, 3.3f, new Bullet(20)), 500),   
            new MeshResource("models/pistol/pistol.obj", "models/pistol")) {
        @Override
        public String toString() {
            return "Pistol";
        }


    };


    
    private Gun gun;
    private MeshResource gunMeshResource;

    private GunType(Gun gun, MeshResource gunMeshResource) {
        this.gun = gun;
        this.gunMeshResource = gunMeshResource;
    }
        
    public Gun getGun() {
        return gun;
    }

    public MeshResource getGunMeshResource() {
        return gunMeshResource;
    }

    public boolean isGunType(Gun gun) {
        return gun.getComponent(Named.class).getName().equals(this.toString());
    }

    public static GunType getGunType(Gun gun) {
        return getGunType(gun.getComponent(Named.class).getName());
    }

    public static GunType getGunType(String gunString) {
        for(GunType gunType : GunType.values()) {
            if (gunType.toString().equals(gunString)) {
                return gunType;
            }
        }

        return null;
    }
}