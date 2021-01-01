package duber.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import duber.engine.graphics.InstancedMesh;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.entities.RenderableEntity;
import duber.engine.entities.SkyBox;

public class Scene {
    private final Map<Mesh, List<RenderableEntity>> meshMap;
    private final Map<InstancedMesh, List<RenderableEntity>> instancedMeshMap;

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

    public Map<Mesh, List<RenderableEntity>> getMeshMap() {
        return meshMap;
    }

    public Map<InstancedMesh, List<RenderableEntity>> getInstancedMeshMap() {
        return instancedMeshMap;
    }

    public void addRenderableEntities(RenderableEntity[] renderableEntities) {
        if(renderableEntities == null) {
            return;
        }
        for(RenderableEntity renderableEntity: renderableEntities) {
            addRenderableEntity(renderableEntity);
        }
    }

    public void addRenderableEntity(RenderableEntity renderableEntity) {
        if(renderableEntity == null) {
            return;
        }

        Mesh[] meshes = renderableEntity.getMeshes();
        for(Mesh mesh: meshes) {
            List<RenderableEntity> associatedRenderableEntities;
            if(mesh instanceof InstancedMesh) {
                associatedRenderableEntities = instancedMeshMap.computeIfAbsent((InstancedMesh) mesh, list -> new ArrayList<>());
            } else {
                associatedRenderableEntities = meshMap.computeIfAbsent(mesh, list -> new ArrayList<>());
            }
            associatedRenderableEntities.add(renderableEntity);
        }
    }

    public void cleanup() {
        for(Mesh mesh: meshMap.keySet()) {
            mesh.cleanup();
        }
    }
    
}