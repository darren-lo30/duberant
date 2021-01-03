package duber.engine.entities;

import java.util.Optional;

import org.joml.Vector3f;

import duber.engine.Transform;
import duber.engine.Face;
import duber.engine.physics.IPhysicsWorld;
import duber.engine.physics.RigidBody;
import duber.engine.physics.collisions.Collider;

public abstract class Entity {
    private final Transform transform;
    private Optional<RigidBody> rigidBody;
    private Optional<Collider> collider;

    protected Entity() {
        transform = new Transform();
        rigidBody = Optional.ofNullable(null);
        collider = Optional.ofNullable(null);
    }

    public Optional<RigidBody> getRigidBody() {
        return rigidBody;
    }

    public void setRigidBody(RigidBody rigidBody) {
        this.rigidBody = Optional.ofNullable(rigidBody);
    }

    public Optional<Collider> getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        this.collider = Optional.ofNullable(collider);
    }

    public Transform getTransform() {
        return transform;
    }


    public abstract Vector3f[] getVertices();
    public abstract Face[] getFaces();

    public void update(IPhysicsWorld physicsWorld) {
        if(collider.isPresent()) {
            collider.get().update(physicsWorld.getIntersectingConstantFaces(collider.get().getBox()));
        }
        
        if(rigidBody.isPresent()) {
            rigidBody.get().update();
        }        
    }

}
