package duber.engine.entities.components;

import java.util.ArrayList;
import java.util.List;

/**
 * A Collider component for an Entity
 * @author Darren Lo
 * @version 1.0
 */
public class Collider extends Component {
    /** The List of ColliderParts the make up the Collider. */
    private final List<ColliderPart> colliderParts;

    /** The ColliderPart that is at the base. */
    private ColliderPart baseCollider;

    /**
     * Constructs a Collider without any parts.
     */
    public Collider() {
        colliderParts = new ArrayList<>();
    }

    /**
     * Gets a List of the ColliderParts.
     * @return the List of ColliderParts
     */
    public List<ColliderPart> getColliderParts() {
        return colliderParts;
    }

    /**
     * Determines if the Collider has any parts.
     * @return whether or not the Collider has parts
     */
    public boolean hasColliderParts() {
        return !colliderParts.isEmpty();
    }

    /**
     * Sets the base collider.
     * @param baseCollider the base collider
     */
    public void setBaseCollider(ColliderPart baseCollider) {
        addColliderPart(baseCollider);
        this.baseCollider = baseCollider;
    }

    /**
     * Gets the base collider.
     * @return the base collider
     */
    public ColliderPart getBaseCollider() {
        return baseCollider;
    }

    /**
     * Adds a ColliderPart.
     * @param colliderPart the ColliderPart to add
     */
    public void addColliderPart(ColliderPart colliderPart) {
        colliderParts.add(colliderPart);
        colliderPart.setCollider(this);
    }
}