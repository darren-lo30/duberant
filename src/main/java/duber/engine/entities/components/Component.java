package duber.engine.entities.components;

import duber.engine.entities.Entity;

/**
 * A component of an Entity.
 * @author Darren Lo
 * @version 1.0
 */
public abstract class Component {
    /** The parent Entity. */
    private Entity entity;
    
    /** 
     * Constructs a Component that is not associated with an Entity. 
     */
    protected Component(){}
    
    /**
     * Constructs a Component.
     * @param entity the parent Entity
     */
    protected Component(Entity entity) {
        this.entity = entity;
    }

    /**
     * Gets the parent Entity.
     * @return the parent Entity
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Sets the parent Entity.
     * @param entity the parent Entity
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}