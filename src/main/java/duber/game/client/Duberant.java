package duber.game.client;

import org.lwjgl.openal.AL11;

import duber.engine.GameEngine;
import duber.engine.GameLogic;
import duber.engine.Window;
import duber.engine.audio.SoundListener;
import duber.engine.audio.SoundManager;
import duber.engine.exceptions.LWJGLException;
import duber.game.SoundData;
import duber.game.User;
import duber.game.client.GameStateManager.GameStateOption;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import java.io.IOException;

import org.joml.Vector3f;


public class Duberant extends GameLogic {
    private Window window;
    private User user;
    private ClientNetwork clientNetwork;
    private GameStateManager gameStateManager;
    private SoundManager soundManager;
    private GameStateKeyListener optionsListener;
    
    public Duberant() {
        clientNetwork = new ClientNetwork();
    }
    
    @Override
    public void init(Window window) throws LWJGLException {
        this.window = window;
        window.applyOptions();

        soundManager = new SoundManager();
        soundManager.setAttenuationModel(AL11.AL_EXPONENT_DISTANCE);    
        soundManager.setListener(new SoundListener(new Vector3f()));
        
        try {
            SoundData.loadSounds(soundManager);
        } catch (IOException ioe) {
            throw new LWJGLException("Could not load sound files");
        }

        gameStateManager = new GameStateManager(this);
        gameStateManager.pushState(GameStateOption.MAIN_MENU);

        optionsListener = new GameStateKeyListener(GLFW_KEY_ESCAPE, GameStateOption.OPTIONS_MENU);

    }

    public Window getWindow() {
        return window;
    }
    
    public User getUser() {
        return user;
    }

    public ClientNetwork getClientNetwork() {
        return clientNetwork;
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public SoundManager getSoundManager() {
        return soundManager;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isConnected() {
        return clientNetwork.isConnected();
    }

    public boolean isLoggedIn() {
        return user != null && user.isLoggedIn();
    }

    @Override
    public void update() {
        gameStateManager.update();
        optionsListener.listenToActivate(window.getKeyboardInput());
    }

    @Override
    public void render() {
        gameStateManager.render();
    }

    @Override
    public void cleanup() {
        clientNetwork.close();
        gameStateManager.cleanup();
        soundManager.cleanup();
    }

    public static void main(String[] args) {
        try {
            GameLogic gameLogic = new Duberant();
            GameEngine gameEngine = new GameEngine("Duberant", 1920, 1080, gameLogic);
            gameEngine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

