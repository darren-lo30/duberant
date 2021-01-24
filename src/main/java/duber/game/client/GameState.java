package duber.game.client;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.CallbackI;

import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;

public abstract class GameState {
    private Duberant game;
    private GameStateManager manager;

    private final List<CallbackI> callbacks = new ArrayList<>();

    private boolean updateInBackground = false;
    private boolean opened = false;
    private boolean shouldClose = false;

    public Duberant getGame() {
        return game;
    }

    public Window getWindow() {
        return game.getWindow();
    }

    public GameStateManager getManager() {
        return manager;
    }

    public List<CallbackI> getCallbacks() {
        return callbacks;
    }

    public void setShouldClose(boolean shouldClose) {
        this.shouldClose = shouldClose;
    }

    public boolean shouldClose() {
        return shouldClose;
    }

    public boolean isUpdateInBackground() {
        return updateInBackground;
    }

    public void setUpdateInBackground(boolean updateInBackground) {
        this.updateInBackground = updateInBackground;
    }

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean isFocused() {
        return manager.stateIsFocused(this);
    }

    public void popSelf() {
        if (isFocused()) {
            manager.popState();
        } else {
            throw new IllegalStateException("Can't pop self if not focused");
        }
    }

    public void pushSelf() {
        manager.pushState(this);
    }

    public void init(Duberant game, GameStateManager manager) throws LWJGLException {
        this.game = game;
        this.manager = manager;
        init();
    }

    public void enableCallbacks() {
        for(CallbackI callback : callbacks) {
            getWindow().addCallback(callback);
        }
    }

    public void disableCallbacks() {
        for(CallbackI callback : callbacks) {
            getWindow().removeCallback(callback);
        }
    }

    protected abstract void init() throws LWJGLException;

    public abstract void startup();

    public abstract void enter();

    public abstract void exit();

    public abstract void close();
    
    public abstract void update();
    public abstract void render();
}