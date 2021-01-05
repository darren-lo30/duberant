package duber.engine.physics.collisions.algorithms;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import duber.engine.entities.components.Transform;
import duber.engine.entities.Face;
import duber.engine.physics.collisions.Box;

public class Octree {
    private BoxNode root;

    public Octree(Vector3f minXYZ, Vector3f maxXYZ) {
        root = new BoxNode(minXYZ, maxXYZ);
    }

    public void addFaces(Face[] faces, Transform transform) {
        for(Face face: faces) {
            addFace(face, transform);
        }
    }

    public void addFace(Face face, Transform transform) {
        Face transformedFace = face.createTransformed(transform);
        root.addFaceBox(new FaceBox(transformedFace));
    }

    public List<Face> getIntersectingFaces(Box box) {
        List<Face> intersectingFaces = new ArrayList<>();
        root.getIntersectingFaces(box, intersectingFaces);
        return intersectingFaces;
    }
}