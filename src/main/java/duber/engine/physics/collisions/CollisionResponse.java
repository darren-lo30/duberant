package duber.engine.physics.collisions;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.ColliderPart;

public class CollisionResponse {
    private final Entity collidingEntity;
    private final Entity collidedEntity;

    private ColliderPart collidingEntityColliderPart;
    private ColliderPart collidedEntityColliderPart;

    private boolean collides;
    private final Vector3f contactPoint;
    private final Vector3f contactNormal;
    private final Vector3f faceNormal;

    public CollisionResponse(Entity collidingEntity, Entity collidedEntity) {
        this.collidingEntity = collidingEntity;
        this.collidedEntity = collidedEntity;

        collides = false;
        contactPoint = new Vector3f();
        contactNormal = new Vector3f();
        faceNormal = new Vector3f();
    }

    public Entity getCollidingEntity() {
        return collidingEntity;
    }

    public Entity getCollidedEntity() {
        return collidedEntity;
    }

    public ColliderPart getCollidingEntityColliderPart() {
        return collidingEntityColliderPart;
    }

    public void setCollidingEntityColliderPart(ColliderPart colliderPart) {
        collidingEntityColliderPart = colliderPart;
    }
    
    public ColliderPart getCollidedEntityColliderPart() {
        return collidedEntityColliderPart;
    }

    public void setCollidedEntityColliderPart(ColliderPart colliderPart) {
        collidedEntityColliderPart = colliderPart;
    }

    public boolean isCollides() {
        return collides;
    }

    public void setCollides(boolean collides) { 
        this.collides = collides;
    }

    public Vector3f getContactPoint() {
        return contactPoint;
    }

    public Vector3f getContactNormal() {
        return contactNormal;
    }

    public Vector3f getFaceNormal() {
        return faceNormal;
    }
}