package duber.engine;

import duber.engine.exceptions.LWJGLException;
import duber.engine.utilities.Timer;

public class GameEngine implements Runnable {
    //Targeted FPS
    private static final int TARGET_FPS = 60;

    //Targetted amount of updates per second
    private static final int TARGET_UPS = 30;

    String windowTitle;
    private final Window window;
    
    private final Timer updateTimer;
    
    private final IGameLogic gameLogic;    

    private final Timer fpsTimer;
    
    private int fps;

    
    public GameEngine(String windowTitle, int width, int height, IGameLogic gameLogic) {
        this.windowTitle = windowTitle;
        this.gameLogic = gameLogic;

        window = new Window(windowTitle, width, height);
        updateTimer = new Timer();
        
        
        fps = 0;
        fpsTimer = new Timer();
    }

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
    }

    private void init() throws LWJGLException {
        gameLogic.init(window);
    }

    private void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1.0f/TARGET_UPS;

        while(!window.shouldClose()) {
            elapsedTime = updateTimer.getElapsedTime();
            accumulator += elapsedTime;

            //Get any input

            //Calculate updates in the scene
            while(accumulator >= interval) {
                input();
                update(interval);
                accumulator -= interval;
            }

            float interpolationFactor = accumulator/interval;
            //Render the scene
            render(interpolationFactor);

            if(!window.getOptions().isTurnedOn(Window.Options.ENABLE_VSYNC)) {
                sync();
            }
        }
    }

    private void sync() {
        float loopSlot = 1.0f/TARGET_FPS;
        double endTime = updateTimer.getLastLoopTime() + loopSlot;
        while(updateTimer.getTime() < endTime) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    private void input() {
        window.getMouseInput().updateCursorDisplacement();
    }

    private void update(float interval) {
        gameLogic.update(interval, window.getMouseInput(), window.getKeyboardInput());
    }

    private void calculateAndDisplayFps() {
        if (fpsTimer.secondHasPassed()) {
            fpsTimer.getElapsedTime();
            window.setTitle(windowTitle + " - " + fps + " FPS");
            fps = 0;
        }
        fps++;
    }

    private void render(float interpolationFactor) {
        calculateAndDisplayFps();        
        gameLogic.render(window, interpolationFactor);
        window.update();
    }

    private void cleanup() {
        gameLogic.cleanup();
    }
}