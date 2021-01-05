package duber.engine.physics.collisions;

import org.joml.Vector3f;

import duber.engine.entities.Entity;

public class CollisionResponse {
    private final Entity collidingEntity;
    private final Entity collidedEntity;

    private boolean collides;
    private final Vector3f contactPoint;
    private final Vector3f contactNormal;

    public CollisionResponse(Entity collidingEntity, Entity collidedEntity) {
        this.collidingEntity = collidingEntity;
        this.collidedEntity = collidedEntity;

        collides = false;
        contactPoint = new Vector3f();
        contactNormal = new Vector3f();
    }

    public Entity getCollidingEntity() {
        return collidingEntity;
    }

    public Entity getCollidedEntity() {
        return collidedEntity;
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
}