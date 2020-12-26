package duber.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * SpotLight
 */
public class SpotLight {
    private PointLight pointLight;
    private Vector3f coneDirection;
    private float cutOffAngle;

    public SpotLight(PointLight pointLight, Vector3f coneDirection, float cutOffAngle) {
        this.pointLight = pointLight;
        this.coneDirection = coneDirection;
        this.cutOffAngle = cutOffAngle;
    }

    public SpotLight(SpotLight spotLight) {
        this(new PointLight(spotLight.getPointLight()), new Vector3f(spotLight.getConeDirection()), spotLight.getCutOffAngle());
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public Vector3f getConeDirection() {
        return coneDirection;
    }

    public void setConeDirection(Vector3f coneDirection) {
        this.coneDirection = coneDirection;
    }

    public float getCutOffAngle() {
        return cutOffAngle;
    }

    public void setCutOffAngleRadians(float cutOffAngle) {
        this.cutOffAngle = cutOffAngle;
    }

    public void setCutOffAngleDegrees(float cutOffAngle) {
        this.cutOffAngle = (float) Math.toRadians(cutOffAngle);
    }
}