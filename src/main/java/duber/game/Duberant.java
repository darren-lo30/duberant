package duber.game;

import java.io.IOException;

import org.joml.Vector3f;

import duber.engine.IGameLogic;
import duber.engine.MouseInput;
import duber.engine.Scene;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.Renderer;
import duber.engine.graphics.lighting.DirectionalLight;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.entities.RenderableEntity;
import duber.engine.entities.SkyBox;
import duber.engine.loaders.MeshLoader;

public class Duberant implements IGameLogic {

    private final Renderer renderer;
    private final DuberantPhysicsWorld physicsWorld;

    private Player currPlayer;
    private Scene currScene;

    private Controls controls;
    private HUD hud;

    public Duberant() {
        hud = new HUD();
        physicsWorld = new DuberantPhysicsWorld();
        renderer = new Renderer();
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
        
        currScene = new Scene();
        currScene.setShaded(false); 
        
        createSceneLighting();


        Mesh[] playerMesh = MeshLoader.load("models/player/model.obj", "models/player");
        currPlayer = new Player(playerMesh);
        addRenderableDynamicEntity(currPlayer.getModel());
        
        controls = new Controls(window, currPlayer);

        Mesh[] csgoMapMesh = MeshLoader.load("models/map/de_dust2-cs-map/source/de_dust2/de_dust2.obj", "models/map/de_dust2-cs-map/source/de_dust2");
        RenderableEntity map = new RenderableEntity(csgoMapMesh);
        map.getTransform().setScale(0.3f);
        map.getTransform().rotateDegrees(90.0f, 0f, 0f);
        addRenderableConstantEntity(map);

        Mesh[] skyBoxMesh = MeshLoader.load("models/skybox/skybox.obj", "models/skybox");
        SkyBox skyBox = new SkyBox(skyBoxMesh[0]);
        skyBox.getTransform().setScale(3000.0f);
        currScene.setSkyBox(skyBox);
    }

    private void createSceneLighting() {
        SceneLighting sceneLight = new SceneLighting();
        currScene.setSceneLighting(sceneLight);

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

    private void addRenderableDynamicEntity(RenderableEntity entity) {
        currScene.addRenderableEntity(entity);
        physicsWorld.addDynamicEntity(entity);
    }

    private void addRenderableConstantEntity(RenderableEntity entity) {
        currScene.addRenderableEntity(entity);
        physicsWorld.addConstantEntity(entity);
    }



    @Override
    public void input(Window window, MouseInput mouseInput) {
        controls.input(mouseInput);
    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        physicsWorld.update();
        currPlayer.updateCamera();
    }

    @Override
    public void render(Window window) {
        renderer.render(window, currPlayer.getCamera(), currScene);
        hud.displayCrosshair(window, currPlayer.getCrosshair(), window.getWidth()/2, window.getHeight()/2);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        currScene.cleanup();
    }
}