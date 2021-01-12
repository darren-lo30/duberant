package duber.engine.entities.components;

import org.joml.Vector3f;
import duber.engine.entities.Entity;
import duber.engine.entities.Face;
import duber.engine.entities.Edge;
import duber.engine.physics.collisions.Box;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.EntityFace;

public class SphereCollider extends ColliderPart {
    private float unscaledRadius;
    private Vector3f colliderOffset;
    
    public SphereCollider(Entity entity, float unscaledRadius, Vector3f colliderOffset) {
        super(entity);
        this.unscaledRadius = unscaledRadius;
        this.colliderOffset = colliderOffset;
    }

    public float getRadius() {
        return unscaledRadius * getTransform().getScale();
    }

    public void setUnscaledRadius(float unscaledRadius) {
        this.unscaledRadius = unscaledRadius;
    }

    public Vector3f getColliderPosition() {
       return new Vector3f(colliderOffset)
            .mul(getTransform().getScale())
            .add(getTransform().getPosition());
    }

    @Override
    public Box getBox() {
        Vector3f position = getColliderPosition();
        float radius = getRadius();
        
        Box box = new Box();

        box.getMinXYZ().set(position)
            .add(-radius, -radius, -radius);
            
        box.getMaxXYZ().set(position)
            .add(radius, radius, radius);

        return box;
    }

    @Override
    public CollisionResponse checkCollision(Edge edge, Vector3f contactPoint, CollisionResponse response) {
        Vector3f vTmp1 = new Vector3f();
        Vector3f vTmp2 = new Vector3f();
        Vector3f vTmp3 = new Vector3f();

        vTmp1.set(contactPoint);
        vTmp1.sub(edge.getPoint1());
        float dot = vTmp1.dot(edge.getNormal());
        
        vTmp1.set(edge.getNormal());
        vTmp1.mul(-dot);
        vTmp1.add(contactPoint);
        
        // keep contact point inside edge
        vTmp3.set(edge.getPoint2());
        vTmp3.sub(edge.getPoint1());
        vTmp2.set(vTmp1);
        vTmp2.sub(edge.getPoint1());

        double dot1 = vTmp3.dot(vTmp3);
        double dot2 = vTmp3.dot(vTmp2);
        if (dot2 < 0) {
            vTmp1.set(edge.getPoint1());
        }
        else if (dot2 > dot1) {
            vTmp1.set(edge.getPoint2());
        }
        
        vTmp2.set(getColliderPosition());
        vTmp2.sub(vTmp1);
        
        response.setCollides(vTmp2.length() <= getRadius());
        response.getContactPoint().set(vTmp1);
        response.getContactNormal().set(vTmp2);
        response.getContactNormal().normalize();
        response.getContactNormal().mul(getRadius() - vTmp2.length());
        return response;
    }

    @Override
    public CollisionResponse checkCollision(EntityFace entityFace) {
        CollisionResponse response = new CollisionResponse(getEntity(), entityFace.getEntity());
        Vector3f colliderPosition = getColliderPosition();

        Face face = entityFace.getFace();
        
        Vector3f vTmp = new Vector3f();
        vTmp.set(colliderPosition); // contact point in the triangle plane
        vTmp.sub(face.getVertices()[0]);

        float dist = vTmp.dot(face.getNormal()); //Distance between sphere and plane

        vTmp.set(face.getNormal());
        vTmp.mul(-dist);
        vTmp.add(colliderPosition); //Projected spheres center onto the triangles plane

                
        for (Edge edge : face.getEdges()) {
            if (!edge.isInside(vTmp)) {
                return checkCollision(edge, vTmp, response);
            }
        }
        
        response.setCollides(Math.abs(dist) <= getRadius());
        response.getContactPoint().set(vTmp);
        response.getContactNormal().set(face.getNormal());
        response.getContactNormal().mul(getRadius() - dist);

        return response;
    }

    @SuppressWarnings("unused")
    private SphereCollider(){}
}