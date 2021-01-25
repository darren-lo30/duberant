package duber.game.phases;

import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.client.match.HUD.Font;
import duber.game.server.MatchManager;

/**
 * The MatchPhase where the Players can purchase guns
 * @author Darren Lo
 * @version 1.0
 */
public class BuyPhase extends MatchPhase {
    /**
     * The amount of time the BuyPhase should last
     */
    private static final int TIME_TO_LAST = 25;

    /**
     * A constructor for a BuyPhase.
     */
    public BuyPhase() {
        setPlayerCanBuy(true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientBuyLogic(match));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeServerLogic(MatchManager match) {
        setPhaseLogic(new TimedPhaseLogic(match, TIME_TO_LAST, new FightPhase()));
        match.startRound();
    }    

    /**
     * The BuyPhase logic used during the phase on the client side
     */
    private class ClientBuyLogic extends ClientLogic {
        /**
         * Constructs the BuyPhase logic for the client
         * @param match the clients match
         */
        public ClientBuyLogic(Match match) {
            super(match);
        }

        /**
         * {@inheritDoc}
         */
		@Override
		public void render() {
            match.renderGameScene();
            Font buyPhaseTitleFont = new Font(HUD.MAIN_FONT_ID, 100.0f, HUD.WHITE);
            getHud().displayTextWithBackground("BUY PHASE", 0.5f, 0.2f, true, buyPhaseTitleFont, 20f, HUD.TRANSLUCENT_BLACK);
        }
    }
}