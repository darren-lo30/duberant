package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.Edge;
import duber.engine.entities.Face;
import duber.engine.physics.collisions.Box;
import duber.engine.physics.collisions.CollisionResponse;

public abstract class Collider {   
    private Transform transform;

    protected Collider(Transform transform) {
        this.transform = transform;
    }

    protected Collider(Entity entity) {
        this.transform = entity.getTransform();
    }
    
    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public Transform getTransform() {
        return transform;
    }   

    protected abstract void initFromEntity(Entity entity);
    public abstract Box getBox();
    public abstract CollisionResponse checkCollision(Edge edge, Vector3f contactPoint, CollisionResponse collisionResponse);
    public abstract CollisionResponse checkCollision(Face face);  
}