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
import duber.game.gameobjects.Bullet;
import duber.game.gameobjects.Player;

/**
 * A class that manages the entities in the duberant match. This includes most of the game logic used to simulate the game.
 */
public class DuberantWorld extends PhysicsWorld {
    private static final float MIN_BOUNDS_VALUE = -10000.0f;
    private static final float MAX_BOUNDS_VALUE = 10000.0f;

    private static final Vector3f minBounds = new Vector3f(MIN_BOUNDS_VALUE, MIN_BOUNDS_VALUE, MIN_BOUNDS_VALUE); 
    private static final Vector3f maxBounds = new Vector3f(MAX_BOUNDS_VALUE, MAX_BOUNDS_VALUE, MAX_BOUNDS_VALUE);

    private final Set<Entity> dynamicEntities = new HashSet<>();
    private final Octree constantEntities = new Octree(minBounds, maxBounds);
    
    public DuberantWorld() {
        super();        
        setCollisionHandler(new DuberantCollisionHandler(constantEntities, dynamicEntities));
    }

    public void addDynamicEntity(Entity entity) {
        dynamicEntities.add(entity);
    }

    public void removeDynamicEntity(Entity entity) {
        dynamicEntities.remove(entity);
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

        //Create a ray for the bullet
        RayAabIntersection bulletRay = new RayAabIntersection(
            bulletOrigin.x(), bulletOrigin.y(), bulletOrigin.z(),
            bulletDirection.x(), bulletDirection.y(), bulletDirection.z());
        
        for(Entity entity : dynamicEntities) {
            //Find all instances of players in the dynamic entities
            if(entity instanceof Player && entity != shootingPlayer) {
                Player hitPlayer = (Player) entity;
                
                //Make sure the player is the enemy and also alive and that the bullet hits them
                if(shootingPlayer.isEnemy(hitPlayer) && hitPlayer.isAlive() && bulletHitsPlayer(hitPlayer, bulletRay)) {
                    //Make hit player take the damage
                    Bullet shotBullet = shootingPlayer.getWeaponsInventory().getEquippedGun().getGunData().getGunBullets();
                    hitPlayer.takeShot(shotBullet);                        
                    
                    //Update the player's scores if it was a kill
                    if(!hitPlayer.isAlive()) {
                        shootingPlayer.getScore().addKill();
                        hitPlayer.getScore().addDeath();
                    }
                }
            }
        }
    }

    private boolean bulletHitsPlayer(Player hitPlayer, RayAabIntersection bulletRay) {
        for(ColliderPart colliderPart : hitPlayer.getComponent(Collider.class).getColliderParts()) {
            Box colliderBox = colliderPart.getBox();

            if(bulletRay.test(colliderBox.getMinXYZ().x(), colliderBox.getMinXYZ().y(), colliderBox.getMinXYZ().z(),
               colliderBox.getMaxXYZ().x(), colliderBox.getMaxXYZ().y(), colliderBox.getMaxXYZ().z())) {
                   return true;
            }
        }

        return false;
    }

    @Override
    public void update() {
        for(Entity entity : dynamicEntities) {
            updateEntityPhysics(entity);

            if(entity instanceof Player) {
                Player player = (Player) entity;
                player.getComponent(Transform.class).limitXRotation((float) -Math.PI/2.0f, (float) Math.PI/2.0f);
            }
        }
    }
}