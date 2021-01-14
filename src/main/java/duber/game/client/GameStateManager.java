package duber.game.client;

import java.util.Stack;

import duber.engine.Cleansable;
import duber.engine.Window;
import duber.engine.exceptions.LWJGLException;
import duber.game.client.gui.MainMenu;
import duber.game.client.gui.OptionsMenu;
import duber.game.client.match.Match;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;


public class GameStateManager implements Cleansable {
    public enum GameStateOption {
        MAIN_MENU(new MainMenu()), MATCH(new Match()), OPTIONS_MENU(new OptionsMenu());

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

    public void pushState(GameStateOption gameStateOption) {
        gameStates.push(gameStateOption.getGameState());
        gameStateStartup();
    }

    public GameState popState() {
        GameState popped = gameStates.pop();
        gameStateStartup();
        return popped;
    }

    public void clearState(GameState firstState) {
        gameStates.clear();
        gameStates.push(firstState);
    }

    public void changeState(GameStateOption gameStateOption) {
        gameStates.pop();
        pushState(gameStateOption);
    }

    public GameState getCurrState() {
        return gameStates.peek();
    }

    public boolean isCurrState(GameStateOption stateOption) {
        return getCurrState() == stateOption.getGameState();
    }

    public void gameStateStartup() {
        gameStates.peek().startup();
    }

    public void update() {
        for(GameState gameState : gameStates) {
            if(gameState == getCurrState() || gameState.isUpdateInBackground()) {
                gameState.update();
            }
        }

        //Open up options panel
        if(window.getKeyboardInput().isKeyPressed(GLFW_KEY_ESCAPE)) {
            if(!escapeHeldDown) {
                escapeHeldDown = true;
                if(isCurrState(GameStateOption.OPTIONS_MENU)) {
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
        gameStates.peek().render();
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