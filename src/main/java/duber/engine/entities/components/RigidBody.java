package duber.engine.entities.components;

import org.joml.Vector3f;

public class RigidBody extends Component {
    private final Vector3f velocity;
    private final Vector3f angularVelocity;
    private boolean dynamic;

    public RigidBody() {
        velocity = new Vector3f();
        angularVelocity = new Vector3f();
        dynamic = true;
    }

    public Vector3f getVelocity() {
        return velocity;
    }

    public Vector3f getAngularVelocity() {
        return angularVelocity;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    public void setDynamic(boolean dynamic) {
        this.dynamic = dynamic;
    }
}