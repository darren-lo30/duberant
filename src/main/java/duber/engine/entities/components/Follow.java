package duber.engine.entities.components;

import org.joml.Vector3f;

import duber.engine.entities.Camera;

public class Follow extends Component {
    private Camera camera;
    private Vector3f cameraOffset;

    public Follow() {
        this(new Vector3f());
    }
    
    public Follow(Vector3f cameraOffset) {
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