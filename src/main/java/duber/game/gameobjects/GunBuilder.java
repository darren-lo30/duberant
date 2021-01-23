package duber.game.gameobjects;

import duber.engine.exceptions.LWJGLException;
import duber.engine.loaders.MeshLoader;
import duber.engine.loaders.MeshResource;

import java.util.EnumMap;
import java.util.Map;

import duber.engine.entities.components.MeshBody;

public class GunBuilder {
    private Map<GunType, MeshBody> gunMeshes;

    private static GunBuilder instance;

    public static GunBuilder getInstance() {
        if(instance == null) {
            instance = new GunBuilder();
        }
        return instance;
    }
    
    private GunBuilder() {
        gunMeshes = new EnumMap<>(GunType.class);
    }

    public Gun buildGun(GunType gunType) {
        return new Gun(gunType.getGun());
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
            gunMeshes.put(gunType, new MeshBody(MeshLoader.load(gunMeshResource).getMeshes()));
        }
        
        MeshBody gunMesh = new MeshBody(gunMeshes.get(gunType));
        gunMesh.setVisible(true);

        gun.addComponent(gunMesh);
    }

}