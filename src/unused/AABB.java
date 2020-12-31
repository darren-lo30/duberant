/*
import org.joml.Vector3f;

import duber.engine.graphics.Mesh;

public class AABB {
    private final Vector3f minXYZ;
    private final Vector3f maxXYZ;

    public AABB() {
        minXYZ = new Vector3f();
        maxXYZ = new Vector3f();
    }

    public AABB(Mesh boundedMesh) {
        this();
        float[] positions = boundedMesh.getPositions();
        for(int i = 0; i<positions.length/3; i+=3){
            minXYZ.set(
                Math.min(minXYZ.x(), positions[i]),
                Math.min(minXYZ.y(), positions[i+1]),
                Math.min(minXYZ.z(), positions[i+2]));

            maxXYZ.set(
                Math.max(maxXYZ.x(), positions[i]),
                Math.max(maxXYZ.y(), positions[i+1]),
                Math.max(maxXYZ.z(), positions[i+2]));
        }

        
        //Make it so the bounding box is never a flat plane
        if(minXYZ.x() == maxXYZ.x()){
            maxXYZ.x = minXYZ.x() + 0.1f;
        }

        if(minXYZ.y() == maxXYZ.y()){
            maxXYZ.y = minXYZ.y() + 0.1f;
        }

        if(minXYZ.z() == maxXYZ.z()){
            maxXYZ.z = minXYZ.z() + 0.1f;
        }
    }

    public void setMinEndPoints(float x, float y, float z) {
        minXYZ.set(x, y, z);
    }

    public void setMaxEndPoints(float x, float y, float z) {
        maxXYZ.set(x, y, z);
    }

    public Vector3f getMinXYZ(){
        return minXYZ;
    }

    public Vector3f getMaxXYZ(){
        return maxXYZ;
    }

}*/