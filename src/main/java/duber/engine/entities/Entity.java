package duber.engine.entities;

import java.util.Optional;

import org.joml.Vector3f;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.Transform;
import duber.engine.entities.components.Collider;

public abstract class Entity {
    private final Transform transform;
    private Optional<RigidBody> rigidBody;
    private Optional<Collider> collider;

    protected Entity() {
        transform = new Transform();
        rigidBody = Optional.ofNullable(null);
        collider = Optional.ofNullable(null);
    }

    public Transform getTransform() {
        return transform;
    }

    public Optional<RigidBody> getRigidBody() {
        return rigidBody;
    }

    public void setRigidBody(RigidBody rigidBody) {
        this.rigidBody = Optional.ofNullable(rigidBody);
    }

    public void addRigidBody() {
        rigidBody = Optional.ofNullable(new RigidBody());
    }

    public Optional<Collider> getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        this.collider = Optional.ofNullable(collider);
    }
    
    public abstract Vector3f[] getVertices();
    public abstract Face[] getFaces();
}
