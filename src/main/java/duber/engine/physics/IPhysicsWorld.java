package duber.engine.physics;

import java.util.List;

import duber.engine.entities.Face;
import duber.engine.physics.collisions.Box;

public interface IPhysicsWorld {
    public abstract void update();

    public abstract List<Face> getIntersectingConstantFaces(Box box);
}