package duber.game.client.gui;

import duber.game.MatchData;
import duber.game.client.GameStateManager.GameStateOption;
import duber.game.gameobjects.Scoreboard;
import duber.game.client.match.Match;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.TextArea;

import duber.engine.entities.components.Named;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.flex.FlexStyle.AlignItems;
import org.liquidengine.legui.style.flex.FlexStyle.JustifyContent;
import org.liquidengine.legui.style.Style.DisplayType;
import org.liquidengine.legui.style.Style.PositionType;
import org.liquidengine.legui.style.length.LengthType;



/**
 * The GUI for the Scoreboard inside a match.
 * @author Andy Tian
 * @version 1.0
 */
public class ScoreboardDisplay extends GUI {
    /**
     * {@inheritDoc}
     * Enters the match
     */
    @Override
    public void enter() {
        super.enter();
        if (!getManager().getState(GameStateOption.MATCH).isOpened()) {
            throw new IllegalStateException("There must be a match ongoing to open the scoreboard");
        }

        getMatchScoreboard().updateScoreboard();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update() {
        //Nothing to update
    }

    /**
     * Gets the match scoreboard
     * @return the scoreboard
     */
    private Scoreboard getMatchScoreboard() {
        return ((Match)getManager().getState(GameStateOption.MATCH)).getScoreboard();
    }

    /**
     * {@inheritDoc}
     * Creates the GUI elements, placing them in the frame
     */
    @Override
    public void createGuiElements() {
        getFrame().getContainer().getStyle().getBackground().setColor(ColorConstants.gray());
        getFrame().getContainer().setFocusable(false);

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

        TextArea namesText= new TextArea(250,250,250,250);
        namesText.getStyle().setMinHeight(250f);
        namesText.getStyle().setMinWidth(100f);
        namesText.getStyle().setPosition(PositionType.RELATIVE);
        TextArea killsText= new TextArea(250,250,250,250);
        killsText.getStyle().setMinWidth(100f);
        killsText.getStyle().setMinHeight(250f);
        killsText.getStyle().setPosition(PositionType.RELATIVE);
        TextArea deathsText=new TextArea(250,250,250,250);
        deathsText.getStyle().setMinHeight(250f);
        deathsText.getStyle().setMinWidth(100f);
        deathsText.getStyle().setPosition(PositionType.RELATIVE);
        
        namesText.setVerticalScrollBarVisible(false);
        namesText.setHorizontalScrollBarVisible(false);
        killsText.setVerticalScrollBarVisible(false);
        killsText.setHorizontalScrollBarVisible(false);
        deathsText.setVerticalScrollBarVisible(false);
        deathsText.setHorizontalScrollBarVisible(false);
  
        namesText.setEditable(false);
        killsText.setEditable(false);
        deathsText.setEditable(false);
        Scoreboard scoreboard = getMatchScoreboard();
        String currName="Name\n\n";
        String currKills="Kills\n"+"Red-"+scoreboard.getWins(MatchData.RED_TEAM)+"\n";
        String currDeaths="Deaths\n\n";

        for(int team = 0; team < 2; team++) {
            for(int player = 0; player < scoreboard.getScores(team).size(); player++) {
                if (team==1&&player==0){
                    currName=currName+"\n";
                    currKills=currKills+"Blue-"+scoreboard.getWins(MatchData.BLUE_TEAM)+"\n";
                    currDeaths=currDeaths+"\n";
                }
                int kills= scoreboard.getScores(team).get(player).getKills();
                int deaths= scoreboard.getScores(team).get(player).getDeaths();
                String name= scoreboard.getScores(team).get(player).getEntity().getComponent(Named.class).getName();

                currName=currName+name+"\n";
                currKills= currKills+Integer.toString(kills)+"\n";
                currDeaths= currDeaths+Integer.toString(deaths)+"\n";
            }
        }
        namesText.getTextState().setText(currName);
        killsText.getTextState().setText(currKills);
        deathsText.getTextState().setText(currDeaths);
        mainPanel.add(namesText);
        mainPanel.add(killsText);
        mainPanel.add(deathsText);
    }
    
}