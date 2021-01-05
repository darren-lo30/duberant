package duber.engine.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.entities.RenderableEntity;
import duber.engine.entities.SkyBox;

public class Scene {
    private final Map<Mesh, List<RenderableEntity>> meshMap;

    private SceneLighting sceneLighting;
    private SkyBox skyBox;

    public Scene() {
        meshMap = new HashMap<>();
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
            List<RenderableEntity> associatedRenderableEntities = meshMap.computeIfAbsent(mesh, list -> new ArrayList<>());
            associatedRenderableEntities.add(renderableEntity);
        }
    }

    public void removeRenderableEntity(RenderableEntity renderableEntity) {
        Mesh[] meshes = renderableEntity.getMeshes();
        for(Mesh mesh: meshes) {
            if(meshMap.containsKey(mesh)) {
                meshMap.get(mesh).remove(renderableEntity);
            }
        }

    }

    public void cleanup() {
        for(Mesh mesh: meshMap.keySet()) {
            mesh.cleanup();
        }
    }
    
}