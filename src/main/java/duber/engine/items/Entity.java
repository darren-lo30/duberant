package duber.engine.items;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Entity {
    private final Vector3f position;
    private final Quaternionf rotationQuat;  
    private final Vector3f rotationDegrees;
    
    public Entity(){
        position = new Vector3f(0, 0, 0);
        rotationQuat = new Quaternionf();
        rotationDegrees = new Vector3f();
    }

    public Vector3f getPosition(){
        return position;
    }

    public void setPosition(float x, float y, float z){
        position.x = x;
        position.y = y;
        position.z = z;
    }


    public Quaternionf getRotationQuat(){
        return rotationQuat;
    }

    public Vector3f getRotationDegrees(){
        return rotationDegrees;
    }

    public void rotate(float rotationX, float rotationY, float rotationZ){
        rotationQuat.rotateXYZ(
            (float) Math.toRadians(rotationX), 
            (float) Math.toRadians(rotationY),
            (float) Math.toRadians(rotationZ));
        
        rotationDegrees.x = (rotationDegrees.x + rotationX) % 360;
        rotationDegrees.y = (rotationDegrees.y + rotationY) % 360;
        rotationDegrees.z = (rotationDegrees.z + rotationZ) % 360;
    }

    public void movePosition(float offsetX, float offsetY, float offsetZ){
        if(offsetZ != 0){
            position.x += (float)Math.sin(Math.toRadians(rotationDegrees.y)) * -1.0f * offsetZ;
            position.z += (float)Math.cos(Math.toRadians(rotationDegrees.y)) * offsetZ;
        }
        if(offsetX != 0){
            position.x += (float)Math.sin(Math.toRadians(rotationDegrees.y - 90)) * -1.0f * offsetX;
            position.z += (float)Math.cos(Math.toRadians(rotationDegrees.y - 90)) * offsetX;
        }
        position.y += offsetY;
    }
    
}
