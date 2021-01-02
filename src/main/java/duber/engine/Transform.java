package duber.engine;

import org.joml.Vector3f;

public class Transform {
    private final Vector3f position;
    private final Vector3f rotation;  
    private float scale;
    
    public Transform() {
        position = new Vector3f();
        rotation = new Vector3f();
        scale = 1.0f;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void rotate(float rotationX, float rotationY, float rotationZ) {
        rotation.set(
            (rotation.x()  + rotationX) % (float) Math.toRadians(360.0f),
            (rotation.y()  + rotationY) % (float) Math.toRadians(360.0f),
            (rotation.z()  + rotationZ) % (float) Math.toRadians(360.0f));
    }

    public void rotateDegrees(float rotationX, float rotationY, float rotationZ) {
        rotate(
            (float) Math.toRadians(rotationX),
            (float) Math.toRadians(rotationY),
            (float) Math.toRadians(rotationZ));
    }

    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
}