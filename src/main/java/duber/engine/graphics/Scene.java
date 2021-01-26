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

/**
 * A scene in a 3D world
 * @author Darren Lo
 * @version 1.0
 */
public class Scene implements Cleansable {
    /** The map between Meshes and Entities with that Mesh. */
    private final Map<Mesh, List<Entity>> meshMap;

    /** All Entities in the Scene. */
    private final Set<Entity> entities;

    /** The lighting of the Scene. */
    private SceneLighting sceneLighting;

    /** The SkyBox for the Scene. */
    private SkyBox skyBox;

    /**
     * Constructs an empty Scene.
     */
    public Scene() {
        meshMap = new HashMap<>();
        entities = new HashSet<>();
    }

    /**
     * Gets the scene lighting.
     * @return the scene lighting
     */
    public SceneLighting getSceneLighting() {
        return sceneLighting;
    }

    /**
     * Sets the scene lighting.
     * @param sceneLighting the scene lighting
     */
    public void setSceneLighting(SceneLighting sceneLighting) {
        this.sceneLighting = sceneLighting;
    }

    /**
     * Gets the sky box.
     * @return the sky box
     */
    public SkyBox getSkyBox() {
        return skyBox;
    }

    /**
     * Sets the sky box.
     * @param skyBox the sky box
     */
    public void setSkyBox(SkyBox skyBox) {
        this.skyBox = skyBox;
    }

    /**
     * Gets the map of Meshes and associated Entities.
     * @return the map of Meshes and associated Entities
     */
    public Map<Mesh, List<Entity>> getMeshMap() {
        return meshMap;
    }

    /**
     * Gets all the Entities in this Scene.
     * @return all the Entities in this Scene
     */
    public Set<Entity> getEntities() {
        return entities;
    }

    /**
     * Checks if an Entity is renderable.
     */
    private void checkRenderable(Entity entity) {
        if (!entity.hasComponent(MeshBody.class) || !entity.getComponent(MeshBody.class).isInitialized()) {
            throw new IllegalArgumentException("Mesh body for entity not valid");
        }
    }

    /**
     * Adds an Entity to the scene
     * @param renderableEntity the Entity to add to the Scene
     */
    public void addRenderableEntity(Entity renderableEntity) {
        if (renderableEntity == null){
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

    /**
     * Removes an Entity from the scene.
     * @param renderableEntity the entity to remove
     */
    public void removeRenderableEntity(Entity renderableEntity) {
        if (renderableEntity == null) {
            return;
        }

        checkRenderable(renderableEntity);

        entities.remove(renderableEntity);
        Mesh[] renderableMeshes = renderableEntity.getComponent(MeshBody.class).getMeshes();
        for(Mesh renderableMesh: renderableMeshes) {
            if (meshMap.containsKey(renderableMesh)) {
                meshMap.get(renderableMesh).remove(renderableEntity);
            }
        }
    }

    /**
     * Clears the scene
     */
    public void clear() {
        entities.clear();
        meshMap.clear();
        sceneLighting = null;
        skyBox = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        for(Mesh renderableMesh: meshMap.keySet()) {
            renderableMesh.cleanup();
        }
    }
    
}