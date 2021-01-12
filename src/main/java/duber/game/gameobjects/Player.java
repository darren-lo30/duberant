package duber.game.gameobjects;

import org.joml.Vector3f;

import duber.engine.entities.Camera;
import duber.engine.entities.Entity;
import duber.engine.entities.components.Component;
import duber.engine.entities.components.Transform;

/**
 * Player
 */
public class Player extends Entity {    
    private Camera camera;
    private PlayerData playerData;

    public Player(int team) {
        addRigidBody();

        camera = new Camera();
        playerData = new PlayerData();
        
        if(team != 0 && team != 1) {
            throw new IllegalArgumentException("The team must either be 0 or 1 for red or blue");
        }

        playerData.setTeam(team);
    }

    public Camera getCamera() {
        return camera;
    }

    public PlayerData getPlayerData() {
        return playerData;
    }

    public void updateCamera() {
        Vector3f playerPosition = getTransform().getPosition();
        Vector3f playerRotation = getTransform().getRotation();

        
        Transform cameraTransform = getCamera().getTransform();
        cameraTransform.getPosition().set(playerPosition);
        cameraTransform.getPosition().add(0, 30, 50);
        cameraTransform.getRotation().set(playerRotation);
    }

    @SuppressWarnings("unused")
    private Player(){}
    
    public static class PlayerData extends Component {
        private int team = 0;
        private float runningSpeed = 1.3f;
        private float walkingSpeed = 1.0f;
        private int health = 100;
        private int money = 1000;
        private boolean jumping = false;

        public void setTeam(int team) {
            this.team = team;
        }
        
        public int getTeam() {
            return team;
        }
    
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