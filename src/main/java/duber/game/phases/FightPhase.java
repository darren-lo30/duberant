package duber.game.phases;

import duber.game.MatchData;
import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.client.match.HUD.Font;
import duber.game.server.MatchManager;

/**
 * The MatchPhase where Players try to eliminate the enemy team
 * @author Darren Lo
 * @version 1.0
 */
public class FightPhase extends MatchPhase {

    /**
     * A constructor for the fight phase
     */
    public FightPhase() {
        setPlayerCanMove(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientFightLogic(match));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeServerLogic(MatchManager match) {
        setPhaseLogic(new ServerFightLogic(match));

    }

    /**
     * The FightPhase logic used during the phase on the server side
     */
    private class ServerFightLogic extends ServerLogic {
        /**
         * Constructs the FightPhase logic for the server.
         * @param match the server's match
         */
        public ServerFightLogic(MatchManager match) {
            super(match);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void tryChangeMatchPhase() {
            int roundWinner = match.getRoundWinner();
            if (match.getRoundWinner() != MatchData.NULL_TEAM) {
                changeMatchPhase(new RoundEndPhase(roundWinner));
            }    
        }
    }

    /**
     * The FightPhase logic used during the phase on the client side
     */
    private class ClientFightLogic extends ClientLogic {
        /**
         * The death message to display.
         */
        private static final String DEATH_MESSAGE = "You are dead. Waiting for the round to end...";
        
        /**
         * The font used to display the death message.
         */
        private final Font deathFont = new Font(HUD.MAIN_FONT_ID, 50.0f, HUD.RED);
        
        /**
         * Constructs the FightPhase logic for the client.
         * @param match the client's match
         */
        public ClientFightLogic(Match match) {
            super(match);
        }

        /**
         * {@inheritDoc}
         */
		@Override
		public void render() {
            if (!match.getMainPlayer().isAlive()) {
                getHud().displayText(DEATH_MESSAGE, 0.5f, 0.5f, true, deathFont);
            } else {
                match.renderGameScene();
            }
        }
    }
    
}