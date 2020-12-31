package duber.game;

import java.io.IOException;
import java.util.Arrays;

import org.joml.Vector3f;
import org.joml.Vector4f;

import duber.engine.IGameLogic;
import duber.engine.MouseInput;
import duber.engine.Scene;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Material;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.Renderer;
import duber.engine.graphics.lighting.DirectionalLight;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.entities.ConcreteEntity;
import duber.engine.entities.SkyBox;
import duber.engine.loaders.MeshLoader;
import duber.game.scenes.Crosshair;

public class Duberant implements IGameLogic {

    private final Renderer renderer;
    private Player player;
    private Crosshair currCrosshair;
    private Scene currentScene;

    private Controls controls;
    private HUD hud;

    public Duberant() {
        hud = new HUD();
        renderer = new Renderer();
        currCrosshair = new Crosshair();
    }

    @Override
    public void init(Window window) throws LWJGLException {
        window.applyOptions();
        try { 
            renderer.init();
            hud.init(window);
        } catch (IOException ioe) {
            throw new LWJGLException("Could not initialize renderer");
        }

        Mesh[] playerMeshes = MeshLoader.load("models/player/player.obj", "/models/player");
        playerMeshes[0].setMaterial(new Material(new Vector4f(1.0f, 1.0f, 1.0f, 1.0f), 1.0f));
        player = new Player(playerMeshes);
        
        controls = new Controls(window, player);

        
        currentScene = new Scene();
        currentScene.setShaded(false);
        currentScene.addConcreteEntity(player.getModel());

        createSceneLighting();
        currentScene.setShaded(true);

        Mesh[] csgoMapMesh = MeshLoader.load("models/map/de_dust2-cs-map/source/de_dust2/de_dust2.obj", "models/map/de_dust2-cs-map/source/de_dust2");
        ConcreteEntity map = new ConcreteEntity(csgoMapMesh);
        map.getTransform().setScale(0.3f);
        map.getTransform().rotate(270.0f, 0f, 0f);
        currentScene.addConcreteEntitys(new ConcreteEntity[]{map});

        Mesh[] skyBoxMesh = MeshLoader.load("models/skybox/skybox.obj", "models/skybox");
        SkyBox skyBox = new SkyBox(skyBoxMesh[0]);
        skyBox.getTransform().setScale(4000.0f);
        
        currentScene.setSkyBox(skyBox);
        
    }

    private void createSceneLighting() {
        SceneLighting sceneLight = new SceneLighting();
        currentScene.setSceneLighting(sceneLight);

        // Ambient Light
        sceneLight.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));
        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

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
        player.update();
    }

    @Override
    public void render(Window window) {
        renderer.render(window, player.getCamera(), currentScene);
        hud.displayCrosshair(window, currCrosshair, window.getWidth()/2, window.getHeight()/2);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        currentScene.cleanup();
    }
}