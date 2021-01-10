package duber.engine.physics.collisions;

import duber.engine.entities.Entity;
import duber.engine.entities.Face;

public class EntityFace {
    private final Entity entity;
    private final Face face;
    
    public EntityFace(Entity entity, Face face) {
        this.entity = entity;
        this.face = face;
    }

    public Entity getEntity() {
        return entity;
    }

    public Face getFace() {
        return face;
    }
}