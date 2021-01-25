package duber.game.phases;

import duber.engine.utilities.Utils;
import duber.game.MatchData;
import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.server.MatchManager;

/**
 * The MatchPhase where the winner of the round is displayed.
 * @author Darren Lo
 * @version 1.0
 */
public class RoundEndPhase extends MatchPhase {
    /**
     * The time the RoundEndPhase should last.
     */
    private static final int TIME_TO_LAST = 5;

    /**
     * The winner of the round.
     */
    private int winningTeam;

    /**
     * Constructs a RoundEndPhase.
     * @param winningTeam the winner of the round
     */
    public RoundEndPhase(int winningTeam) {
        this.winningTeam = winningTeam;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientRoundEndLogic(match));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeServerLogic(MatchManager match) {
        setPhaseLogic(new TimedPhaseLogic(match, TIME_TO_LAST, new BuyPhase()));
        match.getScoreboard().addWin(winningTeam);
        match.resetPlayerMovement();
    }
    
    /**
     * The RoundEnd logic used during the phase on the client side
     */
    private class ClientRoundEndLogic extends ClientLogic {
        /**
         * Constructs the RoundEnd logic for the client.
         * @param match the clients's match
         */
        public ClientRoundEndLogic(Match match) {
            super(match);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void render() {
            String message = String.format("%s team won the round", Utils.capitalize(MatchData.getTeamString(winningTeam)));
            getHud().displayText(message, 0.5f, 0.5f, true, HUD.TITLE_FONT);
        }
    }

    @SuppressWarnings("unused")
    private RoundEndPhase() {}
}