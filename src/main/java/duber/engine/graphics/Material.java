package duber.engine.graphics;

import org.joml.Vector4f;

public class Material {
    public static final Vector4f DEFAULT_COLOUR = new Vector4f(0.5f, 0.5f, 1.0f, 1.0f);
    
    private Vector4f ambientColour;
    private Vector4f diffuseColour;
    private Vector4f specularColour;
    
    private Texture texture;
    private Texture normalMap;
    
    private float reflectance;
    
    public Material(){
        ambientColour = DEFAULT_COLOUR;
        diffuseColour = DEFAULT_COLOUR;
        specularColour = DEFAULT_COLOUR;
        reflectance = 0.0f;

        texture = null;
        normalMap = null;
    }

    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour,
        Texture texture, Texture normalMap, float reflectance){
        this.ambientColour = ambientColour;
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;

        this.texture = texture;
        this.normalMap = normalMap;
        
        this.reflectance = reflectance;
    }

    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, float reflectance){
        this(ambientColour, diffuseColour, specularColour, null, null, reflectance);
    }

    public Material(Vector4f colour, float reflectance){
        this(colour, colour, colour, null, null, reflectance);
    }

    public Material(Texture texture, float reflectance){
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, null, reflectance);
    }

    public Material(Texture texture, Texture normalMap, float reflectance){
        this(DEFAULT_COLOUR, DEFAULT_COLOUR, DEFAULT_COLOUR, texture, normalMap, reflectance);
    }

    public Material(Texture texture, Texture normalMap){
        this(texture, normalMap, 0.0f);
    }

    public Material(Texture texture){
        this(texture, 0.0f);
    }

    public Vector4f getAmbientColour(){
        return ambientColour;
    }

    public void setAmbientColour(Vector4f ambientColour){
        this.ambientColour = ambientColour;
    }

    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    public void setDiffuseColour(Vector4f diffuseColour){
        this.diffuseColour = diffuseColour;
    }

    public Vector4f getSpecularColour() {
        return specularColour;
    }

    public void setSpecularColour(Vector4f specularColour){
        this.specularColour = specularColour;
    }

    public float getReflectance() {
        return reflectance;
    }

    public void setReflectance(float reflectance){
        this.reflectance = reflectance;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture){
        this.texture = texture;
    }

    public boolean hasTexture(){
        return texture != null;
    }

    public boolean hasNormalMap(){
        return normalMap != null;
    }

    public Texture getNormalMap(){
        return normalMap;
    }

    public void setNormalMap(Texture normalMap){
        this.normalMap = normalMap;
    }
}
