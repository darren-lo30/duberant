package duber.game.phases;

import duber.engine.utilities.Utils;
import duber.game.MatchData;
import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.server.MatchManager;

public class MatchEndPhase extends MatchPhase {
    private static final float TIME_TO_LAST = 5;

    private int matchWinner;

    public MatchEndPhase(int matchWinner) {
        this.matchWinner = matchWinner;
    }

    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientLoadingLogic(match));
    }

    @Override
    public void makeServerLogic(MatchManager match) {
        match.setIsOver(true);
        setPhaseLogic(new TimedPhaseLogic(match, TIME_TO_LAST, new MatchLeavePhase()));
    }

    private class ClientLoadingLogic extends ClientLogic {
        public ClientLoadingLogic(Match match) {
            super(match);
        }

        @Override
        public void render() {
            String message = String.format("%s won the match", Utils.capitalize(MatchData.getTeamString(matchWinner)));
            getHud().displayText(message, 0.5f, 0.5f, true, HUD.TITLE_FONT);
        }
    }    

    @SuppressWarnings("unused")
    private MatchEndPhase() {}
}