package duber.game.gameobjects;

import java.util.List;

import org.joml.Vector3f;

import duber.engine.entities.Entity;
import duber.engine.entities.SkyBox;
import duber.engine.entities.components.Transform;
import duber.engine.graphics.lighting.SceneLighting;
import duber.game.MatchData;

/**
 * A game map that the Players play a match in.
 * @author Darren Lo
 * @version 1.0
 */
public class GameMap {

    /** The entity that is the map that the players traverse on. */
    private Entity mainMap;
    
    /** The skybox surrounding the main map. */
    private SkyBox skyBox;

    /** The lighting in the game. */
    private SceneLighting gameLighting;


    /** The initial positions of red players at the start of a round. */
    private transient Vector3f[] initialRedPositions;


    /** The initial positions of blue players at the start of a round. */
    private transient Vector3f[] initialBluePositions;

    /**
     * Constructs a GameMap.
     * @param mainMap the main map that players traverse
     * @param skyBox the skybox surrounding the map
     * @param gameLighting the lighting in the game 
     * @param initialRedPositions the initial positions of red players
     * @param initialBluePositions the initial positions of blue players
     */
    public GameMap(Entity mainMap, SkyBox skyBox, SceneLighting gameLighting, Vector3f[] initialRedPositions, Vector3f[] initialBluePositions) {
        if (initialRedPositions.length < MatchData.NUM_PLAYERS_PER_TEAM || 
            initialBluePositions.length < MatchData.NUM_PLAYERS_PER_TEAM) {
                throw new IllegalArgumentException("There needs to be " + MatchData.NUM_PLAYERS_PER_TEAM + " initial positions per team");
        }       
        
        this.mainMap = mainMap;
        this.skyBox = skyBox;
        this.gameLighting = gameLighting;
        this.initialRedPositions = initialRedPositions;
        this.initialBluePositions = initialBluePositions;
    }

    /**
     * Gets the main map.
     * @return the main map
     */
    public Entity getMainMap() {
        return mainMap;
    }

    /**
     * Gets the sky box.
     * @return the sky box
     */
    public SkyBox getSkyBox() {
        return skyBox;
    }

    /**
     * Gets the game lighting.
     * @return the game lighting
     */
    public SceneLighting getGameLighting() {
        return gameLighting;
    }

    /**
     * Intializes the positions of players.
     * @param team the team that the players are on
     * @param players the players to initialize
     */
    public void setPlayerInitialPositions(int team, List<Player> players) {
        if (players.size() != MatchData.NUM_PLAYERS_PER_TEAM) {
            throw new IllegalArgumentException("Not enough players on the team");
        }

        Vector3f[] initialPositions = team == MatchData.RED_TEAM ? initialRedPositions : initialBluePositions;

        for(int i = 0; i<MatchData.NUM_PLAYERS_PER_TEAM; i++) {
            Player currPlayer = players.get(i);
            currPlayer.getComponent(Transform.class).getPosition().set(initialPositions[i]);
            currPlayer.getComponent(Transform.class).getRotation().set(0, 0, 0);
            currPlayer.getView().getComponent(Transform.class).getRotation().set(0, 0, 0);
        }
    }

    /**
     * Used for Kryonet
     */
    @SuppressWarnings("unused")
    private GameMap() {}

}