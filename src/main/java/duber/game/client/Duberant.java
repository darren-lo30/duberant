package duber.game.client;

import duber.engine.GameEngine;
import duber.engine.IGameLogic;
import duber.engine.Window;
import duber.engine.audio.SoundManager;
import duber.engine.exceptions.LWJGLException;
import duber.game.User;
import duber.game.client.GameStateManager.GameStateOption;

public class Duberant implements IGameLogic {
    private Window window;
    private User user;
    private ClientNetwork clientNetwork;
    private GameStateManager gameStateManager;
    private SoundManager soundManager;
    
    public Duberant() {
        clientNetwork = new ClientNetwork();
        soundManager = new SoundManager();
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

