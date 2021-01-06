package duber.game.client;

import java.util.Stack;

import duber.engine.Cleansable;
import duber.engine.exceptions.LWJGLException;
import duber.game.client.gui.MainMenu;
import duber.game.client.match.Match;

public class GameStateManager implements Cleansable {
    public enum GameStateOption {
        MAIN_MENU    (new MainMenu()),
        MATCH        (new Match());

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

        //Initialize all game states
        for(GameStateOption option: GameStateOption.values()) {
            option.getGameState().init(game);
        }
    }

    public void pushState(GameStateOption gameStateOption) {
        gameStates.push(gameStateOption.getGameState());
    }

    public GameState popState() {
        return gameStates.pop();
    }

    public void changeGameStates(GameStateOption gameStateOption) {
        popState();
        pushState(gameStateOption);
    }

    public void update(Duberant game) {
        gameStates.peek().update(game, this);
    }

    public void render(Duberant game) {
        gameStates.peek().render(game, this);
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