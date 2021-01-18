package duber.engine;

import duber.engine.exceptions.LWJGLException;

public abstract class GameLogic implements Cleansable {
    private GameEngine gameEngine;

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    public abstract void init(Window window) throws LWJGLException;

    public abstract void update();

    public abstract void render();    
}