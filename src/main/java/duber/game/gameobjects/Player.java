package duber.game.gameobjects;

import duber.engine.entities.Camera;
import duber.engine.entities.Entity;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.Component;
import duber.engine.entities.components.Identifier;
import duber.engine.entities.components.Named;

import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Vision;
import duber.engine.entities.components.RigidBody;

/**
 * Player
 */
public class Player extends Entity {
    public Player(int id, String name, int team) {
        //Add default components to a player
        addComponent(new Collider());
        addComponent(new MeshBody());
        addComponent(new RigidBody());
        addComponent(new Vision());
        addComponent(new Score());

        addComponent(new Identifier(id));
        addComponent(new Named(name));
        
        if(team != 0 && team != 1) {
            throw new IllegalArgumentException("The team must either be 0 or 1 for red or blue");
        }

        PlayerData playerData = new PlayerData();
        playerData.setTeam(team);
        addComponent(playerData);

        addComponent(new WeaponsInventory());
    }

    public int getId() {
        return getComponent(Identifier.class).getId();
    }

    public PlayerData getPlayerData() {
        return getComponent(PlayerData.class);
    }

    public WeaponsInventory getWeaponsInventory() {
        return getComponent(WeaponsInventory.class);
    }

    public Camera getView() {
        return getComponent(Vision.class).getCamera();
    }

    public Score getScore() {
        return getComponent(Score.class);
    }

    public boolean canShoot() {
        WeaponsInventory weaponsInventory = getWeaponsInventory();
        if(weaponsInventory.getEquippedGun() != null) {
            return weaponsInventory.getEquippedGun().canFire();
        }

        return false;
    }

    public void shoot() {
        getWeaponsInventory().getEquippedGun().fire();
    }

    public void equipPrimaryGun() {
        getWeaponsInventory().equipPrimaryGun();
    }

    public void equipSecondaryGun() {
        getWeaponsInventory().equipSecondaryGun();
    }

    public boolean isEnemy(Player player) {
        return player.getPlayerData().getTeam() != getPlayerData().getTeam();
    }

    public void takeShot(Bullet bullet) {
        int health = getPlayerData().getHealth();
        int newHealth = health - bullet.getDamage();

        getPlayerData().setHealth(newHealth);

        if(!isAlive()) {
            getComponent(MeshBody.class).setVisible(false);
            getWeaponsInventory().clear();
        }
    }

    public boolean isAlive() {
        return getPlayerData().getHealth() > 0;
    }

    public enum MovementState {
        STOP,
        RUNNING, 
        WALKING,
        JUMPING
    }

    @SuppressWarnings("unused")
    private Player(){}
    
    public static class PlayerData extends Component {
        public static final int DEFAULT_HEALTH = 150;
        
        private int team = 0;
        private MovementState playerMovement = MovementState.STOP;
        private float runningSpeed = 1.3f;
        private float walkingSpeed = 0.7f;
        private float jumpingSpeed = 3.0f;
        private int health = DEFAULT_HEALTH;
        private int money = 1000;

        public void set(PlayerData playerData) {
            team = playerData.team;
            playerMovement = playerData.playerMovement;
            runningSpeed = playerData.runningSpeed;
            walkingSpeed = playerData.walkingSpeed;
            jumpingSpeed = playerData.jumpingSpeed;
            health = playerData.health;
            money = playerData.money;
        }

        public MovementState getPlayerMovement() {
            return playerMovement;
        }

        public void setState(MovementState playerMovement) {
            this.playerMovement = playerMovement;
        }
        
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

        public float getJumpingSpeed() {
            return jumpingSpeed;
        }
    
        public boolean isJumping() {
            return playerMovement == MovementState.JUMPING;
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
    
        public void addMoney(int addedMoney) {
            money += addedMoney;
        }
    }
}