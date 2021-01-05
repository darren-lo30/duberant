package duber.engine.physics.collisions;

import org.joml.Vector3f;

public class Box {
    protected final Vector3f minXYZ;
    protected final Vector3f maxXYZ;
    private final Vector3f length;

    private static final float MIN_POS = Float.MIN_VALUE;
    private static final float MAX_POS = Float.MAX_VALUE;

    public Box() {
        minXYZ = new Vector3f();
        maxXYZ = new Vector3f();
        length = new Vector3f();
    }

    public Box(Vector3f minXYZ, Vector3f maxXYZ) {
        this.minXYZ = minXYZ;
        this.maxXYZ = maxXYZ;
        length = new Vector3f();
        calculateLength();
    }

    public Vector3f getMinXYZ() {
        return minXYZ;
    }

    public Vector3f getMaxXYZ() {
        return maxXYZ;
    }

    public void resetBox() {
        minXYZ.set(MAX_POS, MAX_POS, MAX_POS);
        maxXYZ.set(MIN_POS, MIN_POS, MIN_POS);
    }

    public void fromVertices(Vector3f[] vertices) {
        resetBox();

        for (Vector3f vertex : vertices) {
            minXYZ.set(
                Math.min(minXYZ.x(), vertex.x()), 
                Math.min(minXYZ.y(), vertex.y()),
                Math.min(minXYZ.z(), vertex.z()));

            maxXYZ.set(
                Math.max(maxXYZ.x(), vertex.x()), 
                Math.max(maxXYZ.y(), vertex.y()),
                Math.max(maxXYZ.z(), vertex.z()));
        }
        calculateLength();
    }


    protected Vector3f calculateLength() {
        return length.set(maxXYZ).sub(minXYZ).absolute();
    }

    public boolean isCompletelyInside(Box box) {
        return minXYZ.x >= box.minXYZ.x && minXYZ.x <= box.maxXYZ.x
                && maxXYZ.x >= box.minXYZ.x && maxXYZ.x <= box.maxXYZ.x
                && minXYZ.y >= box.minXYZ.y && minXYZ.y <= box.maxXYZ.y
                && maxXYZ.y >= box.minXYZ.y && maxXYZ.y <= box.maxXYZ.y
                && minXYZ.z >= box.minXYZ.z && minXYZ.z <= box.maxXYZ.z
                && maxXYZ.z >= box.minXYZ.z && maxXYZ.z <= box.maxXYZ.z;
    }

    public boolean intersects (Box box) {
        box.calculateLength();
        calculateLength();

        double xt1 = (box.length.x + length.x);
        double xt2 = Math.abs(Math.max(maxXYZ.x, box.maxXYZ.x) 
                - Math.min(minXYZ.x, box.minXYZ.x));
        
        if (xt2 > xt1) {
            return false;
        }
        
        double yt1 = (box.length.y + length.y);
        double yt2 = Math.abs(Math.max(maxXYZ.y, box.maxXYZ.y) 
                - Math.min(minXYZ.y, box.minXYZ.y));
        
        if (yt2 > yt1) {
            return false;
        }
        
        double zt1 = (box.length.z + length.z);
        double zt2 = Math.abs(Math.max(maxXYZ.z, box.maxXYZ.z) 
                - Math.min(minXYZ.z, box.minXYZ.z));
        
        return zt2 <= zt1;

    }
    
}