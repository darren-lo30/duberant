package duber.engine.physics.collisions;

import org.joml.Vector3f;

/**
 * A 3D box.
 * @author Darren Lo
 * @version 1.0
 */
public class Box {
    /** The min bounds. */
    protected final Vector3f minXYZ;

    /** The max bounds. */
    protected final Vector3f maxXYZ;

    /** The smallest possible bounds for the box in 1D. */
    private static final float MIN_POS = Float.MIN_VALUE;
    
    /** The largest possible bounds for the box in 1D. */
    private static final float MAX_POS = Float.MAX_VALUE;

    /**
     * Constructs a box with maximum length and span.
     */
    public Box() {
        minXYZ = new Vector3f();
        maxXYZ = new Vector3f();
    }

    /**
     * Constructs a box with given bounds.
     * @param minXYZ the min bounds
     * @param maxXYZ the max bounds
     */
    public Box(Vector3f minXYZ, Vector3f maxXYZ) {
        this.minXYZ = minXYZ;
        this.maxXYZ = maxXYZ;
    }

    /**
     * Gets the min bounds of this Box
     * @return the min bounds
     */
    public Vector3f getMinXYZ() {
        return minXYZ;
    }

    /** 
     * Gets the max bounds of this Box
     * @return the max bounds
     */
    public Vector3f getMaxXYZ() {
        return maxXYZ;
    }

    /**
     * Resets this box to the largest span.
     */
    public void resetBox() {
        minXYZ.set(MAX_POS, MAX_POS, MAX_POS);
        maxXYZ.set(MIN_POS, MIN_POS, MIN_POS);
    }

    /**
     * Builds a box around an array of vertices.
     * @param vertices the vertices to build around
     */
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


    /**
     * Calculates the length of this Box
     * @return the length of this Box.
     */
    protected Vector3f calculateLength() {
        return new Vector3f().set(maxXYZ).sub(minXYZ).absolute();
    }

    /**
     * Determines if another Box is completely inside this Box.
     * @param box the other Box
     * @return if the other Box is completely inside
     */
    public boolean isCompletelyInside(Box box) {
        return minXYZ.x >= box.minXYZ.x && minXYZ.x <= box.maxXYZ.x
                && maxXYZ.x >= box.minXYZ.x && maxXYZ.x <= box.maxXYZ.x
                && minXYZ.y >= box.minXYZ.y && minXYZ.y <= box.maxXYZ.y
                && maxXYZ.y >= box.minXYZ.y && maxXYZ.y <= box.maxXYZ.y
                && minXYZ.z >= box.minXYZ.z && minXYZ.z <= box.maxXYZ.z
                && maxXYZ.z >= box.minXYZ.z && maxXYZ.z <= box.maxXYZ.z;
    }

    /**
     * Determines if another Box intersects this Box.
     * @param box the other Box
     * @return if the other Box intersects this Box
     */
    public boolean intersects (Box box) {
        Vector3f length = calculateLength();
        Vector3f otherBoxLength = box.calculateLength();

        double xt1 = (otherBoxLength.x + length.x);
        double xt2 = Math.abs(Math.max(maxXYZ.x, box.maxXYZ.x) 
                - Math.min(minXYZ.x, box.minXYZ.x));
        
        if (xt2 > xt1) {
            return false;
        }
        
        double yt1 = (otherBoxLength.y + length.y);
        double yt2 = Math.abs(Math.max(maxXYZ.y, box.maxXYZ.y) 
                - Math.min(minXYZ.y, box.minXYZ.y));
        
        if (yt2 > yt1) {
            return false;
        }
        
        double zt1 = (otherBoxLength.z + length.z);
        double zt2 = Math.abs(Math.max(maxXYZ.z, box.maxXYZ.z) 
                - Math.min(minXYZ.z, box.minXYZ.z));
        
        return zt2 <= zt1;

    }
    
}