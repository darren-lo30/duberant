package duber.engine.items;

import duber.engine.graphics.Mesh;


public class GameItem extends Entity {
    private Mesh[] meshes;
    private float scale;

    private int textureIndex = 0;
    
    public GameItem() {
        meshes = null;
        scale = 1.0f;
    }

    public GameItem(Mesh mesh) {
        this();
        meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes) {
        this();
        this.meshes = meshes;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Mesh getMesh() {
        return meshes[0];
    }
    
    public Mesh[] getMeshes() {
        return meshes;
    }

    public void setMesh(Mesh mesh) {
        meshes = new Mesh[]{mesh};
    }

    public void setMeshes(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public int getTextureIndex() {
        return textureIndex;
    }

    public void setTextureIndex(int textureIndex) {
        this.textureIndex = textureIndex;
    }

    public void cleanup() {
        if(meshes != null) {
            for(Mesh mesh: meshes) {
                mesh.cleanup();
            }
        }
    }
}