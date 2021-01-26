package duber.game.client;

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
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import java.io.IOException;

import org.joml.Vector3f;
import org.lwjgl.glfw.GLFWKeyCallbackI;


/**
 * The game logic used to run the main game.
 * @author Darren Lo
 * @version 1.0
 */
public class Duberant extends GameLogic {
    /** The Window used to display the game. */
    private Window window;

    /** The User logged in and that is playing the game. */
    private User user;
    
    /** The ClientNetork used to connect to the server with. */
    private ClientNetwork clientNetwork;

    /** The manager of the game's GameStates. */
    private GameStateManager gameStateManager;

    /** The manager of the audio played in the game. */
    private SoundManager soundManager;
    
    /**
     * Constructs a new instance of the game.
     */
    public Duberant() {
        clientNetwork = new ClientNetwork();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void init(Window window) throws LWJGLException {
        this.window = window;
        window.applyOptions();

        soundManager = new SoundManager();
        soundManager.setListener(new SoundListener(new Vector3f()));
        
        try {
            SoundData.loadSounds(soundManager);
        } catch (IOException ioe) {
            throw new LWJGLException("Could not load sound files");
        }

        gameStateManager = new GameStateManager(this);
        gameStateManager.pushState(GameStateOption.MAIN_MENU);
        
        configureOptionsMenuCallback();
    }

    /**
     * Gets the game window.
     * @return the game window
     */
    public Window getWindow() {
        return window;
    }
    
    /**
     * Gets the user playing the game.
     * @return the user playing the game
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the user that is playing the game.
     * @param user the user playing the game
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Gets the client network used to connect to the server.
     * @return the client network
     */
    public ClientNetwork getClientNetwork() {
        return clientNetwork;
    }

    /**
     * Gets the manager that manages all the GameStates.
     * @return the game state manager
     */
    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    /**
     * Gets the manager that manages game sounds.
     * @return the sound manager
     */
    public SoundManager getSoundManager() {
        return soundManager;
    }

    /**
     * Determines if the game is connected to a server.
     * @return whether or not the game is connected to a server
     */
    public boolean isConnected() {
        return clientNetwork.isConnected();
    }

    /**
     * Determines if the game is logged in to a user account.
     * @return whether or not the game is logged in
     */
    public boolean isLoggedIn() {
        return user != null && user.isLoggedIn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        gameStateManager.update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        gameStateManager.render();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        clientNetwork.close();
        gameStateManager.cleanup();
        soundManager.cleanup();
    }

    /**
     * Configures the callbacks used to open the OptionsMenu.
     */
    private void configureOptionsMenuCallback() {
        GLFWKeyCallbackI optionsCallback = (windowHandle, keyCode, scanCode, action, mods) -> {
            GameState optionMenu = GameStateOption.OPTIONS_MENU.getGameState();
            if (keyCode == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                if (optionMenu.isOpened()) {
                    optionMenu.setShouldClose(true);
                } else {
                    optionMenu.pushSelf();
                }
            }
        };

        window.addCallback(optionsCallback);
    }

    /**
     * Runs the game in 1920x1080 resolution.
     * @param args the argiments from the command line
     */
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

