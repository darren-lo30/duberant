package duber.game.client.gui;

import duber.game.client.GameStateManager.GameStateOption;
import duber.game.client.match.Match;
import duber.game.gameobjects.Scoreboard;
import duber.engine.entities.components.Named;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.component.TextArea;
import org.liquidengine.legui.style.Style.DisplayType;
import static org.liquidengine.legui.component.optional.align.HorizontalAlign.CENTER;
import static org.liquidengine.legui.component.optional.align.VerticalAlign.BOTTOM;
public class ScoreboardDisplay extends GUI {
    @Override
    public void enter() {
        super.enter();
        if(!getManager().getState(GameStateOption.MATCH).isOpened()) {
            throw new IllegalStateException("There must be a match ongoing to open the scoreboard");
        }

        getMatchScoreboard().updateScoreboard();
    }

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    @Override
    public void render() {
        // TODO Auto-generated method stub
    }

    private Scoreboard getMatchScoreboard() {
        return ((Match) getManager().getState(GameStateOption.MATCH)).getScoreboard();
    }

    @Override
    public void createGuiElements() {
        getFrame().getContainer().getStyle().getBackground().setColor(ColorConstants.gray());
        getFrame().getContainer().setFocusable(false);
        getFrame().getContainer().getStyle().setDisplay(DisplayType.MANUAL);
        TextArea namesText= new TextArea(420,280,100,250);
        TextArea killsText= new TextArea(520,280,100,250);
        TextArea deathsText=new TextArea(620,280,100,250);
        TextArea scoreboardText=new TextArea(420,230,300,50);
        scoreboardText.getTextAreaField().getStyle().setHorizontalAlign(CENTER);
        namesText.setVerticalScrollBarVisible(false);
        namesText.setPressed(false);
        namesText.setHorizontalScrollBarVisible(false);
        killsText.setVerticalScrollBarVisible(false);
        killsText.setHorizontalScrollBarVisible(false);
        deathsText.setVerticalScrollBarVisible(false);
        deathsText.setHorizontalScrollBarVisible(false);
        scoreboardText.setVerticalScrollBarVisible(false);
        scoreboardText.setHorizontalScrollBarVisible(false);
        scoreboardText.setEditable(false);
        scoreboardText.getTextState().setText("SCORES");
        namesText.setEditable(false);
        killsText.setEditable(false);
        deathsText.setEditable(false);
        String currName="Name\n";
        String currKills="Kills\n";
        String currDeaths="Deaths\n";

        Scoreboard scoreboard = getMatchScoreboard();

        for(int team = 0; team < 2; team++) {
            for(int player = 0; player < scoreboard.getScores(team).size(); player++) {
                int kills= scoreboard.getScores(team).get(player).getKills();
                int deaths= scoreboard.getScores(team).get(player).getDeaths();
                String name= scoreboard.getScores(team).get(player).getEntity().getComponent(Named.class).getName();

                currName=currName+name+"\n";
                currKills= currKills+Integer.toString(kills)+"\n";
                currDeaths= currDeaths+Integer.toString(deaths)+"\n";
            }
        }

        System.out.println(currName);
        System.out.println(currKills);

        namesText.getTextState().setText(currName);
        killsText.getTextState().setText(currKills);
        deathsText.getTextState().setText(currDeaths);
        getFrame().getContainer().add(namesText);
        getFrame().getContainer().add(killsText);
        getFrame().getContainer().add(deathsText);
        getFrame().getContainer().add(scoreboardText);
    }
    
}