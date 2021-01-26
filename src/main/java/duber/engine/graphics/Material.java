package duber.engine.graphics;

import org.joml.Vector4f;

/**
 * A materials for a Mesh.
 * @author Darren Lo
 * @version 1.0
 */
public class Material {
    /** The default material colour. */
    public static final Vector4f DEFAULT_COLOUR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);
    
    /** The ambient colour. */
    private Vector4f ambientColour;
    /** The diffuse colour. */
    private Vector4f diffuseColour;
    /** The specular colour. */
    private Vector4f specularColour;
     
    /** This Material's texture. */
    private Texture texture;

    /** This Material's normal map */
    private Texture normalMap;
    
    /** The Material's reflectance */
    private float reflectance;
    
    /** Creates a default Material */
    public Material() {
        ambientColour = DEFAULT_COLOUR;
        diffuseColour = DEFAULT_COLOUR;
        specularColour = DEFAULT_COLOUR;
        reflectance = 1.0f;

        texture = null;
        normalMap = null;
    }

    /**
     * Constructs a Material.
     * @param ambientColour the ambient colour of this Material
     * @param diffuseColour the diffuse colour of this Material
     * @param specularColour the specular colour of this Material
     * @param texture the texture of this Material
     * @param normalMap the normal map of this Material
     * @param reflectance the reflectance of this Material
     */
    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour,
        Texture texture, Texture normalMap, float reflectance) {
        this.ambientColour = ambientColour;
        this.diffuseColour = diffuseColour;
        this.specularColour = specularColour;

        this.texture = texture;
        this.normalMap = normalMap;
        
        this.reflectance = reflectance;
    }

    /**
     * Constructs a Material without Texture.
     * @param ambientColour the ambient colour of this Material
     * @param diffuseColour the diffuse colour of this Material
     * @param specularColour the specular colour of this Material
     * @param reflectance the reflectance of this Material
     */
    public Material(Vector4f ambientColour, Vector4f diffuseColour, Vector4f specularColour, float reflectance) {
        this(ambientColour, diffuseColour, specularColour, null, null, reflectance);
    }


    /**
     * Gets the ambient colour.
     * @return the ambient colour
     */
    public Vector4f getAmbientColour() {
        return ambientColour;
    }

    /**
     * Gets the diffuse colour.
     * @return the diffuse colour
     */
    public Vector4f getDiffuseColour() {
        return diffuseColour;
    }

    /**
     * Gets the specular colour.
     * @return the specular colour
     */
    public Vector4f getSpecularColour() {
        return specularColour;
    }

    /**
     * Gets the reflectance.
     * @return the reflectance
     */
    public float getReflectance() {
        return reflectance;
    }

    /**
     * Gets the texture.
     * @return the texture
     */
    public Texture getTexture() {
        return texture;
    }

    /**
     * Sets the texture.
     * @param texture the texture
     */
    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    /**
     * Determines if this Material has a Texture
     * @return whether or not this Material has a Texture
     */
    public boolean hasTexture() {
        return texture != null;
    }

    /**
     * Gets the normal map.
     * @return the normal map
     */
    public Texture getNormalMap() {
        return normalMap;
    }

    /**
     * Sets the normal map.
     * @param normalMap the normal map
     */
    public void setNormalMap(Texture normalMap) {
        this.normalMap = normalMap;
    }

    /**
     * Determines if this Material has a normal map
     * @return whether or not this Material has a normal map
     */
    public boolean hasNormalMap() {
        return normalMap != null;
    }
}
