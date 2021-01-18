package duber.game.client.match;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector4f;

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
import duber.game.gameobjects.Scoreboard;
import duber.game.phases.MatchPhaseManager;
import duber.game.client.Duberant;
import duber.game.client.GameState;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.networking.MatchInitializePacket;
import duber.game.networking.MatchPhasePacket;
import duber.game.networking.PlayerDataPacket;
import duber.game.networking.UserInputPacket;
import duber.game.phases.MatchPhase;

public class Match extends GameState implements Cleansable, MatchPhaseManager {
    // Used to render the game
    private Renderer renderer;
    private HUD hud;
    private Scene gameScene;
    private Player mainPlayer;
    
    private Map<Integer, Player> playersById = new HashMap<>();

    private Scoreboard scoreboard;
    private MatchPhase currMatchPhase;

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    @Override
    public void init() {
        try {
            renderer = new Renderer();
            hud = new HUD(getGame().getWindow());
        } catch (IOException | LWJGLException e) {
            leave();
        }

        gameScene = new Scene();
    }

    private Player getPlayerById(int playerId) {
        return playersById.get(playerId);
    }

    private Collection<Player> getPlayers() {
        return playersById.values();
    }

    public Player getMainPlayer() {
        return mainPlayer;
    }

    @Override
    public void startup() {
        mainPlayer = null;
        playersById.clear();
        gameScene.clear();
        currMatchPhase = null;
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
        if(currMatchPhase != null) {
            currMatchPhase.update();
        } else {
            receivePackets();
        }
    }

    @Override
    public void render() {
        if(currMatchPhase != null) {
            currMatchPhase.render();
        } else {
            String matchSearchingMessage = "Finding a match...";
            hud.displayText(matchSearchingMessage, 0.5f, 0.5f, true, HUD.TITLE_FONT);
        }
    }

    public boolean isInitialized() {
        return mainPlayer != null && currMatchPhase != null;
    }

    public void sendPackets() {
        Window window = getGame().getWindow();

        if(isFocused() && isInitialized() && currMatchPhase.playerCanMove()) {
            UserInputPacket matchCommands = new UserInputPacket(window.getKeyboardInput(), window.getMouseInput());
            getGame().getUser().getConnection().sendUDP(matchCommands);
        }
    }


    public void renderGameScene() {
        renderer.render(getGame().getWindow(), mainPlayer.getComponent(Vision.class).getCamera(), gameScene);
        hud.displayMatchHud(this);
    }

    public HUD getHud() {
        return hud;
    }

    public void receivePackets() {
        while(!getGame().getClientNetwork().getPackets().isEmpty()){
            Object packet = getGame().getClientNetwork().getPackets().poll();
        
            if(packet instanceof MatchInitializePacket) {
                processPacket((MatchInitializePacket) packet);
            } else if(packet instanceof MatchPhasePacket) {
                processPacket((MatchPhasePacket) packet);
            }
            
            if(isInitialized()) {
                if(packet instanceof PlayerDataPacket) {
                    processPacket((PlayerDataPacket) packet);
                }
            }
        }
    }

    public void listenInputs() {
        Window window = getGame().getWindow();
        
    }

    private void processPacket(MatchInitializePacket matchInitializePacket) {        
        List<Player> players = matchInitializePacket.players;
        for(Player player: players) {
            playersById.put(player.getId(), player);
        }
        

        mainPlayer = getPlayerById(matchInitializePacket.mainPlayerId);

        try {
            //Set player meshes
            Mesh[] playerMeshes = MeshLoader.load(matchInitializePacket.playerModel);
            for(Player player : getPlayers()) {
                MeshBody playerMeshBody = new MeshBody(playerMeshes, true);
                player.addComponent(playerMeshBody);
            }
    
            //Set mainMap meshes
            Mesh[] mainMapMeshes = MeshLoader.load(matchInitializePacket.mapModel);
            Entity mainMap = matchInitializePacket.gameMap.getMainMap();
            mainMap.addComponent(new MeshBody(mainMapMeshes, true));
    
            //Set skybox mesh
            Mesh[] skyBoxMeshes = MeshLoader.load(matchInitializePacket.skyBoxModel);
            SkyBox skyBox = matchInitializePacket.gameMap.getSkyBox();
            skyBox.addComponent(new MeshBody(skyBoxMeshes, true));
    
            //Add all enitties to the scene
            for(Player player: getPlayers()) {
                gameScene.addRenderableEntity(player);
            }
    
            gameScene.addRenderableEntity(mainMap);
            gameScene.setSkyBox(skyBox);
    
            gameScene.setSceneLighting(matchInitializePacket.gameMap.getGameLighting());   
            
            scoreboard = new Scoreboard(getPlayers());
        } catch (LWJGLException le) {
            leave();
        }
    }

    private void processPacket(PlayerDataPacket playerDataPacket) {
        Player modifiedPlayer = getPlayerById(playerDataPacket.playerId);

        //Update the player position and camera
        modifiedPlayer.getComponent(Transform.class).set(playerDataPacket.playerTransform);
        modifiedPlayer.getView().getComponent(Transform.class).set(playerDataPacket.cameraTransform);
        modifiedPlayer.getComponent(MeshBody.class).setVisible(playerDataPacket.visible);

        //Update the player's data
        modifiedPlayer.getScore().set(playerDataPacket.playerScore);
        modifiedPlayer.getPlayerData().set(playerDataPacket.playerData);
        modifiedPlayer.getWeaponsInventory().updateData(playerDataPacket.weaponsInventory);
    }

    private void processPacket(MatchPhasePacket matchPhasePacket) {
        changeMatchPhase(matchPhasePacket.currMatchPhase);
    }

    public void leave() {
        getManager().changeState(GameStateOption.MAIN_MENU);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
    }

    @Override
    public void changeMatchPhase(MatchPhase nextMatchPhase) {
        currMatchPhase = nextMatchPhase;
        currMatchPhase.makeClientLogic(this);
    }
}