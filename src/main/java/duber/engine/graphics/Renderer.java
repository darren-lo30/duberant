package duber.engine.graphics;

import duber.engine.entities.Entity;
import duber.engine.entities.components.Animation;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Transform;
import duber.engine.entities.Camera;
import duber.engine.graphics.animations.AnimationFrame;
import duber.engine.graphics.lighting.*;
import duber.engine.utilities.Utils;
import duber.engine.Cleansable;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

/**
 * A renderer for a 3D world
 * @author Darren Lo
 * @version 1.0
 */
public class Renderer implements Cleansable {    
    /** The maximum number of point lights. */
    private static final int MAX_POINT_LIGHTS = 100;
    /** The maximum number of spot lights. */
    private static final int MAX_SPOT_LIGHTS = 100;
    
    /** The field of view. */
    private static final float FOV = (float) Math.toRadians(60.0f);

    /** The distance of the closest objects to render. */
    private static final float Z_NEAR = 0.01f;

    /** The distance of the furthest objects to render */
    private static final float Z_FAR = 6000.0f;

    /** The program used to render scenes. */
    private ShaderProgram sceneShaderProgram;

    /** The program used to render the sky box.*/
    private ShaderProgram skyBoxShaderProgram;

    /** The MatrixTransformer used to compute matrices for rendering. */
    private final MatrixTransformer matrixTransformer;

    /**
     * Constructs a renderer.
     * @throws LWJGLException if this Renderer could not be intialized
     * @throws IOException if an IO error occurs while initializing this Renderer
     */
    public Renderer() throws LWJGLException, IOException {
        //Intiialize vertex transformer
        matrixTransformer = new MatrixTransformer();
        setUpSkyBoxShader();
        setUpSceneShader();    
    }

    /**
     * Sets up a scene shader program.
     * @throws LWJGLException if the scene shader could not be intialized
     * @throws IOException if an IO error occurs while initializing the scene shader
     */
    private void setUpSceneShader() throws LWJGLException, IOException {
        sceneShaderProgram = new ShaderProgram();

        //Compile shader programs
        sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/scene_vertex.vs"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/scene_fragment.fs"));
        sceneShaderProgram.link();    

        //Create uniforms for matrixTransformer matrices
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewMatrix");

        //Create texture sampler uniform
        sceneShaderProgram.createUniform("texture_sampler");
        sceneShaderProgram.createUniform("normalMap");

        //Create material uniform
        sceneShaderProgram.createMaterialUniform("material");
        
        //Animations
        sceneShaderProgram.createUniform("jointsMatrix");

        //Create uniform for lights
        sceneShaderProgram.createUniform("ambientLight");        
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS); 
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS); 
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");  
    }

    /**
     * Sets up a sky box shader program.
     * @throws LWJGLException if the sky box shader could not be intialized
     * @throws IOException if an IO error occurs while initializing the sky box shader
     */
    private void setUpSkyBoxShader() throws LWJGLException, IOException {
        skyBoxShaderProgram = new ShaderProgram();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/sky_box_vertex.vs"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/sky_box_fragment.fs"));
        skyBoxShaderProgram.link();

        //Create uniforms for matrixTransformer matrices
        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");

        //Create texture sampler uniform
        skyBoxShaderProgram.createUniform("texture_sampler");

        skyBoxShaderProgram.createUniform("ambientLight");

        skyBoxShaderProgram.createUniform("hasTexture");
        skyBoxShaderProgram.createUniform("colour");
    }

    /**
     * Renders to a window.
     * @param window the window to display on
     * @param camera the Camera to view from
     * @param scene the Scene to render
     */
    public void render(Window window, Camera camera, Scene scene) {        
        //Update camera view matrix
        camera.updateViewMatrix();

        window.updateProjectionMatrix(FOV, Z_NEAR, Z_FAR);
        
        renderScene(window, camera, scene);
    }

    /**
     * Renders all the meshes in a scene.
     * @param scene the scene to render
     * @param viewMatrix the viewMatrix to transform the scene with
     */
    private void renderMeshes(Scene scene, Matrix4f viewMatrix) {
        Map<Mesh, List<Entity>> meshMap = scene.getMeshMap();

        for(Map.Entry<Mesh, List<Entity>> meshMapEntry: meshMap.entrySet()) {
            Mesh renderableMesh = meshMapEntry.getKey();
            List<Entity> entities = meshMapEntry.getValue();
            if (viewMatrix != null) {
                sceneShaderProgram.setUniform("material", renderableMesh.getMaterial());
            }

            renderableMesh.render(entities, (Entity entity) -> {    
                Transform entityTransform = entity.getComponent(Transform.class);           

                Matrix4f modelViewMatrix;
                if (entityTransform.isRelativeView()) {
                    modelViewMatrix = matrixTransformer.buildModelViewMatrix(entityTransform, viewMatrix);
                } else {
                    modelViewMatrix = matrixTransformer.buildModelMatrix(entityTransform);
                }

                if (entity.hasComponent(Animation.class)) {
                    AnimationFrame frame = entity.getComponent(Animation.class).getCurrentAnimation().getCurrentFrame();
                    sceneShaderProgram.setUniform("jointsMatrix", frame.getJointMatrices());
                }
                
                sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            });
        }
    }

    /**
     * Renders a scene.
     * @param window the window to display on
     * @param camera the Camera to view from
     * @param scene the Scene to render
     */
    private void renderScene(Window window, Camera camera, Scene scene) {
        sceneShaderProgram.bind();

        //Set projection matrix uniform
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);

        //Get view matrices
        Matrix4f viewMatrix = camera.getViewMatrix();

        //Render lighting
        renderLights(viewMatrix, scene.getSceneLighting());

        //Set texture uniforms
        sceneShaderProgram.setUniform("texture_sampler", 0);
        sceneShaderProgram.setUniform("normalMap", 1);
        
        //Draw the renderableMesh
        renderMeshes(scene, viewMatrix);

        sceneShaderProgram.unbind();

        renderSkyBox(window, camera, scene);
    }

    /**
     * Renders lighting inside a Scene.
     * @param viewMatrix the matrix to transform the lighting with
     * @param sceneLighting the scene lighting
     */
    private void renderLights(Matrix4f viewMatrix, SceneLighting sceneLighting) {
        //Update light uniforms for ambient lighting
        sceneShaderProgram.setUniform("ambientLight", sceneLighting.getAmbientLight());
        sceneShaderProgram.setUniform("specularPower", sceneLighting.getSpecularPower());

        renderPointLights(viewMatrix, sceneLighting.getPointLights());
        renderSpotLights(viewMatrix, sceneLighting.getSpotLights());
        renderDirectionalLight(viewMatrix, sceneLighting.getDirectionalLight()); 
    }

    /**
     * Renders point lights.
     * @param viewMatrix the matrix to transform the point lights with
     * @param pointLights the array of PointLights
     */
    private void renderPointLights(Matrix4f viewMatrix, PointLight[] pointLights) {
        if (pointLights == null) {
            return;
        }
        
        for(int i = 0; i<pointLights.length; i++) {
            if (pointLights[i] == null) {
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

    /**
     * Renders spot lights.
     * @param viewMatrix the matrix to transform the spot lights with
     * @param spotLights the array of SpotLights
     */
    private void renderSpotLights(Matrix4f viewMatrix, SpotLight[] spotLights) {
        if (spotLights == null) {
            return;
        }
        
        for(int i = 0; i<spotLights.length; i++) {
            if (spotLights[i] == null) {
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

    /**
     * Renders the directional light.
     * @param viewMatrix the matrix to transform the directional light with
     * @param directionalLight the directional light.
     */
    private void renderDirectionalLight(Matrix4f viewMatrix, DirectionalLight directionalLight) {
        if (directionalLight == null) {
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
    
    /**
     * Renders the skybox.
     * @param window the window the render to
     * @param camera the camera to view from
     * @param scene the scene whose SkyBox to render
     */
    private void renderSkyBox(Window window, Camera camera, Scene scene) {  
        Entity skyBox = scene.getSkyBox();

        if (skyBox == null) {
            return;
        }

        Mesh renderableMesh = skyBox.getComponent(MeshBody.class).getMesh();
        
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

        //Set the model view matrix uniform
        Matrix4f modelViewMatrix = matrixTransformer.buildModelViewMatrix(skyBox.getComponent(Transform.class), viewMatrix);
        
        //Set uniforms
        skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
        skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLighting().getSkyBoxLight());
        skyBoxShaderProgram.setUniform("hasTexture", renderableMesh.getMaterial().hasTexture() ? 1 : 0);
        skyBoxShaderProgram.setUniform("colour", renderableMesh.getMaterial().getAmbientColour());

        renderableMesh.render();

        //Restore view matrix
        viewMatrix.m30(m30);
        viewMatrix.m31(m31);
        viewMatrix.m32(m32);
        
        //Restore the state
        skyBoxShaderProgram.unbind();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanup();
        }

        if (skyBoxShaderProgram != null) {
            skyBoxShaderProgram.cleanup();
        }

    }
}