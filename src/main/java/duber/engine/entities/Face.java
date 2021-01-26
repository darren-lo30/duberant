package duber.engine.entities;

import org.joml.Vector3f;

import duber.engine.entities.components.Transform;

/**
 * A Face in the 3D world
 * @author Darren Lo
 * @version 1.0
 */
public class Face {
    /** This Face's normal. */
    private final Vector3f normal;

    /** This Face's vertices. */
    private final Vector3f[] vertices;

    /** This Face's edges */
    private final Edge[] edges;


    /** 
     * Constructs a Face from another Face.
     * @param face the Face to copy
     */
    public Face(Face face) {
        this.normal = face.getNormal();
        this.vertices = face.getVertices();
        this.edges = face.getEdges();
    }

    /**
     * Constructs a Face from an array of vertices.
     * @param vertices the vertices
     */
    public Face(Vector3f[] vertices) {
        this.vertices = vertices;
        edges = new Edge[vertices.length];
        normal = new Vector3f();

        calculateNormal();
        calculateEdges();
    }
    
    /**
     * Gets this Face's normal.
     * @return this Face's normal
     */
    public Vector3f getNormal() {
        return normal;
    }

    /**
     * Gets this Face's edges.
     * @return this Face's edges
     */
    public Edge[] getEdges() {
        return edges;
    }

    /**
     * Gets this Face's vertices.
     * @return this Face's vertices
     */
    public Vector3f[] getVertices() {
        return vertices;
    }

    /**
     * Transforms a point in 3D space.
     * @param point the point to transform
     * @param transform the Transform to use
     * @return the transformed point
     */
    private Vector3f createTransformedPoint(Vector3f point, Transform transform) {
        Vector3f position = transform.getPosition();
        Vector3f rotation = transform.getRotation();
        Vector3f transformedPoint = new Vector3f(point);

        transformedPoint.mul(transform.getScale());
        transformedPoint.rotateX(rotation.x());
        transformedPoint.rotateY(rotation.y());
        transformedPoint.rotateZ(rotation.z());
        transformedPoint.add(position.x(), position.y(), position.z());

        return transformedPoint;
    }

    /**
     * Creates a transformed version of this Face.
     * @param transform the Transform to use
     * @return the transformed face
     */
    public Face createTransformed(Transform transform) {
        Vector3f[] transformedVertices = new Vector3f[vertices.length];
        for(int i = 0; i<vertices.length; i++){
            transformedVertices[i] = createTransformedPoint(vertices[i], transform);
        }
        return new Face(transformedVertices);
    }

    /**
     * Calculates this Face's normal.
     */
    private void calculateNormal() {
        Vector3f va = new Vector3f(vertices[1]).sub(vertices[0]);
        Vector3f vb = new Vector3f(vertices[2]).sub(vertices[0]);

        normal.set(va)
              .cross(vb)
              .normalize();
    }

    /**
     * Calcualtes this Face's edges.
     */
    private void calculateEdges() {
        for(int i = 0; i<edges.length; i++){
            int nextPointIdx = (i + 1) % edges.length;
            edges[i] = new Edge(this, vertices[i], vertices[nextPointIdx]);
        }
    }
}