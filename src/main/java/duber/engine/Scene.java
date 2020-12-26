package duber.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import duber.engine.graphics.InstancedMesh;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.items.GameItem;
import duber.engine.items.SkyBox;

public class Scene {
    private final Map<Mesh, List<GameItem>> meshMap;
    private final Map<InstancedMesh, List<GameItem>> instancedMeshMap;

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

    public Map<Mesh, List<GameItem>> getMeshMap() {
        return meshMap;
    }

    public Map<InstancedMesh, List<GameItem>> getInstancedMeshMap() {
        return instancedMeshMap;
    }

    public void addGameItems(GameItem[] gameItems) {
        if(gameItems == null) {
            return;
        }
        for(GameItem gameItem: gameItems) {
            addGameItem(gameItem);
        }
    }

    public void addGameItem(GameItem gameItem) {
        if(gameItem == null) {
            return;
        }

        Mesh[] meshes = gameItem.getMeshes();
        for(Mesh mesh: meshes) {
            List<GameItem> associatedGameItems;
            if(mesh instanceof InstancedMesh) {
                associatedGameItems = instancedMeshMap.computeIfAbsent((InstancedMesh) mesh, list -> new ArrayList<>());
            } else {
                associatedGameItems = meshMap.computeIfAbsent(mesh, list -> new ArrayList<>());
            }
            associatedGameItems.add(gameItem);
        }
    }

    public void cleanup() {
        for(Mesh mesh: meshMap.keySet()) {
            mesh.cleanup();
        }
    }
    
}