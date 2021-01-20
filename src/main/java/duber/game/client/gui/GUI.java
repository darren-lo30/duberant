package duber.game.client.gui;

import duber.engine.Window;
import duber.game.client.GameState;

public abstract class GUI extends GameState {

    @Override
    public void startup() {
        //Nothing to do on startup
    }

    @Override
    public void enter() {
        getGame().getWindow().setOption(Window.Options.SHOW_CURSOR, true);
        getGame().getWindow().applyOptions();
    }

    @Override
    public void exit() {
        //Nothing to do on exit
    }

    @Override
    public void close() {
        //Nothing to do on close
    }
}