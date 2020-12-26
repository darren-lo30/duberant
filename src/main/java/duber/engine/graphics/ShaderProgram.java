package duber.engine.graphics;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryStack;

import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.lighting.*;

public class ShaderProgram {
    private final int programId;

    //Determines where to draw the vertices
    private int vertexShaderId;

    //Colours the graphics
    private int fragmentShaderId;

    Map<String, Integer> uniforms;

    public ShaderProgram() throws LWJGLException {
        programId = glCreateProgram();
        if(programId == 0) {
            throw new LWJGLException("Could not create shader program");
        }
        uniforms = new HashMap<>();
    }

    public void createVertexShader(String shaderCode) throws LWJGLException {
        vertexShaderId = createShader(shaderCode, GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws LWJGLException {
        fragmentShaderId = createShader(shaderCode, GL_FRAGMENT_SHADER);
    }

    protected int createShader(String shaderCode, int shaderType) throws LWJGLException {
        int shaderId = glCreateShader(shaderType);
        if(shaderId == 0) {
            throw new LWJGLException("Error creating shader of type: " + shaderType);
        }

        //Create the shader
        glShaderSource(shaderId, shaderCode);
        glCompileShader(shaderId);

        if(glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new LWJGLException("Erorr compiling shader. Code: " + glGetShaderInfoLog(shaderId, 1024));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public void link() throws LWJGLException {
        glLinkProgram(programId);
        if(glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new LWJGLException("Error linking shader program. Code: " + glGetProgramInfoLog(programId, 1024));
        }

        if(vertexShaderId != 0) {
            glDetachShader(programId, vertexShaderId);
        }

        if(fragmentShaderId != 0) {
            glDetachShader(programId, fragmentShaderId);
        }

        glValidateProgram(programId);
        if(glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning while validating program");
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if(programId != 0) {
            glDeleteProgram(programId);
        }
    }

    public void createUniform(String uniformName) throws LWJGLException {
        int uniformLocation = glGetUniformLocation(programId, uniformName);
        if(uniformLocation < 0) {
            throw new LWJGLException("Could not find uniform: " + uniformName);
        }

        uniforms.put(uniformName, uniformLocation);
    }

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

    public void createPointLightListUniform(String uniformName, int size) throws LWJGLException {
        for(int i = 0; i<size; i++) {
            createPointLightUniform(String.format("%s[%d]", uniformName, i));
        }
    }

    private void createPointLightUniform(String uniformName) throws LWJGLException{
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".attenuation.constant");
        createUniform(uniformName + ".attenuation.linear");
        createUniform(uniformName + ".attenuation.exponent");
    }

    public void createSpotLightListUniform(String uniformName, int size) throws LWJGLException {
        for(int i = 0; i<size; i++) {
            createSpotLightUniform(String.format("%s[%d]", uniformName, i));
        }
    }

    private void createSpotLightUniform(String uniformName) throws LWJGLException {
        createPointLightUniform(uniformName + ".pointLight");
        createUniform(uniformName + ".coneDirection");
        createUniform(uniformName + ".cutOffAngle");
    }
    
    public void createDirectionalLightUniform(String uniformName) throws LWJGLException {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createMaterialUniform(String uniformName) throws LWJGLException {
        createUniform(uniformName + ".ambientColour");
        createUniform(uniformName + ".diffuseColour");
        createUniform(uniformName + ".specularColour");
        createUniform(uniformName + ".reflectance");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".hasNormalMap");
    }

    public void setUniform(String uniformName, Vector3f value) {
        glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, Vector4f value) {
        glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName, int value) {
        glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        glUniform1f(uniforms.get(uniformName), value);
    }
    
    public void setUniform(String uniformName, DirectionalLight directionalLight) {
        setUniform(uniformName + ".colour", directionalLight.getColour());
        setUniform(uniformName + ".direction", directionalLight.getDirection());
        setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    public void setUniform(String uniformName, Material material) {
        setUniform(uniformName + ".ambientColour", material.getAmbientColour());
        setUniform(uniformName + ".diffuseColour", material.getDiffuseColour());
        setUniform(uniformName + ".specularColour", material.getSpecularColour());
        setUniform(uniformName + ".reflectance", material.getReflectance());
        setUniform(uniformName + ".hasTexture", material.hasTexture() ? 1 : 0);
        setUniform(uniformName + ".hasNormalMap", material.hasNormalMap() ? 1 : 0);
    }

    private void setUniform(String uniformName, PointLight pointLight) {
        setUniform(uniformName + ".colour", pointLight.getColour());
        setUniform(uniformName + ".position", pointLight.getPosition());
        setUniform(uniformName + ".intensity", pointLight.getIntensity());
        PointLight.Attenuation attenuation = pointLight.getAttenuation();
        setUniform(uniformName + ".attenuation.constant", attenuation.getConstant());
        setUniform(uniformName + ".attenuation.linear", attenuation.getLinear());
        setUniform(uniformName + ".attenuation.exponent", attenuation.getExponent());
    }

    public void setUniform(String uniformName, PointLight pointLight, int index) {
        setUniform(String.format("%s[%d]", uniformName, index), pointLight);
    }

    public void setUniform(String uniformName, PointLight[] pointLights) {
        for(int i = 0; i<pointLights.length; i++) {
            setUniform(String.format("%s[%d]", uniformName, i), pointLights[i]);
        }
    }

    private void setUniform(String uniformName, SpotLight spotLight) {
        setUniform(uniformName + ".pointLight", spotLight.getPointLight());
        setUniform(uniformName + ".coneDirection", spotLight.getConeDirection());

        setUniform(uniformName + ".cutOffAngle", spotLight.getCutOffAngle());
    }

    public void setUniform(String uniformName, SpotLight spotLight, int index) {
        setUniform(String.format("%s[%d]", uniformName, index), spotLight);
    }

    public void setUniform(String uniformName, SpotLight[] spotLights) {
        for(int i = 0; i<spotLights.length; i++) {
            setUniform(String.format("%s[%d]", uniformName, i), spotLights[i]);
        }
    }
}