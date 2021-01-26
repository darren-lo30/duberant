package duber.engine.entities;

import org.joml.Vector3f;

/**
 * An edge in 3D space.
 * @author Darren Lo
 * @version 1.0
 */
public class Edge {
    /** The face that this Edge is part of */
    private final Face face;

    /** The first point of this Edge. */
    private final Vector3f point1;

    /** The second point of this Edge. */
    private final Vector3f point2;
    
    /** The length of this Edge. */
    private float length;

    /** The normal of this Edge. */
    private final Vector3f normal;

    /** 
     * Constructs an Edge.
     * @param face the Face that this Edge is part of
     * @param point1 the first point
     * @param point2 the second point
     */
    public Edge(Face face, Vector3f point1, Vector3f point2) { 
        this.face = face;
        this.point1 = point1;
        this.point2 = point2;
        
        normal = new Vector3f();

        length = new Vector3f(point2).sub(point1).length();
        normal.set(point2)
                .sub(point1)
                .cross(face.getNormal())
                .mul(-1)
                .normalize();    
    }

    /**
     * Gets the Face that this Edge is a part of.
     * @return the parent Face
     */
    public Face getFace() {
        return face;
    }

    /**
     * Gets the first point.
     * @return the first point
     */
    public Vector3f getPoint1() {
        return point1;
    }

    /**
     * Gets the second point.
     * @return the second point
     */
    public Vector3f getPoint2() {
        return point2;
    }

    /**
     * Gets the normal.
     * @return the normal
     */
    public Vector3f getNormal() {
        return normal;
    }

    /**
     * Gets the length.
     * @return the length
     */
    public float getLength() {
        return length;
    }

    /**
     * Determines if a point is inside this Edge.
     * @return whether or not the point is inside
     */
    public boolean isInside(Vector3f point) {
        Vector3f vec = new Vector3f(point);
        vec.sub(point1);
        return vec.dot(normal) >= 0;
    }
    
}