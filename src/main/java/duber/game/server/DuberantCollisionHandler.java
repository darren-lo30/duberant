package duber.game.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.ColliderPart;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.Transform;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.EntityFace;
import duber.engine.physics.collisions.ICollisionHandler;
import duber.engine.physics.collisions.Octree;

public class DuberantCollisionHandler implements ICollisionHandler {
    private Octree constantEntities;
    private Set<Entity> dynamicEntities;
    
    public DuberantCollisionHandler(Octree constantEntities, Set<Entity> dynamicEntities) {
        this.constantEntities = constantEntities;    
        this.dynamicEntities = dynamicEntities;
    }

    @Override
    public List<CollisionResponse> detectCollisions(Entity collidingEntity) {
        List<CollisionResponse> collisionResponses = new ArrayList<>();

        constantEntityCollisionDetection(collidingEntity, collisionResponses); 
        //dynamicEntityCollisionDetection(collidingEntity, collisionResponses);
        return collisionResponses;
    }

    private List<CollisionResponse> constantEntityCollisionDetection(Entity collidingEntity, List<CollisionResponse> collisionResponses) {
        List<ColliderPart> colliderParts = collidingEntity.getCollider().getColliderParts();
        for(ColliderPart colliderPart : colliderParts) {
            for(EntityFace entityFace: constantEntities.getIntersectingFaces(colliderPart.getBox())) {
                CollisionResponse response = colliderPart.checkCollision(entityFace);
                if(response.isCollides()) {
                    collisionResponses.add(response);
                }
            }          
        }

        return collisionResponses;
    }
    
    private List<CollisionResponse> dynamicEntityCollisionDetection(Entity collidingEntity, List<CollisionResponse> collisionResponses) {
        for(Entity entity : dynamicEntities) {
            
        }

        return collisionResponses;
    }

    @Override
    public void processCollisions(Entity collidingEntity, List<CollisionResponse> collisionResponses) {
        Vector3f resultPush = new Vector3f();
        collisionResponses.forEach(response -> resultPush.add(response.getContactNormal()));

        RigidBody entityBody = collidingEntity.getRigidBody();
        if(entityBody != null) {
            applyPush(collidingEntity.getTransform(), entityBody, resultPush);
        }
    }

    private void applyPush(Transform transform, RigidBody rigidBody, Vector3f push) {
        //Apply push to the object
        transform.getPosition().add(push);
        
        //Resolve the push
        push.normalize();
        if(push.isFinite()) {
            float dot = push.dot(rigidBody.getVelocity());
            push.mul(dot);
            rigidBody.getVelocity().sub(push);
        }
    }
    
}