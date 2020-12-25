package duber.engine.items;

import duber.engine.graphics.Mesh;

import org.joml.Quaternionf;
import org.joml.Vector3f;


public class GameItem {
    private Mesh[] meshes;
    private final Vector3f position;
    private final Quaternionf rotation;  
    private float scale;

    private int textureIndex = 0;
    
    public GameItem(){
        meshes = null;
        position = new Vector3f(0, 0, 0);
        rotation = new Quaternionf();
        scale = 1.0f;
    }

    public GameItem(Mesh mesh){
        this();
        meshes = new Mesh[]{mesh};
    }

    public GameItem(Mesh[] meshes){
        this();
        this.meshes = meshes;
    }

    public Vector3f getPosition(){
        return position;
    }

    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;
    }


    public Quaternionf getRotation(){
        return rotation;
    }

    public void setRotation(Quaternionf q){
        rotation.set(q);
    }

    public float getScale(){
        return scale;
    }

    public void setScale(float scale){
        this.scale = scale;
    }

    public Mesh getMesh(){
        return meshes[0];
    }
    
    public Mesh[] getMeshes(){
        return meshes;
    }

    public void setMesh(Mesh mesh){
        meshes = new Mesh[]{mesh};
    }

    public void setMeshes(Mesh[] meshes){
        this.meshes = meshes;
    }

    public int getTextureIndex(){
        return textureIndex;
    }

    public void setTextureIndex(int textureIndex){
        this.textureIndex = textureIndex;
    }

    public void cleanup(){
        if(meshes != null){
            for(Mesh mesh: meshes){
                mesh.cleanup();
            }
        }
    }
}