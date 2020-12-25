package duber.game;

import java.io.IOException;

import org.joml.Vector3f;

import duber.engine.IGameLogic;
import duber.engine.MouseInput;
import duber.engine.Scene;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Camera;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.Renderer;
import duber.engine.graphics.lighting.DirectionalLight;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.items.GameItem;
import duber.engine.loaders.MeshLoader;
import duber.game.scenes.Crosshair;

public class Duberant implements IGameLogic {

    private final Renderer renderer;
    private final Camera camera;
    private Crosshair currCrosshair;
    private Scene currentScene;

    private Controls controls;
    private HUD hud;

    public Duberant() {
        hud = new HUD();
        renderer = new Renderer();
        camera = new Camera();
        currCrosshair = new Crosshair();
    }

    @Override
    public void init(Window window) throws LWJGLException {
        window.applyOptions();
        try { 
            renderer.init(window);
            hud.init(window);
        } catch (IOException ioe){
            throw new LWJGLException("Could not initialize renderer");
        }
        controls = new Controls(window);

        
        currentScene = new Scene();
        currentScene.setShaded(false);

        createSceneLighting();


        currentScene.setShaded(true);

        /*
        try {

            float reflectance = 1f;
            Mesh cubeMesh = OBJLoader.loadMesh("/models/cube.obj");
            Material cubeMaterial = new Material(new Vector4f(0, 1, 0, 1), reflectance);
            cubeMesh.setMaterial(cubeMaterial);
            GameItem cubeGameItem = new GameItem(cubeMesh);
            cubeGameItem.setPosition(0, 0, 0);
            cubeGameItem.setScale(0.5f);
            cubeGameItem.getRotation().rotateX((float)Math.toRadians(45));
            Mesh quadMesh = OBJLoader.loadMesh("/models/plane.obj");
            Material quadMaterial = new Material(new Vector4f(0.0f, 0.0f, 1.0f, 1.0f), 1.0f);
            quadMesh.setMaterial(quadMaterial);
            GameItem quadGameItem = new GameItem(quadMesh);
            quadGameItem.setPosition(0, -1, 0);
            quadGameItem.setScale(2.5f);
            currentScene.addGameItems(new GameItem[]{quadGameItem, cubeGameItem});
        } catch (Exception e){}
        /*
        Mesh skyBoxMesh = OBJLoader.loadMesh("/models/skybox.obj");
        skyBoxMesh.setMaterial(new Material(new Texture("textures/skybox.png")));
        SkyBox skyBox = new SkyBox(skyBoxMesh);
        skyBox.setScale(10.0f);
        currentScene.setSkyBox(skyBox);
        */

        
        Mesh[] csgoMapMesh = MeshLoader.load("models/map/de_dust2-cs-map/source/de_dust2/de_dust2.obj", "models/map/de_dust2-cs-map/source/de_dust2");
        GameItem map = new GameItem(csgoMapMesh);
        map.setScale(0.3f);
        map.getRotation().rotateX((float) Math.toRadians(270));
        currentScene.addGameItems(new GameItem[]{map});

        
    }

    private void createSceneLighting(){
        SceneLighting sceneLight = new SceneLighting();
        currentScene.setSceneLighting(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));
        sceneLight.setSkyBoxLight(new Vector3f(0.5f, 0.5f, 0.5f));

        // Directional Light
        float lightIntensity = 0.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        directionalLight.setShadowPositionMultipler(10);
        directionalLight.setOrthoCoord(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
        sceneLight.setDirectionalLight(directionalLight);
    }


    @Override
    public void input(Window window, MouseInput mouseInput) {
        controls.input();
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        controls.updateCameraView(camera, mouseInput);
    }

    @Override
    public void render(Window window) {
        renderer.render(window, camera, currentScene);
        hud.displayCrosshair(window, currCrosshair, window.getWidth()/2, window.getHeight()/2);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        currentScene.cleanup();
    }
}