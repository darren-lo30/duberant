package duber.game.client.match;

import java.io.IOException;
import java.util.Map;

import duber.engine.Cleansable;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Renderer;
import duber.engine.graphics.Scene;
import duber.game.Player;
import duber.game.client.Duberant;
import duber.game.client.GameState;
import duber.game.networking.MatchInitializePacket;
import duber.game.User;

public class Match extends GameState implements Cleansable {
    // Used to render the game
    private Renderer renderer;
    private HUD hud;
    private Scene gameScene;
    private Player mainPlayer;

    private Map<User, Player> players;

    //Whether or not it has received data from server
    private volatile boolean initialized = false;

    @Override
    public void init() throws LWJGLException {
        try {
            renderer = new Renderer();
            hud = new HUD(getGame().getWindow());
        } catch (IOException ioe) {
            throw new LWJGLException(ioe.getMessage());
        }

        gameScene = new Scene();
    }

    @Override
    public void startup() {
        gameScene.clear();
        initialized = false;

        if(!getGame().isLoggedIn()) {
            System.out.println("Not logged in");
        }

        //Turn off cursor
        getGame().getWindow().setOption(Window.Options.SHOW_CURSOR, false);
        getGame().getWindow().applyOptions();

        new Thread(new InitializeMatchJob()).start();
    }

    @Override
    public void update() {
        //TODO
    }

    @Override
    public void render() {
        if(!initialized) {
            hud.displayText("Loading...");        
        } else {
            Duberant game = getGame();
            Window window = game.getWindow();
    
            renderer.render(window, mainPlayer.getCamera(), gameScene);
            hud.displayCrosshair(game.getUser().getCrosshair(), window.getWidth() / 2, window.getHeight() / 2);
        }
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }

    /**
     * Initializes match on another thread
     */
    private class InitializeMatchJob implements Runnable {
        @Override
        public void run() {
            try {
                while(!initialized && getGame().isLoggedIn()) {
                    Object packet = getGame().getClientNetwork().getPackets().take();
    
                    if(packet instanceof MatchInitializePacket) {
                        initializeMatch((MatchInitializePacket) packet);
                        initialized = true;
                    } 
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        private void initializeMatch(MatchInitializePacket matchInitializePacket) {
            System.out.println("received iniitlaiz e match pakcet");
        }
    
    }
}