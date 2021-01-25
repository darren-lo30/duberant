package duber.game;

import duber.engine.loaders.MeshResource;

/**
 * A class that stores all variables about a Match.
 * @author Darren Lo
 * @version 1.0
 */
public class MatchData {
    //Must be a multiple of 2

    /**
     * The number of players per team in a match.
     */
    public static final int NUM_PLAYERS_PER_TEAM = 1;

    /**
     * The number of total players in a match.
     */
    public static final int NUM_PLAYERS_IN_MATCH = NUM_PLAYERS_PER_TEAM * 2;
    
    //DO NOT CHANGE

    /**
     * The number representing a null team.
     */
    public static final int NULL_TEAM = -1;

    /**
     * The number representing a red team.
     */
    public static final int RED_TEAM = 0;

    /**
     * The number representing a blue team.
     */
    public static final int BLUE_TEAM = 1;

    /**
     * The number of rounds in a match.
     */
    public static final int NUM_ROUNDS = 3;

    /**
     * The number of rounds to win a match.
     */
    public static final int NUM_ROUNDS_TO_WIN = (int) Math.ceil(NUM_ROUNDS/2.0);

    /**
     * The amount of money given to each player per round.
     */
    public static final int MONEY_PER_ROUND = 2000;

    //Match models

    /**
     * The player model for the red team.
     */
    public static final MeshResource redPlayerModel = new MeshResource("models/player/redPlayer.fbx", "");

    /**
     * The player model for the blue team.
     */
    public static final MeshResource bluePlayerModel = new MeshResource("models/player/bluePlayer.fbx", "");

    /**
     * The map model.
     */
    public static final MeshResource mapModel = new MeshResource("models/map/map.obj", "models/map");

    /**
     * The skybox model.
     */
    public static final MeshResource skyBoxModel = new MeshResource("models/skybox/skybox.obj", "models/skybox");
    
    /**
     * Gets a String representing the team.
     * @param team the number representing the team
     * @return the String that represents the team
     */
    public static String getTeamString(int team) {
        if (team == RED_TEAM) {
            return "red";
        } else if (team == BLUE_TEAM) {
            return "blue";
        } 

        return "NULL";
    }

    private MatchData() {}
}