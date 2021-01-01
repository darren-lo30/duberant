/*
package duber.engine.physics.collisions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import duber.engine.entities.RenderableEntity;

public class PairManager {
    private Map<RenderableEntity, Set<RenderableEntity>> collidingEntities;

    public void addEntityPair(RenderableEntity entity1, RenderableEntity entity2) {
        Set<RenderableEntity> collidingEntities1 = collidingEntities.computeIfAbsent(entity1, k -> new HashSet<>());
        Set<RenderableEntity> collidingEntities2 = collidingEntities.computeIfAbsent(entity2, k -> new HashSet<>());

        collidingEntities1.add(entity2);
        collidingEntities2.add(entity1);
    }

    public void removeEntityPair(RenderableEntity entity1, RenderableEntity entity2) { 
        try {
            collidingEntities.get(entity1).remove(entity2);
            collidingEntities.get(entity2).remove(entity1);
        } catch (NullPointerException npe) {
            throw new IllegalStateException("Tried removing an entity pair before it was added");
        }
    }

    public boolean isColliding(RenderableEntity entity){
        return !collidingEntities.get(entity).isEmpty();
    }

    public Set<RenderableEntity> getCollidingEntities(RenderableEntity entity) {
        return collidingEntities.get(entity);
    }
    
}*/