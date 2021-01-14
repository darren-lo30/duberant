package duber.game.client;

import duber.engine.exceptions.LWJGLException;

public abstract class GameState {
    private Duberant game;
    private GameStateManager manager;
    private boolean updateInBackground;

    public Duberant getGame() {
        return game;
    }

    public GameStateManager getManager() {
        return manager;
    }

    public void init(Duberant game, GameStateManager manager) throws LWJGLException {
        this.game = game;
        this.manager = manager;
        updateInBackground = false;
        init();
    }

    protected abstract void init() throws LWJGLException;

    public abstract void startup();
    public abstract void update();
    public abstract void render();

    public boolean isUpdateInBackground() {
        return updateInBackground;
    }

    public void setUpdateInBackground(boolean updateInBackground) {
        this.updateInBackground = updateInBackground;
    }

    public void popSelf() {
        if(manager.popState() != this) {
            throw new IllegalStateException("Closing game state that is not currently at the top");
        }
    }    
}