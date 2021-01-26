package duber.game.client.gui;

import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.flex.FlexStyle.AlignItems;
import org.liquidengine.legui.style.flex.FlexStyle.JustifyContent;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.Style.PositionType;
import org.liquidengine.legui.style.length.LengthType;


/**
 * The GUI for the options menu that can be accessed anywhere in the game
 * @author Andy Tian
 * @version 1.0
 */
public class OptionsMenu extends GUI {

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

        Button quitButton = new Button("QUIT GAME");
        quitButton.getStyle().setMinWidth(160f);
        quitButton.getStyle().setMinHeight(30f);
        quitButton.getStyle().setBorder(border);
        quitButton.getStyle().setPosition(PositionType.RELATIVE);
        quitButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if (event.getAction() == MouseClickAction.RELEASE ) {
                getWindow().setShouldClose(true);
            }
        });
        mainPanel.add(quitButton);
        
		
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        //Nothing to update
    }


    
}

