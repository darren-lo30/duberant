package duber.engine.physics.collisions;

import java.util.List;
import duber.engine.entities.Entity;
import duber.engine.entities.components.Collider;

/**
 * An interface that defines a collision handler.
 * @author Darren Lo
 * @version 1.0
 */
public interface ICollisionHandler {
    /**
     * Handles all the collisions for an Entity.
     * @param collidingCollider the Collider to check
     * @param collidingEntity the Entity that is the parent of the Collider
     */
    public default void handleCollisions(Collider collidingCollider, Entity collidingEntity) {
        List<CollisionResponse> collisions = detectCollisions(collidingCollider, collidingEntity);
        processCollisions(collidingEntity, collisions);
    }

    /**
     * Detects collisions between Entities in the world and a Collider.
     * @param collider the Collider that is colliding
     * @param collidingEntity the parent of the Collider
     * @return a list of collision responses
     */
    public abstract List<CollisionResponse> detectCollisions(Collider collidingCollider, Entity collidingEntity);


    /**
     * Processes all the collisions for an Entity.
     * @param collidingEntity the Entity to process
     * @param collisionResponses the CollisionResponses resulting from the Entity
     */
    public abstract void processCollisions(Entity collidingEntity, List<CollisionResponse> collisionResponses);
}