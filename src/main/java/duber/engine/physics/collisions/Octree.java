package duber.engine.physics.collisions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;

import duber.engine.entities.components.Collider;
import duber.engine.entities.components.ColliderPart;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Transform;
import duber.engine.entities.Entity;
import duber.engine.entities.Face;

public class Octree {
    private BoxNode root;

    public Octree(Vector3f minXYZ, Vector3f maxXYZ) {
        root = new BoxNode(minXYZ, maxXYZ);
    }

    public void addEntity(Entity entity, Transform faceTransform) {
        if (!entity.hasComponent(MeshBody.class)){
            throw new IllegalArgumentException("To add an entity to the octree, it must have a mesh body");
        }
        
        for(Face face: entity.getComponent(MeshBody.class).getFaces()) {
            addFace(entity, face, faceTransform);
        }
    }

    private void addFace(Entity entity, Face face, Transform faceTransform) {
        Face transformedFace = face.createTransformed(faceTransform);
        root.addFaceBox(new FaceBox(new EntityFace(entity, transformedFace)));
    }

    public List<EntityFace> getIntersectingFaces(Box box) {
        List<EntityFace> intersectingFaces = new ArrayList<>();
        root.getIntersectingFaces(box, intersectingFaces);
        return intersectingFaces;
    }

    public List<EntityFace> getIntersectingFaces(Entity entity) {
        Set<EntityFace> intersectingFaces = new HashSet<>();

        List<ColliderPart> entityColliderParts = entity.getComponent(Collider.class).getColliderParts();
        for(ColliderPart colliderPart : entityColliderParts) {
            root.getIntersectingFaces(colliderPart.getBox(), intersectingFaces);
        }

        return new ArrayList<>(intersectingFaces);
    }
}