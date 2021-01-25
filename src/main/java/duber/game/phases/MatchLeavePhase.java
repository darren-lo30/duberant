package duber.game.phases;

import duber.game.client.match.Match;
import duber.game.server.MatchManager;

/**
 * The MatchPhase where the Players are leaving the game.
 * @author Darren Lo
 * @version 1.0
 */
public class MatchLeavePhase extends MatchPhase {

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeClientLogic(Match match) {
        match.leave();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeServerLogic(MatchManager match) {
        match.close();
    }
}