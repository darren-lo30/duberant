package duber.game;

import java.util.Optional;

import org.joml.Vector3f;

import duber.engine.entities.Camera;
import duber.engine.entities.RenderableEntity;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.SphereCollider;
import duber.engine.graphics.Mesh;

/**
 * Player
 */
public class Player {
    private RenderableEntity model;
    private Camera camera;
    private PlayerStats playerStats;

    public Player(Mesh[] playerMeshes) {
        model = new RenderableEntity(playerMeshes);
        model.addRigidBody();
        SphereCollider sphereCollider = new SphereCollider(model);
        model.setCollider(sphereCollider);
        sphereCollider.setUnscaledRadius(1.0f);
        model.getTransform().setScale(5.0f);

        model.getTransform().getPosition().set(0, 0, 0);

        camera = new Camera();
        playerStats = new PlayerStats();
    }

    public Camera getCamera() {
        return camera;
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public RenderableEntity getModel() {
        return model;
    }

    public void updateCamera() {
        Vector3f playerPosition = model.getTransform().getPosition();
        Vector3f playerRotation = model.getTransform().getRotation();
        camera.getTransform().getPosition().set(playerPosition);
        camera.getTransform().getPosition().add(0, 20, 50);
        camera.getTransform().getRotation().set(playerRotation);
    }

    public RigidBody getPlayerBody() {
        Optional<RigidBody> playerBody = model.getRigidBody();

        if(playerBody.isPresent()) {
            return playerBody.get();
        } else {
            throw new IllegalStateException("Player lacks a rigid body");
        }
    }   
    
    public class PlayerStats {
        private float runningSpeed = 1.3f;
        private float walkingSpeed = 1.0f;
        private int health = 100;
        private int money = 1000;
        private boolean jumping = false;
    
        public float getWalkingSpeed() {
            return walkingSpeed;
        }
    
        public float getRunningSpeed() {
            return runningSpeed;
        }
    
        public boolean isJumping() {
            return jumping;
        }
    
        public void setJumping(boolean jumping) {
            this.jumping = jumping;
        }
    
        public int getHealth() {
            return health;
        }
    
        public void setHealth(int health) {
            this.health = health;
        }
    
        public int getMoney() {
            return money;
        }
    
        public void setMoney(int money) {
            this.money = money;
        }
    }
}