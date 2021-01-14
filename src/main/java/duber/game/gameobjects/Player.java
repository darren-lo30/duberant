package duber.game.gameobjects;

import duber.engine.entities.Camera;
import duber.engine.entities.Entity;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.Component;
import duber.engine.entities.components.Identifier;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Vision;
import duber.engine.entities.components.RigidBody;

/**
 * Player
 */
public class Player extends Entity {
    public Player(String name, int team) {
        //Add default components to a player
        addComponent(new Collider());
        addComponent(new MeshBody());
        addComponent(new RigidBody());
        addComponent(new Vision());
        addComponent(new Identifier(name));

        if(team != 0 && team != 1) {
            throw new IllegalArgumentException("The team must either be 0 or 1 for red or blue");
        }

        PlayerData playerData = new PlayerData();
        playerData.setTeam(team);
        addComponent(playerData);

        addComponent(new WeaponsInventory());
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

    public boolean canShoot() {
        WeaponsInventory weaponsInventory = getWeaponsInventory();
        if(weaponsInventory.getEquippedGun() != null) {
            return weaponsInventory.getEquippedGun().canFire();
        }

        return false;
    }

    public void shoot() {
        Gun equippedGun = getWeaponsInventory().getEquippedGun();
        if(equippedGun != null) {
            equippedGun.fire();
        }
    }

    public void equipPrimaryGun() {
        getWeaponsInventory().equipPrimaryGun();
    }

    public void equipSecondaryGun() {
        getWeaponsInventory().equipSecondaryGun();
    }

    @SuppressWarnings("unused")
    private Player(){}
    
    public static class PlayerData extends Component {
        private int team = 0;
        private float runningSpeed = 1.3f;
        private float walkingSpeed = 0.7f;
        private float jumpingSpeed = 3.0f;
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

        public float getJumpingSpeed() {
            return jumpingSpeed;
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

    public static class WeaponsInventory extends Component {
        private PrimaryGun primaryGun;
        private SecondaryGun secondaryGun;
        private Gun equippedGun;

        public Gun getEquippedGun() {
            return equippedGun;
        }

        public void setPrimaryGun(PrimaryGun primaryGun) {
            this.primaryGun = primaryGun;
        }

        public void setSecondaryGun(SecondaryGun secondaryGun) {
            this.secondaryGun = secondaryGun;
        }

        public void equipPrimaryGun() {
            equippedGun = primaryGun;
        }

        public void equipSecondaryGun() {
            equippedGun = secondaryGun;
        }
    }
}