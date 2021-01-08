package duber.game.client.gui;

import java.io.IOException;

import duber.engine.exceptions.LWJGLException;
import duber.game.client.Duberant;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.client.match.Crosshair;
import duber.game.networking.UserConnectPacket;

public class MainMenu extends GUI {
    private volatile boolean loggingIn = false;

    @Override
    public void init() throws LWJGLException {
        // Nothing to init
    }

    @Override
    public void update() {
        Duberant game = getGame();
        if (!loggingIn && !game.loggedIn()) {
            // User has not been logged in yet and is not currently attempting to login
            loggingIn = true;
            new Thread(new LoginRequest("Darren")).start();

        } else if (loggingIn) {

            // Currently trying to login to the server
            // Look out for a user connected packet
        } else if (game.loggedIn()) {
            // If the user is already signed in, then proceed to a match
            getManager().changeState(GameStateOption.MATCH);
        }
    }

    @Override
    public void render() {
        // Nothing to render currently
    }

    private class LoginRequest implements Runnable {
        private String username;

        public LoginRequest(String username) {
            this.username = username;
        }

        @Override
        public void run() {

            // If the game is not already connected to the server, attempt to connect
            // Send in login request to server
            // getGame().getClientNetwork().getClient().sendTCP(new
            // UserConnectPacket(username));

            try {
                if (!getGame().connected()) {
                    getGame().getClientNetwork().connect(100000);
                }

                getGame().getClientNetwork().getClient().sendTCP(new UserConnectPacket(username, new Crosshair()));
                
                System.out.println("Send user connect packet");
            } catch (IOException ioe) {
                //Failed to connect
                System.out.println("Failed to connect to server!");
                loggingIn = false;
            }
        }

    }
}