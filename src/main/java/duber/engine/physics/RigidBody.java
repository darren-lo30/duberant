package duber.engine.physics;

import org.joml.Vector3f;

import duber.engine.Transform;

public class RigidBody {
    private final Transform transform;
    private final Vector3f velocity;

    public RigidBody(Transform transform) {
        this.transform = transform;
        velocity = new Vector3f();
    }

    public Vector3f getVelocity() {
        return velocity;
    }
    
    public Transform getTransform() {
        return transform;
    }

    public void addVelocity(float x, float y, float z) {
        velocity.add(x, y, z);
    }

    public void update() {
        transform.movePosition(velocity.x(), velocity.y(), velocity.z());
        velocity.set(0, 0, 0);
    }
}