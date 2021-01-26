package duber.engine.graphics.animations;

import java.util.Arrays;

import org.joml.Matrix4f;

/**
 * A frame inside an animation.
 * @author Darren Lo
 * @version 1.0
 */
public class AnimationFrame {
    /** The maximum number of joints. */
    public static final int MAX_JOINTS = 150;

    /** The matrices for each joint. */
    private final Matrix4f[] jointMatrices;

    /**
     * Constructs an AnimationFrame.
     */
    public AnimationFrame() {
        jointMatrices = new Matrix4f[MAX_JOINTS];
        Arrays.fill(jointMatrices, new Matrix4f());
    }

    /**
     * Gets the matrices for the joints.
     * @return the array of matrices for the joints
     */
    public Matrix4f[] getJointMatrices() {
        return jointMatrices;
    }

    /**
     * Sets the matrix for a joint.
     * @param pos the position of the matrix to set
     * @param jointMatrix the matrix to update to
     */
    public void setMatrix(int pos, Matrix4f jointMatrix) {
        jointMatrices[pos] = jointMatrix;
    }
}
