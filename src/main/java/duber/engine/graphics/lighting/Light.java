package duber.engine.graphics.lighting;

import org.joml.Vector3f;

public abstract class Light {
    private Vector3f colour;
    private float intensity;

    protected Light(Vector3f colour, float intensity){
        this.colour = colour;
        this.intensity = intensity;
    }

    public Vector3f getColour(){
        return colour;
    }

    public void setColour(Vector3f colour){
        this.colour = colour;
    }

    public float getIntensity(){
        return intensity;
    }

    public void setIntensity(float intensity){
        this.intensity = intensity;
    }
}