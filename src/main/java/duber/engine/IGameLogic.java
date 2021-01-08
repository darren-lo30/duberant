package duber.engine;

import duber.engine.exceptions.LWJGLException;

public interface IGameLogic extends Cleansable {
    void init(Window window) throws LWJGLException;

    void update();

    void render();
    
    void cleanup();
}