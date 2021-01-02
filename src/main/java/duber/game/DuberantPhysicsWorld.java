package duber.game;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.Face;
import duber.engine.physics.IPhysicsWorld;
import duber.engine.physics.collisions.Box;
import duber.engine.physics.collisions.algorithm.Octree;

public class DuberantPhysicsWorld implements IPhysicsWorld {
    private static final float MIN_BOUNDS_VALUE = -10000.0f;
    private static final float MAX_BOUNDS_VALUE = 10000.0f;

    private static final Vector3f minBounds = new Vector3f(MIN_BOUNDS_VALUE, MIN_BOUNDS_VALUE, MIN_BOUNDS_VALUE); 
    private static final Vector3f maxBounds = new Vector3f(MAX_BOUNDS_VALUE, MAX_BOUNDS_VALUE, MAX_BOUNDS_VALUE);

    private final Set<Entity> dynamicEntites = new HashSet<>();
    private final Octree constantEntities = new Octree(minBounds, maxBounds);

    @Override
    public void update() {
        dynamicEntites.forEach(dynamicEntity -> dynamicEntity.update(this));
    }

    public List<Face> getIntersectingConstantFaces(Box box) {
        return constantEntities.getIntersectingFaces(box);
    }

    public void addDynamicEntity(Entity entity) {
        dynamicEntites.add(entity);
    }

    public void addConstantEntity(Entity entity) {
        constantEntities.addFaces(entity.getFaces(), entity.getTransform());
    }
}