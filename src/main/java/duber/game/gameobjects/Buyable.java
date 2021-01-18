package duber.game.gameobjects;

import duber.engine.entities.components.Component;

public class Buyable extends Component {
    private int cost;

    public Buyable(int cost) {
        this.cost = cost;
    }

    public int getCost() {
        return cost;
    }

    @SuppressWarnings("unused")
    private Buyable() {}
}