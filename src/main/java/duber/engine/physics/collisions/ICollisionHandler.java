package duber.engine.physics.collisions;

import java.util.List;
import duber.engine.entities.Entity;

public interface ICollisionHandler {
    public default void handleCollisions(Entity collidingEntity) {
        List<CollisionResponse> collisions = detectCollisions(collidingEntity);
        processCollisions(collidingEntity, collisions);
    }

    public abstract List<CollisionResponse> detectCollisions(Entity collidingEntity);
    public abstract void processCollisions(Entity collidingEntity, List<CollisionResponse> collisionResponses);
}