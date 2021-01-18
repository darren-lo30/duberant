package duber.game.gameobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import duber.game.MatchData;

public class Scoreboard {
    private int redWins;
    private int blueWins;

    private List<Score> redTeamScores = new ArrayList<>();
    private List<Score> blueTeamScores = new ArrayList<>();

    public Scoreboard(Collection<Player> players) {
        for(Player player: players) {
            addPlayer(player);
        }
    }
    
    public int getWins(int team) {
        if(team == MatchData.RED_TEAM) {
            return redWins;
        } else if(team == MatchData.BLUE_TEAM) {
            return blueWins;
        }

        return 0;
    }
    
    public int getTotalRounds() {
        return redWins + blueWins;
    }

    public void addPlayer(Player player) {
        int team = player.getPlayerData().getTeam();
        Score playerScore = player.getScore();

        if(team == MatchData.RED_TEAM) {
            redTeamScores.add(playerScore);
        } else if (team == MatchData.BLUE_TEAM) {
            blueTeamScores.add(playerScore);
        }
    }

    public void addWin(int team) {
        if(team == MatchData.RED_TEAM) {
            redWins++;
        } else if(team == MatchData.BLUE_TEAM) {
            blueWins++;
        }
    }

    List<Score> getScores(int team) {
        if(team == MatchData.RED_TEAM) {
            return redTeamScores;
        } else {
            return blueTeamScores;
        }
    }

    public void updateScoreboard() {
        reorderScores(redTeamScores);
        reorderScores(blueTeamScores);
    }

    
    public int getWinner() {
        if(redWins >= MatchData.NUM_ROUNDS_TO_WIN) {
            return MatchData.RED_TEAM;
        } else if(blueWins >= MatchData.NUM_ROUNDS_TO_WIN) {
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