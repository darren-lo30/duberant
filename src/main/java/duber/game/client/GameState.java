package duber.game.client;

import duber.engine.exceptions.LWJGLException;

public abstract class GameState {
    private Duberant game;
    private GameStateManager manager;
    private boolean updateInBackground = false;

    public Duberant getGame() {
        return game;
    }

    public GameStateManager getManager() {
        return manager;
    }

    public boolean isUpdateInBackground() {
        return updateInBackground;
    }

    public void setUpdateInBackground(boolean updateInBackground) {
        this.updateInBackground = updateInBackground;
    }

    public boolean isFocused() {
        return manager.stateIsFocused(this);
    }

    public void init(Duberant game, GameStateManager manager) throws LWJGLException {
        this.game = game;
        this.manager = manager;
        init();
    }

    protected abstract void init() throws LWJGLException;

    public abstract void startup();
    public abstract void enter();
    public abstract void close();
    
    public abstract void update();
    public abstract void render();
}