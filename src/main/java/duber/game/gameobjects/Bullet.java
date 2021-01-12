package duber.game.gameobjects;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Component;

public class Bullet extends Entity {
    
    private BulletData bulletData;

    public Bullet(int damage, float speed) {
    }

    private static class BulletData extends Component {
        private int damage;
        private float speed;
    }   
}