package duber.game.networking;

import java.util.List;
import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.graphics.lighting.SceneLighting;
import duber.engine.loaders.MeshResource;
import duber.game.gameobjects.Player;

/**
 * MatchInitializePacket
 */
public class MatchInitializePacket extends Packet {
    //Send List of player position and id
    public List<Player> players;
    public int mainPlayerId;
    public MeshResource playerModel;    
    
    public SkyBox skyBox;
    public MeshResource skyBoxModel;
    
    public Entity map;  
    public MeshResource mapModel;

    public SceneLighting gameLighting;

    public MatchInitializePacket(List<Player> players, int mainPlayerId, SkyBox skyBox, Entity map, SceneLighting gameLighting) {
        this.players = players;
        this.mainPlayerId = mainPlayerId;
        playerModel = new MeshResource("models/cube/cube.obj", "models/cube");

        this.skyBox = skyBox;
        skyBoxModel = new MeshResource("models/skybox/skybox.obj", "models/skybox");

        this.map = map;
        mapModel = new MeshResource("models/map/map.obj", "models/map");

        this.gameLighting = gameLighting;
    }

    @SuppressWarnings("unused")
	private MatchInitializePacket(){}
}