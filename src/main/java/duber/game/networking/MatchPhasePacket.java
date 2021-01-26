package duber.game.networking;

import duber.game.phases.MatchPhase;

/**
 * A Packet used by Kryonet that notifies the client that the match phase has changed.
 * @author Darren Lo
 * @version 1.0
 */
public class MatchPhasePacket extends Packet {
    /** The new current match phase */
    public MatchPhase currMatchPhase;

    /**
     * Constructs a MatchPhasePackets.
     * @param currMatchPhase the new MatchPhase
     */
    public MatchPhasePacket(MatchPhase currMatchPhase) {
        this.currMatchPhase = currMatchPhase;
    }

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private MatchPhasePacket() {}
}