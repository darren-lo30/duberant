package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.entities.Edge;
import duber.engine.physics.collisions.Box;
import duber.engine.physics.collisions.CollisionResponse;
import duber.engine.physics.collisions.EntityFace;

/**
 * A part of a Collider
 * @author Darren Lo
 * @version 1.0
 */
public abstract class ColliderPart {   
    /** The Collider that this ColliderPart is part of. */
    private Collider collider;

    /**
     * Constructs a ColliderPart without reference to Collider.
     */
    protected ColliderPart(){}

    /**
     * Constructs a ColliderPart.
     * @param collider the parent Collider
     */
    protected ColliderPart(Collider collider) {
        this.collider = collider;
    }

    /**
     * Gets the Transform of the Entity that the ColliderPart is a part of.
     * @return the Transform of the parent Entity.
     */
    protected Transform getEntityTransform() {
        return getCollider().getEntity().getComponent(Transform.class);
    }

    /**
     * Gets the parent Collider.
     * @return the parent Collider
     */
    public Collider getCollider() {
        return collider;
    }

    /**
     * Sets the parent Collider.
     * @param collider the parent Collider
     */
    public void setCollider(Collider collider) {
        this.collider = collider;
    }

    /**
     * Calculates the Box that surounds this ColliderPart.
     * @return the Box that surrounds this ColliderPart
     */
    public abstract Box getBox();

    /**
     * Creates a CollisionResponse between this ColliderPart and an Edge.
     * @param edge the collided edge
     * @param contactPoint the contact point
     * @param collisionResponse the collsiion response to write to
     */
    public abstract CollisionResponse checkCollision(Edge edge, Vector3f contactPoint, CollisionResponse collisionResponse);

    /**
     * Creates a CollisionResponse between this ColliderPart and an EntityFace.
     * @param entityFace the collided EntityFace
     */
    public abstract CollisionResponse checkCollision(EntityFace entityFace);  

}