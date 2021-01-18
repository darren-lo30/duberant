package duber.game.client;

import duber.engine.KeyboardInput;
import duber.game.client.GameStateManager.GameStateOption;

public class GameStateKeyListener {
    private int activationKey;
    private GameState activatedGameState;
    private boolean activated = false;

    public GameStateKeyListener(int activationKey, GameStateOption activatedGameStateOption) {
        this.activationKey = activationKey;
        this.activatedGameState = activatedGameStateOption.getGameState();
    }

    public void listenToActivate(KeyboardInput keyboardInput) {
        if(keyboardInput.isKeyPressed(activationKey)) {
            if(!activated) {
                activated = true;
                if(activatedGameState.isFocused()) {
                    activatedGameState.popSelf();
                } else {
                    activatedGameState.pushSelf();
                }
            }
        } else {
            activated = false;
        }
    }

    public GameState getActivatedGameState() {
        return activatedGameState;
    }
}