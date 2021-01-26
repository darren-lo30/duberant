package duber.engine.entities;

import org.joml.Matrix4f;

import duber.engine.entities.components.Transform;
import duber.engine.graphics.MatrixTransformer;

/**
 * A camera that the 3D world is rendered from.
 * @author Darren Lo
 * @version 1.0
 */
public class Camera extends Entity {
    /** This Camera's view matrix. */
    private final Matrix4f viewMatrix;

    /**
     * Constructs a Camera.
     */
    public Camera() {
        super();
        viewMatrix = new Matrix4f();
    }

    /**
     * Updates this Camera's view matrix.
     */
    public void updateViewMatrix() {
        Transform cameraTransform = getComponent(Transform.class);
        MatrixTransformer.updateGeneralViewMatrix(cameraTransform.getPosition(), cameraTransform.getRotation(), viewMatrix);
    }

    /**
     * Gets this Camera's view matrix.
     * @return the view matrix
     */
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}