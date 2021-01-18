package duber.game.networking;

import duber.game.phases.MatchPhase;

public class MatchPhasePacket extends Packet {
    public MatchPhase currMatchPhase;

    public MatchPhasePacket(MatchPhase currMatchPhase) {
        this.currMatchPhase = currMatchPhase;
    }

    protected MatchPhasePacket() {}
}