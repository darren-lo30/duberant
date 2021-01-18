package duber.game.gameobjects;

import java.util.List;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.entities.components.Transform;
import duber.engine.graphics.lighting.SceneLighting;
import duber.game.MatchData;

public class GameMap {
    private Entity mainMap;
    private SkyBox skyBox;
    private SceneLighting gameLighting;

    private transient Vector3f[] initialRedPositions;
    private transient Vector3f[] initialBluePositions;

    public GameMap(Entity mainMap, SkyBox skyBox, SceneLighting gameLighting, Vector3f[] initialRedPositions, Vector3f[] initialBluePositions) {
        if(initialRedPositions.length != MatchData.NUM_PLAYERS_PER_TEAM || 
            initialBluePositions.length != MatchData.NUM_PLAYERS_PER_TEAM) {
                throw new IllegalArgumentException("There needs to be " + MatchData.NUM_PLAYERS_PER_TEAM + " initial positions per team");
        }       
        
        this.mainMap = mainMap;
        this.skyBox = skyBox;
        this.gameLighting = gameLighting;
        this.initialRedPositions = initialRedPositions;
        this.initialBluePositions = initialBluePositions;
    }

    public Entity getMainMap() {
        return mainMap;
    }

    public SkyBox getSkyBox() {
        return skyBox;
    }

    public SceneLighting getGameLighting() {
        return gameLighting;
    }

    public void setPlayerInitialPositions(int team, List<Player> players) {
        if(players.size() != MatchData.NUM_PLAYERS_PER_TEAM) {
            throw new IllegalArgumentException("Not enough players on the team");
        }

        Vector3f[] initialPositions = team == MatchData.RED_TEAM ? initialRedPositions : initialBluePositions;

        for(int i = 0; i<MatchData.NUM_PLAYERS_PER_TEAM; i++) {
            Player currPlayer = players.get(i);
            System.out.println("Initial pos: " + initialPositions[i]);
            currPlayer.getComponent(Transform.class).getPosition().set(initialPositions[i]);
        }
    }

    @SuppressWarnings("unused")
    private GameMap() {}

}