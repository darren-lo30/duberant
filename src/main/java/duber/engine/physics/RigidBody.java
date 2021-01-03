package duber.engine.physics;

import org.joml.Vector3f;

import duber.engine.Transform;

public class RigidBody {
    private final Transform transform;
    private final Vector3f velocity;
    private final Vector3f angularVelocity;

    public RigidBody(Transform transform) {
        this.transform = transform;
        velocity = new Vector3f();
        angularVelocity = new Vector3f();
    }
    
    public Transform getTransform() {
        return transform;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

    public void update() {
        transform.getPosition().add(velocity);
        transform.rotate(angularVelocity.x(), angularVelocity.y(), angularVelocity.z());
        velocity.y -= 0.005;
        velocity.set(0, velocity.y(), 0);
        if(velocity.y > 1.0) {
            velocity.y = 1.0f;
        } else if (velocity.y < -1.0f) {
            velocity.y = -1.0f;
        }
        angularVelocity.set(0, 0, 0);
        
    }
}