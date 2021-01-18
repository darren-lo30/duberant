package duber.game.client;

import duber.engine.GameEngine;
import duber.engine.GameLogic;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.game.User;
import duber.game.client.GameStateManager.GameStateOption;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;


public class Duberant extends GameLogic {
    private Window window;
    private User user;
    private ClientNetwork clientNetwork;
    private GameStateManager gameStateManager;

    private GameStateKeyListener optionsListener;
    
    public Duberant() {
        clientNetwork = new ClientNetwork();
    }
    
    @Override
    public void init(Window window) throws LWJGLException {
        this.window = window;
        window.applyOptions();

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
        gameStateManager.cleanup();
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

