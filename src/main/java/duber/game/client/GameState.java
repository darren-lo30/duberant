package duber.game.client;

import duber.engine.exceptions.LWJGLException;

public interface GameState {
    public abstract void init(Duberant game) throws LWJGLException ;
    public abstract void update(Duberant game, GameStateManager gameStateManager);
    public abstract void render(Duberant game, GameStateManager gameStateManager);

    public default void close(GameStateManager gameStateManager) {
        if(gameStateManager.popState() != this) {
            throw new IllegalStateException("Closing game state that is not currently at the top");
        }
    }    
}