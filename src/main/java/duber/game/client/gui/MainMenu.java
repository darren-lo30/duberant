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


/**
 * The GUI used for the main menu
 * @author Darren Lo and Andy Tian
 * @version 1.0
 */
public class MainMenu extends GUI {
    /**  */
    private volatile boolean loggingIn = false;
    private volatile boolean inMatchQueue = false;
    
    /**
     * {@inheritDoc}
     * Updates the login status of the user
     */
    @Override
    public void update() {
        Queue<Object> receivedPackets = getGame().getClientNetwork().getPackets();
        
        while(!receivedPackets.isEmpty()){
            Object packet = receivedPackets.poll();

            if (packet instanceof LoginConfirmationPacket) {
                //Receive user connected packet from server
                LoginConfirmationPacket userConnectedPacket = (LoginConfirmationPacket) packet;
                
                //Initialize a user
                User connectedUser = userConnectedPacket.user;
                connectedUser.setConnection(getGame().getClientNetwork().getConnection());
                getGame().setUser(connectedUser);
            } else if (getGame().isLoggedIn() && packet instanceof MatchFoundPacket) {
                inMatchQueue = false;
                getManager().pushState(GameStateOption.MATCH);
            }
        }
    }
    
    /**
     * {@inheritDoc}
     * Creates the GUI elements, placing them in the frame
     */
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

        Button matchButton = new Button("Find Match");
        matchButton.getStyle().setMinWidth(480f);
        matchButton.getStyle().setMinHeight(90f);
        matchButton.getStyle().setBorder(border);
        matchButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if (getGame().isLoggedIn() && event.getAction() == MouseClickAction.RELEASE) {
                inMatchQueue = !inMatchQueue;
                getGame().getClientNetwork().getConnection().sendTCP(new MatchQueuePacket(inMatchQueue));
                if (inMatchQueue) {
                    matchButton.getTextState().setText("Stop Queue");
                } else {
                    matchButton.getTextState().setText("Find Match");
                }
            }
        });

        
        if(!getGame().isLoggedIn()) {
            TextInput loginInput= new TextInput();
            loginInput.getStyle().setMinWidth(150f);
            loginInput.getStyle().setMinHeight(30f);
            loginInput.getStyle().setBorder(border);
            loginInput.getStyle().setMarginTop(250f);
            loginInput.getStyle().setRight(25f);
            loginInput.getStyle().setPosition(PositionType.RELATIVE);

            Button loginButton = new Button("Login");
            loginButton.getStyle().setMinWidth(160f);
            loginButton.getStyle().setMinHeight(30f);
            loginButton.getStyle().setBorder(border);
            loginButton.getStyle().setMarginTop(250f);
            loginButton.getStyle().setLeft(25f);
            loginButton.getStyle().setPosition(PositionType.RELATIVE);
            
            loginButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
                if (event.getAction() == MouseClickAction.RELEASE && !loggingIn && !getGame().isLoggedIn()) {
                    loggingIn = true;
                    if(login(loginInput.getTextState().getText())) {
                        loginButton.getParent().remove(loginButton);
                        loginInput.getParent().remove(loginInput);  
                    }
                    
                }
            });
            mainPanel.add(loginInput);
            mainPanel.add(loginButton);
        }
        
        mainPanel.add(matchButton);
        getFrame().getContainer().add(mainPanel);
        
    }

    /**
     * Logs the user into the server.
     * @param username the username to login with
     * @return whether or not the login was succesful.
     */
    private boolean login(String username) {
        try {
            if (!getGame().isConnected()) {
                getGame().getClientNetwork().connect(1000);
            }
    
            //Send user login packet with username
            if (!getGame().isLoggedIn()) { 
                getGame().getClientNetwork().getClient().sendTCP(new LoginPacket(username));       
            }
        } catch (IOException ioe) {
            //Failed to connect
            return false;
        } finally {
            loggingIn = false;
        }

        return true;
    }
}
