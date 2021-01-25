package duber.game.gameobjects;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import duber.game.MatchData;

/**
 * A scoreboard that stores the Scores of Players on both red and blue teams.
 * @author Darren Lo
 * @version 1.0
 */
public class Scoreboard {
    /** The number of wins for each team. */
    private int[] teamWins = new int[2];

    /** The Scores for both teams */
    private List<List<Score>> teamScores = new ArrayList<>(2);

    /**
     * Constructs an empty Scoreboard.
     */
    public Scoreboard() {
        for(int i = 0; i<2; i++) {
            teamScores.add(new ArrayList<>());
        }
    }

    /**
     * Constructs a Scoreboard from a Collection of Players.
     * @param players the Players in the Scoreboard
     */
    public Scoreboard(Collection<Player> players) {
        this();
        for(Player player: players) {
            addPlayer(player);
        }
    }
    
    /**
     * Gets the number of round wins for a team
     * @param team the team to check
     * @return the number of round wins for a team
     */
    public int getWins(int team) {
        return teamWins[team];
    }
    
    /**
     * Gets the total number of rounds that have been played
     */
    public int getTotalRounds() {
        return teamWins[MatchData.RED_TEAM] + teamWins[MatchData.BLUE_TEAM];
    }

    /**
     * Adds a Player to the Scoreboard
     * @param player the Player to add
     */
    public void addPlayer(Player player) {
        addScore(player.getPlayerData().getTeam(), player.getScore());
    }

    /**
     * Adds a Score to the Scoreboard.
     * @param team the team to add it to
     * @param score the Score to add
     */
    public void addScore(int team, Score score) {
        teamScores.get(team).add(score);
    }

    /**
     * Adds a win to a team.
     * @param team the team to add the win to
     */
    public void addWin(int team) {
        teamWins[team]++;
    }

    /**
     * Gets the Scores for a team.
     * @param team the team to query
     * @return the Scores for a team
     */
    public List<Score> getScores(int team) {
        return teamScores.get(team);
    }

    /**
     * Updates the Scoreboard
     */
    public void updateScoreboard() {
        reorderScores(getScores(MatchData.RED_TEAM));
        reorderScores(getScores(MatchData.BLUE_TEAM));
    }
    
    /**
     * Determines the winner of the match.
     * @return the team of the winners of the match
     */
    public int getWinner() {
        if (teamWins[MatchData.RED_TEAM] >= MatchData.NUM_ROUNDS_TO_WIN) {
            return MatchData.RED_TEAM;
        } else if (teamWins[MatchData.BLUE_TEAM] >= MatchData.NUM_ROUNDS_TO_WIN) {
            return MatchData.BLUE_TEAM;
        }
        return MatchData.NULL_TEAM;
    }

    
    /**
     * Compares to Scores used for sorting the Scoreboard.
     * @param left the left Score
     * @param right the right Score
     * @return if the left Score should be swapped with the right Score
     */
    private static boolean compareScores(Score left, Score right) {
        if (left.getKills() == right.getKills()) {
            //Sort deaths ascending
            return left.getDeaths() > right.getKills();
        }

        //Sort descending kills
        return left.getKills() < right.getKills();
    }

    /**
     * Reorders a List of Scores based on the compareScores function using BubbleSort.
     * @param scoresList the List of Scores to sort
     */
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

