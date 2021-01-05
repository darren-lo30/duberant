package duber.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.Face;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.Transform;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.ICollisionHandler;
import duber.engine.physics.collisions.algorithms.Octree;

public class DuberantCollisionHandler implements ICollisionHandler {
    private Octree constantEntities;
    private Set<Entity> dynamicEntites;

    public DuberantCollisionHandler(Octree constantEntities, Set<Entity> dynamicEntities) {
        this.constantEntities = constantEntities;    
        this.dynamicEntites = dynamicEntities;
    }

    @Override
    public List<CollisionResponse> detectCollisions(Entity collidingEntity) {
        List<CollisionResponse> collisionResponses = new ArrayList<>();
        constantEntityCollisionDetection(collidingEntity, collisionResponses); 
        
        return collisionResponses;
    }

    private List<CollisionResponse> constantEntityCollisionDetection(Entity collidingEntity, List<CollisionResponse> collisionResponses) {
        Optional<Collider> collider = collidingEntity.getCollider();
        if(!collider.isPresent()) {
            return collisionResponses;
        }

        for(Face face: constantEntities.getIntersectingFaces(collider.get().getBox())) {
            CollisionResponse response = collider.get().checkCollision(face);
            if(response.isCollides()) {
                collisionResponses.add(response);
            }
        }          
        
        return collisionResponses;
    }

    @Override
    public void processCollisions(Entity collidingEntity, List<CollisionResponse> collisionResponses) {
        Vector3f resultPush = new Vector3f();

        collisionResponses.forEach(response -> resultPush.add(response.getContactNormal()));

        Optional<RigidBody> entityBody = collidingEntity.getRigidBody();
        if(entityBody.isPresent()) {
            applyPush(collidingEntity.getTransform(), entityBody.get(), resultPush);
        }
    }

    private void applyPush(Transform transform, RigidBody rigidBody, Vector3f push) {
        transform.getPosition().add(push);
        
        push.normalize();
        if(push.isFinite()) {
            float dot = push.dot(rigidBody.getVelocity());
            push.mul(dot);
            rigidBody.getVelocity().sub(push);
        }
    }
    
}