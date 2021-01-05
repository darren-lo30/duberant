package duber.engine.physics.collisions;

import java.util.List;
import duber.engine.entities.Entity;

public interface ICollisionHandler {
    public default void handleCollisions(Entity entity) {
        List<CollisionResponse> collisions = detectCollisions(entity);
        processCollisions(entity, collisions);
    }

    public abstract List<CollisionResponse> detectCollisions(Entity entity);
    public abstract void processCollisions(Entity entity, List<CollisionResponse> collisions);
}