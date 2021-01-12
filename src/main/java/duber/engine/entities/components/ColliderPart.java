package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.Edge;
import duber.engine.physics.collisions.Box;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.EntityFace;

public abstract class ColliderPart {   
    private Transform transform;
    private Entity entity;
    
    protected ColliderPart(){
        entity = null;
        transform = null;
    }

    protected ColliderPart(Entity entity) {
        this.entity = entity;
        this.transform = entity.getTransform();
    }
    
    public Transform getTransform() {
        return transform;
    }   

    public Entity getEntity() {
        return entity;
    }

    public abstract Box getBox();
    public abstract CollisionResponse checkCollision(Edge edge, Vector3f contactPoint, CollisionResponse collisionResponse);
    public abstract CollisionResponse checkCollision(EntityFace entityFace);  

}