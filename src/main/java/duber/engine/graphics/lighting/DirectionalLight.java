package duber.engine.graphics.lighting;

import org.joml.Vector3f;

import duber.engine.graphics.OrthoCoord;

public class DirectionalLight extends Light {
    
    private Vector3f direction;

    private OrthoCoord orthoCoord;

    private float shadowPositionMultiplier;
    
    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        super(colour, intensity);
        this.direction = direction;
        
        orthoCoord = new OrthoCoord();
        shadowPositionMultiplier = 1;
    }

    public DirectionalLight(DirectionalLight directionalLight) {
        this(new Vector3f(directionalLight.getColour()),
             new Vector3f(directionalLight.getDirection()),
             directionalLight.getIntensity());
    }

    public Vector3f getDirection() {
        return this.direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public float getShadowPositionMultipler() {
        return shadowPositionMultiplier;
    }

    public void setShadowPositionMultipler(float shadowPositionMultiplier) {
        this.shadowPositionMultiplier = shadowPositionMultiplier;
    }

    public OrthoCoord getOrthoCoord() {
        return orthoCoord;
    }
    
    public void setOrthoCoord(float left, float right, float bottom, float top, float near, float far) {
        orthoCoord.setLeft(left);
        orthoCoord.setRight(right);
        orthoCoord.setBottom(bottom);
        orthoCoord.setTop(top);
        orthoCoord.setNear(near);
        orthoCoord.setFar(far);
    }
}