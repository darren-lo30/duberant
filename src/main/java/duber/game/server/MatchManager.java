package duber.game.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

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
import duber.game.MatchData;
import duber.game.gameobjects.GameMap;
import duber.game.gameobjects.Gun;
import duber.game.gameobjects.GunBuilder;
import duber.game.gameobjects.GunType;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.Scoreboard;
import duber.game.gameobjects.Player.MovementState;
import duber.game.gameobjects.Player.PlayerData;
import duber.game.gameobjects.WeaponsInventory;
import duber.game.User;
import duber.game.networking.GunPurchasePacket;
import duber.game.networking.MatchInitializePacket;
import duber.game.networking.MatchPhasePacket;
import duber.game.networking.Packet;
import duber.game.networking.PlayerUpdatePacket;
import duber.game.networking.UserInputPacket;
import duber.game.phases.LoadingPhase;
import duber.game.phases.MatchPhase;
import duber.game.phases.MatchPhaseManager;

public class MatchManager implements Runnable, MatchPhaseManager {
    public static final int TARGET_UPS = 30;
    
    private volatile boolean running = true;
    private boolean isOver = false;
    private ServerNetwork serverNetwork;

    private MatchPhase currMatchPhase;

    private GameMap gameMap;
    private Map<User, Player> players = new HashMap<>();
    private DuberantWorld gameWorld;
    private Scoreboard scoreboard;

    public MatchManager(ServerNetwork serverNetwork, List<User> users) throws LWJGLException {
        this.serverNetwork = serverNetwork;
        gameWorld = new DuberantWorld();

        List<User> redTeam = users.subList(0, MatchData.NUM_PLAYERS_PER_TEAM);
        List<User> blueTeam = users.subList(MatchData.NUM_PLAYERS_PER_TEAM, MatchData.NUM_PLAYERS_IN_MATCH);

        try {
            loadGameMap();
            loadPlayers(redTeam, blueTeam);
        } catch (LWJGLException le) {
            System.out.println("Error: Could not load match");
        }

        scoreboard = new Scoreboard(getPlayers());
        changeMatchPhase(new LoadingPhase());
    }

    @Override
    public void changeMatchPhase(MatchPhase nextMatchPhase) {
        currMatchPhase = nextMatchPhase;
        currMatchPhase.makeServerLogic(this);
        sendAllTCP(new MatchPhasePacket(currMatchPhase));
    }
    
    public boolean isRunning() {
        return running;
    }

    public GameMap getGameMap() {
        return gameMap;
    }

    public MatchPhase getCurrMatchPhase() {
        return currMatchPhase;
    }
    
    public DuberantWorld getGameWorld() {
        return gameWorld;
    }

    public ServerNetwork getServerNetwork() {
        return serverNetwork;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
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
            elapsedTime = Math.min(0.25f, updateTimer.getElapsedTimeAndUpdate());
            accumulator += elapsedTime;

            //Calculate updates in the scene
            while(accumulator >= interval) {
                update();
                accumulator -= interval;
            }
        }
    }
    
    private void update() {
        currMatchPhase.update();
    }

    public <E extends Packet> void sendAllTCP(E packet) {
        for(User user : getUsers()) {
            user.getConnection().sendTCP(packet);
        }
    }

    public <E extends Packet> void sendAllUDP(E packet) {
        for(User user : getUsers()) {
            user.getConnection().sendUDP(packet);
        }
    }

    public void sendMatchInitializationData() {
        for(User user : getUsers()) {
            Player usersPlayer = players.get(user);
            MatchInitializePacket matchInitializePacket = 
                new MatchInitializePacket(getPlayers(), usersPlayer.getId(), gameMap);
            user.getConnection().sendTCP(matchInitializePacket);
        }

        for(Player player : getPlayers()) {
            WeaponsInventory playerInventory = player.getWeaponsInventory();
            playerInventory.setPrimaryGun(GunBuilder.getInstance().buildGun(GunType.RIFLE));
            playerInventory.setSecondaryGun(GunBuilder.getInstance().buildGun(GunType.PISTOL));
            playerInventory.equipPrimaryGun();
        }
    }

    public Set<User> getUsers() {
        return players.keySet();
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players.values());
    }

    public Player getUsersPlayer(User user) {
        return players.get(user);
    }

    public int getRoundWinner() {
        //Choose a random team if it draws
        boolean redWin = getPlayersByTeam(MatchData.BLUE_TEAM).stream().allMatch(p -> !p.isAlive());
        boolean blueWin = getPlayersByTeam(MatchData.RED_TEAM).stream().allMatch(p -> !p.isAlive());

        if(redWin && blueWin) {
            double choice = Math.random();
            return choice > 0.5 ? MatchData.RED_TEAM : MatchData.BLUE_TEAM;
        } else if(redWin) {
            return MatchData.RED_TEAM;
        } else if(blueWin) {
            return MatchData.BLUE_TEAM;
        } 

        return MatchData.NULL_TEAM;
    }

    public List<Player> getPlayersByTeam(int team) {
        return players.values()
                      .stream()
                      .filter(p -> p.getPlayerData().getTeam() == team)
                      .collect(Collectors.toList());
    }

    public void loadPlayers(List<User> redTeam, List<User> blueTeam) throws LWJGLException {
        Mesh[] playerMeshes = MeshLoader.load(MatchData.playerModel.getModelFile());

        for(User user: redTeam) {
            Player redPlayer = createPlayer(user.getId(), user.getUsername(), playerMeshes, MatchData.RED_TEAM);
            players.put(user, redPlayer);
            gameWorld.addDynamicEntity(redPlayer);
        }

        for(User user: blueTeam) {
            Player bluePlayer = createPlayer(user.getId(), user.getUsername(), playerMeshes, MatchData.BLUE_TEAM);
            players.put(user, bluePlayer);
            gameWorld.addDynamicEntity(bluePlayer);
        }
    }

    public int getMatchWinner() {
        return scoreboard.getWinner();
    }
    
    private Player createPlayer(int id, String username, Mesh[] playerMeshes, int team) {
        Player player = new Player(id, username, team);

        //Add new mesh body
        player.addComponent(new MeshBody(playerMeshes, false));

        //Add rigid body
        player.addComponent(new RigidBody());
        
        //Set up player collider
        Collider playerCollider = new Collider();
        SphereCollider sphereCollider1 = new SphereCollider(1.0f, new Vector3f(0, 1, 0));
        SphereCollider sphereCollider2 = new SphereCollider(0.8f, new Vector3f(0, 1.5f, 0));
        SphereCollider sphereCollider3 = new SphereCollider(0.8f, new Vector3f(0, 3.0f, 0));

        playerCollider.setBaseCollider(sphereCollider1);
        playerCollider.addColliderPart(sphereCollider2);
        playerCollider.addColliderPart(sphereCollider3);
        player.addComponent(playerCollider);

        //Set transform
        Transform playerTransform = player.getComponent(Transform.class);
        playerTransform.setScale(5.0f);
        
        //Add player camera
        Vision playerVision = new Vision(new Vector3f(0, 30, 0));
        player.addComponent(playerVision);
        return player;
    }
    
    public void loadGameMap() throws LWJGLException {        
        Mesh[] mapMesh = MeshLoader.load(MatchData.mapModel.getModelFile());
        Entity map = new Entity();
        map.addComponent(new MeshBody(mapMesh));
        map.getComponent(Transform.class).setScale(0.3f);
        gameWorld.addConstantEntity(map);         
        
        Mesh[] skyBoxMesh = MeshLoader.load(MatchData.skyBoxModel.getModelFile());
        SkyBox skyBox = new SkyBox(skyBoxMesh[0]);
        skyBox.getComponent(Transform.class).setScale(3000.0f);
        
        SceneLighting gameLighting = new SceneLighting();

        // Ambient Light
        gameLighting.setAmbientLight(new Vector3f(1.0f, 1.0f, 1.0f));
        gameLighting.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, 0.3f);
        gameLighting.setDirectionalLight(directionalLight);   

        Vector3f[] redPositions = new Vector3f[] {
            new Vector3f(50, 0, 0),
            new Vector3f(100, 0, 0)
        };

        Vector3f[] bluePositions = new Vector3f[] {
            new Vector3f(150, 0, 0),
            new Vector3f(200, 0, 0)
        };
        
        gameMap = new GameMap(map, skyBox, gameLighting, redPositions, bluePositions);   
    }

    public void startRound() {
        for(Player player : getPlayers()) {
            //Reset players data
            player.getPlayerData().setHealth(PlayerData.DEFAULT_HEALTH);
            player.getWeaponsInventory().resetGuns();
            player.getComponent(MeshBody.class).setVisible(true);
            gameWorld.addDynamicEntity(player);

            //Give player money
            player.getPlayerData().addMoney(1000); 
            player.getPlayerData().setMovementState(MovementState.STOP); 
        } 

        resetPlayerMovement();

        //Reset player positions
        gameMap.setPlayerInitialPositions(MatchData.RED_TEAM, getPlayersByTeam(MatchData.RED_TEAM));
        gameMap.setPlayerInitialPositions(MatchData.BLUE_TEAM, getPlayersByTeam(MatchData.BLUE_TEAM));
    }

    public void resetPlayerMovement() {
        for(Player player : getPlayers()) {
            player.getPlayerData().setMovementState(MovementState.STOP);
        }
    }

    /**
     * Receive an packets from the client and apply them to the game
     */
    public void receivePackets() {
        for(User user: getUsers()) {            
            //Get any packets from the users connection
            Queue<Object> receivedPackets = serverNetwork.getPackets(user.getConnection());
                
            //Process all the packets
            while(!receivedPackets.isEmpty()) {
                Object packet = receivedPackets.poll();
                if(packet instanceof UserInputPacket) {
                    processPacket(user, (UserInputPacket) packet);
                } else if(packet instanceof GunPurchasePacket) {
                    processPacket(user, (GunPurchasePacket) packet);
                }
            }
        }
    }

    private void processPacket(User user, UserInputPacket userInputPacket) {
        if(currMatchPhase.playerCanMove()) {
            Controls.updatePlayer(this, getUsersPlayer(user), userInputPacket.mouseInput, userInputPacket.keyboardInput);
        }
    }

    private void processPacket(User user, GunPurchasePacket gunPurchasePacket) {
        Player player = getUsersPlayer(user);
        Gun purchasedGun = GunBuilder.getInstance().buildGun(gunPurchasePacket.gunType);
        player.purchaseGun(purchasedGun);
    }

    
    /**
     * Update the match state and send updates out to users
     */
    public void sendPackets() {
        for(Player player : getPlayers()) {
            sendAllUDP(new PlayerUpdatePacket(player));
        } 
    }

    public void close() {
        running = false;
    }

    public boolean isOver() {
        return isOver;
    }

    public void setIsOver(boolean isOver) {
        this.isOver = isOver;
    }
}    
