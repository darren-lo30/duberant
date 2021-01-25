package duber.engine.loaders;

/**
 * A vertex weight used in animations.
 * @author Darren Lo
 * @version 1.0
 */
public class VertexWeight {
    /** The id of the Bone. */
    private int boneId;

    /** The id of the vertex. */
    private int vertexId;

    /** The weight. */
    private float weight;

    /**
     * Constructs a VertexWeight.
     * @param boneId the id of the Bone
     * @param vertexId the id of the vertex
     * @param weight the weight
     */
    public VertexWeight(int boneId, int vertexId, float weight) {
        this.boneId = boneId;
        this.vertexId = vertexId;
        this.weight = weight;
    }

    /**
     * Gets the Bone's id.
     * @return the id of the Bone
     */
    public int getBoneId() {
        return boneId;
    }

    /**
     * Gets the vertex's id.
     * @return the id of the vertex
     */
    public int getVertexId() {
        return vertexId;
    }

    /**
     * Sets the vertex's id.
     * @param vertexId the id of the vertex
     */
    public void setVertexId(int vertexId) {
        this.vertexId = vertexId;
    }

    /**
     * Gets the weight.
     * @return the weight
     */
    public float getWeight() {
        return weight;
    }


    /**
     * Sets the weight.
     * @param weight the weight
     */
    public void setWeight(float weight) {
        this.weight = weight;
    }
}