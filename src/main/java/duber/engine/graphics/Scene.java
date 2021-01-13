package duber.engine.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.Cleansable;
import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.entities.components.MeshBody;

public class Scene implements Cleansable {
    private final Map<Mesh, List<Entity>> meshMap;

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

    public Map<Mesh, List<Entity>> getMeshMap() {
        return meshMap;
    }

    public void addRenderableEntities(Entity[] renderableEntities) {
        if(renderableEntities == null) {
            return;
        }
        
        for(Entity renderableEntity: renderableEntities) {
            addRenderableEntity(renderableEntity);
        }
    }

    public void addRenderableEntity(Entity renderableEntity) {
        if(renderableEntity == null || !renderableEntity.hasComponent(MeshBody.class)) {
            return;
        }
        
        Mesh[] renderableMeshes = renderableEntity.getComponent(MeshBody.class).getMeshes();
        for(Mesh renderableMesh: renderableMeshes) {
            //Make the mesh renderable
            renderableMesh.makeRenderable();
            
            List<Entity> associatedRenderableEntities = meshMap.computeIfAbsent(renderableMesh, list -> new ArrayList<>());
            associatedRenderableEntities.add(renderableEntity);
        }
    }

    public void removeRenderableEntity(Entity renderableEntity) {
        if(renderableEntity == null || !renderableEntity.hasComponent(MeshBody.class)) {
            return;
        }

        Mesh[] renderableMeshes = renderableEntity.getComponent(MeshBody.class).getMeshes();
        for(Mesh renderableMesh: renderableMeshes) {
            if(meshMap.containsKey(renderableMesh)) {
                meshMap.get(renderableMesh).remove(renderableEntity);
            }
        }
    }

    public void clear() {
        meshMap.clear();
        sceneLighting = null;
        skyBox = null;
    }

    public void cleanup() {
        for(Mesh renderableMesh: meshMap.keySet()) {
            renderableMesh.cleanup();
        }
    }
    
}