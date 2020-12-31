/*
package duber.engine.physics.collisions;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;


import duber.engine.entities.ConcreteEntity;

public class PairManager {
    private Map<ConcreteEntity, Set<ConcreteEntity>> collidingEntities;

    public void addEntityPair(ConcreteEntity entity1, ConcreteEntity entity2) {
        Set<ConcreteEntity> collidingEntities1 = collidingEntities.computeIfAbsent(entity1, k -> new HashSet<>());
        Set<ConcreteEntity> collidingEntities2 = collidingEntities.computeIfAbsent(entity2, k -> new HashSet<>());

        collidingEntities1.add(entity2);
        collidingEntities2.add(entity1);
    }

    public void removeEntityPair(ConcreteEntity entity1, ConcreteEntity entity2) { 
        try {
            collidingEntities.get(entity1).remove(entity2);
            collidingEntities.get(entity2).remove(entity1);
        } catch (NullPointerException npe) {
            throw new IllegalStateException("Tried removing an entity pair before it was added");
        }
    }

    public boolean isColliding(ConcreteEntity entity){
        return !collidingEntities.get(entity).isEmpty();
    }

    public Set<ConcreteEntity> getCollidingEntities(ConcreteEntity entity) {
        return collidingEntities.get(entity);
    }
    
}*/