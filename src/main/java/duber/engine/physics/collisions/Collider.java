package duber.engine.physics.collisions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.joml.Vector3f;

import duber.engine.Transform;
import duber.engine.entities.Entity;
import duber.engine.Edge;
import duber.engine.Face;
import duber.engine.physics.RigidBody;

public abstract class Collider {   
    private Transform transform;
    private RigidBody rigidBody;

    private final List<CollisionResponse> collisionResponses = new ArrayList<>();
    private final Vector3f resultPush = new Vector3f();

    protected Collider(Transform transform, RigidBody rigidBody) {
        this.transform = transform;
        this.rigidBody = rigidBody;
    }

    protected Collider(Entity entity) {
        Optional<RigidBody> entityBody = entity.getRigidBody();
        if(!entityBody.isPresent()) {
            throw new IllegalStateException("An entity with a collider must also have a rigid body");
        }

        this.transform = entity.getTransform();
        this.rigidBody = entityBody.get();
        initFromEntity(entity);
    }

    protected Collider(Transform transform) {
        this.transform = transform;
    }
    
    public void setTransform(Transform transform) {
        this.transform = transform;
    }

    public Transform getTransform() {
        return transform;
    }

    public static boolean edgeIsInside(Vector3f contactPoint, Edge edge) {
        Vector3f inside = new Vector3f(contactPoint);
        inside.sub(edge.getPoint1());
        return inside.dot(edge.getNormal()) >= 0;        
    }

    public abstract void initFromEntity(Entity entity);
    public abstract Box getBox();
    public abstract CollisionResponse checkCollision(Edge edge, Vector3f contactPoint, CollisionResponse collisionResponse);
    public abstract CollisionResponse checkCollision(Face face);  
    
    public void update(List<Face> nearbyFaces) {
        for(Face face: nearbyFaces) {
            CollisionResponse response = checkCollision(face);
            if(response.isCollides()) {
                System.out.println("collides");
                collisionResponses.add(response);
            }
        }    
        resultPush.set(0, 0, 0);

        if(!collisionResponses.isEmpty()) {
            collisionResponses.forEach(response -> resultPush.add(response.getContactNormal()));
            transform.getPosition().add(resultPush);
    
    
            resultPush.normalize();
            float dot = resultPush.dot(rigidBody.getVelocity());
            resultPush.mul(dot);

            rigidBody.getVelocity().sub(resultPush);
        }        
    }
}