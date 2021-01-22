package duber.game.gameobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import duber.game.MatchData;

public class Scoreboard {

    private int[] teamWins = new int[2];
    private List<List<Score>> teamScores = new ArrayList<>(2);

    public Scoreboard(Collection<Player> players) {
        for(int i = 0; i<2; i++) {
            teamScores.add(new ArrayList<>());
        }

        for(Player player: players) {
            addPlayer(player);
        }
    }
    
    public int getWins(int team) {
        return teamWins[team];
    }
    
    public int getTotalRounds() {
        return teamWins[MatchData.RED_TEAM] + teamWins[MatchData.BLUE_TEAM];
    }

    public void addPlayer(Player player) {
        int team = player.getPlayerData().getTeam();
        Score playerScore = player.getScore();

        teamScores.get(team).add(playerScore);
    }

    public void addWin(int team) {
        teamWins[team]++;
    }

    public List<Score> getScores(int team) {
        return teamScores.get(team);
    }

    public void updateScoreboard() {
        reorderScores(getScores(MatchData.RED_TEAM));
        reorderScores(getScores(MatchData.BLUE_TEAM));
    }
    
    public int getWinner() {
        if(teamWins[MatchData.RED_TEAM] >= MatchData.NUM_ROUNDS_TO_WIN) {
            return MatchData.RED_TEAM;
        } else if(teamWins[MatchData.BLUE_TEAM] >= MatchData.NUM_ROUNDS_TO_WIN) {
            return MatchData.BLUE_TEAM;
        }

        return MatchData.NULL_TEAM;
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