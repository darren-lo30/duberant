package duber.engine;

import duber.engine.exceptions.LWJGLException;

public interface IGameLogic extends Cleansable {
    void init(Window window) throws LWJGLException;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput);

    void render(Window window);
    
    void cleanup();
}