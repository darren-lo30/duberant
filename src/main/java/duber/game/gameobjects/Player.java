package duber.game.gameobjects;

import duber.engine.entities.Camera;
import duber.engine.entities.Entity;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.Component;
import duber.engine.entities.components.Identifier;
import duber.engine.entities.components.Named;

import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Vision;
import duber.game.MatchData;
import duber.engine.entities.components.RigidBody;

/**
 * A Player inside the a Duberant match.
 * @author Darren Lo
 * @version 1.0
 */
public class Player extends Entity {

    /**
     * Constructs a player with id, name, and team
     * @param id the id of the Player
     * @param name the name of the Player
     * @param team thet team that the Player is on
     */
    public Player(int id, String name, int team) {
        //Add default components to a player
        addComponent(new Collider());
        addComponent(new MeshBody());
        addComponent(new RigidBody());
        addComponent(new Vision());
        addComponent(new Score());

        addComponent(new Identifier(id));
        addComponent(new Named(name));

        PlayerData playerData = new PlayerData();
        playerData.setTeam(team);
        addComponent(playerData);

        addComponent(new WeaponsInventory());
    }

    /**
     * Gets the id.
     * @return the id
     */
    public int getId() {
        return getComponent(Identifier.class).getId();
    }

    /**
     * Gets the Player's data.
     * @return the Player's data
     */
    public PlayerData getPlayerData() {
        return getComponent(PlayerData.class);
    }

    /**
     * Gets the Player's WeaponInventory.
     * @return the Player's WeaponInventory
     */
    public WeaponsInventory getWeaponsInventory() {
        return getComponent(WeaponsInventory.class);
    }

    /**
     * Gets the Player's camera that they see from.
     * @return the Player's camera
     */
    public Camera getView() {
        return getComponent(Vision.class).getCamera();
    }

    /**
     * Gets the Player's score.
     * @return the Player's score
     */
    public Score getScore() {
        return getComponent(Score.class);
    }

    /**
     * Determines if the Player can shoot.
     * @return whether or not the Player can shoot
     */
    public boolean canShoot() {
        WeaponsInventory weaponsInventory = getWeaponsInventory();
        if (weaponsInventory.getEquippedGun() != null) {
            return weaponsInventory.getEquippedGun().canFire();
        }

        return false;
    }

    /**
     * Makes the Player shoot their equipped gun.
     */
    public void shoot() {
        getWeaponsInventory().getEquippedGun().fire();
    }

    /**
     * Makes the Player equip their primary weapon.
     */
    public void equipPrimaryGun() {
        getWeaponsInventory().equipPrimaryGun();
    }

    /**
     * Makes the Player equip their secondary weapon.
     */
    public void equipSecondaryGun() {
        getWeaponsInventory().equipSecondaryGun();
    }

    /**
     * Determines if another Player is an enemy.
     */
    public boolean isEnemy(Player player) {
        return player.getPlayerData().getTeam() != getPlayerData().getTeam();
    }

    /**
     * Simulate getting hit by a bullet that is shot by another Player.
     */
    public void takeShot(Bullet bullet) {
        int health = getPlayerData().getHealth();
        int newHealth = health - bullet.getDamage();

        getPlayerData().setHealth(newHealth);

        if (!isAlive()) {
            getComponent(MeshBody.class).setVisible(false);
            getWeaponsInventory().clear();
        }
    }

    /**
     * Determines if the Player is alive.
     * @return whether or not the Player is alive
     */
    public boolean isAlive() {
        return getPlayerData().getHealth() > 0;
    }


    /**
     * Makes the Player purchase a Gun.
     * @param gun the Gun that is purchased
     */
    public void purchaseGun(Gun gun) {
        int gunCost = gun.getComponent(Buyable.class).getCost();
        if (gunCost <= getPlayerData().getMoney()) {
            if (gun.isPrimaryGun()) {
                getWeaponsInventory().setPrimaryGun(gun);
            } else if (gun.isSecondaryGun()) {
                getWeaponsInventory().setSecondaryGun(gun);
            }

            getPlayerData().addMoney(-gunCost);
        }
    }

    /**
     * Used for Kryonet
     */
    @SuppressWarnings("unused")
    private Player(){}
    
    /**
     * A component that stores all data related to the Player.
     */
    public static class PlayerData extends Component {
        /**
         * Different states that the Player can take on while moving.
         */
        public enum MovementState {
            STOP,
            RUNNING, 
            WALKING,
            JUMPING
        }
    
        /** The default amount of health that the Player has. */
        public static final int DEFAULT_HEALTH = 150;
        
        /** The team that the Player is on. */
        private int team = 0;
        
        /** The current MovementState of the Player. */
        private MovementState movementState = MovementState.STOP;

        /** The running speed of the Player. */
        private float runningSpeed = 1.3f;

        /** The walking speed of the Player. */
        private float walkingSpeed = 0.7f;

        /** The jumping speed of the Player. */
        private float jumpingSpeed = 3.0f;

        /** The current amount of health. */
        private int health = DEFAULT_HEALTH;

        /** The current amount of money. */
        private int money = 1000;

        /**
         * Sets the PlayerData to be equal to another PlayerData.
         * @param playerData the PlayerData to copy
         */
        public void set(PlayerData playerData) {
            team = playerData.team;
            movementState = playerData.movementState;
            runningSpeed = playerData.runningSpeed;
            walkingSpeed = playerData.walkingSpeed;
            jumpingSpeed = playerData.jumpingSpeed;
            health = playerData.health;
            money = playerData.money;
        }

        /**
         * Gets the Player's current movementState.
         * @return the current movementState
         */
        public MovementState getPlayerMovement() {
            return movementState;
        }

        /**
         * Sets the Player's current movementState.
         * @param movementState the Player's movementState
         */
        public void setMovementState(MovementState movementState) {
            this.movementState = movementState;
        }
        
        /**
         * Sets the Player's team.
         * @param team the Player's team
         */
        public void setTeam(int team) {
            if (team != MatchData.RED_TEAM && team != MatchData.BLUE_TEAM) {
                throw new IllegalArgumentException("The team does not exist");
            }
            this.team = team;
        }
        
        /**
         * Gets the Player's team.
         * @return the Player's team
         */
        public int getTeam() {
            return team;
        }
    
        /**
         * Gets the Player's walking speed.
         * @return the Player's walking speed
         */
        public float getWalkingSpeed() {
            return walkingSpeed;
        }
    
        /**
         * Gets the Player's running speed.
         * @return the Player's running speed
         */
        public float getRunningSpeed() {
            return runningSpeed;
        }

        /**
         * Gets the Player's jumping speed.
         * @return the Player's jumping speed
         */
        public float getJumpingSpeed() {
            return jumpingSpeed;
        }
    
        /**
         * Determines if the Player is jumping.
         * @return if the Player is jumping
         */
        public boolean isJumping() {
            return movementState == MovementState.JUMPING;
        }
    
        /**
         * Gets the Player's heatlh
         * @return the Player's health
         */
        public int getHealth() {
            return health;
        }
    
        /**
         * Sets the Player's health.
         * @param health the Player's health
         */
        public void setHealth(int health) {
            this.health = health;
        }
    
        /**
         * Gets the Player's money.
         * @return the Player's money
         */
        public int getMoney() {
            return money;
        }
    
        /**
         * Adds money to the Player.
         * @param addedMoney the amount of money to add
         */
        public void addMoney(int addedMoney) {
            money += addedMoney;
        }
    }
}