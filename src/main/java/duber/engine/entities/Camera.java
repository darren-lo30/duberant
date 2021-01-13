package duber.engine.entities;

import org.joml.Matrix4f;

import duber.engine.entities.components.Transform;
import duber.engine.graphics.MatrixTransformer;
public class Camera extends Entity {
    private final Matrix4f viewMatrix;

    public Camera() {
        super();
        viewMatrix = new Matrix4f();
    }

    public void updateViewMatrix() {
        Transform cameraTransform = getComponent(Transform.class);
        MatrixTransformer.updateGeneralViewMatrix(cameraTransform.getPosition(), cameraTransform.getRotation(), viewMatrix);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}