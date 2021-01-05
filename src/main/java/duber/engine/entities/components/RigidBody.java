package duber.engine.entities.components;

import org.joml.Vector3f;

public class RigidBody {
    private final Vector3f velocity;
    private final Vector3f angularVelocity;

    public RigidBody() {
        velocity = new Vector3f();
        angularVelocity = new Vector3f();
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }
}