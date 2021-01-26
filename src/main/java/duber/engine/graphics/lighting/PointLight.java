package duber.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * A PointLight which emits light in all directions from a single point.
 * @author Darren Lo
 * @version 1.0
 */
public class PointLight extends Light {
    /** The position of this PointLight */
    Vector3f position;

    /** The attenuation of the light */
    private Attenuation attenuation;

    /** 
     * Constructs a PointLight with default attenuation.
     * @param colour the colour of this PointLight
     * @param position the position of this PointLight
     * @param intensity the intensity of this PointLight
     */
    public PointLight(Vector3f colour, Vector3f position, float intensity) {
        super(colour, intensity);
        this.position = position;
        attenuation = new Attenuation();
    }

    /** 
     * Constructs a PointLight.
     * @param colour the colour of this PointLight
     * @param position the position of this PointLight
     * @param intensity the intensity of this PointLight
     * @param attenuation the attenuation of this PointLight
     */
    public PointLight(Vector3f colour, Vector3f position, float intensity, Attenuation attenuation) {
        this(colour, position, intensity);
        this.attenuation = attenuation;
    }

    /**
     * Constructs a PointLight from another PointLight.
     * @param pointLight the PointLight to copy
     */
    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.getColour()), new Vector3f(pointLight.getPosition()), 
            pointLight.getIntensity(), pointLight.getAttenuation());
    }


    /**
     * Gets the position.
     * @return the position
     */
    public Vector3f getPosition() {
        return position;
    }

    /**
     * Sets the position.
     * @param position the position
     */
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    /**
     * Gets the attenuation.
     * @return the attenuation
     */
    public Attenuation getAttenuation() {
        return attenuation;
    }

    /**
     * Sets the attenuation.
     * @param attenuation the attenuation
     */
    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    /**
     * Used by Kryonet
     */
    @SuppressWarnings("unused")
    private PointLight(){}
    
    /**
     * Stores information about the dropoff of light.
     */
    public static class Attenuation {
        /** The constant factor. */
        private float constant;

        /** The linear factor. */
        private float linear;

        /** The exponential factor. */
        private float exponent;

        /** 
         * Constructs attenuation with default values.
         */
        public Attenuation() {
            constant = 0f;
            linear = 0f;
            exponent = 1f;
        }

        /**
         * Constructs an Attenuation.
         * @param constant the constant factor
         * @param linear the linear factor
         * @param exponent the exponent factor
         */
        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        /**
         * Gets the constant factor.
         * @return the constant factor
         */
        public float getConstant() {
            return constant;
        }

        /**
         * Gets the linear factor.
         * @return the linear factor
         */
        public float getLinear() {
            return linear;
        }

        /**
         * Gets the exponent factor.
         * @return the exponent factor
         */
        public float getExponent() {
            return exponent;
        }   
    }
}