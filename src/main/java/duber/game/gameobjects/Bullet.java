package duber.game.gameobjects;

/**
 * A representation of a bullet.
 * @author Darren Lo
 * @version 1.0
 */
public class Bullet {
    /**
     * The amount of damage that the bullet deals.
     */
    private int damage;

    /**
     * Constructs a bullet with no damage.
     */
    public Bullet() {
        this(0);
    }
    
    /**
     * Constructs a copy of a bullet.
     * @param bullet the bullet to copy
     */
    public Bullet(Bullet bullet) {
        this(bullet.getDamage());
    }
    
    /**
     * Constructs a bullet with given damage.
     * @param damage the damage the bullet deals
     */
    public Bullet(int damage) {
        this.damage = damage;
    }

    /**
     * Gets the amount of damage the bullet deals.
     * @return the amount of damage that the bullet deals
     */
    public int getDamage() {
        return damage;
    }
}