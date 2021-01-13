package duber.game.client;

import java.util.Stack;

import duber.engine.Cleansable;
import duber.engine.exceptions.LWJGLException;
import duber.game.client.gui.MainMenu;
import duber.game.client.gui.OptionsMenu;
import duber.game.client.match.Match;

public class GameStateManager implements Cleansable {
    
    public enum GameStateOption {
        MAIN_MENU    (new MainMenu()),
        MATCH        (new Match()),
        OPTIONS_MENU (new OptionsMenu());

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

    public void gameStateStartup() {
        gameStates.peek().startup();
    }

    public void update() {
        gameStates.peek().update();
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