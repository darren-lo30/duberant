package duber.game.client.gui;

import duber.game.client.GameStateManager.GameStateOption;
import duber.game.client.match.Match;

public class ShopMenu extends GUI {
    private Match match;

    @Override
    public void init() {
        super.init();
        match = (Match) GameStateOption.MATCH.getGameState();
    }

    @Override
    public void update() {
        if(!match.getCurrMatchPhase().playerCanBuy()) {
            setShouldClose(true);
        }
    }

    @Override
    public void render() {
        // TODO Auto-generated method stub

    }

    @Override
    public void createGuiElements() {
        // TODO Auto-generated method stub

    }
    
    
}