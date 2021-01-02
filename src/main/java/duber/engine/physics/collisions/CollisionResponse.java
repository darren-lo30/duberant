package duber.engine.physics.collisions;

import org.joml.Vector3f;

public class CollisionResponse {
    private boolean collides;
    private final Vector3f contactPoint;
    private final Vector3f contactNormal;

    public CollisionResponse() {
        collides = false;
        contactPoint = new Vector3f();
        contactNormal = new Vector3f();
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