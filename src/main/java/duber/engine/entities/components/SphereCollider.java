package duber.engine.entities.components;

import org.joml.Vector3f;
import duber.engine.entities.Face;
import duber.engine.entities.Edge;
import duber.engine.physics.collisions.Box;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.EntityFace;

/**
 * A ColliderPart in the shape of a sphere.
 * @author Darren Lo
 * @version 1.0
 */
public class SphereCollider extends ColliderPart {
    /** The unscaled radius of the sphere. */
    private float unscaledRadius;

    /** The offset the collider has from the Entity. */
    private Vector3f colliderOffset;
    
    /**
     * Constructs a SphereCollider.
     * @param unscaledRadius the unscaled radius
     * @param colliderOffset the sphere's offset from the Entity
     */
    public SphereCollider(float unscaledRadius, Vector3f colliderOffset) {
        this.unscaledRadius = unscaledRadius;
        this.colliderOffset = colliderOffset;
    }

    /**
     * Gets the scaled radius/actual radius.
     * @return the actual radius
     */
    public float getRadius() {
        return unscaledRadius * getEntityTransform().getScale();
    }

    /**
     * Gets the scaled and shifted position of this SphereCollider.
     * @return the actual position of this SphereCollider
     */
    public Vector3f getColliderPosition() {
        return new Vector3f(colliderOffset)
            .mul(getEntityTransform().getScale())
            .add(getEntityTransform().getPosition());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Box getBox() {
        Vector3f position = getColliderPosition();
        float radius = getRadius();
        
        Box box = new Box();

        box.getMinXYZ().set(position)
            .add(-radius, -radius, -radius);
            
        box.getMaxXYZ().set(position)
            .add(radius, radius, radius);

        return box;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollisionResponse checkCollision(Edge edge, Vector3f contactPoint, CollisionResponse response) {
        Vector3f v1 = new Vector3f();
        Vector3f v2 = new Vector3f();
        Vector3f v3 = new Vector3f();

        v1.set(contactPoint);
        v1.sub(edge.getPoint1());
        float dot = v1.dot(edge.getNormal());
        
        v1.set(edge.getNormal());
        v1.mul(-dot);
        v1.add(contactPoint);
        
        // keep contact point inside edge
        v3.set(edge.getPoint2());
        v3.sub(edge.getPoint1());
        v2.set(v1);
        v2.sub(edge.getPoint1());

        double dot1 = v3.dot(v3);
        double dot2 = v3.dot(v2);
        if (dot2 < 0) {
            v1.set(edge.getPoint1());
        }
        else if (dot2 > dot1) {
            v1.set(edge.getPoint2());
        }
        
        v2.set(getColliderPosition());
        v2.sub(v1);
        
        response.setCollides(v2.length() <= getRadius());
        response.getContactPoint().set(v1);
        response.getContactNormal().set(v2);
        response.getContactNormal().normalize();
        response.getContactNormal().mul(getRadius() - v2.length());
        return response;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public CollisionResponse checkCollision(EntityFace entityFace) {
        CollisionResponse response = new CollisionResponse(getCollider().getEntity(), entityFace.getEntity());
        Vector3f colliderPosition = getColliderPosition();

        Face face = entityFace.getFace();
        
        Vector3f vec = new Vector3f();
        
        //Contact point in the triangle plane
        vec.set(colliderPosition);
        vec.sub(face.getVertices()[0]);

        //Distance between sphere and plane
        float dist = vec.dot(face.getNormal()); 

        //Project sphere onto triangle
        vec.set(face.getNormal());
        vec.mul(-dist);
        vec.add(colliderPosition);

        
        response.getFaceNormal().set(face.getNormal());
        
        for (Edge edge : face.getEdges()) {
            if (!edge.isInside(vec)) {
                return checkCollision(edge, vec, response);
            }
        }
        
        response.setCollides(Math.abs(dist) <= getRadius());
        response.getContactPoint().set(vec);
        response.getContactNormal().set(face.getNormal());
        response.getContactNormal().mul(getRadius() - dist);

        return response;
    }

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private SphereCollider(){}
}