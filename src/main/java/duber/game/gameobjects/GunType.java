package duber.game.gameobjects;

import duber.engine.entities.components.Named;
import duber.engine.loaders.MeshResource;
import duber.game.gameobjects.Gun.GunData;
import static duber.game.gameobjects.Gun.GunData.PRIMARY_GUN;
import static duber.game.gameobjects.Gun.GunData.SECONDARY_GUN;

/**
 * The different types of guns in the game
 * @author Darren Lo
 * @version 1.0
 */
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

<<<<<<< Updated upstream

    
=======
    /** The Gun associated with the GunType */
>>>>>>> Stashed changes
    private Gun gun;

    /** The MeshResource associated with the GunType */
    private MeshResource gunMeshResource;

    /**
     * Constructs a GunType.
     * @param gun the associated Gun
     * @param gunMeshResource the associated MeshResource
     */
    private GunType(Gun gun, MeshResource gunMeshResource) {
        this.gun = gun;
        this.gunMeshResource = gunMeshResource;
    }
        
    /**
     * Gets the Gun associated with a GunType.
     * @return the associated Gun
     */
    public Gun getGun() {
        return gun;
    }

    /**
     * Gets the MeshResource associated with a GunType.
     * @return the associated MeshResource
     */
    public MeshResource getGunMeshResource() {
        return gunMeshResource;
    }

    /**
     * Determines whether or not a Gun is a GunType.
     * @param gun the Gun to check
     * @return whether or not the Gun is the GunType
     */
    public boolean isGunType(Gun gun) {
        return gun.getComponent(Named.class).getName().equals(this.toString());
    }

    /**
     * Gets the GunType of a Gun.
     * @return the GunType of a Gun
     */
    public static GunType getGunType(Gun gun) {
        return getGunType(gun.getComponent(Named.class).getName());
    }

    /**
     * Gets the GunType that matches a given String
     * @param gunString the String to check
     * @return the GunType associated with the String.
     */
    public static GunType getGunType(String gunString) {
        for(GunType gunType : GunType.values()) {
            if (gunType.toString().equals(gunString)) {
                return gunType;
            }
        }

        return null;
    }
}