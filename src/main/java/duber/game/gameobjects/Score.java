package duber.game.gameobjects;

import duber.engine.entities.components.Component;

/**
 * A Score that is associated with a Player inside a match.
 * @author Darren Lo
 * @version 1.0
 */
public class Score extends Component {

    /** The number of kills. */
    private int kills;

    /** The number of deaths */
    private int deaths;

    /**
     * Constructs a new Score with no kills or deaths.
     */
    public Score() {
        kills = 0;
        deaths = 0;
    }

    /**
     * Constructs a Score with a given number of kills and deaths.
     * @param kills the number of kills
     * @param deaths the number of deaths
     */
    public Score(int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
    }

    /**
     * Sets the Score to be equal to another Score.
     * @param score the Score to copy
     */
    public void set(Score score) {
        kills = score.getKills();
        deaths = score.getDeaths();
    }

    /**
     * Gets the number of kills.
     * @return the number of kills
     */
    public int getKills() {
        return kills;
    }

    /**
     * Adds a kill.
     */
    public void addKill() {
        kills++;
    }

    /**
     * Sets the number of kills.
     * @param kills the number of kills
     */
    public void setKills(int kills) {
        this.kills = kills;
    }

    /**
     * Gets the number of deaths.
     * @return the number of deaths
     */
    public int getDeaths() {
        return deaths;
    }

    /**
     * Adds a death.
     */
    public void addDeath() {
        deaths++;
    }

    /**
     * Sets the number of deaths.
     * @param deaths the number of deaths
     */
    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}