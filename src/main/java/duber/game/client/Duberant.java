package duber.game.client;

import java.io.IOException;

import org.joml.Vector3f;
import duber.engine.IGameLogic;
import duber.engine.KeyboardInput;
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
import duber.game.DuberantPhysicsWorld;

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
        //window.getOptions().setOption(Window.Options.DISPLAY_TRIANGLES, true);
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


        Mesh[] playerMesh = MeshLoader.load("models/cube/cube.obj", "models/cube");
        currPlayer = new Player(playerMesh);
        addRenderableDynamicEntity(currPlayer.getModel());
        
        controls = new Controls(currPlayer);

        Mesh[] csgoMapMesh = MeshLoader.load("models/map/map.obj", "models/map");
        RenderableEntity map = new RenderableEntity(csgoMapMesh);
        map.getTransform().setScale(0.3f);
        addRenderableConstantEntity(map);
        
        
        Mesh[] terrainMesh = MeshLoader.load("models/castle/castle.obj", "models/castle");
        RenderableEntity terrain = new RenderableEntity(terrainMesh);
        terrain.getTransform().setScale(100.0f);
        //addRenderableConstantEntity(terrain);
        
        Mesh[] cubeMesh = MeshLoader.load("models/cube/cube.obj", "models/cube");
        RenderableEntity cube = new RenderableEntity(cubeMesh);
        cube.getTransform().setScale(5.0f);
        cube.getTransform().getPosition().set(0, 0, 0);
        //addRenderableConstantEntity(cube);


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
    public void update(float interval, MouseInput mouseInput, KeyboardInput keyboardInput) {
        controls.update(mouseInput, keyboardInput);
        currPlayer.updateCamera();
        physicsWorld.update();
    }

    @Override
    public void render(Window window, float alpha) {
        renderer.render(window, currPlayer.getCamera(), currScene);
        hud.displayCrosshair(window, currPlayer.getCrosshair(), window.getWidth()/2, window.getHeight()/2);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        currScene.cleanup();
    }
}

