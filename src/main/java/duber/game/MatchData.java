package duber.game;

public class MatchData {
    public static final int NUM_PLAYERS_IN_MATCH = 2;

    public static final int RED_TEAM = 0;
    public static final int BLUE_TEAM = 1;

    public static final int NUM_ROUNDS = 15;
    public static final int NUM_ROUNDS_TO_WIN = (int) Math.ceil(NUM_ROUNDS/2.0);


    public enum MatchPhase {
        LOAD_PHASE,
        BUY_PHASE,
        SHOOTING_PHASE,
        END_PHASE
    }
    
    private MatchData() {}
}