package duber.engine;

import duber.engine.exceptions.LWJGLException;
import duber.engine.utilities.Timer;

public final class GameEngine implements Runnable, Cleansable {
    //Targeted FPS
    private static final int TARGET_FPS = 60;

    //Targetted amount of updates per second
    private static final int TARGET_UPS = 30;

    String windowTitle;
    
    private final Window window;
    
    private final Timer updateTimer;
    
    private final GameLogic gameLogic;    

    private final Timer fpsTimer;
    
    private int fps;

    private float interpolationFactor;

    
    public GameEngine(String windowTitle, int width, int height, GameLogic gameLogic) {
        this.windowTitle = windowTitle;
        this.gameLogic = gameLogic;
        gameLogic.setGameEngine(this);

        window = new Window(windowTitle, width, height);
        updateTimer = new Timer();
        
        
        fps = 0;
        fpsTimer = new Timer();
    }

    public float getInterpolationFactor() {
        return interpolationFactor;
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

        System.exit(0);
    }

    private void init() throws LWJGLException {
        gameLogic.init(window);
    }

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

            if(!window.optionIsTurnedOn(Window.Options.ENABLE_VSYNC)) {
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

    public void update() {
        gameLogic.update();
    }

    private void calculateAndDisplayFps() {
        if (fpsTimer.secondHasPassed()) {
            fpsTimer.getElapsedTimeAndUpdate();
            if(window.optionIsTurnedOn(Window.Options.DISPLAY_FPS)) {
                window.setTitle(windowTitle + " - " + fps + " FPS");
            }
            fps = 0;
        }
        fps++;
    }

    public void render() {
        window.clear();        
        calculateAndDisplayFps();        
        gameLogic.render();
        window.update();
    }

    public void cleanup() {
        gameLogic.cleanup();
    }
}