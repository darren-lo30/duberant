package duber.engine.physics;

import java.util.Optional;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Transform;
import duber.engine.entities.components.Collider;
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
        if(entity.getCollider().isPresent()) {
            updateCollider(entity);            
        }
        
        if(entity.getRigidBody().isPresent()) {
            updateRigidBody(entity);
        }        
    }

    private void updateCollider(Entity entity) {
        Optional<Collider> collider = entity.getCollider();
        if(!collider.isPresent()) {
            throw new IllegalArgumentException("Tried updating collider of entity without one");
        }
        collisionHandler.handleCollisions(entity);
    }

    private void updateRigidBody(Entity entity) {
        Transform transform = entity.getTransform();

        Optional<RigidBody> rigidBody = entity.getRigidBody();
        if(!rigidBody.isPresent()) {
            throw new IllegalArgumentException("Tried updating rigid body of entity without one");
        }
        
        Vector3f velocity = rigidBody.get().getVelocity();
        Vector3f angularVelocity = rigidBody.get().getAngularVelocity();
        
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