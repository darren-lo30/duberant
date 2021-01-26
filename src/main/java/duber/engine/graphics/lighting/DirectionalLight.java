package duber.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * Directional lighting in the game.
 * @author Darren Lo
 * @version 1.0
 */
public class DirectionalLight extends Light {
    
    /** The direction of the light */
    private Vector3f direction;
    
    /**
     * Constructs a Directionalight.
     * @param colour the colour of the light
     * @param direction the direction of the light
     * @param intensity the intensity of the light
     */
    public DirectionalLight(Vector3f colour, Vector3f direction, float intensity) {
        super(colour, intensity);
        this.direction = direction;    }

    /**
     * Constructs a DirectionaLight from a DirectionaLight.
     * @param directionalLight the DirectionalLight to copy
     */
    public DirectionalLight(DirectionalLight directionalLight) {
        this(new Vector3f(directionalLight.getColour()),
             new Vector3f(directionalLight.getDirection()),
             directionalLight.getIntensity());
    }

    /**
     * Gets the direction of the light.
     * @return the direction of the light
     */
    public Vector3f getDirection() {
        return this.direction;
    }

    /**
     * Sets the direction of the light.
     * @param direction the direction of the light
     */
    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    /** 
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private DirectionalLight(){}

}