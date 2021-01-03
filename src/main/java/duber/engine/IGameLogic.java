package duber.engine;

import duber.engine.exceptions.LWJGLException;

public interface IGameLogic {
    void init(Window window) throws LWJGLException;


    void update(float interval, MouseInput mouseInput);

    void render(Window window, float alpha);
    
    void cleanup();
}