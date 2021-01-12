package duber.engine.entities;

import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.Transform;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.Component;
import duber.engine.entities.components.MeshBody;

public class Entity {
    private final Transform transform;
    private RigidBody rigidBody;
    private Collider collider;
    private transient MeshBody meshBody;
    
    public Entity() {
        transform = new Transform();
        rigidBody = null;
        collider = new Collider();
        meshBody = null;
    }

    public Transform getTransform() {
        return transform;
    }

    public RigidBody getRigidBody() {
        return rigidBody;
    }

    public void setRigidBody(RigidBody rigidBody) {
        removeComponent(rigidBody);

        rigidBody.setEntity(this);
        this.rigidBody = rigidBody;
    }

    public boolean hasRigidBody() {
        return rigidBody != null;
    }

    public void addRigidBody() {
        rigidBody = new RigidBody();
    }

    public Collider getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        removeComponent(this.collider);

        collider.setEntity(this);
        this.collider = collider;
    }

    public boolean hasCollider() {
        return collider.hasColliderParts();
    }

    public MeshBody getMeshBody() {
        return meshBody;
    }

    public void setMeshBody(MeshBody meshBody) {
        removeComponent(meshBody);

        meshBody.setEntity(this);
        this.meshBody = meshBody;
    }

    public boolean hasMeshBody() {
        return meshBody != null;
    }

    public void removeComponent(Component component) {
        if(component != null) {
            component.setEntity(null);
        }
    }
}
