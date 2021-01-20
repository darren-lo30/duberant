package duber.game.gameobjects;

import duber.engine.entities.components.Named;
import duber.engine.loaders.MeshResource;

public enum GunType {
    RIFLE (new MeshResource("models/rifle/rifle.obj", "models/rifle")) {
        @Override
        public String toString() {
            return "Rifle";
        }
    }, 
    LMG (new MeshResource("models/lmg/lmg.obj", "models/lmg")) {
        @Override
        public String toString() {
            return "Light Machine Gun";
        }
    }, 
    PISTOL (new MeshResource("models/pistol/pistol.obj", "models/pistol")) {
        @Override
        public String toString() {
            return "Pistol";
        }
    };
    
    private MeshResource gunMeshResource;

    private GunType(MeshResource gunMeshResource) {
        this.gunMeshResource = gunMeshResource;
    }

    public MeshResource getGunMeshResource() {
        return gunMeshResource;
    }

    public boolean isGunType(Gun gun) {
        return gun.getComponent(Named.class).getName().equals(this.toString());
    }

    public static GunType getGunType(Gun gun) {
        for(GunType gunType : GunType.values()) {
            if(gunType.isGunType(gun)) {
                return gunType;
            }
        }

        return null;
    }
}