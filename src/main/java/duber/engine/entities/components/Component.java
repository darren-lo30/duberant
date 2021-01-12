package duber.engine.entities.components;

import duber.engine.entities.Entity;

public abstract class Component {
    private Entity entity;
    
    protected Component(){}
    
    protected Component(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }
}