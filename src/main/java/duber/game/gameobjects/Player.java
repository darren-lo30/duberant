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
        Gun equippedGunIdx = getWeaponsInventory().getEquippedGun();
        if(equippedGunIdx != null) {
            equippedGunIdx.fire();
        }
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

    public void addKill() {
        getScore().setKills(getScore().getKills() + 1);
    }

    public void addDeath() {
        getScore().setDeaths(getScore().getDeaths() + 1);
    }

    @SuppressWarnings("unused")
    private Player(){}
    
    public static class PlayerData extends Component {
        public static final int DEFAULT_HEALTH = 150;
        
        private int team = 0;
        private float runningSpeed = 1.3f;
        private float walkingSpeed = 0.7f;
        private float jumpingSpeed = 3.0f;
        private int health = DEFAULT_HEALTH;
        private int money = 1000;
        private boolean jumping = false;

        public void set(PlayerData playerData) {
            team = playerData.team;
            runningSpeed = playerData.runningSpeed;
            walkingSpeed = playerData.walkingSpeed;
            jumpingSpeed = playerData.jumpingSpeed;
            health = playerData.health;
            money = playerData.money;
            jumping = playerData.jumping;
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
    
        public void addMoney(int addedMoney) {
            money += addedMoney;
        }
    }

    public static class WeaponsInventory extends Component {
        private static final int PRIMARY_GUN_IDX = 0;
        private static final int SECONDARY_GUN_IDX = 1;
        
        Gun[] guns = new Gun[2];
        private int equippedGunIdx = 0;

        public void updateData(WeaponsInventory weaponsInventory) {
            setPrimaryGun(weaponsInventory.getPrimaryGun());
            setSecondaryGun(weaponsInventory.getSecondaryGun());

            //Update equipped index
            equippedGunIdx = weaponsInventory.getEquippedGunIdx();
        }    
        
        public void clear() {
            guns[0] = null;
            guns[1] = null;
        }

        public void resetGuns() {
            if(getPrimaryGun() != null) {
                getPrimaryGun().getGunData().reset();
            }

            if(getSecondaryGun() != null) {
                getSecondaryGun().getGunData().reset();
            }
        }

        public Gun getEquippedGun() {
            return guns[equippedGunIdx];
        }

        public int getEquippedGunIdx() {
            return equippedGunIdx;
        }

        public boolean hasEquippedGun() {
            return guns[equippedGunIdx] != null;
        }

        public PrimaryGun getPrimaryGun() {
            return (PrimaryGun) guns[PRIMARY_GUN_IDX];
        }

        public void setPrimaryGun(PrimaryGun primaryGun) {
            guns[PRIMARY_GUN_IDX] = primaryGun;
        }

        public SecondaryGun getSecondaryGun() {
            return (SecondaryGun) guns[SECONDARY_GUN_IDX];
        }

        public void setSecondaryGun(SecondaryGun secondaryGun) {
            guns[SECONDARY_GUN_IDX] = secondaryGun;
        }

        public void equipPrimaryGun() {
            equippedGunIdx = PRIMARY_GUN_IDX;
        }

        public void equipSecondaryGun() {
            equippedGunIdx = SECONDARY_GUN_IDX;
        }
    }
}