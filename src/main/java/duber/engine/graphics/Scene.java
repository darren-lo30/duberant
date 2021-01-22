package duber.engine.graphics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.Cleansable;
import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.entities.components.MeshBody;

public class Scene implements Cleansable {
    private final Map<Mesh, List<Entity>> meshMap;
    private final Set<Entity> entities;

    private SceneLighting sceneLighting;
    private SkyBox skyBox;

    public Scene() {
        meshMap = new HashMap<>();
        entities = new HashSet<>();
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

    public Set<Entity> getEntities() {
        return entities;
    }

    private void checkRenderable(Entity entity) {
        if(!entity.hasComponent(MeshBody.class) || !entity.getComponent(MeshBody.class).isInitialized()) {
            throw new IllegalArgumentException("Mesh body for entity not valid");
        }
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
        if(renderableEntity == null){
            return;
        }

        checkRenderable(renderableEntity);

        entities.add(renderableEntity);
        Mesh[] renderableMeshes = renderableEntity.getComponent(MeshBody.class).getMeshes();
        for(Mesh renderableMesh: renderableMeshes) {
            //Make the mesh renderable
            renderableMesh.makeRenderable();
            
            List<Entity> associatedEntities = meshMap.computeIfAbsent(renderableMesh, list -> new ArrayList<>());
            associatedEntities.add(renderableEntity);
        }
    }

    public void removeRenderableEntity(Entity renderableEntity) {
        if(renderableEntity == null) {
            return;
        }

        checkRenderable(renderableEntity);

        entities.remove(renderableEntity);
        Mesh[] renderableMeshes = renderableEntity.getComponent(MeshBody.class).getMeshes();
        for(Mesh renderableMesh: renderableMeshes) {
            if(meshMap.containsKey(renderableMesh)) {
                meshMap.get(renderableMesh).remove(renderableEntity);
            }
        }
    }

    public void clear() {
        entities.clear();
        meshMap.clear();
        sceneLighting = null;
        skyBox = null;
    }

    @Override
    public void cleanup() {
        for(Mesh renderableMesh: meshMap.keySet()) {
            renderableMesh.cleanup();
        }
    }
    
}