package duber.game.gameobjects;

import duber.engine.entities.components.Component;

public class Score extends Component {

    private int kills;
    private int deaths;

    public Score() {
        kills = 0;
        deaths = 0;
    }

    public Score(int kills, int deaths) {
        this.kills = kills;
        this.deaths = deaths;
    }

    public void set(Score score) {
        kills = score.getKills();
        deaths = score.getDeaths();
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        kills++;
    }

    public void setKills(int kills) {
        this.kills = kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public void addDeath() {
        deaths++;
    }

    public void setDeaths(int deaths) {
        this.deaths = deaths;
    }
}