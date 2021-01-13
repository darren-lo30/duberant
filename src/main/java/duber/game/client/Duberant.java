package duber.game.client;

import duber.engine.GameEngine;
import duber.engine.IGameLogic;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.game.User;
import duber.game.client.GameStateManager.GameStateOption;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;


public class Duberant implements IGameLogic {
    private Window window;
    private User user;
    private ClientNetwork clientNetwork;
    private GameStateManager gameStateManager;
    
    public Duberant() {
        clientNetwork = new ClientNetwork();
    }
    
    @Override
    public void init(Window window) throws LWJGLException {
        this.window = window;
        window.applyOptions();

        gameStateManager = new GameStateManager(this);
        gameStateManager.pushState(GameStateOption.MAIN_MENU);
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

        //Open up options panel
        if(window.getKeyboardInput().isKeyPressed(GLFW_KEY_ESCAPE)) {
            gameStateManager.pushState(GameStateOption.OPTIONS_MENU);
        }
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
            IGameLogic gameLogic = new Duberant();
            GameEngine gameEngine = new GameEngine("Duberant", 1000, 1000, gameLogic);
            gameEngine.run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}

