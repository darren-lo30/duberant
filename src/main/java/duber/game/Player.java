package duber.game;

import java.util.Optional;

import org.joml.Vector3f;

import duber.engine.entities.Camera;
import duber.engine.entities.Entity;
import duber.engine.entities.components.RigidBody;

/**
 * Player
 */
public class Player {
    public static final int RED_TEAM = 0;
    public static final int BLUE_TEAM = 1;
    
    private Entity model;
    private Camera camera;
    private PlayerStats playerStats;
    private int team;

    public Player(Entity model, int team) {
        this.model = model;
        camera = new Camera();
        playerStats = new PlayerStats();
        
        if(team != 0 && team != 1) {
            throw new IllegalArgumentException("The team must either be 0 or 1 for red or blue");
        }

        this.team = team;
    }

    public Camera getCamera() {
        return camera;
    }

    public boolean isRedTeam() {
        return team == RED_TEAM;
    }

    public boolean isBlueTeam() {
        return team == BLUE_TEAM;
    }

    public PlayerStats getPlayerStats() {
        return playerStats;
    }

    public Entity getModel() {
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

    @SuppressWarnings("unused")
    private Player(){}
    
    public static class PlayerStats {
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