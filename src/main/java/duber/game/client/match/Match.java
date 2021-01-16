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
import duber.game.networking.PlayerDataPacket;
import duber.game.networking.UserInputPacket;

public class Match extends GameState implements Cleansable {
    // Used to render the game
    private Renderer renderer;
    private HUD hud;
    private Scene gameScene;
    private Player mainPlayer;

    private Map<Integer, Player> playersById = new HashMap<>();

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
        Window window = getGame().getWindow();
        
        //If the current window is focused, the match has been initialized
        if(isFocused() && initialized) {
            //Send the command 
            UserInputPacket matchCommands = new UserInputPacket(window.getKeyboardInput(), window.getMouseInput());
            getGame().getUser().getConnection().sendUDP(matchCommands);
        }

        //Receive any match data from the server
        try {
            receiveMatchUpdate();
        } catch (LWJGLException le) {
            System.out.println("Received bad data from server");
        }
    }

    @Override
    public void render() {
        Duberant game = getGame();
        Window window = game.getWindow();

        if (!initialized) {
            hud.displayText("Loading...");
        } else if(mainPlayer.isAlive()) {
            renderer.render(window, mainPlayer.getComponent(Vision.class).getCamera(), gameScene);
            hud.displayCrosshair(game.getUser().getCrosshair(), window.getWidth() / 2, window.getHeight() / 2);
        } else {
            hud.displayText("You died...");
        }
        
    }

    private void receiveMatchUpdate() throws LWJGLException {
        while(!getGame().getClientNetwork().getPackets().isEmpty()){
            Object packet = getGame().getClientNetwork().getPackets().poll();
            
            if(!initialized) {
                //If the game is not initialized wait for a match initializiation packet
                if(packet instanceof MatchInitializePacket) {
                    processPacket((MatchInitializePacket) packet);
                }
            } else {
                //If the game is already initialized, listen for any game updates
                if(packet instanceof PlayerDataPacket) {
                    processPacket((PlayerDataPacket) packet);
                }
            }
        }
    }

    private void processPacket(MatchInitializePacket matchInitializePacket) throws LWJGLException {        
        List<Player> players = matchInitializePacket.players;
        for(Player player: players) {
            playersById.put(player.getId(), player);
        }

        mainPlayer = getPlayerById(matchInitializePacket.mainPlayerId);
        
        if(mainPlayer == null) {
            throw new IllegalStateException("The current user is not in the game");
        }


        //Set player meshes
        Mesh[] playerMeshes = MeshLoader.load(matchInitializePacket.playerModel);
        for(Player player : getPlayers()) {
            MeshBody playerMeshBody = new MeshBody(playerMeshes, true);
            
            if(player == mainPlayer) {
                playerMeshBody.setVisible(true);
            }

            player.addComponent(playerMeshBody);
        }

        //Set map meshes
        Mesh[] mapMeshes = MeshLoader.load(matchInitializePacket.mapModel);
        Entity map = matchInitializePacket.map;
        map.addComponent(new MeshBody(mapMeshes, true));

        //Set skybox mesh
        Mesh[] skyBoxMeshes = MeshLoader.load(matchInitializePacket.skyBoxModel);
        SkyBox skyBox = matchInitializePacket.skyBox;
        skyBox.addComponent(new MeshBody(skyBoxMeshes, true));

        //Add all enitties to the scene
        for(Player player: getPlayers()) {
            gameScene.addRenderableEntity(player);
        }

        gameScene.addRenderableEntity(map);
        gameScene.setSkyBox(skyBox);

        gameScene.setSceneLighting(matchInitializePacket.gameLighting);        
        initialized = true;
    }

    private void processPacket(PlayerDataPacket playerDataPacket) {
        Player modifiedPlayer = getPlayerById(playerDataPacket.playerId);

        if(modifiedPlayer != null) {
            //Update the player position and camera
            modifiedPlayer.getComponent(Transform.class).set(playerDataPacket.playerTransform);
            modifiedPlayer.getView().getComponent(Transform.class).set(playerDataPacket.cameraTransform);

            //Update the player's data
            modifiedPlayer.getPlayerData().set(playerDataPacket.playerData);

            //Remove the player from being rendered if they died
            if(!modifiedPlayer.isAlive()) {
                gameScene.removeRenderableEntity(modifiedPlayer);
            }
        }
    }

    
    @Override
    public void cleanup() {
        renderer.cleanup();
    }
}