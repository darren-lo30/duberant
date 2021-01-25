package duber.engine;

/**
 * An interface used to clean up resources
 * @author Darren Lo
 * @version 1.0
 */
public interface Cleansable {
    /**
     * Cleans up a resource.
     */
    public abstract void cleanup();
}