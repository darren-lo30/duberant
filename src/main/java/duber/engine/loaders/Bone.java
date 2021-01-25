package duber.engine.loaders;

import org.joml.Matrix4f;

/**
 * A bone in an animated model
 * @author Darren Lo
 * @version 1.0
 */
public class Bone {

    /** The id of this Bone */
    private final int boneId;

    /** The name of this Bone */
    private final String boneName;

    /** The offset of this Bone */
    private Matrix4f offsetMatrix;

    /**
     * Constructs a bone
     * @param boneId the id of this Bone
     * @param boneName the name of this Bone
     * @param offsetMatrix the offset of this Bone
     */
    public Bone(int boneId, String boneName, Matrix4f offsetMatrix) {
        this.boneId = boneId;
        this.boneName = boneName;
        this.offsetMatrix = offsetMatrix;
    }

    /**
     * Gets the id of this Bone
     * @return the id of this Bone
     */
    public int getBoneId() {
        return boneId;
    }

    /**
     * Gets the name of this Bone
     * @return the name of this Bone
     */
    public String getBoneName() {
        return boneName;
    }

    /**
     * Gets the offset of this Bone.
     * @return the offset of this Bone.
     */
    public Matrix4f getOffsetMatrix() {
        return offsetMatrix;
    }

}