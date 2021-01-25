package duber.game.phases;

/**
 * A class that can manage MatchPhases.
 * @author Darren Lo
 * @version 1.0
 */
public interface MatchPhaseManager {

    /**
     * Changes the current MatchPhase
     * @param nextMatchPhase the next MatchPhase
     */
    public abstract void changeMatchPhase(MatchPhase nextMatchPhase);
}