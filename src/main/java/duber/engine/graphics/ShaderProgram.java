package duber.engine.graphics;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import duber.engine.Cleansable;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.lighting.PointLight;
import duber.engine.graphics.lighting.SpotLight;
import duber.engine.graphics.lighting.DirectionalLight;

import static org.lwjgl.opengl.GL20.glShaderSource;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1f;
import static org.lwjgl.opengl.GL20.glUniform3f;
import static org.lwjgl.opengl.GL20.glUniform4f;
import static org.lwjgl.opengl.GL20.glUniformMatrix4fv;
import static org.lwjgl.opengl.GL20.glValidateProgram;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;

/**
 * A program used to render a 3D world
 * @author Darren Lo
 * @version 1.0
 */
public class ShaderProgram implements Cleansable {
    /** The id of the program. */
    private final int programId;

    /** The vertex shader id. */
    private int vertexShaderId;

    /** The fragment shader id. */
    private int fragmentShaderId;

    /** The uniforms in the program. */
    Map<String, Integer> uniforms;

    /**
     * Constructs a ShaderProgram.
     * @throws LWJGLException if this ShaderProgram could not be initialized
     */
    public ShaderProgram() throws LWJGLException {
        programId = glCreateProgram();
        if (programId == 0) {
            throw new LWJGLException("Could not create shader program");
        }
        uniforms = new HashMap<>();
    }

    /**
     * Creates a vertex shader.
     * @param shaderCode the shader code
     * @throws LWJGLException if the vertex shader could not be created
     */
    public void createVertexShader(String shaderCode) throws LWJGLException {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    /**
     * Creates a fragment shader.
     * @param shaderCode the shader code
     * @throws LWJGLException if the fragment shader could not be created
     */
    public void createFragmentShader(String shaderCode) throws LWJGLException {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    /**
     * Creates a shader.
     * @param shaderCode the shader code
     * @param shaderType the shader type
     * @throws LWJGLException if the shader could not be created
     */
    protected int createShader(String shaderCode, int shaderType) throws LWJGLException {
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new LWJGLException("Error creating shader of type: " + shaderType);
        }

        //Create the shader
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new LWJGLException("Erorr compiling shader. Code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    /**
     * Links the shader program.
     * @throws LWJGLException if the program could not be linked
     */
    public void link() throws LWJGLException {
        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new LWJGLException("Error linking shader program. Code: " + glGetProgramInfoLog(programId, 1024));
        }

        if (vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }

        if (fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning while validating program");
        }
    }

    /**
     * Creates a uniform.
     * @param uniformName the name of the uniform
     * @throws LWJGLException if the uniform could not be created
     */
    public void createUniform(String uniformName) throws LWJGLException {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if (uniformLocation < 0) {
            throw new LWJGLException("Could not find uniform: " + uniformName);
        }

        uniforms.put(uniformName, uniformLocation);
    }

    
    /**
     * Sets a uniform to a Matrix4f.
     * @param uniformName the name of the uniform
     * @param value the Matrix4f
     */
    public void setUniform(String uniformName, Matrix4f value) {
        try(MemoryStack memoryStack = MemoryStack.stackPush()) {

            //Allocates space for 16 for floats
            FloatBuffer matrixBuffer = memoryStack.mallocFloat(16);

            //Inserts the 4x4 matrix into the memory stack
            value.get(matrixBuffer);

            //Sets the uniform
            glUniformMatrix4fv(uniforms.get(uniformName), false, matrixBuffer);
        }
    }

    /**
     * Sets a uniform to an array of Matrix4f.
     * @param uniformName the name of the uniform
     * @param value the array of Matrix4fs
     */
    public void setUniform(String uniformName, Matrix4f[] matrices) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            int length = matrices != null ? matrices.length : 0;
            FloatBuffer fb = stack.mallocFloat(16 * length);
            for (int i = 0; i < length; i++) {
                matrices[i].get(16 * i, fb);
            }
            glUniformMatrix4fv(uniforms.get(uniformName), false, fb);
        }
    }


    /**
     * Sets a uniform to a Vector3f.
     * @param uniformName the name of the uniform
     * @param value the Vector3f
     */
    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    /**
     * Sets a uniform to a Vector4f.
     * @param uniformName the name of the uniform
     * @param value the Vector4f
     */
    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    /**
     * Sets a uniform to a float.
     * @param uniformName the name of the uniform
     * @param value the float
     */
    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }

    /**
     * Sets a uniform to an int.
     * @param uniformName the name of the uniform
     * @param value the int
     */
    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }


    /**
     * Creates a uniform for PointLights.
     * @param uniformName the name of the uniform
     * @param size the number of PointLights
     * @throws LWJGLException if the uniform could not be created
     */
    public void createPointLightListUniform(String uniformName, int size) throws LWJGLException {
        for(int i = 0; i<size; i++) {
            createPointLightUniform(String.format("%s[%d]", uniformName, i));
        }
    }

    /**
     * Creates a uniform for a PointLight.
     * @param uniformName the name of the uniform
     * @throws LWJGLException if the uniform could not be created
     */
    private void createPointLightUniform(String uniformName) throws LWJGLException{
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".attenuation.constant");
        createUniform(uniformName + ".attenuation.linear");
        createUniform(uniformName + ".attenuation.exponent");
    }

    /**
     * Creates a uniform for SpotLights.
     * @param uniformName the name of the uniform
     * @param size the number of SpotLights
     * @throws LWJGLException if the uniform could not be created
     */
    public void createSpotLightListUniform(String uniformName, int size) throws LWJGLException {
        for(int i = 0; i<size; i++) {
            createSpotLightUniform(String.format("%s[%d]", uniformName, i));
        }
    }

    /**
     * Creates a uniform for a SpotLight.
     * @param uniformName the name of the uniform
     * @throws LWJGLException if the uniform could not be created
     */
    private void createSpotLightUniform(String uniformName) throws LWJGLException {
        createPointLightUniform(uniformName + ".pointLight");
        createUniform(uniformName + ".coneDirection");
        createUniform(uniformName + ".cutOffAngle");
    }
    
    /**
     * Creates a uniform for a DirectionalLight
     * @param uniformName the name of the uniform
     * @throws LWJGLException if the uniform could not be created
     */
    public void createDirectionalLightUniform(String uniformName) throws LWJGLException {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    /**
     * Creates a uniform for a Material.
     * @param uniformName the name of the uniform
     * @throws LWJGLException if the uniform could not be created
     */
    public void createMaterialUniform(String uniformName) throws LWJGLException {
        createUniform(uniformName + ".ambientColour");
        createUniform(uniformName + ".diffuseColour");
        createUniform(uniformName + ".specularColour");
        createUniform(uniformName + ".reflectance");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".hasNormalMap");
    }
    
    /**
     * Sets the uniform for a DirectionalLight
     * @param uniformName the name of the uniform 
     * @param directionalLight the DirectionalLight to set
     */
    public void setUniform(String uniformName, DirectionalLight directionalLight) {
        setUniform(uniformName + ".colour", directionalLight.getColour());
        setUniform(uniformName + ".direction", directionalLight.getDirection());
        setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    /**
     * Sets the uniform for a Material
     * @param uniformName the name of the uniform 
     * @param material the Material to set
     */
    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambientColour", material.getAmbientColour());
        setUniform(uniformName + ".diffuseColour", material.getDiffuseColour());
        setUniform(uniformName + ".specularColour", material.getSpecularColour());
        setUniform(uniformName + ".reflectance", material.getReflectance());
        setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0);
        setUniform(uniformName + ".hasNormalMap", material.hasNormalMap() ? 1 : 0);
    }

    /**
     * Sets the uniform for a PointLight
     * @param uniformName the name of the uniform 
     * @param pointLight the PointLight to set
     */
    private void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColour());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation attenuation = pointLight.getAttenuation();
        setUniform(uniformName + ".attenuation.constant", attenuation.getConstant());
        setUniform(uniformName + ".attenuation.linear", attenuation.getLinear());
        setUniform(uniformName + ".attenuation.exponent", attenuation.getExponent());
    }

    /**
     * Sets the uniform for a PointLight at an index
     * @param uniformName the name of the uniform 
     * @param pointLight the PointLight to set
     * @param index the index to set
     */
    public void setUniform(String uniformName, PointLight pointLight, int index) {
        setUniform(String.format("%s[%d]", uniformName, index), pointLight);
    }

    /**
     * Sets the uniform for an array of PointLights
     * @param uniformName the name of the uniform 
     * @param pointLights the PointLights to set
     */
    public void setUniform(String uniformName, PointLight[] pointLights) {
        for(int i = 0; i<pointLights.length; i++) {
            setUniform(String.format("%s[%d]", uniformName, i), pointLights[i]);
        }
    }

    /**
     * Sets the uniform for a SpotLight
     * @param uniformName the name of the uniform 
     * @param spotLight the SpotLight to set
     */
    private void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".pointLight", spotLight.getPointLight());
        setUniform(uniformName + ".coneDirection", spotLight.getConeDirection());

        setUniform(uniformName + ".cutOffAngle", spotLight.getCutOffAngle());
    }

    /**
     * Sets the uniform for a SpotLight at an index
     * @param uniformName the name of the uniform 
     * @param spotLight the SpotLight to set
     * @param index the index to set
     */
    public void setUniform(String uniformName, SpotLight spotLight, int index) {
        setUniform(String.format("%s[%d]", uniformName, index), spotLight);
    }

    /**
     * Sets the uniform for an array of SpotLights
     * @param uniformName the name of the uniform 
     * @param spotLights the SpotLights to set
     */
    public void setUniform(String uniformName, SpotLight[] spotLights) {
        for(int i = 0; i<spotLights.length; i++) {
            setUniform(String.format("%s[%d]", uniformName, i), spotLights[i]);
        }
    }

    /**
     * Binds this ShaderProgram.
     */
    public void bind() {
        glUseProgram(programId);
    }

    /**
     * Unbinds this ShaderProgram.
     */
    public void unbind() {
        glUseProgram(0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }
}