package duber.engine.physics.collisions.algorithms;

import org.joml.Vector3f;

import duber.engine.entities.Face;
import duber.engine.physics.collisions.Box;

public class FaceBox extends Box {
    private Face face;

    public FaceBox() {
        super();
    }

    public FaceBox(Face face) {
        super();
        fromFace(face);
    }

    public Face getFace() {
        return face;
    }

    public void fromFace(Face face) {
        this.face = face;
        resetBox();
        Vector3f[] faceVertices = face.getVertices();
        fromVertices(faceVertices);
    }
}