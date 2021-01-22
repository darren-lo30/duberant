package duber.game.phases;

import duber.engine.utilities.Utils;
import duber.game.MatchData;
import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.server.MatchManager;

public class RoundEndPhase extends MatchPhase {
    private static final int TIME_TO_LAST = 5;

    private int winningTeam;

    public RoundEndPhase(int winningTeam) {
        this.winningTeam = winningTeam;
    }

    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientRoundEndLogic(match));
    }

    @Override
    public void makeServerLogic(MatchManager match) {
        setPhaseLogic(new TimedPhaseLogic(match, TIME_TO_LAST, new BuyPhase()));
        match.getScoreboard().addWin(winningTeam);
        match.resetPlayerMovement();
    }
    
    private class ClientRoundEndLogic extends ClientLogic {
        public ClientRoundEndLogic(Match match) {
            super(match);
        }

        @Override
        public void render() {
            String message = String.format("%s team won the round", Utils.capitalize(MatchData.getTeamString(winningTeam)));
            getHud().displayText(message, 0.5f, 0.5f, true, HUD.TITLE_FONT);
        }
    }

    @SuppressWarnings("unused")
    private RoundEndPhase() {}
}