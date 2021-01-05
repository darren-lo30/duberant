package duber.game;

import java.util.HashSet;
import java.util.Set;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.physics.PhysicsWorld;
import duber.engine.physics.collisions.Octree;

public class DuberantPhysicsWorld extends PhysicsWorld {
    private static final float MIN_BOUNDS_VALUE = -10000.0f;
    private static final float MAX_BOUNDS_VALUE = 10000.0f;

    private static final Vector3f minBounds = new Vector3f(MIN_BOUNDS_VALUE, MIN_BOUNDS_VALUE, MIN_BOUNDS_VALUE); 
    private static final Vector3f maxBounds = new Vector3f(MAX_BOUNDS_VALUE, MAX_BOUNDS_VALUE, MAX_BOUNDS_VALUE);

    private final Set<Entity> dynamicEntities = new HashSet<>();
    private final Octree constantEntities = new Octree(minBounds, maxBounds);

    public DuberantPhysicsWorld() {
        super();        
        setCollisionHandler(new DuberantCollisionHandler(constantEntities, dynamicEntities));
    }

    public void addDynamicEntity(Entity entity) {
        dynamicEntities.add(entity);
    }

    public void addConstantEntity(Entity entity) {
        constantEntities.addFaces(entity.getFaces(), entity.getTransform());
    }

    @Override
    public void update() {
        dynamicEntities.forEach(entity -> updateEntityComponents(entity));

    }
}