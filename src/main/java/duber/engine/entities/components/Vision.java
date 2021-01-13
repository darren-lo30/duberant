package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.entities.Camera;

public class Vision extends Component {
    private Camera camera;
    private Vector3f cameraOffset;

    public Vision() {
        this(new Vector3f());
    }
    
    public Vision(Vector3f cameraOffset) {
        camera = new Camera();
        this.cameraOffset = cameraOffset;
    }

    public Vector3f getCameraOffset() {
        return cameraOffset;
    }

    public Camera getCamera() {
        return camera;
    }
}