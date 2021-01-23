package duber.game.phases;

import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.client.match.HUD.Font;
import duber.game.server.MatchManager;

public class BuyPhase extends MatchPhase {
    private static final int TIME_TO_LAST = 15;

    public BuyPhase() {
        setPlayerCanBuy(true);
    }

    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientBuyLogic(match));
    }

    @Override
    public void makeServerLogic(MatchManager match) {
        setPhaseLogic(new TimedPhaseLogic(match, TIME_TO_LAST, new FightPhase()));
        match.startRound();
    }    

    private class ClientBuyLogic extends ClientLogic {
        public ClientBuyLogic(Match match) {
            super(match);
        }

		@Override
		public void render() {
            match.renderGameScene();
            Font buyPhaseTitleFont = new Font(HUD.MAIN_FONT_ID, 100.0f, HUD.WHITE);
            getHud().displayTextWithBackground("BUY PHASE", 0.5f, 0.2f, true, buyPhaseTitleFont, 20f, HUD.TRANSLUCENT_BLACK);
        }
    }
}