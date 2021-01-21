package duber.game.client.gui;

import java.io.IOException;
import java.util.Queue;

import duber.game.User;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.networking.LoginPacket;
import duber.game.networking.MatchFoundPacket;
import duber.game.networking.MatchQueuePacket;
import duber.game.networking.LoginConfirmationPacket;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;

public class MainMenu extends GUI {
    private volatile boolean loggingIn = false;
    private volatile boolean inMatchQueue = false;

    @Override
    public void update() {
        Queue<Object> receivedPackets = getGame().getClientNetwork().getPackets();
        
        while(!receivedPackets.isEmpty()){
            Object packet = receivedPackets.poll();

            if(packet instanceof LoginConfirmationPacket) {
                //Receive user connected packet from server
                LoginConfirmationPacket userConnectedPacket = (LoginConfirmationPacket) packet;
                
                //Initialize a user
                User connectedUser = userConnectedPacket.user;
                connectedUser.setConnection(getGame().getClientNetwork().getConnection());
                getGame().setUser(connectedUser);
            } else if(getGame().isLoggedIn() && packet instanceof MatchFoundPacket) {
                getManager().pushState(GameStateOption.MATCH);
            }
        }
    }

    @Override
    public void createGuiElements() {
        getFrame().getContainer().getStyle().getBackground().setColor(ColorConstants.lightBlue());
        getFrame().getContainer().setFocusable(false);

        Button logginButton = new Button("Login", 20, 20, 160, 30);
        SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
        logginButton.getStyle().setBorder(border);

        logginButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(event.getAction() == MouseClickAction.RELEASE && !loggingIn && !getGame().isLoggedIn()) {
                loggingIn = true;
                new Thread(new LoginRequest("Darren")).start();
            }
        });

        
        Button matchButton = new Button("Find Match", 20, 100, 160, 30);
        matchButton.getStyle().setBorder(border);
        matchButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(getGame().isLoggedIn() && event.getAction() == MouseClickAction.RELEASE) {
                inMatchQueue = !inMatchQueue;
                getGame().getClientNetwork().getConnection().sendTCP(new MatchQueuePacket(inMatchQueue));
            }
        });

        getFrame().getContainer().add(logginButton);
        getFrame().getContainer().add(matchButton);
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
                    getGame().getClientNetwork().connect(1000);
                }

                //Send user login packet with username
                getGame().getClientNetwork().getClient().sendTCP(new LoginPacket(username));       
                System.out.println("Connected lol");
            } catch (IOException ioe) {
                //Failed to connect
                System.out.println("Failed to connect to server!");
            } finally {
                loggingIn = false;
            }

        }
    }

}
