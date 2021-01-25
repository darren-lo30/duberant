package duber.engine.physics;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Transform;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.RigidBody;
import duber.engine.physics.collisions.ICollisionHandler;


/**
 * A physics world for a game.
 * @author Darren Lo
 * @version 1.0
 */
public abstract class PhysicsWorld {
    /** The gravity in the world. */
    private static final float GRAVITY = 0.25f;

    /** The maximum speed in the y-direction. */
    private static final float MAX_Y_SPEED = 3.0f;

    /** The collision handler used in the game. */
    private ICollisionHandler collisionHandler;

    /**
     * Updates the physics world.
     */
    public abstract void update();

    /**
     * Sets the collision handler being used.
     * @param collisionHandler the collsion handler being used
     */
    public void setCollisionHandler(ICollisionHandler collisionHandler) {
        this.collisionHandler = collisionHandler;
    }
    
    /**
     * Updates the physics for an Entity.
     * @param entity the Entity to update physics for
     */
    public void updateEntityPhysics(Entity entity) {
        if (entity.hasComponent(RigidBody.class)) {
            updateRigidBody(entity.getComponent(RigidBody.class), entity);
        }
        
        if (entity.hasComponent(Collider.class)) {
            updateCollider(entity.getComponent(Collider.class), entity);            
        }
    }

    /**
     * Updates the Collider of an Entity.
     * @param collider the Entity's Collider
     * @param entity the Enity
     */
    private void updateCollider(Collider collider, Entity entity) {
        collisionHandler.handleCollisions(collider, entity);
    }

    /**
     * Updates the RigidBody of an Entity.
     * @param rigidBody the Entity's RigidBody
     * @param entity the Entity
     */
    private void updateRigidBody(RigidBody rigidBody, Entity entity) {
        Transform transform = entity.getComponent(Transform.class);
        
        Vector3f velocity = rigidBody.getVelocity();
        Vector3f angularVelocity = rigidBody.getAngularVelocity();
        
        transform.getPosition().add(velocity);

        transform.rotate(angularVelocity.x(), angularVelocity.y(), angularVelocity.z());
        velocity.y -= GRAVITY;
        velocity.set(0, velocity.y(), 0);
        if (velocity.y > MAX_Y_SPEED) {
            velocity.y = MAX_Y_SPEED;
        } else if (velocity.y < -MAX_Y_SPEED) {
            velocity.y = -MAX_Y_SPEED;
        }
        angularVelocity.set(0, 0, 0);
    }
}