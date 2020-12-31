package duber.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import duber.engine.graphics.InstancedMesh;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.entities.ConcreteEntity;
import duber.engine.entities.SkyBox;

public class Scene {
    private final Map<Mesh, List<ConcreteEntity>> meshMap;
    private final Map<InstancedMesh, List<ConcreteEntity>> instancedMeshMap;

    private SceneLighting sceneLighting;
    private SkyBox skyBox;

    private boolean shaded;

    public Scene() {
        instancedMeshMap = new HashMap<>();
        meshMap = new HashMap<>();
        
        shaded = true;
    }

    public boolean isShaded() {
        return shaded;
    }

    public void setShaded(boolean shaded) {
        this.shaded = shaded;
    }

    public SceneLighting getSceneLighting() {
        return sceneLighting;
    }

    public void setSceneLighting(SceneLighting sceneLighting) {
        this.sceneLighting = sceneLighting;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    public Map<Mesh, List<ConcreteEntity>> getMeshMap() {
        return meshMap;
    }

    public Map<InstancedMesh, List<ConcreteEntity>> getInstancedMeshMap() {
        return instancedMeshMap;
    }

    public void addConcreteEntitys(ConcreteEntity[] concreteEntities) {
        if(concreteEntities == null) {
            return;
        }
        for(ConcreteEntity concreteEntity: concreteEntities) {
            addConcreteEntity(concreteEntity);
        }
    }

    public void addConcreteEntity(ConcreteEntity concreteEntity) {
        if(concreteEntity == null) {
            return;
        }

        Mesh[] meshes = concreteEntity.getMeshes();
        for(Mesh mesh: meshes) {
            List<ConcreteEntity> associatedConcreteEntitys;
            if(mesh instanceof InstancedMesh) {
                associatedConcreteEntitys = instancedMeshMap.computeIfAbsent((InstancedMesh) mesh, list -> new ArrayList<>());
            } else {
                associatedConcreteEntitys = meshMap.computeIfAbsent(mesh, list -> new ArrayList<>());
            }
            associatedConcreteEntitys.add(concreteEntity);
        }
    }

    public void cleanup() {
        for(Mesh mesh: meshMap.keySet()) {
            mesh.cleanup();
        }
    }
    
}