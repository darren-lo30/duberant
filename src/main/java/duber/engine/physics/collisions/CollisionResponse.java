package duber.engine.physics.collisions;

import org.joml.Vector3f;

import duber.engine.entities.Entity;

public class CollisionResponse {
    private Entity collidedEntity;

    private boolean collides;
    private final Vector3f contactPoint;
    private final Vector3f contactNormal;

    public CollisionResponse(Entity collidedEntity) {
        this.collidedEntity = collidedEntity;
        collides = false;
        contactPoint = new Vector3f();
        contactNormal = new Vector3f();
    }

    public Entity getCollidedEntity() {
        return collidedEntity;
    }

    public void setCollidedEntity(Entity collidedEntity) {
        this.collidedEntity = collidedEntity;
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