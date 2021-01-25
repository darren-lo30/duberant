package duber.game.phases;

import duber.engine.utilities.Utils;
import duber.game.MatchData;
import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.server.MatchManager;

/**
 * The MatchPhase where the winner of the match is displayed.
 * @author Darren Lo
 * @version 1.0
 */
public class MatchEndPhase extends MatchPhase {
    /**
     * The time the phase should last for.
     */
    private static final float TIME_TO_LAST = 5;

    /**
     * The winner of the match.
     */
    private int matchWinner;

    /**
     * Constructs a MatchEndPhase.
     * @param matchWinner the winner of the match
     */
    public MatchEndPhase(int matchWinner) {
        this.matchWinner = matchWinner;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientMatchEndLogic(match));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeServerLogic(MatchManager match) {
        match.setIsOver(true);
        setPhaseLogic(new TimedPhaseLogic(match, TIME_TO_LAST, new MatchLeavePhase()));
    }

    /**
     * The MatchEndPhase logic used during the phase on the client side
     */
    private class ClientMatchEndLogic extends ClientLogic {
        /**
         * Constructs the MatchEnd logic for the client.
         * @param match the clients's match
         */
        public ClientMatchEndLogic(Match match) {
            super(match);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void render() {
            String message = String.format("%s won the match", Utils.capitalize(MatchData.getTeamString(matchWinner)));
            getHud().displayText(message, 0.5f, 0.5f, true, HUD.TITLE_FONT);
        }
    }    

    /**
     * Used for Kryonet
     */
    @SuppressWarnings("unused")
    private MatchEndPhase() {}
}