package duber.engine.entities;

import java.util.HashMap;
import java.util.Map;

import duber.engine.entities.components.Component;
import duber.engine.entities.components.Identifier;
import duber.engine.entities.components.Transform;

/**
 * A collection of Components
 * @author Darren Lo
 * @version 1.0
 */
public class Entity {
    /** The component that this Entity has. */
    private Map<Class<? extends Component>, Component> components;

    /**
     * Constructs an Entity with only a Transform.
     */
    public Entity() {
        components = new HashMap<>();
        addComponent(new Transform());
    }
    
    /**
     * Determines if this Entity has a Component of a given class.
     * @param type the Component subclass
     * @return whether or not this Entity has the Component with the given class
     */
    public boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    /**
     * Gets the Component of a given class from this Entity.
     * @param <T> the subclass of Component to query
     * @param type the class
     * @return the Entity's Component with the given class
     */
    public <T extends Component> T getComponent(Class<T> type) {
        Component component = components.get(type);
        if (component == null) {
            throw new IllegalArgumentException("The entity does not have the component with class: " + type);
        }
        return type.cast(component);
    }

    /**
     * Adds a Component to this Entity
     * @param <T> the type of Component to add
     * @param component the Component to add
     * @return the added component
     */
    public <T extends Component> T addComponent(T component) {
        component.setEntity(this);
        components.put(component.getClass(), component);
        return component;
    }


    /**
     * Removes a Component from this Entity.
     * @param <T> the type of Component to remove
     * @param type the class
     * @return the removed Component
     */
    public <T extends Component> T removeComponent(Class<T> type) {
        return type.cast(components.remove(type));
    }

    /**
     * Uses id as hashcode if it exists. Otherwise uses Object's implementation.
     * @return the hashcode of this Entity
     */
    @Override
    public int hashCode() {
        if (!hasComponent(Identifier.class)) {
            return super.hashCode();
        }

        return getComponent(Identifier.class).getId();
    }

    /**
     * Uses id to determine equivelance if it exists. Otherwises uses Object's implementation.
     * @param obj the Object to compare to
     * @return if this Entity is equal to obj
     */
    @Override
    public boolean equals(Object obj) {
        if (!hasComponent(Identifier.class)) {
            return super.equals(obj);
        }

        if (obj == this) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (obj.getClass() != getClass()) {
            return false;
        }
        
        Entity entity = (Entity) obj;

        int thisId = getComponent(Identifier.class).getId();
        int otherId = entity.getComponent(Identifier.class).getId();
        return thisId == otherId;
    }
}
