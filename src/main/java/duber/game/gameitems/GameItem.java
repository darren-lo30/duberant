package duber.game.gameitems;

import duber.engine.entities.Entity;

/**
 * GameItem
 */
public abstract class GameItem {
    private Entity model;

    protected GameItem(){}

    protected GameItem(Entity model) {
        this.model = model;
    }

    public Entity getModel() {
        return model;
    }
}