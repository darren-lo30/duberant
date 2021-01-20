package duber.game.gameobjects;

import duber.engine.entities.components.Named;

public enum GunType {
    RIFLE {
        @Override
        public String toString() {
            return "Rifle";
        }
    }, 
    LMG {
        @Override
        public String toString() {
            return "Light Machine Gun";
        }
    }, 
    PISTOL {
        @Override
        public String toString() {
            return "Pistol";
        }
    };

    public boolean isGunType(Gun gun) {
        return gun.getComponent(Named.class).getName().equals(this.toString());
    }
}