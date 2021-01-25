package duber.game.phases;

import duber.engine.utilities.Timer;
import duber.game.MatchData;
import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.server.MatchManager;

/**
 * A phase in a match
 * @author Darren Lo
 * @version 1.0
 */
public abstract class MatchPhase {
    /**
     * The logic used for the match phase.
     */
    private transient MatchPhaseLogic phaseLogic;
    
    /**
     * Whether or not the player can move.
     */
    private boolean playerCanMove = false;
    
    /**
     * Whether or not the player can buy items.
     */
    private boolean playerCanBuy = false;

    /**
     * Determines whether or not the player can move
     * @return whether or not the player can move
     */
    public boolean playerCanMove() {
        return playerCanMove;
    }

    /**
     * Sets whether or not the player can move
     * @param playerCanMove whether or not the player can move
     */
    public void setPlayerCanMove(boolean playerCanMove) {
        this.playerCanMove = playerCanMove;
    }

    /**
     * Determines whether or not the player can buy
     * @return whether or not the player can buy
     */
    public boolean playerCanBuy() {
        return playerCanBuy;
    }

    /**
     * Sets whether or not the player can buy
     * @param playerCanMove whether or not the player can buy
     */
    public void setPlayerCanBuy(boolean playerCanBuy) {
        this.playerCanBuy = playerCanBuy;
    }

    /**
     * Sets the phase logic that is being used
     * @param phaseLogic the phase logic to use
     */
    public void setPhaseLogic(MatchPhaseLogic phaseLogic) {
        this.phaseLogic = phaseLogic;
    }

    /**
     * Changes the match phase.
     * @param nextMatchPhase the MatchPhase to move on to
     */
    public void changeMatchPhase(MatchPhase nextMatchPhase) {
        phaseLogic.getMatchPhaseManager().changeMatchPhase(nextMatchPhase);
    }

    /**
     * Makes the MatchPhase client sided.
     */
    public abstract void makeClientLogic(Match match);
    
    /**
     * Makes the MatchPhase server sided.
     */
    public abstract void makeServerLogic(MatchManager match);

    /**
     * Renders the game in the MatchPhase.
     */
    public void render() {
        if (phaseLogic != null) {
            phaseLogic.render();
        }
    }

    /**
     * Updates the game in the MatchPhase.
     */
    public void update() {
        if (phaseLogic != null) {
            phaseLogic.update();
        }
    }

    /**
     * The logic used in a match phase.
     */
    private interface MatchPhaseLogic {
        /**
         * Renders the MatchPhase.
         */
        public abstract void render();

        /**
         * Updates the MatchPhase.
         */
        public abstract void update();

        /**
         * Gets the MatchPhaseManager that manages MatchPhases.
         */
        public abstract MatchPhaseManager getMatchPhaseManager();
    }

    /**
     * MatchPhaseLogic that is used on the clients side.
     */
    protected abstract class ClientLogic implements MatchPhaseLogic {
        /**
         * The client's match.
         */
        protected Match match;

        /**
         * Constructs ClientLogic.
         * @param match the client's match
         */
        protected ClientLogic(Match match) {
            this.match = match;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MatchPhaseManager getMatchPhaseManager() {
            return match;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update() {
            match.receivePackets();
            match.getMatchSounds().updateSoundSources();
            match.sendPackets();
            
            match.updateAnimations();
            match.getMatchSounds().playMovementSounds();
        }

        /**
         * Gets the client's HUD.
         * @return the client's HUD
         */
        public HUD getHud() {
            return match.getHud();
        }
    }

    protected abstract class ServerLogic implements MatchPhaseLogic {
        /**
         * The server's match.
         */
        protected MatchManager match;

        /**
         * Constructs ServerLogic.
         * @param match the server's match
         */
        protected ServerLogic(MatchManager match) {
            this.match = match;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void render() {
            //Change this if you ever add server interface
            throw new UnsupportedOperationException("Server does not currently have an interface");
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public MatchPhaseManager getMatchPhaseManager() {
            return match;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void update() {
            int matchWinner = match.getMatchWinner();
            if (matchWinner != MatchData.NULL_TEAM && !match.isOver()) {
                changeMatchPhase(new MatchEndPhase(matchWinner));
                return;
            }

            tryChangeMatchPhase();
            
            match.receivePackets();
            match.getGameWorld().update();
            match.sendPackets();
        }

        /**
         * Changes the MatchPhase at the appropriate time.
         */
        public abstract void tryChangeMatchPhase();   
    }

    /**
     * Server logic that is timed for a certain amount of time before moving on to the next MatchPhase.
     */
    protected class TimedPhaseLogic extends ServerLogic {
        /**
         * The timer that determines when to move on.
         */
        private Timer phaseTimer;
        
        /**
         * The amount of time for the MatchPhase to last.
         */
        private float timeToLast;
        
        /**
         * The next MatchPhase.
         */
        private MatchPhase nextPhase;

        /**
         * Constructs TimedPhaseLogic used for the ServerLogic.
         * @param match the server's match
         * @param timeToLast the amount of time the MatchPhase should last
         * @param nextPhase the next MatchPhase
         */
        public TimedPhaseLogic(MatchManager match, float timeToLast, MatchPhase nextPhase) {
            super(match);
            this.timeToLast = timeToLast;
            this.phaseTimer = new Timer();
            phaseTimer.updateTime();

            this.nextPhase = nextPhase;
        }   

        /**
         * {@inheritDoc}
         */
        @Override
        public void tryChangeMatchPhase() {
            if (phaseTimer.getElapsedTime() > timeToLast) {
                changeMatchPhase(nextPhase);
            }
        }
    }
}