package duber.engine.physics;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Transform;
import duber.engine.entities.components.RigidBody;
import duber.engine.physics.collisions.ICollisionHandler;

public abstract class PhysicsWorld {
    private static final float GRAVITY = 0.02f;
    private static final float MAX_Y_SPEED = 3.0f;
    private ICollisionHandler collisionHandler;

    public abstract void update();

    public void setCollisionHandler(ICollisionHandler collisionHandler) {
        this.collisionHandler = collisionHandler;
    }
    
    public void updateEntityComponents(Entity entity) {
        if(entity.hasCollider()) {
            updateCollider(entity);            
        }
        
        if(entity.hasRigidBody()) {
            updateRigidBody(entity);
        }        
    }

    private void updateCollider(Entity entity) {
        collisionHandler.handleCollisions(entity);
    }

    private void updateRigidBody(Entity entity) {
        Transform transform = entity.getTransform();

        if(!entity.hasRigidBody()) {
            throw new IllegalArgumentException("Tried updating rigid body of entity without one");
        }

        RigidBody rigidBody = entity.getRigidBody();
        
        Vector3f velocity = rigidBody.getVelocity();
        Vector3f angularVelocity = rigidBody.getAngularVelocity();
        
        transform.getPosition().add(velocity);

        transform.rotate(angularVelocity.x(), angularVelocity.y(), angularVelocity.z());
        velocity.y -= GRAVITY;
        velocity.set(0, velocity.y(), 0);
        if(velocity.y > MAX_Y_SPEED) {
            velocity.y = MAX_Y_SPEED;
        } else if (velocity.y < -MAX_Y_SPEED) {
            velocity.y = -MAX_Y_SPEED;
        }
        angularVelocity.set(0, 0, 0);
    }
}