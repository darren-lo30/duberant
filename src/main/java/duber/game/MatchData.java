package duber.game;

import org.joml.Vector4f;

import duber.engine.loaders.MeshResource;

public class MatchData {
    //Must be a multiple of 2
    public static final int NUM_PLAYERS_PER_TEAM = 1;
    public static final int NUM_PLAYERS_IN_MATCH = NUM_PLAYERS_PER_TEAM * 2;
    
    //DO NOT CHANGE
    public static final int NULL_TEAM = -1;
    public static final int RED_TEAM = 0;
    public static final int BLUE_TEAM = 1;

    public static final int NUM_ROUNDS = 3;
    public static final int NUM_ROUNDS_TO_WIN = (int) Math.ceil(NUM_ROUNDS/2.0);

    public static final int MONEY_PER_ROUND = 2000;

    //Match models
    public static final MeshResource redPlayerModel = new MeshResource("models/player/redPlayer.fbx", "");
    public static final MeshResource bluePlayerModel = new MeshResource("models/player/bluePlayer.fbx", "");

    public static final MeshResource mapModel = new MeshResource("models/map/map.obj", "models/map");
    public static final MeshResource skyBoxModel = new MeshResource("models/skybox/skybox.obj", "models/skybox");

    public static final Vector4f redPlayerColour = new Vector4f(1, 0, 0, 1);
    public static final Vector4f bluePlayerColour = new Vector4f(0, 0, 1, 1);
    
    private MatchData() {}

    public static String getTeamString(int team) {
        if (team == RED_TEAM) {
            return "red";
        } else if (team == BLUE_TEAM) {
            return "blue";
        } 

        return null;
    }
    
}