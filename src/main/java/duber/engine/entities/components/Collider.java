package duber.engine.entities.components;

import java.util.ArrayList;
import java.util.List;

public class Collider extends Component {
    private List<ColliderPart> colliderParts;

    public Collider() {
        colliderParts = new ArrayList<>();
    }

    public List<ColliderPart> getColliderParts() {
        return colliderParts;
    }

    public boolean hasColliderParts() {
        return !colliderParts.isEmpty();
    }

    public void addColliderPart(ColliderPart colliderPart) {
        colliderParts.add(colliderPart);
    }
    
}