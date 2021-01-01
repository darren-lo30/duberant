package duber.engine;

import org.joml.Vector3f;

public class Edge {
    private final Face face;
    private final Vector3f point1;
    private final Vector3f point2;
    private float length;

    private final Vector3f normal;

    public Edge(Face face, Vector3f point1, Vector3f point2) { 
        this.face = face;
        this.point1 = point1;
        this.point2 = point2;
        normal = new Vector3f();
        updateLengthAndNormal();
    }

    private void updateLengthAndNormal() {
        normal.set(point2);
        normal.sub(point1);
        length = normal.length();
        normal.cross(face.getNormal());
        normal.normalize();
    }


    public Vector3f getPoint1() {
        return point1;
    }

    public Vector3f getPoint2() {
        return point2;
    }

    public Vector3f getNormal() {
        return normal;
    }

    public float getLength() {
        return length;
    }
    
}