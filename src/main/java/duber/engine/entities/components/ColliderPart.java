package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.entities.Edge;
import duber.engine.physics.collisions.Box;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.EntityFace;

public abstract class ColliderPart {   
    private Collider collider;

    protected ColliderPart(){}

    protected ColliderPart(Collider collider) {
        this.collider = collider;
    }

    public Collider getCollider() {
        return collider;
    }

    public void setCollider(Collider collider) {
        this.collider = collider;
    }

    public abstract Box getBox();
    public abstract CollisionResponse checkCollision(Edge edge, Vector3f contactPoint, CollisionResponse collisionResponse);
    public abstract CollisionResponse checkCollision(EntityFace entityFace);  

}