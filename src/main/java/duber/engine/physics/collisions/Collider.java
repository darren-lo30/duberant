package duber.engine.physics.collisions;

import duber.engine.Transform;
import duber.engine.entities.ConcreteEntity;
import duber.engine.graphics.Edge;
import duber.engine.graphics.Face;

public interface Collider {   
    public abstract void initFromEntity(ConcreteEntity entity);
    public abstract Box getBox(Transform transform);
    public abstract CollisionResponse checkCollision(Edge edge, CollisionResponse collisionResponse);
    public abstract CollisionResponse checkCollision(Face face);   
}