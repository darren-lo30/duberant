package duber.engine.physics.collisions;

import org.joml.Vector3f;

import duber.engine.entities.Entity;

public class CollisionResponse {
    private Entity otherEntity;

    private boolean collides;
    private final Vector3f contactPoint;
    private final Vector3f contactNormal;

    public CollisionResponse() {
        collides = false;
        contactPoint = new Vector3f();
        contactNormal = new Vector3f();
    }

    public Entity getOtherEntity() {
        return otherEntity;
    }

    public void setOtherEntity(Entity otherEntity) {
        this.otherEntity = otherEntity;
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