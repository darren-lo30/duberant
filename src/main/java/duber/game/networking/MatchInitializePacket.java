package duber.game.networking;

import java.util.Map;
import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.graphics.lighting.SceneLighting;
import duber.game.gameitems.Player;
import duber.game.User;

/**
 * MatchInitializePacket
 */
public class MatchInitializePacket extends Packet {
    //Send list of player position and id
    public Map<User, Player> players;
    public ModelLoadPacket playerModel;    
    
    public SkyBox skyBox;
    public ModelLoadPacket skyBoxModel;
    
    public Entity map;  
    public ModelLoadPacket mapModel;

    public SceneLighting gameLighting;

    public MatchInitializePacket(Map<User, Player> players, SkyBox skyBox, Entity map, SceneLighting gameLighting) {
        this.players = players;
        playerModel = new ModelLoadPacket("models/cube/cube.obj", "models/cube");

        this.skyBox = skyBox;
        skyBoxModel = new ModelLoadPacket("models/skybox/skybox.obj", "models/skybox");

        this.map = map;
        mapModel = new ModelLoadPacket("models/map/map.obj", "models/map");

        this.gameLighting = gameLighting;
    }

    @SuppressWarnings("unused")
	private MatchInitializePacket(){}
}