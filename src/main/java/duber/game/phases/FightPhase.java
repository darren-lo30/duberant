package duber.game.phases;

import duber.game.MatchData;
import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.client.match.HUD.Font;
import duber.game.server.MatchManager;

public class FightPhase extends MatchPhase {

    public FightPhase() {
        setPlayerCanMove(true);
    }

    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientFightLogic(match));
    }

    @Override
    public void makeServerLogic(MatchManager match) {
        setPhaseLogic(new ServerFightLogic(match));

    }

    private class ServerFightLogic extends ServerLogic {
        public ServerFightLogic(MatchManager match) {
            super(match);
        }

        @Override
        public void tryChangeMatchPhase() {
            int roundWinner = match.getRoundWinner();
            if(match.getRoundWinner() != MatchData.NULL_TEAM) {
                changeMatchPhase(new RoundEndPhase(roundWinner));
            }    
        }
    }

    private class ClientFightLogic extends ClientLogic {
        private static final String DEATH_MESSAGE = "You are dead. Waiting for the round to end...";
        
        private final Font deathFont = new Font(HUD.MAIN_FONT_ID, 50.0f, HUD.RED);
        
        public ClientFightLogic(Match match) {
            super(match);
        }

		@Override
		public void render() {
            if(!match.getMainPlayer().isAlive()) {
                getHud().displayText(DEATH_MESSAGE, 0.5f, 0.5f, true, deathFont);
            } else {
                match.renderGameScene();
            }
        }
    }
    
}