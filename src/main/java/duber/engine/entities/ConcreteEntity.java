package duber.engine.entities;

import duber.engine.graphics.Mesh;


public class ConcreteEntity extends Entity {
    private final Mesh[] meshes;
    private int textureIndex = 0;

    public ConcreteEntity(Mesh mesh) {
        this(new Mesh[]{mesh});
    }
    
    public ConcreteEntity(Mesh[] meshes) {
        this.meshes = meshes;
    }

    public Mesh getMesh() {
        return meshes[0];
    }
    
    public Mesh[] getMeshes() {
        return meshes;
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