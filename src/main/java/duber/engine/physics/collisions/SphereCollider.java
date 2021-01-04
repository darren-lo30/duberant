package duber.engine.physics.collisions;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.Edge;
import duber.engine.entities.Face;

public class SphereCollider extends Collider {
    private float unscaledRadius;
    private final Box box;
    
    public SphereCollider(Entity entity) {
        super(entity);
        box = new Box();
        unscaledRadius = 0;
        initFromEntity(entity);
    }

    @Override
    protected void initFromEntity(Entity entity) {
        for(Vector3f vertex: entity.getVertices()) {
            unscaledRadius = Math.max(unscaledRadius, vertex.length());
        }
    }

    public float getRadius() {
        return unscaledRadius * getTransform().getScale();
    }

    public void setUnscaledRadius(float unscaledRadius) {
        this.unscaledRadius = unscaledRadius;
    }

    @Override
    public Box getBox() {
        Vector3f position = getTransform().getPosition();
        float radius = getRadius();
        box.getMinXYZ().set(
            position.x() - radius, 
            position.y() - radius,
            position.z() - radius);
            
        box.getMaxXYZ().set(
            position.x() + radius, 
            position.y() + radius,
            position.z() + radius);
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
        
        vTmp2.set(getTransform().getPosition());
        vTmp2.sub(vTmp1);
        
        response.setCollides(vTmp2.length() <= getRadius());
        response.getContactPoint().set(vTmp1);
        response.getContactNormal().set(vTmp2);
        response.getContactNormal().normalize();
        response.getContactNormal().mul(getRadius() - vTmp2.length());
        return response;
    }

    @Override
    public CollisionResponse checkCollision(Face face) {
        CollisionResponse response = new CollisionResponse();
        
        Vector3f vTmp = new Vector3f();
        vTmp.set(getTransform().getPosition()); // contact point in the triangle plane
        vTmp.sub(face.getVertices()[0]);

        float dot = vTmp.dot(face.getNormal());
        vTmp.set(face.getNormal());
        vTmp.mul(-dot);
        vTmp.add(getTransform().getPosition());

                
        for (Edge edge : face.getEdges()) {
            if (!edge.isInside(vTmp)) {
                return checkCollision(edge, vTmp, response);
            }
        }
        
        response.setCollides(Math.abs(dot) <= getRadius());
        response.getContactPoint().set(vTmp);
        response.getContactNormal().set(face.getNormal());
        response.getContactNormal().mul(getRadius() - dot);

        return response;
    }
}