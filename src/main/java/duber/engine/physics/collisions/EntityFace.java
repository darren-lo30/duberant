package duber.engine.physics.collisions;

import duber.engine.entities.Entity;
import duber.engine.entities.Face;

/**
 * A Face that is for an Entity
 * @author Darren Lo
 * @version 1.0
 */
public class EntityFace {
    /** The reference to the Entity */
    private final Entity entity;

    /** The reference to the Face */
    private final Face face;
    
    /** 
     * Constructs an EntityFace.
     * @param entity the reference to the Entity
     * @param face the reference to the Face
     */
    public EntityFace(Entity entity, Face face) {
        this.entity = entity;
        this.face = face;
    }

    /**
     * Gets the Entity of this EntityFace.
     * @return the Entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the Face of this EntityFace.
     * @return the Face
     */
    public Face getFace() {
        return face;
    }
}