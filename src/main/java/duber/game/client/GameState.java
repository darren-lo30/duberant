package duber.game.client;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.system.CallbackI;

import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;

/**
 * A representation of a state in the game.
 * @author Darren Lo
 * @version 1.0
 */
public abstract class GameState {
    /** The game that this GameState is for. */
    private Duberant game;

    /** this GameStateManager that manages this GameState. */
    private GameStateManager manager;

    /**
     * The callbacks to use during this GameState.
     */
    private final List<CallbackI> callbacks = new ArrayList<>();

    /** If updates should happen when not focused. */
    private boolean updateInBackground = false;

    /** If it is opened. */
    private boolean opened = false;

    /** If it should close. */
    private boolean shouldClose = false;

    /**
     * Gets the game.
     * @return the game
     */
    public Duberant getGame() {
        return game;
    }

    /**
     * Gets the Window used to display the game.
     * @return the Window displaying the game.
     */
    public Window getWindow() {
        return game.getWindow();
    }

    /**
     * Gets this GameStateManager that manages this.
     * @return this GameStateManager that manages this.
     */
    public GameStateManager getManager() {
        return manager;
    }

    /**
     * Gets the callbacks to use during this GameState.
     * @return the callbacks to use
     */
    public List<CallbackI> getCallbacks() {
        return callbacks;
    }

    /**
     * Sets whether or not this GameState should close.
     * @param shouldClose
     */
    public void setShouldClose(boolean shouldClose) {
        this.shouldClose = shouldClose;
    }

    /**
     * Gets if this GameState should close.
     * @return whether or not this GameState should close.
     */
    public boolean shouldClose() {
        return shouldClose;
    }

    /**
     * Gets if this GameState should update in the background.
     * @return whethter or not this GameState should update in the background
     */
    public boolean isUpdateInBackground() {
        return updateInBackground;
    }

    /**
     * Sets if this GaemState should update in the background. 
     * @param updateInBackground if this GameState should update in the background
     */
    public void setUpdateInBackground(boolean updateInBackground) {
        this.updateInBackground = updateInBackground;
    }

    /**
     * Gets if this GameState is opeend.
     * @return whether or not this GameState is opeend
     */
    public boolean isOpened() {
        return opened;
    }

    /**
     * Sets if this GameState is opened.
     * @param opened if this GameState is opened
     */
    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    /**
     * Gets if this GameState is focused.
     * @return if this GameState is focused
     */
    public boolean isFocused() {
        return manager.stateIsFocused(this);
    }

    /**
     * Pops this GameState from the parent GameStateManager
     */
    public void popSelf() {
        if (isFocused()) {
            manager.popState();
        } else {
            throw new IllegalStateException("Can't pop self if not focused");
        }
    }

    /**
     * Pushs this GameState onto the parent GameStateManager.
     */
    public void pushSelf() {
        manager.pushState(this);
    }

    /**
     * Initializes this GameState along with references to the game and the manager.
     * @param game the game this GameState is for
     * @param manager the GameStateManager that manages this GameState
     * @throws LWJGLException if this GameState could not be initialized
     */
    public void init(Duberant game, GameStateManager manager) throws LWJGLException {
        this.game = game;
        this.manager = manager;
        init();
    }

    /**
     * Enables all the callbacks associated with this GameState.
     */
    public void enableCallbacks() {
        for(CallbackI callback : callbacks) {
            getWindow().addCallback(callback);
        }
    }

    /**
     * Disables all callbacks associated with this GameState.
     */
    public void disableCallbacks() {
        for(CallbackI callback : callbacks) {
            getWindow().removeCallback(callback);
        }
    }

    /**
     * Initializes this GameState.
     * @throws LWJGLException if this GameState could not be initialized
     */
    protected abstract void init() throws LWJGLException;

    /**
     * Runs when this GameState is pushed to the manager.
     */
    public abstract void startup();

    /**
     * Runs this GameState becomes the focused GameState.
     */
    public abstract void enter();

    /**
     * Runs when this GameState loses focus.
     */
    public abstract void exit();

    /**
     * Runs when this GameState is popped from the manager.
     */
    public abstract void close();
    
    /**
     * Updates this GameState.
     */
    public abstract void update();

    /**
     * Renders this GameState.
     */
    public abstract void render();
}