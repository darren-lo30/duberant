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
import duber.engine.entities.components.Animation;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.Transform;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.Renderer;
import duber.engine.graphics.Scene;
import duber.engine.loaders.MeshLoader;
import duber.engine.loaders.MeshLoader.MeshData;
import duber.game.gameobjects.Gun;
import duber.game.gameobjects.GunBuilder;
import duber.game.gameobjects.GunType;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Scoreboard;
import duber.game.gameobjects.WeaponsInventory;
import duber.game.gameobjects.Player.PlayerData.MovementState;
import duber.game.phases.MatchPhaseManager;
import duber.game.MatchData;
import duber.game.client.GameState;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.networking.GunFirePacket;
import duber.game.networking.GunPurchasePacket;
import duber.game.networking.MatchInitializePacket;
import duber.game.networking.MatchPhasePacket;
import duber.game.networking.PlayerUpdatePacket;
import duber.game.networking.UserInputPacket;
import duber.game.phases.MatchPhase;

import static org.lwjgl.glfw.GLFW.glfwSetCursorPos;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;

/**
 * A match inside the game
 * @author Darren Lo
 * @version 1.0
 */
public class Match extends GameState implements Cleansable, MatchPhaseManager {    
    /** The renderer to render the game. */
    private Renderer renderer;

    /** The HUD during the match. */
    private HUD hud;

    /** The match scene. */
    private Scene gameScene;

    /** The main player. */
    private Player mainPlayer;
    
    /** All the players mapped by their id's. */
    private Map<Integer, Player> playersById = new HashMap<>();

    /** The scoreboard containing all the Player's scores. */
    private Scoreboard scoreboard;

    /** The current match phase. */
    private MatchPhase currMatchPhase;

    /** The match sounds. */
    private MatchSounds matchSounds;

    /** The user's mouse input data. */
    private MouseInput mouseInput;

    /** The user's keyboard input data. */
    private KeyboardInput keyboardInput;
    
    /**
     * {@inheritDoc}
     */
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

    /**
     * Configures mouse callbacks used during the match.
     */
    private void configureMouseCallbacks() {
        List<CallbackI> callbacks = getCallbacks();
        
        //Cursor position callback
        GLFWCursorPosCallbackI mouseCursorCallback = (window, xPos, yPos) -> {
            if (isFocused()) {
                mouseInput.setCurrentPos(xPos, yPos);
            }
        };
        callbacks.add(mouseCursorCallback);

        //Mouse click callback
        GLFWMouseButtonCallbackI mouseButtonCallback = (window, button, action, mode) -> {
            if (isFocused()) {
                mouseInput.setLeftButtonIsPressed(button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS);
                mouseInput.setRightButtonIsPressed(button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS);
            }
        };
        callbacks.add(mouseButtonCallback);
    }

    /**
     * Configures keyboard callbacks used during the match.
     */
    private void configureKeyboardCallbacks() {
        List<CallbackI> callbacks = getCallbacks();

        GLFWKeyCallbackI keyboardCallback = (window, keyCode, scanCode, action, mods) -> {
            if (isFocused()) {
                if (action == GLFW_PRESS) {
                    keyboardInput.setKeyPressed(keyCode, true);
                } else if (action == GLFW_RELEASE) {
                    keyboardInput.setKeyPressed(keyCode, false);
                }
            }

            //Shop menu
            if (isInitialized() && currMatchPhase.playerCanBuy() && keyCode == GLFW_KEY_B && action == GLFW_RELEASE) {
                GameState shopMenu = GameStateOption.SHOP_MENU.getGameState();
                if (shopMenu.isOpened()) {
                    shopMenu.setShouldClose(true);
                } else {
                    shopMenu.pushSelf();
                }
            }

            if (isInitialized() && keyCode == GLFW_KEY_TAB && action == GLFW_RELEASE) {
                GameState scoreboardDisplay = GameStateOption.SCOREBOARD_DISPLAY.getGameState();
                if (scoreboardDisplay.isOpened()) {
                    scoreboardDisplay.setShouldClose(true);
                } else {
                    scoreboardDisplay.pushSelf();
                }
            }
        };
        callbacks.add(keyboardCallback);
    }

    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    @Override
    public void close() {
        matchSounds.clear();
        for(CallbackI callback : getCallbacks()) {
            getWindow().removeCallback(callback);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enter() {
        glfwSetCursorPos(getWindow().getWindowHandle(), 0, 0);

        matchSounds.configureSettings();
        getWindow().restoreState();

        //Disable cursor
        getWindow().setOption(Window.Options.SHOW_CURSOR, false);
        getWindow().applyOptions();  
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void exit() {
        mouseInput.clear();
        keyboardInput.clear();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {       
        mouseInput.updateCursorDisplacement(); 
        
        if (isInitialized()) {
            if (!currMatchPhase.playerCanBuy() && GameStateOption.SHOP_MENU.getGameState().isOpened()) {
                GameStateOption.SHOP_MENU.getGameState().setShouldClose(true);
            }
            currMatchPhase.update();
        } else {
            receivePackets();
        }        
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public void render() {
        if (isInitialized()) {
            currMatchPhase.render();
        } else {
            hud.displayText("Waiting for server...", 0.5f, 0.5f, true, HUD.TITLE_FONT);
        }
    }

    /**
     * Gets the match scoreboard.
     * @return the match scoreboard
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    /**
     * Gets a Player by id.
     * @param playerId the id of the Player
     * @return the Player with the id
     */
    public Player getPlayerById(int playerId) {
        return playersById.get(playerId);
    }

    /**
     * Gets a Collection of Players.
     * @return the Collection of Players
     */
    public Collection<Player> getPlayers() {
        return playersById.values();
    }

    /**
     * Gets the current match phase.
     * @return the current match phase
     */
    public MatchPhase getCurrMatchPhase() {
        return currMatchPhase;
    }

    /**
     * Gets the main player.
     * @return the main player
     */
    public Player getMainPlayer() {
        return mainPlayer;
    }

    /**
     * Gets the match sounds.
     * @return the match sounds
     */
    public MatchSounds getMatchSounds() {
        return matchSounds;
    }

    /**
     * Determines if the match is initialized.
     * @return if the match is initialized
     */
    public boolean isInitialized() {
        return mainPlayer != null && currMatchPhase != null;
    }

    /**
     * Sends packets to the server.
     */
    public void sendPackets() {
        if (isInitialized() && currMatchPhase.playerCanMove()) {
            UserInputPacket matchCommands = new UserInputPacket(keyboardInput, mouseInput);
            getGame().getUser().getConnection().sendUDP(matchCommands);
        }
    }

    /**
     * Renders the game scene.
     */
    public void renderGameScene() {
        renderer.render(getWindow(), mainPlayer.getComponent(Vision.class).getCamera(), gameScene);
        hud.displayMatchHud(this);
    }

    /**
     * Gets the HUD.
     * @return the HUD.
     */
    public HUD getHud() {
        return hud;
    }

    /**
     * Updates the animations for the Players.
     */
    public void updateAnimations() {
        for(Player player: getPlayers()) {
            MovementState playerMovement = player.getPlayerData().getPlayerMovement();
            if (playerMovement == MovementState.RUNNING || playerMovement == MovementState.WALKING) {
                player.getComponent(Animation.class).getCurrentAnimation().nextFrame();
            }
        }
    }

    /**
     * Receives and processes packets from the server.
     */
    public void receivePackets() {
        while(!getGame().getClientNetwork().getPackets().isEmpty()){
            Object packet = getGame().getClientNetwork().getPackets().poll();
        
            if (packet instanceof MatchInitializePacket) {
                processPacket((MatchInitializePacket) packet);
            } else if (packet instanceof MatchPhasePacket) {
                processPacket((MatchPhasePacket) packet);
            } else if (packet instanceof PlayerUpdatePacket) {
                processPacket((PlayerUpdatePacket) packet);
            } else if (packet instanceof GunFirePacket) {
                processPacket((GunFirePacket) packet);
            }
        }
    }

    /**
     * Processes a MatchInitializePacket
     * @param matchInitializePacket the MatchInitializePacket.
     */
    private void processPacket(MatchInitializePacket matchInitializePacket) {        
        List<Player> players = matchInitializePacket.players;
        for(Player player: players) {
            playersById.put(player.getId(), player);
        }

        scoreboard = new Scoreboard(getPlayers());
        mainPlayer = getPlayerById(matchInitializePacket.mainPlayerId);

        try {
            //Set player meshes
            for(Player player : getPlayers()) {
                MeshData playerMeshData;
                if (player.getPlayerData().getTeam() == MatchData.RED_TEAM) {
                    playerMeshData = MeshLoader.load(matchInitializePacket.redPlayerModel);
                } else {
                    playerMeshData = MeshLoader.load(matchInitializePacket.bluePlayerModel);
                }

                MeshBody playerMeshBody = new MeshBody(playerMeshData.getMeshes(), true);
                if (player == mainPlayer) {
                    playerMeshBody.setVisible(false);
                }

                player.addComponent(playerMeshBody);
                player.addComponent(new Animation(playerMeshData.getAnimationData()));
            }
    
            //Set mainMap meshes
            Mesh[] mainMapMeshes = MeshLoader.load(matchInitializePacket.mapModel).getMeshes();
            Entity mainMap = matchInitializePacket.gameMap.getMainMap();
            mainMap.addComponent(new MeshBody(mainMapMeshes, true));
    
            //Set skybox mesh
            Mesh[] skyBoxMeshes = MeshLoader.load(matchInitializePacket.skyBoxModel).getMeshes();
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

    /**
     * Processes a PlayerUpdatePacket.
     * @param playerUpdatePacket the PlayerUpdatePacket
     */
    private void processPacket(PlayerUpdatePacket playerUpdatePacket) {
        if (!isInitialized()) {
            return;
        }

        Player modifiedPlayer = getPlayerById(playerUpdatePacket.playerId);

        //Update the player position and camera
        modifiedPlayer.getComponent(Transform.class).set(playerUpdatePacket.playerTransform);
        modifiedPlayer.getView().getComponent(Transform.class).set(playerUpdatePacket.cameraTransform);

        //Update other player information
        modifiedPlayer.getScore().set(playerUpdatePacket.playerScore);
        modifiedPlayer.getPlayerData().set(playerUpdatePacket.playerData);

        updatePlayerWeapons(modifiedPlayer.getWeaponsInventory(), playerUpdatePacket.playerInventory);
        
        Gun equippedGun = modifiedPlayer.getWeaponsInventory().getEquippedGun();
        if (equippedGun != null) {
            if (modifiedPlayer == mainPlayer) {
                equippedGun.getComponent(Transform.class).setRelativeView(false);
                equippedGun.getComponent(Transform.class).getPosition().set(5.5f, -5, -7);
                equippedGun.getComponent(Transform.class).getRotation().set(0, 0, 0);
                equippedGun.getComponent(Transform.class).setScale(1.4f);
            }

            equippedGun.getComponent(MeshBody.class).setVisible(playerUpdatePacket.visible);
        }

        if (modifiedPlayer != mainPlayer) {
            modifiedPlayer.getComponent(MeshBody.class).setVisible(playerUpdatePacket.visible);
        }
    }

    /**
     * Updates a player's weapons.
     * @param weaponsInventory the players current weapons
     * @param upddatedWeaponsInventory the update weapons
     */
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

    
    /**
     * Processes a MatchPhasePacket.
     * @param matchPhasePacket the matchPhasePacket
     */
    private void processPacket(MatchPhasePacket matchPhasePacket) {
        changeMatchPhase(matchPhasePacket.currMatchPhase);
    }

    /**
     * Processes a GunFirePacket.
     * @param gunFirePacket the GunFirePacket
     */
    private void processPacket(GunFirePacket gunFirePacket) {
        matchSounds.playGunSounds(getPlayerById(gunFirePacket.shooterId));
    }

    /**
     * Sends a gun purchase request to the server.
     */
    public void sendGunPurchaseRequest(GunType gunType) {
        getGame().getUser().getConnection().sendTCP(new GunPurchasePacket(gunType));
    }

    /** 
     * Leaves the match.
     */
    public void leave() {
        setShouldClose(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        renderer.cleanup();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void changeMatchPhase(MatchPhase nextMatchPhase) {
        currMatchPhase = nextMatchPhase;
        currMatchPhase.makeClientLogic(this);
    }
    
}