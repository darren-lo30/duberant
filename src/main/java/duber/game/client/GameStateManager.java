package duber.game.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import duber.engine.Cleansable;
import duber.engine.exceptions.LWJGLException;
import duber.game.client.gui.MainMenu;
import duber.game.client.gui.OptionsMenu;
import duber.game.client.gui.ScoreboardDisplay;
import duber.game.client.gui.ShopMenu;
import duber.game.client.match.Match;

/**
 * A manager for GameStates
 * @author Darren Lo
 * @version 1.0
 */
public class GameStateManager implements Cleansable {
    /**
     * All the GameStates available in Duberant
     */
    public enum GameStateOption {
        MAIN_MENU           (new MainMenu()), 
        MATCH               (new Match()), 
        OPTIONS_MENU        (new OptionsMenu()),
        SCOREBOARD_DISPLAY  (new ScoreboardDisplay()),
        SHOP_MENU           (new ShopMenu());

        private final GameState gameState;

        private GameStateOption(GameState gameState) {
            this.gameState = gameState;
        }

        public GameState getGameState() {
            return gameState;
        }
    }

    /**
     * The stack of GameStates in the game.
     */
    private Stack<GameState> gameStates;

    /**
     * Constructs a GameStateManager.
     * @param game the game that is being managed
     * @throws LWJGLException if the GameStateManager could not be created
     */
    public GameStateManager(Duberant game) throws LWJGLException {
        gameStates = new Stack<>();

        //Initialize all states
        for(GameStateOption option: GameStateOption.values()) {
            option.getGameState().init(game, this);
        }

        //Update match in background
        GameStateOption.MATCH.getGameState().setUpdateInBackground(true);
    }

    /**
     * Gets the GameState associated with a GameStateOption.
     * @param gameStateOption the associated GameStateOption
     * @return the associated GameState
     */
    public GameState getState(GameStateOption gameStateOption) {
        return gameStateOption.getGameState();
    }

    /**
     * Pushes a GameStateOption to the stack.
     * @param gameStateOption the GameStateOption whose GameState to push
     */
    public void pushState(GameStateOption gameStateOption) {
        pushState(gameStateOption.getGameState());
    }

    /**
     * Pushes a GameState to the stack.
     * @param gameState the GameState to push
     */
    public void pushState(GameState gameState) {
        if (gameState.isOpened()) {
            throw new IllegalStateException("The game state is already open");
        }

        if (!gameStates.empty()) {
            gameStates.peek().exit();
        }

        gameState.setOpened(true);
        gameState.setShouldClose(false);
        gameState.startup();
        gameState.enter();

        gameStates.push(gameState);
    }

    /**
     * Pops a GameState without entering the new one.
     * @return the popped GameState
     */
    private GameState popStateLogic() {
        GameState poppedState = gameStates.pop();
        poppedState.setOpened(false);
        poppedState.setShouldClose(false);
        poppedState.exit();
        poppedState.close();

        return poppedState;
    }

    /**
     * Pops a GameState from the stack.
     * @return the popped GameState
     */
    public GameState popState() {
        GameState poppedState = popStateLogic();

        if (!gameStates.isEmpty()) {
            gameStates.peek().enter();
        }
        return poppedState;
    }

    /**
     * Clears the GameState stack.
     * @param firstState the starting GameState to push after clearing
     */
    public void clearState(GameStateOption firstState) {
        while(!gameStates.isEmpty()) {
            popStateLogic();
        }

        pushState(firstState);
    }

    /**
     * Changes the current GameState to another GameState.
     * @param gameStateOption the GameStateOption whose GameState to change to
     * @return the removed GameState
     */
    public GameState changeState(GameStateOption gameStateOption) {
        GameState poppedState = popStateLogic();
        pushState(gameStateOption);

        return poppedState;
    }

    /**
     * Gets the current focused GameState.
     * @return the focused GameState
     */
    public GameState getFocusedState() {
        return gameStates.isEmpty() ? null : gameStates.peek();
    }

    /**
     * Determines if a GameState is focused.
     * @param gameState the GameState to check
     * @return whether or not the GameState is focused
     */
    public boolean stateIsFocused(GameState gameState) {
        return getFocusedState() == gameState;
    }

    /**
     * Updates the GameStates.
     */
    public void update() {
        boolean popped = false;
        while(getFocusedState() != null && getFocusedState().shouldClose()) {
            popStateLogic();
            popped = true;
        }

        if (popped && !gameStates.isEmpty()) {
            gameStates.peek().enter();
        }

        List<GameState> gameStatesList = new ArrayList<>(gameStates);
        
        //Iterate the list backwards to simulate the stack order
        for(int i = gameStatesList.size()-1; i >= 0; i--) {
            GameState gameState = gameStatesList.get(i);
            
            if (!gameState.shouldClose() && (stateIsFocused(gameState) || gameState.isUpdateInBackground())) {
                gameState.update();
            }
        }
    }

    /**
     * Renders the focused GameState.
     */
    public void render() {
        if (getFocusedState() != null) {
            getFocusedState().render();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup() {
        while(!gameStates.isEmpty()){
            GameState nextState = gameStates.pop();
            if (nextState instanceof Cleansable) {
                ((Cleansable) nextState).cleanup();
            }
        }
    }    

}