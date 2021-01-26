package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.entities.Camera;

/**
 * A component that gives the Entity a place to render the 3D world from
 * @author Darren Lo
 * @version 1.0
 */
public class Vision extends Component {
    /** The camera that the vision comes from. */
    private Camera camera;
    
    /** The offset of the camera from the Entity. */
    private Vector3f cameraOffset;

    /**
     * Constructs a Vision component with no offfset.
     */
    public Vision() {
        this(new Vector3f());
    }
    
    /**
     * Constructs a Vision component.
     * @param cameraOffset the offset of the Camera
     */
    public Vision(Vector3f cameraOffset) {
        camera = new Camera();
        this.cameraOffset = cameraOffset;
    }

    /**
     * Gets the camera offset.
     * @return the camera offset
     */
    public Vector3f getCameraOffset() {
        return cameraOffset;
    }

    /**
     * Gets the camera.
     * @return the camera
     */
    public Camera getCamera() {
        return camera;
    }
}