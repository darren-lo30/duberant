package duber.engine.entities.components;

import org.joml.Vector3f;

/**
 * A component that all Entities have that given orientation and position.
 * @author Darren Lo
 * @version 1.0
 */
public class Transform extends Component {
    /** The 3D position. */
    private final Vector3f position;

    /** The 3D rotation in radians. */
    private final Vector3f rotation;

    /** The scale. */
    private float scale;

    /** If the transform is relative to the camera or not. */
    private boolean relativeView = true;

    /**
     * Constructs a default Transform.
     */
    public Transform() {
        this(new Vector3f(), new Vector3f(), 1.0f);
    }

    /**
     * Constructs a Transform.
     * @param position the position
     * @param rotation the rotation
     * @param scale the scale
     */
    public Transform(Vector3f position, Vector3f rotation, float scale) {
        this.position = position;
        this.rotation = rotation;
        this.scale = scale;
    }

    /**
     * Constructs a Transform from another Transform.
     * @param transform the Transform to copy
     */
    public Transform(Transform transform) {
        position = new Vector3f(transform.getPosition());
        rotation = new Vector3f(transform.getRotation());
        scale = transform.getScale();
    }

    /**
     * Sets a Transform from another Transform.
     * @param transform the Transform to copy
     */
    public Transform set(Transform transform) {
        position.set(transform.getPosition());
        rotation.set(transform.getRotation());
        scale = transform.getScale();
        return this;
    }

    /**
     * Gets this Transforms position.
     * @return the position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Gets this Transforms rotation.
     * @return the rotation
     */
    public Vector3f getRotation() {
        return rotation;
    }
    

    /**
     * Rotates this transform with radians.
     * @param rotationX the x rotation in radians
     * @param rotationY the y rotation in radians
     * @param rotationZ the z rotation in radians
     */
    public Vector3f rotate(float rotationX, float rotationY, float rotationZ) {
        return rotation.set(
                (rotation.x() + rotationX) % (float) Math.toRadians(360.0f),
                (rotation.y() + rotationY) % (float) Math.toRadians(360.0f),
                (rotation.z() + rotationZ) % (float) Math.toRadians(360.0f));
    }

    /**
     * Limits the rotation in the x axis.
     * @param lowerBound the lower bound of rotation
     * @param upperBound the upper bound of rotation
     */
    public void limitXRotation(float lowerBound, float upperBound) {
        rotation.x = Math.max(Math.min(rotation.x, upperBound), lowerBound);
    }

    /**
     * Gets the scale of this Transform.
     * @return the scale
     */
    public float getScale() {
        return scale;
    }

    /**
     * Sets the scale of this Transform.
     * @param scale the scale
     */
    public void setScale(float scale) {
        this.scale = scale;
    }

    /**
     * Gets if this Transform is relative to the view
     * @return whether or not this Transform is relative to the view
     */
    public boolean isRelativeView() {
        return relativeView;
    }

    /**
     * Sets whether or not this Transform is relative to the view.
     * @param relative if relative to the view
     */
    public void setRelativeView(boolean relativeView) {
        this.relativeView = relativeView;
    }
}