package duber.engine.graphics;

import duber.engine.entities.components.Transform;

import org.joml.Matrix4f;
import org.joml.Vector3f;


/**
 * A class used to build matrices.
 * @author Darren Lo
 * @version 1.0
 */
public class MatrixTransformer {
    /**
     * Maps to world coordinates.
     */
    private final Matrix4f modelMatrix;

    /**
     * Mapes objects relative to camera.
     */
    private final Matrix4f modelViewMatrix;
    
    /**
     * Constructs a MatrixTransformer.
     */
    public MatrixTransformer() {
        modelMatrix = new Matrix4f();
        
        modelViewMatrix = new Matrix4f();        
    }

    /**
     * Updates a view matrix.
     * @param position the position of the view
     * @param rotation the rotation of the view
     * @param viewMatrix the matrix to update
     */
    public static final Matrix4f updateGeneralViewMatrix(Vector3f position, Vector3f rotation, Matrix4f viewMatrix) {
        //Rotate and translate the object relative to the camera
        return viewMatrix
            .identity()
            .rotateX(rotation.x)
            .rotateY(rotation.y)
            .translate(-position.x, -position.y, -position.z);
    }

    /**
     * Builds a model matrix.
     * @param transform the transform of the model
     * @return the model matrix
     */
    public final Matrix4f buildModelMatrix(Transform transform) {
        Vector3f position = transform.getPosition();
        Vector3f rotation = transform.getRotation();
        float scale = transform.getScale();

        return modelMatrix
            .identity()
            .translate(position)
            .rotateX(-rotation.x())
            .rotateY(-rotation.y())
            .rotateZ(-rotation.z())
            .scale(scale);
    }

    /**
     * Builds a model view matrix.
     * @param modelMatrix the model matrix
     * @param viewMatrix the view matrix
     */
    public final Matrix4f buildModelViewMatrix(Matrix4f modelMatrix, Matrix4f viewMatrix) {        
        return viewMatrix.mulAffine(modelMatrix, modelViewMatrix);
    }

    /**
     * Builds a model view matrix.
     * @param transform the transform of the model
     * @param viewMatrix the view matrix
     */
    public final Matrix4f buildModelViewMatrix(Transform transform, Matrix4f viewMatrix) {
        return buildModelViewMatrix(buildModelMatrix(transform), viewMatrix);
    }
}


