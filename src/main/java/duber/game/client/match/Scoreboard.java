package duber.game.client.match;

import java.util.ArrayList;
import java.util.List;

import duber.game.gameobjects.Score;

public class Scoreboard {
    List<Score> redTeamScores = new ArrayList<>();
    List<Score> blueTeamScores = new ArrayList<>();
    
    public void addRedPlayerScore(Score redPlayerScore) {
        redTeamScores.add(redPlayerScore);
    }
    
    List<Score> getRedTeamScores() {
        return redTeamScores;
    }

    public void addBluePlayerScore(Score bluePlayerScore) {
        blueTeamScores.add(bluePlayerScore);
    }

    List<Score> getBlueTeamScores() {
        return blueTeamScores;
    }

    public void updateScoreboard() {
        reorderScores(redTeamScores);
        reorderScores(blueTeamScores);
    }

    private static void reorderScores(List<Score> scoresList) {
        scoresList.sort((Score left, Score right) -> {
            if(left.getKills() == right.getKills()) {
                //Sort by deaths ascending
                return left.getDeaths() - right.getDeaths();    
            }

            //Sort by kills descending
            return right.getKills() - left.getKills();
        });
    }
}