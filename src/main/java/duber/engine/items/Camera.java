package duber.engine.items;

import org.joml.Matrix4f;

import duber.engine.Transformation;

public class Camera extends Entity {
    private final Matrix4f viewMatrix;

    public Camera() {
        super();
        viewMatrix = new Matrix4f();
    }

    public void updateViewMatrix() {
        Transformation.updateGeneralViewMatrix(getPosition(), getRotationDegrees(), viewMatrix);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
}