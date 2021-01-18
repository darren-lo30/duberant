package duber.game.phases;

import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.server.MatchManager;

public class LoadingPhase extends MatchPhase {

    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientLoadingLogic(match));

    }

    @Override
    public void makeServerLogic(MatchManager match) {
        setPhaseLogic(new ServerLoadingLogic(match));
        match.sendMatchInitializationData();
    }

    private class ClientLoadingLogic extends ClientLogic {
        public ClientLoadingLogic(Match match) {
            super(match);
        }

        @Override
        public void render() {
            match.getHud().displayText("Loading...", 0.5f, 0.5f, true, HUD.TITLE_FONT);
        }
    }

    private class ServerLoadingLogic extends ServerLogic {
        public ServerLoadingLogic(MatchManager match) {
            super(match);
        }

        @Override
        public void tryChangeMatchPhase() {
            changeMatchPhase(new BuyPhase());
        }
    }
    
}