package duber.game.server;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.entities.components.MeshBody;
import duber.engine.entities.components.SphereCollider;
import duber.engine.exceptions.LWJGLException;
import duber.engine.graphics.Mesh;
import duber.engine.graphics.lighting.DirectionalLight;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.loaders.MeshLoader;
import duber.engine.utilities.Timer;
import duber.game.Controls;
import duber.game.Player;
import duber.game.User;
import duber.game.networking.MatchInitializePacket;
import duber.game.networking.Packet;
import duber.game.networking.PlayerPositionPacket;
import duber.game.networking.UserInputPacket;

public class MatchManager implements Runnable {
    public static final int NUM_PLAYERS_IN_MATCH = 1;
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

    public MatchManager(ServerNetwork serverNetwork, List<User> users) throws LWJGLException {
        this.serverNetwork = serverNetwork;
        physicsWorld = new DuberantPhysicsWorld();
        
        redTeam = new HashSet<>(users);
        blueTeam = new HashSet<>();
        
        loadPlayers();
        loadMatch();
        sendMatchData();
    }

    public <E extends Packet> void sendAllUDP(E packet) {
        for(User user : players.keySet()) {
            user.getConnection().sendUDP(packet);
        }
    }

    public <E extends Packet> void sendAllTCP(E packet) {
        for(User user : players.keySet()) {
            user.getConnection().sendTCP(packet);
        }
    }

    private void loadPlayers() throws LWJGLException {
        Mesh[] playerMeshes = MeshLoader.load("models/cube/cube.obj");
        for(User user: redTeam) {
            Player redPlayer = createPlayer(playerMeshes, new Vector3f(0, 0, 0), Player.RED_TEAM);
            players.put(user, redPlayer);
            physicsWorld.addDynamicEntity(redPlayer.getModel());
        }

        for(User user: blueTeam) {
            Player bluePlayer = createPlayer(playerMeshes, new Vector3f(0, 0, 0), Player.BLUE_TEAM);
            players.put(user, bluePlayer);
            physicsWorld.addDynamicEntity(bluePlayer.getModel());
        }
    }

    private Player createPlayer(Mesh[] playerMeshes, Vector3f position, int team) {
        Entity playerModel = new Entity();
        playerModel.setMeshBody(new MeshBody(playerMeshes, false));
        
        playerModel.addRigidBody();
        SphereCollider sphereCollider = new SphereCollider(playerModel);
        playerModel.setCollider(sphereCollider);
        sphereCollider.setUnscaledRadius(1.0f);
        playerModel.getTransform().setScale(5.0f);

        playerModel.getTransform().getPosition().set(position);

        return new Player(playerModel, team);
    }
    
    private void loadMatch() throws LWJGLException {        
        Mesh[] mapMesh = MeshLoader.load("models/map/map.obj");
        map = new Entity();
        map.setMeshBody(new MeshBody(mapMesh));
        map.getTransform().setScale(0.3f);
        physicsWorld.addConstantEntity(map);         
        
        Mesh[] skyBoxMesh = MeshLoader.load("models/skybox/skybox.obj");
        skyBox = new SkyBox(skyBoxMesh[0]);
        skyBox.getTransform().setScale(3000.0f);
        
        gameLighting = new SceneLighting();

        // Ambient Light
        gameLighting.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));
        gameLighting.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 0.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        gameLighting.setDirectionalLight(directionalLight);        
    }

    private void sendMatchData() {
        MatchInitializePacket matchInitializePacket = new MatchInitializePacket(players, skyBox, map, gameLighting);
        sendAllTCP(matchInitializePacket);
    }


    @Override
    public void run() {
        try {
            serverLoop();
        } catch (LWJGLException le) {
            System.out.println("Error occured while running match loop");
        }
    }

    private void serverLoop() throws LWJGLException {
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
        updatePlayers();
        physicsWorld.update();
        sendPlayerPositionUpdates();
    }

    private void updatePlayers() {
        for(User matchUser : players.keySet()) {
            //Get any packets from the users connection
            Queue<Object> receivedPackets = serverNetwork.getPackets(matchUser.getConnection());
                
            //Process all the packets
            while(!receivedPackets.isEmpty()) {
                Object packet = receivedPackets.poll();
                if(packet instanceof UserInputPacket) {
                    UserInputPacket userInput = (UserInputPacket) packet;
                    playerControls.update(players.get(matchUser), userInput.mouseInput, userInput.keyboardInput);
                }
            }
        }
    }
    
    private void sendPlayerPositionUpdates() {
        for(User user : players.keySet()) {
            sendAllUDP(new PlayerPositionPacket(user.getId(), players.get(user).getModel().getTransform()));
        }
    }


}    
