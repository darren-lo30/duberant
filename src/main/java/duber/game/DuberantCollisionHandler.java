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
    public List<CollisionResponse> detectCollisions(Entity entity) {

        Optional<Collider> collider = entity.getCollider();
        List<CollisionResponse> collisionResponses = new ArrayList<>();
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
    public void processCollisions(Entity entity, List<CollisionResponse> collisionResponses) {
        Vector3f resultPush = new Vector3f();

        collisionResponses.forEach(response -> resultPush.add(response.getContactNormal()));

        Optional<RigidBody> entityBody = entity.getRigidBody();
        if(entityBody.isPresent()) {
            applyPush(entity.getTransform(), entityBody.get(), resultPush);
        }
    }

    private void applyPush(Transform transform, RigidBody rigidBody, Vector3f push) {
        transform.getPosition().add(push);
        
        Vector3f normalizedPush = new Vector3f(push).normalize();
        if(normalizedPush.isFinite()) {
            float dot = normalizedPush.dot(rigidBody.getVelocity());
            normalizedPush.mul(dot);
            rigidBody.getVelocity().sub(normalizedPush);
        }
    }
    
}