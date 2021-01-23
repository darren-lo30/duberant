package duber.game.networking;

import java.util.List;
import duber.engine.loaders.MeshResource;
import duber.game.MatchData;
import duber.game.gameobjects.GameMap;
import duber.game.gameobjects.Player;

/**
 * MatchInitializePacket
 */
public class MatchInitializePacket extends Packet {
    //Send List of player position and id
    public List<Player> players;
    public int mainPlayerId;
    public MeshResource redPlayerModel;    
    public MeshResource bluePlayerModel;    
    
    public GameMap gameMap;

    public MeshResource skyBoxModel;
    public MeshResource mapModel;

    public MatchInitializePacket(List<Player> players, int mainPlayerId, GameMap gameMap){
        this.players = players;
        this.mainPlayerId = mainPlayerId;
        redPlayerModel = MatchData.redPlayerModel;
        bluePlayerModel = MatchData.bluePlayerModel;

        this.gameMap = gameMap;

        skyBoxModel = MatchData.skyBoxModel;
        mapModel = MatchData.mapModel;

    }

    @SuppressWarnings("unused")
	private MatchInitializePacket(){}
}