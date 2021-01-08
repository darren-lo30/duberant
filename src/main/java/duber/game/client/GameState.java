package duber.game.client;

import duber.engine.exceptions.LWJGLException;

public abstract class GameState {
    private Duberant game;
    private GameStateManager manager;

    public Duberant getGame() {
        return game;
    }

    public GameStateManager getManager() {
        return manager;
    }

    public void init(Duberant game, GameStateManager manager) throws LWJGLException {
        this.game = game;
        this.manager = manager;
        init();
    }

    protected abstract void init() throws LWJGLException;

    public abstract void startup();
    public abstract void update();
    public abstract void render();

    public void popSelf() {
        if(manager.popState() != this) {
            throw new IllegalStateException("Closing game state that is not currently at the top");
        }
    }    
}