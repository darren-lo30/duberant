package duber.game.phases;

import duber.game.client.match.Match;
import duber.game.server.MatchManager;

public class MatchLeavePhase extends MatchPhase {

    @Override
    public void makeClientLogic(Match match) {
        match.leave();
    }

    @Override
    public void makeServerLogic(MatchManager match) {
        match.close();
    }
}