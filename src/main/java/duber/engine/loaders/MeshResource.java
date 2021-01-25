package duber.engine.loaders;

import static org.lwjgl.assimp.Assimp.aiProcess_JoinIdenticalVertices;
import static org.lwjgl.assimp.Assimp.aiProcess_Triangulate;
import static org.lwjgl.assimp.Assimp.aiProcess_GenSmoothNormals;
import static org.lwjgl.assimp.Assimp.aiProcess_LimitBoneWeights;

/** 
 * Stores file and loading information about a mesh.
 * @author Darren Lo
 * @version 1.0
 */
public class MeshResource {
    /** The defualt flags to use to load the mesh. */
    public static final int DEFAULT_FLAGS = aiProcess_GenSmoothNormals | aiProcess_JoinIdenticalVertices | 
                                            aiProcess_Triangulate | aiProcess_LimitBoneWeights;

    /** The path to the model. */
    private String modelFile;

    /** The path to the texture directory. */
    private String textureDirectory;

    /** The flags to use while loading. */
    private int flags;

    /**
     * Constructs a MeshResource without default flags
     * @param modelFile the path to the model
     * @param textureDirectory the path to the textures directory
     */
    public MeshResource(String modelFile, String textureDirectory) {
        this(modelFile, textureDirectory, DEFAULT_FLAGS);
    }

    /**
     * Constructs a MeshResource without custom flags
     * @param modelFile the path to the model
     * @param textureDirectory the path to the textures directory
     * @param flags the loading flags to use
     */
    public MeshResource(String modelFile, String textureDirectory, int flags) {
        this.modelFile = modelFile;
        this.textureDirectory = textureDirectory;
        this.flags = flags;
    }

    /**
     * Gets the model file.
     * @return the model file
     */
    public String getModelFile() {
        return modelFile;
    }

    /**
     * Gets the texture directory.
     * @return the texture directory
     */
    public String getTextureDirectory() {
        return textureDirectory;
    }

    /**
     * Gets the flags.
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }

    @SuppressWarnings("unused")
    private MeshResource() {}
}