package duber.game.phases;

import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.server.MatchManager;

/**
 * The MatchPhase where the Players are loading in to the game
 * @author Darren Lo
 * @version 1.0
 */
public class LoadingPhase extends MatchPhase {

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeClientLogic(Match match) {
        setPhaseLogic(new ClientLoadingLogic(match));

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeServerLogic(MatchManager match) {
        setPhaseLogic(new ServerLoadingLogic(match));
        match.sendMatchInitializationData();
    }

    /**
     * The LoadingPhase logic used during the phase on the client side
     */
    private class ClientLoadingLogic extends ClientLogic {
        /**
         * Constructs the LoadingPhase logic for the client.
         * @param match the clients's match
         */
        public ClientLoadingLogic(Match match) {
            super(match);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void render() {
            match.getHud().displayText("Loading...", 0.5f, 0.5f, true, HUD.TITLE_FONT);
        }
    }

    /**
     * The LoadingPhase logic used during the phase on the server side
     */
    private class ServerLoadingLogic extends ServerLogic {
        /**
         * Constructs the LoadingPhase logic for the server.
         * @param match the servers's match
         */
        public ServerLoadingLogic(MatchManager match) {
            super(match);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void tryChangeMatchPhase() {
            changeMatchPhase(new BuyPhase());
        }
    }
    
}