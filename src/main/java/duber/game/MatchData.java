package duber.game;

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

    //Match models
    public static final MeshResource playerModel = new MeshResource("models/cube/cube.obj", "models/cube");
    public static final MeshResource mapModel = new MeshResource("models/map/map.obj", "models/map");
    public static final MeshResource skyBoxModel = new MeshResource("models/skybox/skybox.obj", "models/skybox");
    
    private MatchData() {}

    public static String getTeamString(int team) {
        if(team == RED_TEAM) {
            return "red";
        } else if(team == BLUE_TEAM) {
            return "blue";
        } 

        return null;
    }
    
}