package duber.engine.entities.components;

import org.joml.Vector3f;

/**
 * A component that gives an Entity a rigid body.
 * @author Darren Lo
 * @version 1.0
 */
public class RigidBody extends Component {
    /** The linear velocity. */
    private final Vector3f velocity;
    /** The angular velocity. */
    private final Vector3f angularVelocity;

    /**
     * Constructs a RigidBody.
     */
    public RigidBody() {
        velocity = new Vector3f();
        angularVelocity = new Vector3f();
    }

    /**
     * Gets the linear velocity.
     * @return the linear velocity
     */
    public Vector3f getVelocity() {
        return velocity;
    }

    /**
     * Gets the angular velocity.
     * @return the angular velocity
     */
    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

}