package duber.game.client.match;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import duber.game.networking.PlayerDeathPacket;
import duber.game.networking.PlayerDataPacket;
import duber.game.networking.UserInputPacket;

public class Match extends GameState implements Cleansable {
    // Used to render the game
    private Renderer renderer;
    private HUD hud;
    private Scene gameScene;
    private Player mainPlayer;

    private Map<Integer, Player> playersById = new HashMap<>();

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

    private Player getPlayerById(int playerId) {
        return playersById.get(playerId);
    }

    private Collection<Player> getPlayers() {
        return playersById.values();
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

            //Receive any match data from the server
            receiveMatchUpdate();
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


    private void receiveMatchUpdate() {
        while(!getGame().getClientNetwork().getPackets().isEmpty()){
            Object packet = getGame().getClientNetwork().getPackets().poll();
            if(packet instanceof PlayerDataPacket) {
                processPacket((PlayerDataPacket) packet);
            } else if (packet instanceof PlayerDeathPacket) {
                processPacket((PlayerDeathPacket) packet);
            }
        }
    }

    private void processPacket(PlayerDataPacket playerDataPacket) {
        Player modifiedPlayer = getPlayerById(playerDataPacket.playerId);

        if(modifiedPlayer != null) {
            //Update the player position and camera
            modifiedPlayer.getComponent(Transform.class).set(playerDataPacket.playerTransform);
            modifiedPlayer.getView().getComponent(Transform.class).set(playerDataPacket.cameraTransform);

            modifiedPlayer.getPlayerData().set(playerDataPacket.playerData);
        }
    }


    private void processPacket(PlayerDeathPacket playerDeathPacket) {
        Player deadPlayer = getPlayerById(playerDeathPacket.playerId);
        gameScene.removeRenderableEntity(deadPlayer);
    }


    private void initializeMatch(MatchInitializePacket matchData) throws LWJGLException {
        System.out.println("received iniitlaize match packet");

        
        List<Player> players = matchData.players;
        for(Player player: players) {
            playersById.put(player.getId(), player);
        }

        mainPlayer = getPlayerById(matchData.mainPlayerId);
        
        if(mainPlayer == null) {
            throw new IllegalStateException("The current user is not in the game");
        }

        //Set player meshes
        Mesh[] playerMeshes = MeshLoader.load(matchData.playerModel);
        for(Player player : getPlayers()) {
            MeshBody playerMeshBody = new MeshBody(playerMeshes, true);
            
            if(player == mainPlayer) {
                playerMeshBody.setVisible(true);
            }

            player.addComponent(playerMeshBody);
        }

        //Set map meshes
        Mesh[] mapMeshes = MeshLoader.load(matchData.mapModel);
        Entity map = matchData.map;
        map.addComponent(new MeshBody(mapMeshes, true));

        //Set skybox mesh
        Mesh[] skyBoxMeshes = MeshLoader.load(matchData.skyBoxModel);
        SkyBox skyBox = matchData.skyBox;
        skyBox.addComponent(new MeshBody(skyBoxMeshes, true));

        //Add all enitties to the scene
        for(Player player: getPlayers()) {
            gameScene.addRenderableEntity(player);
        }

        gameScene.addRenderableEntity(map);
        gameScene.setSkyBox(skyBox);

        gameScene.setSceneLighting(matchData.gameLighting);        
        initialized = true;
    }
    
    @Override
    public void cleanup() {
        renderer.cleanup();
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