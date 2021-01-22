package duber.game.client.gui;

import duber.game.client.GameStateManager.GameStateOption;
import duber.game.client.match.Match;
import duber.game.User;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.networking.LoginPacket;
import duber.game.networking.MatchFoundPacket;
import duber.game.networking.MatchQueuePacket;
import duber.game.networking.LoginConfirmationPacket;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.TextArea;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import static org.liquidengine.legui.component.optional.align.HorizontalAlign.CENTER;
import static org.liquidengine.legui.component.optional.align.VerticalAlign.BOTTOM;
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
    public void createGuiElements() {
        getFrame().getContainer().getStyle().getBackground().setColor(ColorConstants.gray());
        getFrame().getContainer().setFocusable(false);
        

        Button smgButton = new Button("Pistol", 20, 20, 160, 30);
        SimpleLineBorder border = new SimpleLineBorder(ColorConstants.black(), 1);
        smgButton.getStyle().setBorder(border);

        smgButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(event.getAction() == MouseClickAction.RELEASE ) {
                
            }
        });
        Button lmgButton = new Button("LMG", 100, 20, 160, 30);
        smgButton.getStyle().setBorder(border);

        smgButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(event.getAction() == MouseClickAction.RELEASE ) {
                
            }
        });
        Button arButton = new Button("AR", 180, 20, 160, 30);
        smgButton.getStyle().setBorder(border);

        smgButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if(event.getAction() == MouseClickAction.RELEASE ) {
                
            }
        });

        
        getFrame().getContainer().add(smgButton);
        getFrame().getContainer().add(arButton);
        getFrame().getContainer().add(lmgButton);
        

    }
    
    
}