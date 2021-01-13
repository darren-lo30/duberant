package duber.engine.entities.components;

import org.joml.Vector3f;

public class Transform extends Component {
    private final Vector3f position;
    private final Vector3f rotation;
    private float scale;

    public Transform() {
        this(new Vector3f(), new Vector3f(), 1.0f);
    }

    public Transform(Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    public Transform(Transform transform) {
        position = new Vector3f(transform.getPosition());
        rotation = new Vector3f(transform.getRotation());
        scale = transform.getScale();
    }

    public Transform set(Transform transform) {
        position.set(transform.getPosition());
        rotation.set(transform.getRotation());
        scale = transform.getScale();
        return this;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public Vector3f rotate(float rotationX, float rotationY, float rotationZ) {
        return rotation.set((rotation.x() + rotationX) % (float) Math.toRadians(360.0f),
                (rotation.y() + rotationY) % (float) Math.toRadians(360.0f),
                (rotation.z() + rotationZ) % (float) Math.toRadians(360.0f));
    }

    public Vector3f rotateDegrees(float rotationX, float rotationY, float rotationZ) {
        return rotate((float) Math.toRadians(rotationX), (float) Math.toRadians(rotationY),
                (float) Math.toRadians(rotationZ));
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Transform lerp(Transform next, float factor) {
        Transform interpolated = new Transform();
        position.lerp(next.getPosition(), factor, interpolated.getPosition());
        rotation.lerp(next.getRotation(), factor, interpolated.getPosition());
        interpolated.setScale(scale + (next.getScale() - scale) * factor);

        return interpolated;
    }
}