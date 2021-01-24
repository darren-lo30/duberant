package duber.engine.entities;

import java.util.HashMap;
import java.util.Map;

import duber.engine.entities.components.Component;
import duber.engine.entities.components.Identifier;
import duber.engine.entities.components.Transform;

public class Entity {
    private Map<Class<? extends Component>, Component> components;

    public Entity() {
        components = new HashMap<>();
        addComponent(new Transform());
    }
    
    public boolean hasComponent(Class<? extends Component> type) {
        return components.containsKey(type);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        Component component = components.get(type);
        if (component == null) {
            throw new IllegalArgumentException("The entity does not have the component with class: " + type);
        }
        return type.cast(component);
    }

    public <T extends Component> T addComponent(T component) {
        component.setEntity(this);
        components.put(component.getClass(), component);
        return component;
    }

    public <T extends Component> T removeComponent(Class<T> type) {
        return type.cast(components.remove(type));
    }

    @Override
    public int hashCode() {
        if (!hasComponent(Identifier.class)) {
            return super.hashCode();
        }

        return getComponent(Identifier.class).getId();
    }

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
