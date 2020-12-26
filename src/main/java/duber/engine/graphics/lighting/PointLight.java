package duber.engine.graphics.lighting;

import org.joml.Vector3f;

public class PointLight extends Light {
    Vector3f position;
    private Attenuation attenuation;

    public PointLight(Vector3f colour, Vector3f position, float intensity) {
        super(colour, intensity);
        this.position = position;
        attenuation = new Attenuation();
    }

    public PointLight(Vector3f colour, Vector3f position, float intensity, Attenuation attenuation) {
        this(colour, position, intensity);
        this.attenuation = attenuation;
    }

    public PointLight(PointLight pointLight) {
        this(new Vector3f(pointLight.getColour()), new Vector3f(pointLight.getPosition()), 
            pointLight.getIntensity(), pointLight.getAttenuation());
    }


    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }
    
    public static class Attenuation {
        private static final float DEFAULT_CONSTANT = 0.0f;
        private static final float DEFAULT_LINEAR = 0.0f;
        private static final float DEFAULT_EXPONENT = 1.0f;

        private float constant;
        private float linear;
        private float exponent;

        public Attenuation() {
            constant = DEFAULT_CONSTANT;
            linear = DEFAULT_LINEAR;
            exponent = DEFAULT_EXPONENT;
        }

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public float getConstant() {
            return constant;
        }

        public void setConstant(float constant) {
            this.constant = constant;
        }

        public float getLinear() {
            return linear;
        }

        public void setLinear(float linear) {
            this.linear = linear;
        }

        public float getExponent() {
            return exponent;
        }

        public void setExponent(float exponent) {
            this.exponent = exponent;
        }

        
    }
}