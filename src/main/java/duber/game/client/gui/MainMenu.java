package duber.game.client.gui;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import duber.engine.exceptions.LWJGLException;
import duber.game.User;
import duber.game.client.Duberant;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.networking.LoginPacket;
import duber.game.networking.LoginConfirmationPacket;

public class MainMenu extends GUI {
    private volatile boolean loggingIn = false;

    @Override
    public void init() throws LWJGLException {
        // Nothing to init
    }

    @Override
    public void update() {
        Duberant game = getGame();
        if (!loggingIn && !game.isLoggedIn()) {
            // User has not been logged in yet and is not currently attempting to login
            loggingIn = true;
            new Thread(new LoginRequest("Darren")).start();
        }
        
        if (game.isLoggedIn()) {
            // If the user is already signed in, then proceed to a match
            getManager().pushState(GameStateOption.MATCH);
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
            try {
                if (!getGame().isConnected()) {
                    getGame().getClientNetwork().connect(100);
                }

                //Send user login packet with username
                getGame().getClientNetwork().getClient().sendTCP(new LoginPacket(username));       

                //Wait to receive response from server
                waitForLoginResponse();
            } catch (IOException ioe) {
                //Failed to connect
                System.out.println("Failed to connect to server!");
            } catch (InterruptedException ie) {
                System.out.println("Failed logging in to server!");
                Thread.currentThread().interrupt();
            } finally {
                loggingIn = false;
            }

            
        }

        private void waitForLoginResponse() throws InterruptedException {
            
            BlockingQueue<Object> receivedPackets = getGame().getClientNetwork().getPackets();
            Object packet = receivedPackets.take();
            if(packet instanceof LoginConfirmationPacket) {
                //Receive user connected packet from server
                LoginConfirmationPacket userConnectedPacket = (LoginConfirmationPacket) packet;
                
                //Initialize a user
                User connectedUser = userConnectedPacket.user;
                connectedUser.setConnection(getGame().getClientNetwork().getConnection());
                getGame().setUser(connectedUser);
            } else {
                System.out.println("Received faulty packet");
            }            
        }
    }
}