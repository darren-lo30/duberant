package duber.game.gameobjects;

public class Bullet {
    private int damage;

    public Bullet() {
        this(0);
    }
    
    public Bullet(Bullet bullet) {
        this(bullet.getDamage());
    }
    
    public Bullet(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return damage;
    }
}