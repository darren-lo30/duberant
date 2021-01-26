package duber.engine.graphics.lighting;

import org.joml.Vector3f;

/**
 * Contains data about lighting in a Scene
 * @author Darren Lo
 * @version 1.0
 */
public class SceneLighting {
    /** The ambient lighting in the Scene. */
    private Vector3f ambientLight;

    /** The skybox lighting in the Scene. */
    private Vector3f skyBoxLight;

    /** The point lights in the Scene. */
    private PointLight[] pointLights;

    /** The spot lights in the Scene. */
    private SpotLight[] spotLights;
    
    /** The directional light in the Scene. */
    private DirectionalLight directionalLight;
    
    /** The specular power. */
    private float specularPower = 10.0f;

    /**
     * Gets the ambient lighting.
     * @return the ambient lighting
     */
    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    /**
     * Sets the ambient lighting.
     * @param ambientLight the ambient lighting
     */
    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    /**
     * Gets the skybox lighting.
     * @return the skybox lighting
     */
    public Vector3f getSkyBoxLight() {
        return skyBoxLight;
    }

    /**
     * Sets the skybox lighting.
     * @param ambientLight the skybox lighting
     */
    public void setSkyBoxLight(Vector3f skyBoxLight) {
        this.skyBoxLight = skyBoxLight;
    }

    /**
     * Gets the point lights.
     * @return the point lights
     */
    public PointLight[] getPointLights() {
        return pointLights;
    }


    /**
     * Sets the point lights.
     * @param pointLights the point lights
     */
    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    /**
     * Gets the spot lights.
     * @return the spot lights
     */
    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    /**
     * Sets the spot lights.
     * @param spotLights the spot lights
     */
    public void setSpotLights(SpotLight[] spotLights) {
        this.spotLights = spotLights;
    }

    /**
     * Gets the directional light.
     * @return the directional light
     */
    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    /**
     * Sets the directional light.
     * @param directionalLight the directional light
     */
    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    /**
     * Gets the specular power.
     * @return the specular power
     */
    public float getSpecularPower() {
        return specularPower;
    }

    /**
     * Sets the specular power.
     * @param specularPower the specular power
     */
    public void setSpecularPower(float specularPower) {
        this.specularPower = specularPower;
    }

}