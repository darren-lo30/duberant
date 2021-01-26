package duber.game.networking;

import java.util.List;
import duber.engine.loaders.MeshResource;
import duber.game.MatchData;
import duber.game.gameobjects.GameMap;
import duber.game.gameobjects.Player;

/**
 * A Packet used by Kryonet that sends all the match data to the client.
 * @author Darren Lo
 * @version 1.0
 */
public class MatchInitializePacket extends Packet {
    /** The players in the match. */
    public List<Player> players;

    /** The id of the main player. */
    public int mainPlayerId;
    
    /** The red player's model. */
    public MeshResource redPlayerModel;    

    /** The blue player's model. */
    public MeshResource bluePlayerModel;    
    
    /** The game map. */
    public GameMap gameMap;

    /** The sky box model. */
    public MeshResource skyBoxModel;

    /** The map model. */
    public MeshResource mapModel;

    /**
     * Constructs a MatchIntiializePacket.
     * @param players the players in the match
     * @param mainPlayerId the id of the main player
     * @param gameMap the game map
     */
    public MatchInitializePacket(List<Player> players, int mainPlayerId, GameMap gameMap){
        this.players = players;
        this.mainPlayerId = mainPlayerId;
        redPlayerModel = MatchData.redPlayerModel;
        bluePlayerModel = MatchData.bluePlayerModel;

        this.gameMap = gameMap;

        skyBoxModel = MatchData.skyBoxModel;
        mapModel = MatchData.mapModel;

    }

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
	private MatchInitializePacket(){}
}