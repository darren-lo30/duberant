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

/**
 * A datastrcuture paritioning a 3D space into 8.
 * @author Darren Lo
 * @version 1.0
 */
public class Octree {
    /** The root node */
    private BoxNode root;

    /** Constructs an Octree with min and max bounds.
     * @param minXYZ the min bounds
     * @param maxXYZ the max bounds
     */
    public Octree(Vector3f minXYZ, Vector3f maxXYZ) {
        root = new BoxNode(minXYZ, maxXYZ);
    }

    /**
     * Adds an Entity to this Octree.
     * @param entity the Entity to add
     * @param faceTransform the Transform to be applied to the Entity's faces
     */
    public void addEntity(Entity entity, Transform faceTransform) {
        if (!entity.hasComponent(MeshBody.class)){
            throw new IllegalArgumentException("To add an entity to the octree, it must have a mesh body");
        }
        
        for(Face face: entity.getComponent(MeshBody.class).getFaces()) {
            addFace(entity, face, faceTransform);
        }
    }

    /**
     * Adds a Face to this Octree.
     * @param entity the Entity whose face it is
     * @param face the Face being added
     * @param faceTransform the transform to be applied to the Face
     */
    private void addFace(Entity entity, Face face, Transform faceTransform) {
        Face transformedFace = face.createTransformed(faceTransform);
        root.addFaceBox(new FaceBox(new EntityFace(entity, transformedFace)));
    }

    /**
     * Gets all the intersecting EntityFaces with a Box.
     * @param box the Box to check
     * @return a List of intersecting EntityFaces
     */
    public List<EntityFace> getIntersectingFaces(Box box) {
        List<EntityFace> intersectingFaces = new ArrayList<>();
        root.getIntersectingFaces(box, intersectingFaces);
        return intersectingFaces;
    }

    /**
     * Gets all the intersecting Entity faces with an Entity.
     * @param entity the Entity to check
     * @return the List of intersecting EntityFaces
     */
    public List<EntityFace> getIntersectingFaces(Entity entity) {
        Set<EntityFace> intersectingFaces = new HashSet<>();

        List<ColliderPart> entityColliderParts = entity.getComponent(Collider.class).getColliderParts();
        for(ColliderPart colliderPart : entityColliderParts) {
            root.getIntersectingFaces(colliderPart.getBox(), intersectingFaces);
        }

        return new ArrayList<>(intersectingFaces);
    }
}