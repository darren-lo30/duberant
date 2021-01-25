package duber.engine.physics.collisions;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.ColliderPart;

/**
 * A response to a collision between two Entities
 * @author Darren Lo
 * @version 1.0
 */
public class CollisionResponse {
    /** The Entity that is colliding. */
    private final Entity collidingEntity;

    /** The Entity that is being collided with. */
    private final Entity collidedEntity;

    /** The ColliderPart of the collidingEntity. */
    private ColliderPart collidingEntityColliderPart;

    /** The ColliderPart of the collidedEntity. */
    private ColliderPart collidedEntityColliderPart;

    /** Whether or not a collision actually occured. */
    private boolean collides;

    /** The contact point of the collision. */
    private final Vector3f contactPoint;

    /** The contact normal of the collision. */
    private final Vector3f contactNormal;
    
    /** The normal of the face being collided with */
    private final Vector3f faceNormal;

    /**
     * Constructs a CollisionResponse between two Entities.
     * @param collidingEntity the Entity that is colliding
     * @param collidedEntity the Entity that is being collided with
     */
    public CollisionResponse(Entity collidingEntity, Entity collidedEntity) {
        this.collidingEntity = collidingEntity;
        this.collidedEntity = collidedEntity;

        collides = false;
        contactPoint = new Vector3f();
        contactNormal = new Vector3f();
        faceNormal = new Vector3f();
    }

    /**
     * Gets the colliding entity.
     * @return the colliding entity
     */
    public Entity getCollidingEntity() {
        return collidingEntity;
    }

    /**
     * Gets the collided entity.
     * @return the collided entity
     */
    public Entity getCollidedEntity() {
        return collidedEntity;
    }

    /**
     * Gets the ColliderPart of the colliding entity.
     * @return the ColliderPart of the colliding entity
     */
    public ColliderPart getCollidingEntityColliderPart() {
        return collidingEntityColliderPart;
    }

    /**
     * Sets the ColliderPart of the colliding entity
     * @return the ColliderPart of the colliding entity
     */
    public void setCollidingEntityColliderPart(ColliderPart colliderPart) {
        collidingEntityColliderPart = colliderPart;
    }
    
    /**
     * Gets the ColliderPart of the collided entity.
     * @return the ColliderPart of the collided entity
     */
    public ColliderPart getCollidedEntityColliderPart() {
        return collidedEntityColliderPart;
    }

    /**
     * Sets the ColliderPart of the collided entity
     * @return the ColliderPart of the collided entity
     */
    public void setCollidedEntityColliderPart(ColliderPart colliderPart) {
        collidedEntityColliderPart = colliderPart;
    }

    /**
     * Gets if a collision occured.
     * @return whether or not a collision occured
     */
    public boolean isCollides() {
        return collides;
    }

    /**
     * Sets if a collision occured.
     * @param collides if a collision occured
     */
    public void setCollides(boolean collides) { 
        this.collides = collides;
    }

    /**
     * Gets the contact point of the collision.
     * @return the contact point of the collision
     */
    public Vector3f getContactPoint() {
        return contactPoint;
    }

    /**
     * Gets the contact normal of the collision.
     * @return the contact normal of the collision.
     */
    public Vector3f getContactNormal() {
        return contactNormal;
    }

    /**
     * Gets the face normal of the collsion.
     * @return the face normal of the collision
     */
    public Vector3f getFaceNormal() {
        return faceNormal;
    }
}