package duber.game.client.match;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import duber.engine.Cleansable;
import duber.engine.Window;
import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.entities.components.Vision;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Transform;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.Renderer;
import duber.engine.graphics.Scene;
import duber.engine.loaders.MeshLoader;
import duber.game.gameobjects.Player;
import duber.game.client.Duberant;
import duber.game.client.GameState;
import duber.game.networking.MatchInitializePacket;
import duber.game.networking.PlayerPositionPacket;
import duber.game.networking.UserInputPacket;
import duber.game.User;

public class Match extends GameState implements Cleansable {
    // Used to render the game
    private Renderer renderer;
    private HUD hud;
    private Scene gameScene;
    private Player mainPlayer;

    private Map<User, Player> players;
    private MatchInitializePacket receivedMatchData;

    private Scoreboard scoreboard;

    // Whether or not it has received data from server
    private volatile boolean initialized = false;

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

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
        initialized = false;
        gameScene.clear();
        new Thread(new GetMatchData()).start();
    }

    @Override
    public void enter() {
        getGame().getWindow().setOption(Window.Options.SHOW_CURSOR, false);
        getGame().getWindow().applyOptions();
    }

    @Override
    public void close() {
        //Nothing to do on close
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }

    @Override
    public void update() {
        if (!initialized && receivedMatchData != null) {
            try {
                initializeMatch(receivedMatchData);
            } catch (LWJGLException le) {
                System.out.println("Unable to initialize match data");
            }
        } else if (initialized) {
            Window window = getGame().getWindow();
            
            //If the current window is focused, then send user inputs to server
            if(isFocused()) {
                //Send the command 
                UserInputPacket matchCommands = new UserInputPacket(window.getKeyboardInput(), window.getMouseInput());
                getGame().getUser().getConnection().sendUDP(matchCommands);
            }

            //Update player transforms
            receiveMatchUpdate();
        }
    }

    private void receiveMatchUpdate() {
        while(!getGame().getClientNetwork().getPackets().isEmpty()){
            Object packet = getGame().getClientNetwork().getPackets().poll();
            if(packet instanceof PlayerPositionPacket) {
                PlayerPositionPacket playerPositionData = (PlayerPositionPacket) packet;
                Player modifiedPlayer = getPlayer(playerPositionData.userId);

                if(modifiedPlayer != null) {
                    //Update the player position and camera
                    modifiedPlayer.getComponent(Transform.class).set(playerPositionData.playerTransform);
                    modifiedPlayer.getView().getComponent(Transform.class).set(playerPositionData.cameraTransform);
                }
            }
        }
    }

    @Override
    public void render() {
        if (!initialized) {
            hud.displayText("Loading...");
        } else {
            Duberant game = getGame();
            Window window = game.getWindow();

            renderer.render(window, mainPlayer.getComponent(Vision.class).getCamera(), gameScene);
            hud.displayCrosshair(game.getUser().getCrosshair(), window.getWidth() / 2, window.getHeight() / 2);
        }
    }


    private Player getPlayer(int userId) {
        for (Entry<User, Player> playersEntry : players.entrySet()) {
            if(playersEntry.getKey().getId() == userId) {
                return playersEntry.getValue();
            }
        }

        return null;
    }

    private void initializeMatch(MatchInitializePacket matchData) throws LWJGLException {
        System.out.println("received iniitlaize match packet");

        
        players = matchData.players;
        mainPlayer = getPlayer(getGame().getUser().getId());
        if(mainPlayer == null) {
            throw new IllegalStateException("The current user is not in the game");
        }

        //Set player meshes
        Mesh[] playerMeshes = MeshLoader.load(matchData.playerModel.modelFile, matchData.playerModel.textureDirectory);
        for(Player player : players.values()) {
            MeshBody playerMeshBody = new MeshBody(playerMeshes, true);
            
            if(player == mainPlayer) {
                playerMeshBody.setVisible(true);
            }

            player.addComponent(playerMeshBody);
        }

        //Set map meshes
        Mesh[] mapMeshes = MeshLoader.load(matchData.mapModel.modelFile, matchData.mapModel.textureDirectory);
        Entity map = matchData.map;
        map.addComponent(new MeshBody(mapMeshes, true));

        //Set skybox mesh
        Mesh[] skyBoxMeshes = MeshLoader.load(matchData.skyBoxModel.modelFile, matchData.skyBoxModel.textureDirectory);
        SkyBox skyBox = matchData.skyBox;
        skyBox.addComponent(new MeshBody(skyBoxMeshes, true));

        //Add all enitties to the scene
        for(Player player: players.values()) {
            gameScene.addRenderableEntity(player);
        }

        gameScene.addRenderableEntity(map);
        gameScene.setSkyBox(skyBox);

        gameScene.setSceneLighting(matchData.gameLighting);        
        initialized = true;
    }

    /**
     * Initializes match on another thread
     */
    private class GetMatchData implements Runnable {
        @Override
        public void run() {
            try {
                while(receivedMatchData == null && getGame().isLoggedIn()) {
                    Object packet = getGame().getClientNetwork().getPackets().take();
                    
                    //Initialize match in main thread because of OpenGL requirements and GL Context
                    if(packet instanceof MatchInitializePacket) {
                        receivedMatchData = (MatchInitializePacket) packet;
                    } 
                }
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }
}