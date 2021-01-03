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
    
    private final MouseInput mouseInput;
    
    private final Timer fpsTimer;
    
    private int fps;

    
    public GameEngine(String windowTitle, int width, int height, boolean vSync, IGameLogic gameLogic) {
        this.windowTitle = windowTitle;
        this.gameLogic = gameLogic;

        window = new Window(windowTitle, width, height, vSync);
        updateTimer = new Timer();
        
        mouseInput = new MouseInput();
        
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

    protected void init() throws LWJGLException {
        window.init();
        gameLogic.init(window);
        mouseInput.init(window);
    }

    protected void gameLoop() {
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

            //Render the scene
            render();

            if(!window.isvSync()) {
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

    protected void input() {
        mouseInput.updateDisplacementVec();
    }

    protected void update(float interval) {
        gameLogic.update(interval, mouseInput);
    }

    private void calculateAndDisplayFps() {
        if (fpsTimer.secondHasPassed()) {
            fpsTimer.getElapsedTime();
            window.setTitle(windowTitle + " - " + fps + " FPS");
            fps = 0;
        }
        fps++;
    }

    protected void render() {
        calculateAndDisplayFps();        
        gameLogic.render(window);
        window.update();
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }
}