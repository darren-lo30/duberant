package duber.engine.physics.collisions;

import org.joml.Vector3f;

/**
 * A box built around an EntityFace.
 * @author Darren Lo
 * @version 1.0
 */
public class FaceBox extends Box {
    /** The EntityFace that thsi FaceBox is for. */
    private EntityFace entityFace;

    /** 
     * Constructs a FaceBox without a reference to an EntityFace. 
     */
    public FaceBox() {
        super();
    }

    /**
     * Constructs a FaceBox for an EntityFace.
     * @param entityFace the EntityFace to construct for
     */
    public FaceBox(EntityFace entityFace) {
        super();
        fromEntityFace(entityFace);
    }

    /**
     * Gets the EntityFace that this FaceBox is for.
     * @return the EntiyFace this FaceBox is for
     */
    public EntityFace getFace() {
        return entityFace;
    }

    /**
     * Constructs a FaceBox from an EntityFace.
     * @param entityFace the EntityFace to build around
     */
    public void fromEntityFace(EntityFace entityFace) {
        this.entityFace = entityFace;

        resetBox();
        Vector3f[] faceVertices = entityFace.getFace().getVertices();
        fromVertices(faceVertices);
    }
}