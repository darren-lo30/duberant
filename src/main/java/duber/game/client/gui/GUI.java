package duber.game.client.gui;

import duber.engine.Window;
import duber.game.client.GameState;

public abstract class GUI extends GameState {

    @Override
    public void enter() {
        getGame().getWindow().setOption(Window.Options.SHOW_CURSOR, true);
        getGame().getWindow().applyOptions();
    }
}