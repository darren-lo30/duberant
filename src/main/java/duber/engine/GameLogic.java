package duber.engine;

import duber.engine.exceptions.LWJGLException;

/**
 * A class that abstracts GameLogic used to make a game
 */
public abstract class GameLogic implements Cleansable {

    /**
     * The game engine that runs the game.
     */
    private GameEngine gameEngine;

    /**
     * Gets the game engine running the game.
     * @return the game engine running the game
     */
    public GameEngine getGameEngine() {
        return gameEngine;
    }

    /**
     * Sets the game engine running the game.
     * @param gameEngine the game engine running the game
     */
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Initializes the game.
     * @param window the Window displaying the game
     * @throws LWJGLException if the game could not be initialized
     */
    public abstract void init(Window window) throws LWJGLException;

    /**
     * Updates the game.
     */
    public abstract void update();

    /**
     * Renders the game.
     */
    public abstract void render();    
}