package duber.game.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.ColliderPart;
import duber.engine.entities.components.RigidBody;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.EntityFace;
import duber.engine.physics.collisions.ICollisionHandler;
import duber.engine.physics.collisions.Octree;
import duber.engine.utilities.Utils;
import duber.game.gameobjects.Player;

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
            if(entity != collidingEntity) {

            }
        }

        return collisionResponses;
    }

    @Override
    public void processCollisions(Entity collidingEntity, List<CollisionResponse> collisionResponses) {
        if(collidingEntity instanceof Player) {
            resolvePlayerCollisions((Player) collidingEntity, collisionResponses);
        }
    }

    private void resolvePlayerCollisions(Player player, List<CollisionResponse> collisionResponses) {
        for(CollisionResponse collisionResponse : collisionResponses) {
            Vector3f resultPush = new Vector3f();
            collisionResponses.forEach(response -> resultPush.add(response.getContactNormal()));   

            RigidBody playerBody = player.getRigidBody();
            if(playerBody != null) {
                Utils.clamp(resultPush, new Vector3f(0.5f, 1.00f, 0.5f));
        
        
                //Apply resultPush to the object
                Vector3f snapBack = new Vector3f(resultPush);
                snapBack.y *= 0.5f;
                player.getTransform().getPosition().add(snapBack);
                
                //Resolve the resultPush
                resultPush.normalize();
                if(resultPush.isFinite()) {
                    float dot = resultPush.dot(playerBody.getVelocity());
                    resultPush.mul(dot);
                    playerBody.getVelocity().sub(resultPush);
                }

            }

            if(collisionResponse.isCollides() && player.getPlayerData().isJumping()) {
                Vector3f groundNormal = new Vector3f(0, 1, 0);
                Vector3f faceNormal = collisionResponse.getFaceNormal();

                float cosAngleBetween = faceNormal.dot(groundNormal);
                if(cosAngleBetween > 0.85) {
                    player.getPlayerData().setJumping(false);
                }
            }
        }
    }

    
}