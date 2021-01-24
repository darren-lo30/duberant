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

public class GameStateManager implements Cleansable {
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

    private Stack<GameState> gameStates;

    public GameStateManager(Duberant game) throws LWJGLException {
        gameStates = new Stack<>();

        //Initialize all states
        for(GameStateOption option: GameStateOption.values()) {
            option.getGameState().init(game, this);
        }

        //Update match in background
        GameStateOption.MATCH.getGameState().setUpdateInBackground(true);
    }

    public GameState getState(GameStateOption gameStateOption) {
        return gameStateOption.getGameState();
    }

    public void pushState(GameStateOption gameStateOption) {
        pushState(gameStateOption.getGameState());
    }

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

    private GameState popStateLogic() {
        GameState poppedState = gameStates.pop();
        poppedState.setOpened(false);
        poppedState.setShouldClose(false);
        poppedState.exit();
        poppedState.close();

        return poppedState;
    }

    public GameState popState() {
        GameState poppedState = popStateLogic();

        if (!gameStates.isEmpty()) {
            gameStates.peek().enter();
        }
        return poppedState;
    }

    public void clearState(GameStateOption firstState) {
        while(!gameStates.isEmpty()) {
            popStateLogic();
        }

        pushState(firstState);
    }

    public GameState changeState(GameStateOption gameStateOption) {
        GameState poppedState = popStateLogic();
        pushState(gameStateOption);

        return poppedState;
    }

    public GameState getFocusedState() {
        return gameStates.isEmpty() ? null : gameStates.peek();
    }

    public boolean stateIsFocused(GameState gameState) {
        return getFocusedState() == gameState;
    }

    public boolean stateIsFocused(GameStateOption gameStateOption) {
        return stateIsFocused(gameStateOption.getGameState());
    }
    
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

    public void render() {
        if (getFocusedState() != null) {
            getFocusedState().render();
        }
    }

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