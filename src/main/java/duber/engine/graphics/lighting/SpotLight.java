package duber.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * A Light that is in a cone.
 * @author Darren Lo
 * @version 1.0
 */
public class SpotLight {
    /** The light used for the spot light. */
    private PointLight pointLight;

    /** The direction of the light. */
    private Vector3f coneDirection;

    /** The cut off angle of the light. */
    private float cutOffAngle;

    /**
     * Constructs a SpotLight.
     * @param pointLight the point light being used
     * @param coneDirection the direction of the cone
     * @param cutOffAngle the cut off angle for the cone
     */
    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        this.cutOffAngle = cutOffAngle;
    }

    /**
     * Constructs a SpotLight from another SpotLight.
     * @param spotLight the SpotLight to make a copy of
     */
    public SpotLight(SpotLight spotLight) {
        this(new PointLight(spotLight.getPointLight()), new Vector3f(spotLight.getConeDirection()), spotLight.getCutOffAngle());
    }

    /**
     * Gets the point light being used.
     * @return the point light being used
     */
    public PointLight getPointLight() {
        return pointLight;
    }

    /**
     * Gets the direction of the cone.
     * @return the direction of the cone
     */
    public Vector3f getConeDirection() {
        return coneDirection;
    }

    /**
     * Sets the direction of the cone.
     * @param coneDirection the direction of the cone
     */
    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    /**
     * Gets the cut off angle.
     * @return the cut off angle
     */
    public float getCutOffAngle() {
        return cutOffAngle;
    }

    /**
     * Used by Kryonet
     */
    @SuppressWarnings("unused")
    private SpotLight(){}
}