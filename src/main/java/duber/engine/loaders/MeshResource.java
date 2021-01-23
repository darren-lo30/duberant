package duber.engine.loaders;

import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_LimitBoneWeights;




public class MeshResource {
    public static final int DEFAULT_FLAGS = aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | 
                                            aiProcess_Triangulate | aiProcess_LimitBoneWeights;

    private String modelFile;
    private String textureDirectory;
    private int flags;

    public MeshResource(String modelFile, String textureDirectory) {
        this(modelFile, textureDirectory, DEFAULT_FLAGS);
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

    @SuppressWarnings("unused")
    private MeshResource() {}
}