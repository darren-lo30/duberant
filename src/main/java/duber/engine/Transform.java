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

    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        if(offsetZ != 0) {
            position.x += (float)Math.sin(rotation.y()) * -1.0f * offsetZ;
            position.z += (float)Math.cos(rotation.y()) * offsetZ;
        }
        
        if(offsetX != 0) {
            position.x += (float)Math.sin(rotation.y() - Math.toRadians(90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(rotation.y() - Math.toRadians(90)) * offsetX;
        }
        position.y += offsetY;
    }

    public float getScale() {
        return scale;
    }
    
    public void setScale(float scale) {
        this.scale = scale;
    }
}