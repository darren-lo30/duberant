package duber.game.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Map.Entry;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.entities.components.Collider;
import duber.engine.entities.components.Vision;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.RigidBody;
import duber.engine.entities.components.SphereCollider;
import duber.engine.entities.components.Transform;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.lighting.DirectionalLight;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.loaders.MeshLoader;
import duber.engine.utilities.Timer;
import duber.game.Controls;
import duber.game.MatchData;
import duber.game.gameobjects.GunBuilder;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Player.WeaponsInventory;
import duber.game.User;
import duber.game.networking.MatchInitializePacket;
import duber.game.networking.Packet;
import duber.game.networking.PlayerPositionPacket;
import duber.game.networking.UserInputPacket;

public class MatchManager implements Runnable {
    public static final int TARGET_UPS = 30;
    
    private volatile boolean running = true;
    private ServerNetwork serverNetwork;

    private Set<User> redTeam = new HashSet<>();
    private Set<User> blueTeam = new HashSet<>();
    private Map<User, Player> players = new HashMap<>();

    private DuberantPhysicsWorld physicsWorld;

    private Entity map;
    private SkyBox skyBox;
    private SceneLighting gameLighting;

    private Controls playerControls = new Controls();
    private GunBuilder gunBuilder = new GunBuilder();

    public MatchManager(ServerNetwork serverNetwork, List<User> users) throws LWJGLException {
        this.serverNetwork = serverNetwork;
        physicsWorld = new DuberantPhysicsWorld();
        
        redTeam = new HashSet<>(users.subList(0, MatchData.NUM_PLAYERS_IN_MATCH/2));
        blueTeam = new HashSet<>(users.subList(MatchData.NUM_PLAYERS_IN_MATCH/2, MatchData.NUM_PLAYERS_IN_MATCH));
        
        loadPlayers();
        loadMatch();
        sendMatchInitializationData();
    }

    private <E extends Packet> void sendAllUDP(E packet) {
        for(User user : usersInMatch()) {
            user.getConnection().sendUDP(packet);
        }
    }

    private <E extends Packet> void sendAllTCP(E packet) {
        for(User user : usersInMatch()) {
            user.getConnection().sendTCP(packet);
        }
    }

    private void loadPlayers() throws LWJGLException {
        Mesh[] playerMeshes = MeshLoader.load("models/cube/cube.obj");
        for(User user: redTeam) {
            Player redPlayer = createPlayer(user.getUsername(), playerMeshes, new Vector3f(50, 0, 0), MatchData.RED_TEAM);
            players.put(user, redPlayer);
            physicsWorld.addDynamicEntity(redPlayer);
        }

        for(User user: blueTeam) {
            Player bluePlayer = createPlayer(user.getUsername(), playerMeshes, new Vector3f(50, 0, 0), MatchData.BLUE_TEAM);
            players.put(user, bluePlayer);
            physicsWorld.addDynamicEntity(bluePlayer);
        }
    }
    
    private Set<User> usersInMatch() {
        return players.keySet();
    }

    private Player createPlayer(String username, Mesh[] playerMeshes, Vector3f position, int team) {
        Player player = new Player(username, team);

        //Add new mesh body
        player.addComponent(new MeshBody(playerMeshes, false));

        //Add rigid body
        player.addComponent(new RigidBody());
        
        //Set up player collider
        Collider playerCollider = new Collider();
        SphereCollider sphereCollider1 = new SphereCollider(1.0f, new Vector3f(0, 0, 0));
        SphereCollider sphereCollider2 = new SphereCollider(0.8f, new Vector3f(0, 1.5f, 0));
        SphereCollider sphereCollider3 = new SphereCollider(0.8f, new Vector3f(0, 3.0f, 0));

        playerCollider.setBaseCollider(sphereCollider1);
        playerCollider.addColliderPart(sphereCollider2);
        playerCollider.addColliderPart(sphereCollider3);
        player.addComponent(playerCollider);

        //Set transform
        Transform playerTransform = player.getComponent(Transform.class);
        playerTransform.setScale(5.0f);
        playerTransform.getPosition().set(position);
        
        //Add player camera
        Vision playerVision = new Vision(new Vector3f(0, 30, 30));
        player.addComponent(playerVision);

        //Add gun
        WeaponsInventory playerInventory = player.getWeaponsInventory();
        playerInventory.setSecondaryGun(gunBuilder.buildPistol());
        playerInventory.equipSecondaryGun();
        
        return player;
    }
    
    private void loadMatch() throws LWJGLException {        
        Mesh[] mapMesh = MeshLoader.load("models/map/map.obj");
        map = new Entity();
        map.addComponent(new MeshBody(mapMesh));
        map.getComponent(Transform.class).setScale(0.3f);
        physicsWorld.addConstantEntity(map);         
        
        Mesh[] skyBoxMesh = MeshLoader.load("models/skybox/skybox.obj");
        skyBox = new SkyBox(skyBoxMesh[0]);
        skyBox.getComponent(Transform.class).setScale(3000.0f);
        
        gameLighting = new SceneLighting();

        // Ambient Light
        gameLighting.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));
        gameLighting.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 0.3f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        gameLighting.setDirectionalLight(directionalLight);        
    }

    private void sendMatchInitializationData() {
        MatchInitializePacket matchInitializePacket = new MatchInitializePacket(players, skyBox, map, gameLighting);
        sendAllTCP(matchInitializePacket);
    }


    @Override
    public void run() {
        serverLoop();
    }

    private void serverLoop() {
        Timer updateTimer = new Timer();
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1.0f/TARGET_UPS;

        while(running) {
            elapsedTime = updateTimer.getElapsedTime();
            if(elapsedTime > 0.25f) {
                elapsedTime = 0.25f;
            }

            accumulator += elapsedTime;

            //Get any input

            //Calculate updates in the scene
            while(accumulator >= interval) {
                update();
                accumulator -= interval;
            }
        }
    }

    private void update() {
        receivePlayerInputs();
        physicsWorld.update();
        sendGameUpdates();
    }

    /**
     * Receive an inputs from the client and apply them
     */
    private void receivePlayerInputs() {
        for(Entry<User, Player> userPlayerEntry : players.entrySet()) {
            User user = userPlayerEntry.getKey();
            Player player = userPlayerEntry.getValue();
            
            //Get any packets from the users connection
            Queue<Object> receivedPackets = serverNetwork.getPackets(user.getConnection());
                
            //Process all the packets
            while(!receivedPackets.isEmpty()) {
                Object packet = receivedPackets.poll();
                if(packet instanceof UserInputPacket) {
                    UserInputPacket userInput = (UserInputPacket) packet;
                    playerControls.update(player, physicsWorld, userInput.mouseInput, userInput.keyboardInput);
                }
            }
        }
    }
    
    /**
     * Send out any updates to the users
     */
    private void sendGameUpdates() {
        for(Entry<User, Player> userPlayerEntry : players.entrySet()) {
            User user = userPlayerEntry.getKey();
            Player player = userPlayerEntry.getValue();

            sendAllUDP(new PlayerPositionPacket(user.getId(), player));
        }
    }


}    
