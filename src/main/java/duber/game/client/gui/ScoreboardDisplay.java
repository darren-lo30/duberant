package duber.game.client.gui;

import duber.engine.exceptions.LWJGLException;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.client.match.Match;
import duber.game.gameobjects.Scoreboard;

public class ScoreboardDisplay extends GUI {
    @Override
    protected void init() throws LWJGLException {
        // TODO Auto-generated method stub
    }

    @Override
    public void enter() {
        super.enter();
        if(!getManager().getState(GameStateOption.MATCH).isOpened()) {
            throw new IllegalStateException("There must be a match ongoing to open the scoreboard");
        }

        getMatchScoreboard().updateScoreboard();
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render() {
        // TODO Auto-generated method stub
    }

    private Scoreboard getMatchScoreboard() {
        return ((Match) getManager().getState(GameStateOption.MATCH)).getScoreboard();
    }
    
}