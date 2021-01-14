package duber.game.server;

import java.util.HashSet;
import java.util.Set;

import org.joml.Vector3f;
import org.joml.RayAabIntersection;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.ColliderPart;
import duber.engine.entities.components.Transform;
import duber.engine.physics.PhysicsWorld;
import duber.engine.physics.collisions.Box;
import duber.engine.physics.collisions.Octree;
import duber.game.gameobjects.Player;

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
        constantEntities.addEntity(entity, entity.getComponent(Transform.class));
    }

    public void simulateShot(Player shootingPlayer) {
        shootingPlayer.shoot();

        Transform cameraTransform = shootingPlayer.getView().getComponent(Transform.class);
        Vector3f cameraRotation = cameraTransform.getRotation();

        //Set up bullet ray origin and direction
        Vector3f bulletOrigin = cameraTransform.getPosition();
        Vector3f bulletDirection = new Vector3f(0, 0, -1);
        bulletDirection.rotateX(-cameraRotation.x())
                       .rotateY(-cameraRotation.y());
        bulletDirection.normalize();

    
        System.out.println("Bullet origin: " + bulletOrigin);
        System.out.println("Bullet direction: " + bulletDirection);

        //Create a ray for the bullet
        RayAabIntersection bulletRay = new RayAabIntersection(
            bulletOrigin.x(), bulletOrigin.y(), bulletOrigin.z(),
            bulletDirection.x(), bulletDirection.y(), bulletDirection.z());
        
        for(Entity entity : dynamicEntities) {
            //Find all instances of players in the dynamic entities
            if(entity instanceof Player && entity != shootingPlayer) {
                Player player = (Player) entity;
                boolean hitsPlayer = false;

                //Determine if the bullet ray intersects the boxes of any of the player's colliders
                for(ColliderPart colliderPart : player.getComponent(Collider.class).getColliderParts()) {
                    Box colliderBox = colliderPart.getBox();
                    hitsPlayer |= (bulletRay.test(colliderBox.getMinXYZ().x(), colliderBox.getMinXYZ().y(), colliderBox.getMinXYZ().z(),
                        colliderBox.getMaxXYZ().x(), colliderBox.getMaxXYZ().y(), colliderBox.getMaxXYZ().z()));
                }

                if(hitsPlayer) {
                    System.out.println("player hit!");
                }
            }
        }
    }

    @Override
    public void update() {
        dynamicEntities.forEach(entity -> updateEntityPhysics(entity));
    }
}