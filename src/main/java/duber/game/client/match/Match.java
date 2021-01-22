package duber.game.client.match;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.glfw.GLFWCursorPosCallbackI;
import org.lwjgl.glfw.GLFWKeyCallbackI;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;
import org.lwjgl.system.CallbackI;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_2;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

import duber.engine.Cleansable;
import duber.engine.KeyboardInput;
import duber.engine.MouseInput;
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
import duber.game.gameobjects.Gun;
import duber.game.gameobjects.GunBuilder;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Scoreboard;
import duber.game.gameobjects.WeaponsInventory;
import duber.game.phases.MatchPhaseManager;
import duber.game.client.GameState;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.networking.GunFirePacket;
import duber.game.networking.MatchInitializePacket;
import duber.game.networking.MatchPhasePacket;
import duber.game.networking.PlayerUpdatePacket;
import duber.game.networking.UserInputPacket;
import duber.game.phases.MatchPhase;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
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

    private MouseInput mouseInput;
    private KeyboardInput keyboardInput;
    
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
        
        mouseInput = new MouseInput();
        keyboardInput = new KeyboardInput();
        
        configureMouseCallbacks();
        configureKeyboardCallbacks();
    }

    private void configureMouseCallbacks() {
        List<CallbackI> callbacks = getCallbacks();
        
        //Cursor position callback
        GLFWCursorPosCallbackI mouseCursorCallback = (window, xPos, yPos) -> {
            if(isFocused()) {
                mouseInput.setCurrentPos(xPos, yPos);
            }
        };
        callbacks.add(mouseCursorCallback);

        //Mouse click callback
        GLFWMouseButtonCallbackI mouseButtonCallback = (window, button, action, mode) -> {
            if(isFocused()) {
                mouseInput.setLeftButtonIsPressed(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS);
                mouseInput.setRightButtonIsPressed(button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS);
            }
        };
        callbacks.add(mouseButtonCallback);
    }

    private void configureKeyboardCallbacks() {
        List<CallbackI> callbacks = getCallbacks();

        GLFWKeyCallbackI keyboardCallback = (window, keyCode, scanCode, action, mods) -> {
            if(isFocused()) {
                if(action == GLFW_PRESS) {
                    keyboardInput.setKeyPressed(keyCode, true);
                } else if(action == GLFW_RELEASE) {
                    keyboardInput.setKeyPressed(keyCode, false);
                }
            }

            //Shop menu
            if(isInitialized() && currMatchPhase.playerCanBuy() && keyCode == GLFW_KEY_B && action == GLFW_RELEASE) {
                GameState shopMenu = GameStateOption.SHOP_MENU.getGameState();
                if(shopMenu.isOpened()) {
                    shopMenu.setShouldClose(true);
                } else {
                    shopMenu.pushSelf();
                }
            }

            if(isInitialized() && keyCode == GLFW_KEY_TAB && action == GLFW_RELEASE) {
                GameState scoreboardDisplay = GameStateOption.SCOREBOARD_DISPLAY.getGameState();
                if(scoreboardDisplay.isOpened()) {
                    scoreboardDisplay.setShouldClose(true);
                } else {
                    scoreboardDisplay.pushSelf();
                }
            }
        };
        callbacks.add(keyboardCallback);
    }

    @Override
    public void startup() {
        mainPlayer = null;
        playersById.clear();
        gameScene.clear();
        currMatchPhase = null;

        for(CallbackI callback : getCallbacks()) {
            getWindow().addCallback(callback);
        }
    }


    @Override
    public void close() {
        matchSounds.clear();
        for(CallbackI callback : getCallbacks()) {
            getWindow().removeCallback(callback);
        }
    }

    @Override
    public void enter() {
        glfwSetCursorPos(getWindow().getWindowHandle(), 0, 0);

        matchSounds.configureSettings();
        getWindow().restoreState();

        //Disable cursor
        getWindow().setOption(Window.Options.SHOW_CURSOR, false);
        getWindow().applyOptions();  
    }

    @Override
    public void exit() {
        mouseInput.clear();
        keyboardInput.clear();
    }

    @Override
    public void update() {       
        mouseInput.updateCursorDisplacement(); 
        
        if(isInitialized()) {
            if(!currMatchPhase.playerCanBuy() && GameStateOption.SHOP_MENU.getGameState().isOpened()) {
                GameStateOption.SHOP_MENU.getGameState().setShouldClose(true);
            }
            currMatchPhase.update();
        } else {
            receivePackets();
        }        
    }

    @Override
    public void render() {
        if(isInitialized()) {
            currMatchPhase.render();
        } else {
            String matchSearchingMessage = "Waiting for server...";
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

    public MatchPhase getCurrMatchPhase() {
        return currMatchPhase;
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

    public void sendPackets() {
        if(isInitialized() && currMatchPhase.playerCanMove()) {
            UserInputPacket matchCommands = new UserInputPacket(keyboardInput, mouseInput);
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

        updatePlayerWeapons(modifiedPlayer.getWeaponsInventory(), playerUpdatePacket.playerInventory);
        
        if(modifiedPlayer == mainPlayer) {
            Gun equippedGun = modifiedPlayer.getWeaponsInventory().getEquippedGun();

            if(equippedGun != null) {
                equippedGun.getComponent(Transform.class).setRelativeView(false);
                equippedGun.getComponent(Transform.class).getPosition().set(5.5f, -5, -7);
            }
        }
    }

    private void updatePlayerWeapons(WeaponsInventory weaponsInventory, WeaponsInventory updatedWeaponsInventory) {
        gameScene.removeRenderableEntity(weaponsInventory.getPrimaryGun());
        gameScene.removeRenderableEntity(weaponsInventory.getSecondaryGun());

        weaponsInventory.set(updatedWeaponsInventory);

        try {
            GunBuilder.getInstance().loadGunMesh(weaponsInventory.getPrimaryGun());
            GunBuilder.getInstance().loadGunMesh(weaponsInventory.getSecondaryGun());
        } catch (LWJGLException le) {
            System.err.println("Could not load gun meshes");
        }

        gameScene.addRenderableEntity(weaponsInventory.getEquippedGun());
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