package duber.engine.entities;

import java.util.HashMap;
import java.util.Map;

import duber.engine.entities.components.Component;
import duber.engine.entities.components.Transform;

public class Entity {
    private Map<Class<? extends Component>, Component> components;

    public Entity() {
        components = new HashMap<>();
        addComponent(new Transform());
    }
    
    public <T extends Component> boolean hasComponent(Class<T> type) {
        return components.containsKey(type);
    }

    public <T extends Component> T getComponent(Class<T> type) {
        return type.cast(components.get(type));
    }

    public <T extends Component> T addComponent(T instance) {
        instance.setEntity(this);
        components.put(instance.getClass(), instance);
        return instance;
    }

    public <T extends Component> T removeComponent(Class<T> type) {
        return type.cast(components.remove(type));
    }
}
