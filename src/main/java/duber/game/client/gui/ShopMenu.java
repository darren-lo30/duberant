package duber.game.client.gui;

import duber.game.client.GameStateManager.GameStateOption;
import duber.game.client.match.Match;
import duber.game.gameobjects.GunType;

import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.style.border.SimpleLineBorder;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.*;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.Style.PositionType;
import org.liquidengine.legui.style.flex.FlexStyle.*;
import org.liquidengine.legui.style.length.LengthType;
import duber.game.gameobjects.Buyable;
public class ShopMenu extends GUI {
    private Match match;

    @Override
    public void init() {
        super.init();
        match = (Match) GameStateOption.MATCH.getGameState();
    }

    @Override
    public void update() {
        if (!match.getCurrMatchPhase().playerCanBuy()) {
            setShouldClose(true);
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

        Button pistolButton = new Button("Pistol $"+Integer.toString(GunType.PISTOL.getGun().getComponent(Buyable.class).getCost()));
        pistolButton.getStyle().setMinWidth(160f);
        pistolButton.getStyle().setMinHeight(30f);
        pistolButton.getStyle().setBorder(border);
        pistolButton.getStyle().setMarginRight(20f);
        pistolButton.getStyle().setPosition(PositionType.RELATIVE);

        Button lmgButton = new Button("Light Machine Gun $"+Integer.toString(GunType.LMG.getGun().getComponent(Buyable.class).getCost()));
        lmgButton.getStyle().setMinWidth(480f);
        lmgButton.getStyle().setMinHeight(90f);
        lmgButton.getStyle().setBorder(border);
        lmgButton.getStyle().setPosition(PositionType.RELATIVE);
        
        Button rifleButton = new Button("Assault Rifle $"+Integer.toString(GunType.RIFLE.getGun().getComponent(Buyable.class).getCost()));
        rifleButton.getStyle().setMinWidth(480f);
        rifleButton.getStyle().setMinHeight(90f);
        rifleButton.getStyle().setBorder(border);
        rifleButton.getStyle().setPosition(PositionType.RELATIVE);
        
        rifleButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if (event.getAction() == MouseClickAction.RELEASE) {
                match.sendGunPurchaseRequest(GunType.RIFLE);
            }
        });
        lmgButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if (event.getAction() == MouseClickAction.RELEASE) {
                match.sendGunPurchaseRequest(GunType.LMG);
            }
        });
        
        pistolButton.getListenerMap().addListener(MouseClickEvent.class, event -> {
            if (event.getAction() == MouseClickAction.RELEASE) {
                match.sendGunPurchaseRequest(GunType.PISTOL);
            }
        });

        mainPanel.add(pistolButton);
        mainPanel.add(lmgButton);
        mainPanel.add(rifleButton);
        getFrame().getContainer().add(mainPanel);
    }   
}