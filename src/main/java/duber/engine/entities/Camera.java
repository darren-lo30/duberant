package duber.engine.entities;

import org.joml.Matrix4f;

import duber.engine.Transform;
import duber.engine.Transformation;

public class Camera extends Entity {
    private final Matrix4f viewMatrix;

    public Camera() {
        super();
        viewMatrix = new Matrix4f();
    }

    public void updateViewMatrix() {
        Transform cameraTransform = getTransform();
        Transformation.updateGeneralViewMatrix(cameraTransform.getPosition(), cameraTransform.getRotationDegrees(), viewMatrix);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}