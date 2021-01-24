package duber.game.gameobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import duber.game.MatchData;

public class Scoreboard {
    private int[] teamWins = new int[2];
    private List<List<Score>> teamScores = new ArrayList<>(2);

    public Scoreboard() {
        for(int i = 0; i<2; i++) {
            teamScores.add(new ArrayList<>());
        }
    }

    public Scoreboard(Collection<Player> players) {
        this();
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
        addScore(player.getPlayerData().getTeam(), player.getScore());
    }

    public void addScore(int team, Score score) {
        teamScores.get(team).add(score);
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
        if (teamWins[MatchData.RED_TEAM] >= MatchData.NUM_ROUNDS_TO_WIN) {
            return MatchData.RED_TEAM;
        } else if (teamWins[MatchData.BLUE_TEAM] >= MatchData.NUM_ROUNDS_TO_WIN) {
            return MatchData.BLUE_TEAM;
        }
        return MatchData.NULL_TEAM;
    }

    
    private static boolean compareScores(Score left, Score right) {
        if (left.getKills() == right.getKills()) {
            //Sort deaths ascending
            return left.getDeaths() > right.getKills();
        }

        //Sort descending kills
        return left.getKills() < right.getKills();
    }

    private static void reorderScores(List<Score> scoresList) {
        //Bubble sort
        boolean sortEnded;
        do {
            sortEnded = true;
            for(int i = 0; i<scoresList.size()-1; i++) {
                Score leftScore = scoresList.get(i);
                Score rightScore = scoresList.get(i+1);
                
                if (compareScores(leftScore, rightScore)) {
                    //Swap the scores if the comparison returns true
                    //Also set the flag to true so the sort does not end
                    sortEnded = false;
                    scoresList.set(i+1, leftScore);
                    scoresList.set(i, rightScore);                    
                }
            }
        } while (!sortEnded);
    }
}

