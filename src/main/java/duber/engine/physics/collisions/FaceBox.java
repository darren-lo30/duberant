package duber.engine.physics.collisions;

import org.joml.Vector3f;

public class FaceBox extends Box {
    private EntityFace entityFace;

    public FaceBox() {
        super();
    }

    public FaceBox(EntityFace entityFace) {
        super();
        fromEntityFace(entityFace);
    }

    public EntityFace getFace() {
        return entityFace;
    }

    public void fromEntityFace(EntityFace entityFace) {
        this.entityFace = entityFace;

        resetBox();
        Vector3f[] faceVertices = entityFace.getFace().getVertices();
        fromVertices(faceVertices);
    }
}