package duber.game.server;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.ColliderPart;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.Transform;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.EntityFace;
import duber.engine.physics.collisions.ICollisionHandler;
import duber.engine.physics.collisions.Octree;
import duber.engine.utilities.Utils;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Player.PlayerData.MovementState;

/**
 * Handles collisions between Entities.
 * @author Darren Lo
 * @version 1.0
 */
public class DuberantCollisionHandler implements ICollisionHandler {    
    /**
     * An Octree containing all the constant entities
     */
    private Octree constantEntities;
    
    /**
     * Constructs a collision handler.
     * @param constantEntities the constant entities.
     */
    public DuberantCollisionHandler(Octree constantEntities) {
        this.constantEntities = constantEntities;    
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CollisionResponse> detectCollisions(Collider collidingCollider, Entity collidingEntity) {
        List<CollisionResponse> collisionResponses = new ArrayList<>();

        constantEntityCollisionDetection(collidingCollider, collisionResponses); 
        return collisionResponses;
    }

    /**
     * Detects collisions between a Collider and all the constant Entities.
     * @param collidingCollider the Collider that is colliding
     * @param collisionResponses the parent of the Collider
     * @return a list of collision responses
     */
    private List<CollisionResponse> constantEntityCollisionDetection(Collider collidingCollider, List<CollisionResponse> collisionResponses) {
        List<ColliderPart> colliderParts = collidingCollider.getColliderParts();
        for(ColliderPart colliderPart : colliderParts) {
            for(EntityFace entityFace: constantEntities.getIntersectingFaces(colliderPart.getBox())) {
                CollisionResponse response = colliderPart.checkCollision(entityFace);
                response.setCollidedEntityColliderPart(colliderPart);
                
                if (response.isCollides()) {
                    collisionResponses.add(response);
                }
            }          
        }

        return collisionResponses;
    }

    /**
     * {@inheritDoc} 
     */
    @Override
    public void processCollisions(Entity collidingEntity, List<CollisionResponse> collisionResponses) {
        if (collidingEntity instanceof Player) {
            resolvePlayerCollisions((Player) collidingEntity, collisionResponses);
        }
    }

    /**
     * Resolves a players collision with other Entities.
     * @param player the player to resolve
     * @param collisionResponses the collision responses between the player and other Entities.
     */
    private void resolvePlayerCollisions(Player player, List<CollisionResponse> collisionResponses) {
        for(CollisionResponse collisionResponse : collisionResponses) {
            Vector3f resultPush = new Vector3f();
            collisionResponses.forEach(response -> resultPush.add(response.getContactNormal()));   

            RigidBody playerBody = player.getComponent(RigidBody.class);
            Utils.clamp(resultPush, new Vector3f(1.0f, 1.0f, 1.0f));
            
            Vector3f snapBack = new Vector3f(resultPush);
            snapBack.y *= 0.5f;
            //Apply resultPush to the object
            player.getComponent(Transform.class).getPosition().add(snapBack);
            
            //Resolve the resultPush
            resultPush.normalize();
            if (resultPush.isFinite()) {
                float dot = resultPush.dot(playerBody.getVelocity());
                resultPush.mul(dot);
                playerBody.getVelocity().sub(resultPush);
            }

            //Resolve jumpng
            if (resultPush.isFinite() && collisionResponse.isCollides() && player.getPlayerData().isJumping() &&
                collisionResponse.getCollidedEntityColliderPart() == player.getComponent(Collider.class).getBaseCollider() &&
                collisionWithGround(collisionResponse)) {
                
                //Make player not jumping anymore
                player.getPlayerData().setMovementState(MovementState.STOP);
            }
        }
    }

    /**
     * Determines whether a collision response was the result of touching the ground.
     * @param collisionData the collision response to check
     * @return whether or not the collision was with the ground
     */
    private boolean collisionWithGround(CollisionResponse collisionData) {
        Vector3f groundNormal = new Vector3f(0, 1, 0);
        Vector3f faceNormal = collisionData.getFaceNormal();

        float cosAngleBetween = faceNormal.dot(groundNormal);
        return cosAngleBetween > 0.9;
    }    
}