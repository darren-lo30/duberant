package duber.engine.entities;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import duber.engine.graphics.Transformation;
public class Camera extends Entity {
    private final Matrix4f viewMatrix;

    public Camera() {
        super();
        viewMatrix = new Matrix4f();
    }

    public void updateViewMatrix() {
        Transform cameraTransform = getTransform();
        Transformation.updateGeneralViewMatrix(cameraTransform.getPosition(), cameraTransform.getRotation(), viewMatrix);
    }

    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }

    @Override
    public Vector3f[] getVertices() {
        return new Vector3f[0];
    }

    @Override
    public Face[] getFaces() {
        return new Face[0];
    }
}