package duber.engine.loaders;

import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;


public class MeshResource {
    private String modelFile;
    private String textureDirectory;
    private int flags;

    public MeshResource(String modelFile, String textureDirectory) {
        this(modelFile, textureDirectory, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate);
    }

    public MeshResource(String modelFile, String textureDirectory, int flags) {
        this.modelFile = modelFile;
        this.textureDirectory = textureDirectory;
        this.flags = flags;
    }

    public String getModelFile() {
        return modelFile;
    }

    public String getTextureDirectory() {
        return textureDirectory;
    }

    public int getFlags() {
        return flags;
    }
}