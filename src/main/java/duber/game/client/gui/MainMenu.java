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
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.Style.PositionType;
import org.liquidengine.legui.style.flex.FlexStyle.*;
import org.liquidengine.legui.style.length.LengthType;


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
                inMatchQueue = false;
                getManager().pushState(GameStateOption.MATCH);
            }
        }
    }

    @Override
    public void createGuiElements() {
        
        getFrame().getContainer().getStyle().getBackground().setColor(ColorConstants.gray());
        getFrame().getContainer().setFocusable(false);
        getFrame().getContainer().getStyle().setDisplay(DisplayType.FLEX);
        

        Component frameContainer = getFrame().getContainer();
        frameContainer.getStyle().getBackground().setColor(ColorConstants.gray());
        frameContainer.getStyle().setPadding(10);
        frameContainer.getStyle().getFlexStyle().setJustifyContent(JustifyContent.CENTER);
        frameContainer.getStyle().getFlexStyle().setAlignItems(AlignItems.CENTER);
        frameContainer.getStyle().setDisplay(DisplayType.FLEX);

        Panel mainPanel= new Panel();
        mainPanel.getStyle().getBackground().setColor(ColorConstants.lightGray());
        mainPanel.getStyle().getFlexStyle().setJustifyContent(JustifyContent.CENTER);
        mainPanel.getStyle().getFlexStyle().setAlignItems(AlignItems.CENTER);
        mainPanel.getStyle().setDisplay(DisplayType.FLEX);
        mainPanel.getStyle().setWidth(LengthType.percent(100));
        mainPanel.getStyle().setHeight(LengthType.percent(100));
        frameContainer.add(mainPanel);

        SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);

        Button loginButton = new Button("Login");
        loginButton.getStyle().setMinWidth(160f);
        loginButton.getStyle().setMinHeight(30f);
        loginButton.getStyle().setBorder(border);
        loginButton.getStyle().setRight(320f);
        loginButton.getStyle().setTop(150f);
        loginButton.getStyle().setPosition(PositionType.RELATIVE);

        Button matchButton = new Button("Find Match");
        matchButton.getStyle().setMinWidth(480f);
        matchButton.getStyle().setMinHeight(90f);
        matchButton.getStyle().setBorder(border);
        matchButton.getStyle().setPosition(PositionType.RELATIVE);
        loginButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(event.getAction() == MouseClickAction.RELEASE && !loggingIn && !getGame().isLoggedIn()) {
                loggingIn = true;
                new Thread(new LoginRequest("Darren")).start();
            }
        });
        
        matchButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(getGame().isLoggedIn() && event.getAction() == MouseClickAction.RELEASE) {
                inMatchQueue = !inMatchQueue;
                getGame().getClientNetwork().getConnection().sendTCP(new MatchQueuePacket(inMatchQueue));
            }
        });
        mainPanel.add(matchButton);
        mainPanel.add(loginButton);
        getFrame().getContainer().add(mainPanel);
        
        
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
