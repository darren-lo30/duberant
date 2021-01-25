package duber.game.gameobjects;

import duber.engine.exceptions.LWJGLException;
import duber.engine.loaders.MeshLoader;
import duber.engine.loaders.MeshResource;

import java.util.EnumMap;
import java.util.Map;

import duber.engine.entities.components.MeshBody;


/**
 * Used to build guns in the game
 * @author Darren Lo
 * @version 1.0
 */
public class GunBuilder {
    /** The meshes for each gun type. */
    private Map<GunType, MeshBody> gunMeshes;

    /** The instance of the GunBuilder. */
    private static GunBuilder instance;

    /**
     * Gets an instance of the GunBuilder.
     * @return the instance of the GunBuilder
     */
    public static GunBuilder getInstance() {
        if (instance == null) {
            instance = new GunBuilder();
        }
        return instance;
    }
    
    /**
     * Constructs a GunBuilder.
     */
    private GunBuilder() {
        gunMeshes = new EnumMap<>(GunType.class);
    }

    /**
     * Builds a gun of a GunType.
     * @return the Gun that was built
     */
    public Gun buildGun(GunType gunType) {
        return new Gun(gunType.getGun());
    }

    /**
     * Loads a mesh for a gun.
     * @param gun the gun whose mesh to load
     * @throws LWJGLException if the mesh could not be loaded
     */
    public void loadGunMesh(Gun gun) throws LWJGLException {
        if (gun == null) {
            return;
        }
        
        GunType gunType = GunType.getGunType(gun);
        if (gunType == null) {
            throw new IllegalArgumentException("The gun is not registered with a mesh");
        }

        if (!gunMeshes.containsKey(gunType)) {
            MeshResource gunMeshResource = gunType.getGunMeshResource();
            gunMeshes.put(gunType, new MeshBody(MeshLoader.load(gunMeshResource).getMeshes()));
        }
        
        MeshBody gunMesh = new MeshBody(gunMeshes.get(gunType));
        gunMesh.setVisible(true);

        gun.addComponent(gunMesh);
    }

}