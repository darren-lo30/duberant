package duber.engine.graphics.lighting;

import org.joml.Vector3f;

public class DirectionalLight extends Light {
    
    private Vector3f direction;
    
    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        super(colour, intensity);
        this.direction = direction;    }

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
}