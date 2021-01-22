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

public class OptionsMenu extends GUI {

    @Override
    public void update() {
        // TODO Auto-generated method stub
    }


	@Override
	public void createGuiElements() {
		getFrame().getContainer().getStyle().getBackground().setColor(ColorConstants.gray());
        getFrame().getContainer().setFocusable(false);

        Button quitButton = new Button("Quit Game", 500, 350, 480, 90);
        SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
        quitButton.getStyle().setBorder(border);

        quitButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(event.getAction() == MouseClickAction.RELEASE ) {
                
            }
        });

        getFrame().getContainer().add(quitButton);
		
	}


    
}

