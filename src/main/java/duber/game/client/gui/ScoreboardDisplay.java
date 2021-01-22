package duber.game.client.gui;

import duber.game.client.GameStateManager.GameStateOption;
import duber.game.client.match.Match;
import duber.game.gameobjects.Scoreboard;
import duber.engine.entities.components.Named;

import org.liquidengine.legui.component.TextArea;

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
        int team=0;
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
        scoreboardText.getTextState().setText("SCORES");
        namesText.setEditable(false);
        killsText.setEditable(false);
        deathsText.setEditable(false);
        String currName="Name\n";
        String currKills="Kills\n";
        String currDeaths="Deaths\n";
        for(int i=0; i<10; i++){
            if(i==5){
                team =1;
                currName=currName+"\n";
                currKills=currKills+"\n";
                currDeaths=currDeaths+"\n";
            }
            int kills= getMatchScoreboard().getScores(team).get(i).getKills();
            int deaths= getMatchScoreboard().getScores(team).get(i).getDeaths();
            String name= getMatchScoreboard().getScores(team).get(i).getEntity().getComponent(Named.class).getName();
            currName=currName+name+"\n";
            currKills= currKills+Integer.toString(kills)+"\n";
            currDeaths= currDeaths+Integer.toString(deaths)+"\n";
        }
        namesText.getTextState().setText(currName);
        killsText.getTextState().setText(currKills);
        deathsText.getTextState().setText(currDeaths);
        namesText.setCaretPosition(12);
        namesText.getTextAreaField().getStyle().setHorizontalAlign(CENTER);
        namesText.getTextAreaField().getStyle().setVerticalAlign(BOTTOM);
        killsText.setCaretPosition(12);
        killsText.getTextAreaField().getStyle().setHorizontalAlign(CENTER);
        killsText.getTextAreaField().getStyle().setVerticalAlign(BOTTOM);
        deathsText.setCaretPosition(12);
        deathsText.getTextAreaField().getStyle().setHorizontalAlign(CENTER);
        deathsText.getTextAreaField().getStyle().setVerticalAlign(BOTTOM);
        getFrame().getContainer().add(namesText);
        getFrame().getContainer().add(killsText);
        getFrame().getContainer().add(deathsText);
        getFrame().getContainer().add(scoreboardText);

    }
    
}