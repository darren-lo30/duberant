package duber.engine;

import duber.engine.exceptions.LWJGLException;

public class GameEngine implements Runnable {
    //Targeted FPS
    private static final int TARGET_FPS = 60;

    //Targetted amount of updates per second
    private static final int TARGET_UPS = 30;

    private final Window window;
    
    private final Timer updateTimer;

    private final Timer fpsTimer;
    
    private final IGameLogic gameLogic;

    private int fps;

    String title;

    private final MouseInput mouseInput;

    public GameEngine(String title, int width, int height, boolean vSync, IGameLogic gameLogic){
        this.title = title;
        window = new Window(title, width, height, vSync);
        this.gameLogic = gameLogic;
        updateTimer = new Timer();
        fpsTimer = new Timer();

        mouseInput = new MouseInput();
    }

    @Override
    public void run() {
        try {
            init();
            gameLoop();
            
        } catch (LWJGLException lwjgle){
            lwjgle.printStackTrace();
        } finally {
            cleanup();
        }
    }

    protected void init() throws LWJGLException {
        window.init();
        updateTimer.init();
        updateTimer.init();
        gameLogic.init(window);
        mouseInput.init(window);
    }

    protected void gameLoop(){
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1.0f/TARGET_UPS;

        while(!window.shouldClose()){
            elapsedTime = updateTimer.getElapsedTime();
            accumulator += elapsedTime;

            //Get any input
            input();

            //Calculate updates in the scene
            while(accumulator >= interval){
                update(interval);
                accumulator -= interval;
            }

            //Render the scene
            render();

            if(!window.isvSync()){
                sync();
            }
        }
    }

    private void sync(){
        float loopSlot = 1.0f/TARGET_FPS;
        double endTime = updateTimer.getLastLoopTime() + loopSlot;
        while(updateTimer.getTime() < endTime){
            try {
                Thread.sleep(1);
            } catch (InterruptedException ie){
                ie.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

    protected void input(){
        mouseInput.input();
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval){
        gameLogic.update(interval, mouseInput);
    }

    protected void render(){
        if (fpsTimer.secondHasPassed()) {
            fpsTimer.getLastLoopTime();
            window.setTitle(title + " - " + fps + " FPS");
            fps = 0;
        }
        fps++;
        gameLogic.render(window);
        window.update();
    }

    protected void cleanup(){
        gameLogic.cleanup();
    }
}