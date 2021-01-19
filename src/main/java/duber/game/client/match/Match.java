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
import duber.game.gameobjects.Scoreboard;
import duber.game.phases.MatchPhaseManager;
import duber.game.client.GameState;
import duber.game.client.GameStateKeyListener;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.networking.GunFirePacket;
import duber.game.networking.MatchInitializePacket;
import duber.game.networking.MatchPhasePacket;
import duber.game.networking.PlayerUpdatePacket;
import duber.game.networking.UserInputPacket;
import duber.game.phases.MatchPhase;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;


public class Match extends GameState implements Cleansable, MatchPhaseManager {    
    private Renderer renderer;
    private HUD hud;
    private Scene gameScene;
    private Player mainPlayer;
    
    private Map<Integer, Player> playersById = new HashMap<>();

    private Scoreboard scoreboard;
    private MatchPhase currMatchPhase;
    private MatchSounds matchSounds;

    private GameStateKeyListener shopListener;
    private GameStateKeyListener scoreboardListener;
    
    @Override
    public void init() {
        try {
            renderer = new Renderer();
            hud = new HUD(getWindow());
        } catch (IOException | LWJGLException e) {
            leave();
        }

        gameScene = new Scene();

        matchSounds = new MatchSounds(this, getGame().getSoundManager());
        shopListener = new GameStateKeyListener(GLFW_KEY_B, GameStateOption.SHOP_MENU);
        scoreboardListener = new GameStateKeyListener(GLFW_KEY_TAB, GameStateOption.SCOREBOARD_DISPLAY);
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
        //Disable cursor
        getWindow().setOption(Window.Options.SHOW_CURSOR, false);
        getWindow().applyOptions();  
        
        matchSounds.configureSettings();
    }

    @Override
    public void close() {
        matchSounds.clear();
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

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public Player getPlayerById(int playerId) {
        return playersById.get(playerId);
    }

    public Collection<Player> getPlayers() {
        return playersById.values();
    }

    public Player getMainPlayer() {
        return mainPlayer;
    }

    public MatchSounds getMatchSounds() {
        return matchSounds;
    }

    public boolean isInitialized() {
        return mainPlayer != null && currMatchPhase != null;
    }

    public void listenInputs() {
        Window window = getWindow();

        if(currMatchPhase.playerCanBuy()) {
            shopListener.listenToActivate(window.getKeyboardInput());
        } else {
            shopListener.getActivatedGameState().setShouldClose(true);
        }

        scoreboardListener.listenToActivate(window.getKeyboardInput());
    }

    public void sendPackets() {
        Window window = getWindow();

        if(isFocused() && isInitialized() && currMatchPhase.playerCanMove()) {
            UserInputPacket matchCommands = new UserInputPacket(window.getKeyboardInput(), window.getMouseInput());
            getGame().getUser().getConnection().sendUDP(matchCommands);
        }
    }

    public void renderGameScene() {
        renderer.render(getWindow(), mainPlayer.getComponent(Vision.class).getCamera(), gameScene);
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
            } else if(packet instanceof PlayerUpdatePacket) {
                processPacket((PlayerUpdatePacket) packet);
            } else if(packet instanceof GunFirePacket) {
                processPacket((GunFirePacket) packet);
            }
        }
    }

    private void processPacket(MatchInitializePacket matchInitializePacket) {        
        List<Player> players = matchInitializePacket.players;
        for(Player player: players) {
            playersById.put(player.getId(), player);
        }

        scoreboard = new Scoreboard(getPlayers());
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
        } catch (LWJGLException le) {

            //Leave match if something fails
            leave();
        }

        matchSounds.addMatchPlayers();
    }

    private void processPacket(PlayerUpdatePacket playerUpdatePacket) {
        if(!isInitialized()) {
            return;
        }

        Player modifiedPlayer = getPlayerById(playerUpdatePacket.playerId);

        //Update the player position and camera
        modifiedPlayer.getComponent(Transform.class).set(playerUpdatePacket.playerTransform);
        modifiedPlayer.getView().getComponent(Transform.class).set(playerUpdatePacket.cameraTransform);
        modifiedPlayer.getComponent(MeshBody.class).setVisible(playerUpdatePacket.visible);

        //Update other player information
        modifiedPlayer.getScore().set(playerUpdatePacket.playerScore);
        modifiedPlayer.getPlayerData().set(playerUpdatePacket.playerData);
        modifiedPlayer.getWeaponsInventory().updateData(playerUpdatePacket.playerInventory);
    }

    private void processPacket(MatchPhasePacket matchPhasePacket) {
        changeMatchPhase(matchPhasePacket.currMatchPhase);
    }

    private void processPacket(GunFirePacket gunFirePacket) {
        matchSounds.playGunSounds(getPlayerById(gunFirePacket.shooterId));
    }

    public void leave() {
        setShouldClose(true);
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