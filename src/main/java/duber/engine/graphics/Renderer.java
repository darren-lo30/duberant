package duber.engine.graphics;

import duber.engine.items.GameItem;
import duber.engine.items.SkyBox;
import duber.engine.Scene;
import duber.engine.Transformation;
import duber.engine.graphics.lighting.*;
import duber.engine.Utils;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;

import static org.lwjgl.opengl.GL11.glViewport;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Renderer {    
    private static final int MAX_POINT_LIGHTS = 100;
    private static final int MAX_SPOT_LIGHTS = 100;
    
    private ShaderProgram sceneShaderProgram;
    private ShaderProgram skyBoxShaderProgram;

    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 5000.0f;

    private final Transformation transformation;
    
    private float specularPower;

    public Renderer(){
        //Intiialize vertex transformer
        transformation = new Transformation();
        specularPower = 10.0f;
    }
    
    public void init(Window window) throws LWJGLException, IOException {
        setUpSkyBoxShader();
        setUpSceneShader();     
    }

    private void setUpSceneShader() throws LWJGLException, IOException {
        sceneShaderProgram = new ShaderProgram();

        //Compile shader programs
        sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
        sceneShaderProgram.link();    

        //Create uniforms for transformation matrices
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("nonInstancedModelViewMatrix");

        //Create texture sampler uniform
        sceneShaderProgram.createUniform("texture_sampler");
        sceneShaderProgram.createUniform("normalMap");

        //Create material uniform
        sceneShaderProgram.createMaterialUniform("material");

        //Create uniform for lights
        sceneShaderProgram.createUniform("ambientLight");        
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS); 
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS); 
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");  
        
        //For instanced rendering
        sceneShaderProgram.createUniform("isInstanced");

        //Texture
        sceneShaderProgram.createUniform("numRows");
        sceneShaderProgram.createUniform("numColumns");
    }

    private void setUpSkyBoxShader() throws LWJGLException, IOException {
        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/sky_box_vertex.vs"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/sky_box_fragment.fs"));
        skyBoxShaderProgram.link();

        //Create uniforms for transformation matrices
        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");

        //Create texture sampler uniform
        skyBoxShaderProgram.createUniform("texture_sampler");

        skyBoxShaderProgram.createUniform("ambientLight");

        skyBoxShaderProgram.createUniform("hasTexture");
        skyBoxShaderProgram.createUniform("colour");
    }

    

    public void clear(){
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, Scene scene){
        clear();
        
        //Update camera view matrix
        camera.updateViewMatrix();

        //Resize the window
        glViewport(0, 0, window.getWidth(), window.getHeight());

        window.updateProjectionMatrix(FOV, Z_NEAR, Z_FAR);
        
        renderScene(window, camera, scene);
        renderSkyBox(window, camera, scene);
    }

    private void renderNonInstancedMeshes(Scene scene, ShaderProgram shaderProgram, Matrix4f viewMatrix){
        sceneShaderProgram.setUniform("isInstanced", 0);

        Map<Mesh, List<GameItem>> meshMap = scene.getMeshMap();

        for(Map.Entry<Mesh, List<GameItem>> meshMapEntry: meshMap.entrySet()){
            Mesh mesh = meshMapEntry.getKey();
            List<GameItem> gameItems = meshMapEntry.getValue();
            Texture texture = mesh.getMaterial().getTexture();

            if(texture != null){
                sceneShaderProgram.setUniform("numColumns", texture.getNumColumns());
                sceneShaderProgram.setUniform("numRows", texture.getNumRows());
            }

            if(viewMatrix != null){
                shaderProgram.setUniform("material", mesh.getMaterial());
            }

            mesh.render(gameItems, (GameItem gameItem) -> {
                Matrix4f modelMatrix = transformation.buildModelMatrix(gameItem);
                
                if(viewMatrix != null){
                    Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(modelMatrix, viewMatrix);
                    sceneShaderProgram.setUniform("nonInstancedModelViewMatrix", modelViewMatrix);
                }
            });
        }
    }

    private void renderInstancedMeshes(Scene scene, ShaderProgram shaderProgram, Matrix4f viewMatrix, Matrix4f lightViewMatrix){
        sceneShaderProgram.setUniform("isInstanced", 1);
        Map<InstancedMesh, List<GameItem>> meshMap = scene.getInstancedMeshMap();
        
        for(Map.Entry<InstancedMesh, List<GameItem>> meshMapEntry: meshMap.entrySet()){
            InstancedMesh mesh = meshMapEntry.getKey();
            Texture texture = mesh.getMaterial().getTexture();
            List<GameItem> gameItems = meshMapEntry.getValue();

            if(texture != null){
                sceneShaderProgram.setUniform("numColumns", texture.getNumColumns());
                sceneShaderProgram.setUniform("numRows", texture.getNumRows());
            }
            
            if(viewMatrix != null){
                shaderProgram.setUniform("material", mesh.getMaterial());
            }

            mesh.render(gameItems, transformation, viewMatrix, lightViewMatrix);
        }
    }

    private void renderScene(Window window, Camera camera, Scene scene){
        sceneShaderProgram.bind();

        //Set projection matrix uniform
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //Get view matrices
        Matrix4f viewMatrix = camera.getViewMatrix();
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        //Render lighting
        renderLights(viewMatrix, scene.getSceneLighting());

        //Set texture uniforms
        sceneShaderProgram.setUniform("texture_sampler", 0);
        sceneShaderProgram.setUniform("normalMap", 1);
        
        //Draw the mesh
        renderInstancedMeshes(scene, sceneShaderProgram, viewMatrix, lightViewMatrix);
        renderNonInstancedMeshes(scene, sceneShaderProgram, viewMatrix);

        sceneShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, SceneLighting sceneLighting){

        //Update light uniforms for ambient lighting
        sceneShaderProgram.setUniform("ambientLight", sceneLighting.getAmbientLight());
        sceneShaderProgram.setUniform("specularPower", specularPower);

        renderPointLights(viewMatrix, sceneLighting.getPointLights());
        renderSpotLights(viewMatrix, sceneLighting.getSpotLights());
        renderDirectionalLights(viewMatrix, sceneLighting.getDirectionalLight()); 
    }

    private void renderPointLights(Matrix4f viewMatrix, PointLight[] pointLights){
        if(pointLights == null){
            return;
        }
        
        for(int i = 0; i<pointLights.length; i++){
            if(pointLights[i] == null){
                continue;
            }

            PointLight viewPointLight = new PointLight(pointLights[i]);
            Vector4f newPosition = new Vector4f(viewPointLight.getPosition(), 1.0f);
            newPosition.mul(viewMatrix);

            //Update the point lights position to the view position (relative to camera)
            viewPointLight.setPosition(new Vector3f(
                newPosition.x,
                newPosition.y,
                newPosition.z
            ));
            //Set the point light uniform
            sceneShaderProgram.setUniform("pointLights", viewPointLight, i);
        }
    }

    private void renderSpotLights(Matrix4f viewMatrix, SpotLight[] spotLights){
        if(spotLights == null){
            return;
        }
        
        for(int i = 0; i<spotLights.length; i++){
            if(spotLights[i] == null){
                continue;
            }

            SpotLight viewSpotLight = new SpotLight(spotLights[i]);

            //Transform cone direction to view coordinate space
            Vector4f coneDirection = new Vector4f(viewSpotLight.getConeDirection(), 0);
            coneDirection.mul(viewMatrix);
            viewSpotLight.setConeDirection(new Vector3f(coneDirection.x, coneDirection.y, coneDirection.z));            

            Vector3f lightPosition = viewSpotLight.getPointLight().getPosition();

            //Transform position to view coordinate space
            Vector4f newPosition = new Vector4f(lightPosition, 1);
            newPosition.mul(viewMatrix);
            viewSpotLight.getPointLight().setPosition(new Vector3f(
                newPosition.x, newPosition.y, newPosition.z
            ));

            sceneShaderProgram.setUniform("spotLights", viewSpotLight, i);
        }
    }

    private void renderDirectionalLights(Matrix4f viewMatrix, DirectionalLight directionalLight){
        if(directionalLight == null){
            return;
        }
        
        DirectionalLight viewDirectionalLight = new DirectionalLight(directionalLight);
        Vector4f newPosition = new Vector4f(directionalLight.getDirection(), 0);
        newPosition.mul(viewMatrix);
        viewDirectionalLight.setDirection(new Vector3f(
            newPosition.x,
            newPosition.y,
            newPosition.z
        ));

        sceneShaderProgram.setUniform("directionalLight", viewDirectionalLight);
    }

    private void renderSkyBox(Window window, Camera camera, Scene scene){
        SkyBox skyBox = scene.getSkyBox();
        
        if(skyBox == null){
            return;
        }
        
        
        skyBoxShaderProgram.bind();
        skyBoxShaderProgram.setUniform("texture_sampler", 0);

        //Set projection matrix uniform
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //Update the view matrix
        Matrix4f viewMatrix = camera.getViewMatrix();
        float m30 = viewMatrix.m30();
        float m31 = viewMatrix.m31();
        float m32 = viewMatrix.m32();
        
        //Make it so skybox is not translated
        viewMatrix.m30(0);
        viewMatrix.m31(0);
        viewMatrix.m32(0);

        Mesh mesh = skyBox.getMesh();

        //Set the model view matrix uniform
        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
        
        //Set uniforms
        skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLighting().getSkyBoxLight());
        skyBoxShaderProgram.setUniform("hasTexture", mesh.getMaterial().hasTexture() ? 1 : 0);
        skyBoxShaderProgram.setUniform("colour", mesh.getMaterial().getAmbientColour());

        mesh.render();

        //Restore view matrix
        viewMatrix.m30(m30);
        viewMatrix.m31(m31);
        viewMatrix.m32(m32);
        
        //Restore the state
        skyBoxShaderProgram.unbind();
    }



    public void cleanup(){
        if(sceneShaderProgram != null){
            sceneShaderProgram.cleanup();
        }

        if(skyBoxShaderProgram != null){
            skyBoxShaderProgram.cleanup();
        }

    }
}