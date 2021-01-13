package duber.engine.physics.collisions;

import java.util.List;
import duber.engine.entities.Entity;
import duber.engine.entities.components.Collider;

public interface ICollisionHandler {
    public default void handleCollisions(Collider collidingCollider, Entity collidingEntity) {
        List<CollisionResponse> collisions = detectCollisions(collidingCollider, collidingEntity);
        processCollisions(collidingEntity, collisions);
    }

    public abstract List<CollisionResponse> detectCollisions(Collider collidingCollider, Entity collidingEntity);
    public abstract void processCollisions(Entity collidingEntity, List<CollisionResponse> collisionResponses);
}