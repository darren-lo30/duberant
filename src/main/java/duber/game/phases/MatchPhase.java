package duber.game.phases;

import duber.engine.utilities.Timer;
import duber.game.MatchData;
import duber.game.client.match.HUD;
import duber.game.client.match.Match;
import duber.game.server.MatchManager;

public abstract class MatchPhase {
    private transient MatchPhaseLogic phaseLogic;
    
    private boolean playerCanMove = false;
    private boolean playerCanBuy = false;

    public boolean playerCanMove() {
        return playerCanMove;
    }

    public void setPlayerCanMove(boolean playerCanMove) {
        this.playerCanMove = playerCanMove;
    }

    public boolean playerCanBuy() {
        return playerCanBuy;
    }

    public void setPlayerCanBuy(boolean playerCanBuy) {
        this.playerCanBuy = playerCanBuy;
    }

    public void setPhaseLogic(MatchPhaseLogic phaseLogic) {
        this.phaseLogic = phaseLogic;
    }

    public void changeMatchPhase(MatchPhase nextMatchPhase) {
        phaseLogic.getMatchPhaseManager().changeMatchPhase(nextMatchPhase);
    }

    public abstract void makeClientLogic(Match match);
    public abstract void makeServerLogic(MatchManager match);

    public void render() {
        if (phaseLogic != null) {
            phaseLogic.render();
        }
    }

    public void update() {
        if (phaseLogic != null) {
            phaseLogic.update();
        }
    }

    private interface MatchPhaseLogic {
        public abstract void render();

        public abstract void update();

        public abstract MatchPhaseManager getMatchPhaseManager();
    }
    
    protected abstract class ClientLogic implements MatchPhaseLogic {
        protected Match match;
        
        protected ClientLogic(Match match) {
            this.match = match;
        }

        @Override
        public MatchPhaseManager getMatchPhaseManager() {
            return match;
        }

        @Override
        public void update() {
            match.receivePackets();
            match.getMatchSounds().updateSoundSources();
            match.sendPackets();
            
            match.updateAnimations();
            match.getMatchSounds().playMovementSounds();
        }

        public HUD getHud() {
            return match.getHud();
        }
    }

    protected abstract class ServerLogic implements MatchPhaseLogic {
        protected MatchManager match;

        protected ServerLogic(MatchManager match) {
            this.match = match;
        }

        @Override
        public void render() {
            //Change this if you ever add server interface
            throw new UnsupportedOperationException("Server does not currently have an interface");
        }

        @Override
        public MatchPhaseManager getMatchPhaseManager() {
            return match;
        }

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

        public abstract void tryChangeMatchPhase();   
    }

    protected class TimedPhaseLogic extends ServerLogic {
        private Timer phaseTimer;
        private float timeToLast;
        private MatchPhase nextPhase;

        public TimedPhaseLogic(MatchManager match, float timeToLast, MatchPhase nextPhase) {
            super(match);
            this.timeToLast = timeToLast;
            this.phaseTimer = new Timer();
            phaseTimer.updateTime();

            this.nextPhase = nextPhase;
        }

        @Override
        public void tryChangeMatchPhase() {
            if (phaseTimer.getElapsedTime() > timeToLast) {
                changeMatchPhase(nextPhase);
            }
        }
    }
}