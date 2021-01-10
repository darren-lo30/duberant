package duber.engine.entities;

import java.util.Optional;

import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.Transform;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.MeshBody;

public class Entity {
    private final Transform transform;
    private Optional<RigidBody> rigidBody;
    private Optional<Collider> collider;
    private transient Optional<MeshBody> meshBody;
    
    public Entity() {
        transform = new Transform();
        rigidBody = Optional.ofNullable(null);
        collider = Optional.ofNullable(null);
        meshBody = Optional.ofNullable(null);
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

    public boolean hasRigidBody() {
        return rigidBody.isPresent();
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

    public boolean hasCollider() {
        return collider.isPresent();
    }

    public Optional<MeshBody> getMeshBody() {
        return meshBody;
    }
    

    public void setMeshBody(MeshBody meshBody) {
        this.meshBody = Optional.ofNullable(meshBody);
    }

    public boolean hasMeshBody() {
        return meshBody.isPresent();
    }
}
