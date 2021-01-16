package duber.game.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import duber.engine.Cleansable;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.game.client.gui.MainMenu;
import duber.game.client.gui.OptionsMenu;
import duber.game.client.gui.ScoreboardDisplay;
import duber.game.client.match.Match;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;


public class GameStateManager implements Cleansable {
    public enum GameStateOption {
        MAIN_MENU           (new MainMenu()), 
        MATCH               (new Match()), 
        OPTIONS_MENU        (new OptionsMenu()),
        SCOREBOARD_DISPLAY  (new ScoreboardDisplay());

        private final GameState gameState;

        private GameStateOption(GameState gameState) {
            this.gameState = gameState;
        }

        public GameState getGameState() {
            return gameState;
        }
    }

    private Window window;
    private Stack<GameState> gameStates;

    private boolean escapeHeldDown;

    public GameStateManager(Duberant game) throws LWJGLException {
        gameStates = new Stack<>();
        window = game.getWindow();

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
        GameState gameState = gameStateOption.getGameState();

        gameState.startup();
        gameState.enter();

        gameStates.push(gameState);
    }

    public GameState popState() {
        GameState popped = gameStates.pop();
        popped.close();

        if(!gameStates.isEmpty()) {
            gameStates.peek().enter();
        }
        return popped;
    }

    public void clearState(GameStateOption firstState) {
        while(!gameStates.isEmpty()) {
            gameStates.pop().close();
        }

        pushState(firstState);
    }

    public GameState changeState(GameStateOption gameStateOption) {
        GameState popped = gameStates.pop();
        popped.close();
        pushState(gameStateOption);

        return popped;
    }

    public GameState getFocusedState() {
        return gameStates.peek();
    }

    public boolean stateIsFocused(GameState gameState) {
        return getFocusedState() == gameState;
    }

    public boolean stateIsFocused(GameStateOption gameStateOption) {
        return stateIsFocused(gameStateOption.getGameState());
    }
    
    public void update() {
        List<GameState> gameStatesList = new ArrayList<>(gameStates);


        //Iterate the list backwards to simulate the stack order
        for(int i = gameStatesList.size() -1; i >= 0; i--) {
            GameState gameState = gameStatesList.get(i);
            
            if(gameState == getFocusedState() || gameState.isUpdateInBackground()) {
                gameState.update();
            }
        }
    
        //Open up options panel
        if(window.getKeyboardInput().isKeyPressed(GLFW_KEY_ESCAPE)) {
            if(!escapeHeldDown) {
                escapeHeldDown = true;
                if(stateIsFocused(GameStateOption.OPTIONS_MENU)) {
                    popState();
                } else {
                    pushState(GameStateOption.OPTIONS_MENU);
                }
            }
        } else {
            escapeHeldDown = false;
        }
    }

    public void render() {
        getFocusedState().render();
    }

    @Override
    public void cleanup() {
        while(!gameStates.isEmpty()){
            GameState nextState = gameStates.pop();
            if(nextState instanceof Cleansable) {
                ((Cleansable) nextState).cleanup();
            }
        }
    }    

}