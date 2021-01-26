package duber.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * A representation of a light in the 3D world.
 * @author Darren Lo
 * @version 1.0
 */
public abstract class Light {
    /** The colour of the light */
    private Vector3f colour;
    /** The intensity of the light */
    private float intensity;

    /** 
     * Constructs a default light. 
     */
    protected Light() {
        colour = new Vector3f(1.0f, 1.0f, 1.0f);
        intensity = 1.0f;
    }

    /**
     * Constructs a light with a given colour and intensity.
     * @param colour the colour of this Light
     * @param intensity the intensity of this Light
     */
    protected Light(Vector3f colour, float intensity) {
        this.colour = colour;
        this.intensity = intensity;
    }

    /**
     * Gets the colour.
     * @return the colour
     */
    public Vector3f getColour() {
        return colour;
    }

    /**
     * Sets the colour.
     * @param colour the colour
     */
    public void setColour(Vector3f colour) {
        this.colour = colour;
    }

    /**
     * Gets the intensity.
     * @return the intensity
     */
    public float getIntensity() {
        return intensity;
    }

    /**
     * Sets the intensity.
     * @param intensity the intensity
     */
    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }
    

}