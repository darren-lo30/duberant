package duber.engine.physics;

import java.util.Optional;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Transform;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.RigidBody;
import duber.engine.physics.collisions.ICollisionHandler;

public abstract class PhysicsWorld {
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
        velocity.y -= 0.005;
        velocity.set(0, velocity.y(), 0);
        if(velocity.y > 1.0) {
            velocity.y = 1.0f;
        } else if (velocity.y < -1.0f) {
            velocity.y = -1.0f;
        }
        angularVelocity.set(0, 0, 0);
    }
}