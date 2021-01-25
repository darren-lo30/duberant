package duber.game.gameobjects;

import duber.engine.entities.components.Component;

/**
 * A component of an Entity that makes it buyable
 * @author Darren Lo
 * @version 1.0
 */
public class Buyable extends Component {
    /**
     * The cost the Entity.
     */
    private int cost;

    /**
     * Constructs a Buyable component.
     * @param cost the cost of the Entity
     */
    public Buyable(int cost) {
        this.cost = cost;
    }

    /**
     * Gets the cost.
     * @return the cost
     */
    public int getCost() {
        return cost;
    }

    /**
     * Used for Kryonet
     */
    @SuppressWarnings("unused")
    private Buyable() {}
}