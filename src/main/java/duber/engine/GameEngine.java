package duber.engine;

import duber.engine.exceptions.LWJGLException;
import duber.engine.utilities.Timer;

/**
 * A game engine used to run a game loop.
 */
public final class GameEngine implements Runnable, Cleansable {
    /** The target frames per second to acheive. */
    private static final int TARGET_FPS = 60;

    /** The target updates per second to achieve. */
    private static final int TARGET_UPS = 30;

    /**
     * The title of the game.
     */
    String gameTitle;
    
    /** The window used to display the game. */
    private final Window window;
    
    /** The timer used for the game loop. */
    private final Timer updateTimer;
    
    /** The GameLogic used to run the game. */
    private final GameLogic gameLogic;    

    /** The Timer used to calculate the frames per second. */
    private final Timer fpsTimer;
    
    /** The number of frames per second of the game. */
    private int fps;
    
    /** The interpolation factor of the render. */
    private float interpolationFactor;

    /**
     * Constructs a GameEngine.
     * @param gameTitle the title of the game and the initial title of the window.
     * @param width the width of the window
     * @param height the height of the window
     * @param gameLogic the game running in the GameEngine
     */
    public GameEngine(String gameTitle, int width, int height, GameLogic gameLogic) {
        this.gameTitle = gameTitle;
        this.gameLogic = gameLogic;
        gameLogic.setGameEngine(this);

        window = new Window(gameTitle, width, height);
        updateTimer = new Timer();
        
        
        fps = 0;
        fpsTimer = new Timer();
    }

    /**
     * Gets the interpolation factor.
     * @return the interpolation factor
     */
    public float getInterpolationFactor() {
        return interpolationFactor;
    }

    /**
     * Runs the game loop
     */
    @Override
    public void run() {
        try {
            init();
            gameLoop();
        } catch (LWJGLException lwjgle) {
            lwjgle.printStackTrace();
        } finally {
            cleanup();
        }

        System.exit(0);
    }

    /**
     * Initializes the GameEngine when it runs.
     * @throws LWJGLException if the GameEngine could not be initialized
     */
    private void init() throws LWJGLException {
        gameLogic.init(window);
    }

    /**
     * The game loop that constantly updates and renders the game.
     */
    private void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1.0f/TARGET_UPS;

        while(!window.shouldClose()) {
            elapsedTime = Math.min(0.25f, updateTimer.getElapsedTimeAndUpdate());
            accumulator += elapsedTime;

            //Get any input

            //Calculate updates in the scene
            while(accumulator >= interval) {
                update();
                accumulator -= interval;
            }

            interpolationFactor = accumulator/interval;
            render();

            if (!window.optionIsTurnedOn(Window.Options.ENABLE_VSYNC)) {
                sync();
            }
        }
    }

    /**
     * Syncs the game so that there are no extra renders.
     */
    private void sync() {
        float loopSlot = 1.0f/TARGET_FPS;
        double endTime = updateTimer.getLastRecordedTime() + loopSlot;
        while(updateTimer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Updates the game
     */
    public void update() {
        gameLogic.update();
    }

    /**
     * Calculates and displays the frames per second.
     */
    private void calculateAndDisplayFps() {
        if (fpsTimer.secondHasPassed()) {
            fpsTimer.getElapsedTimeAndUpdate();
            if (window.optionIsTurnedOn(Window.Options.DISPLAY_FPS)) {
                window.setTitle(gameTitle + " - " + fps + " FPS");
            }
            fps = 0;
        }
        fps++;
    }

    /**
     * Renders the game.
     */
    public void render() {
        window.clear();        
        calculateAndDisplayFps();        
        gameLogic.render();
        window.update();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        gameLogic.cleanup();
    }
}