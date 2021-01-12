package duber.game.gameobjects;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Component;

public abstract class Gun extends Entity {
    private GunData gunData;






    private static class GunData extends Component {
        private int totalBullets;
        private int remainingBullets;
        private float reloadTime;
        private float fireRate;
        private float lastFiredTime;
        private Bullet gunBullets;
    }    
}