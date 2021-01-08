package duber.game;

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