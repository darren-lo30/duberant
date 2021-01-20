package duber.game.networking;

public class MatchQueuePacket {
    public boolean joinQueue;

    public MatchQueuePacket(boolean joinQueue) {
        this.joinQueue = joinQueue;
    }

    @SuppressWarnings("unused")
    private MatchQueuePacket() {}
}