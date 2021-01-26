package duber.game.networking;

/**
 * A Packet used by Kryonet that notifies the server that a User is trying to join/leave the match queue.
 * @author Darren Lo
 * @version 1.0
 */
public class MatchQueuePacket {
    /** If the player is trying to join or leave the queue. */
    public boolean joinQueue;

    /**
     * Constructs a MatchQueuePacket.
     * @param joingQueue if the User is trying to join the queue. They are leaving it otherwise.
     */
    public MatchQueuePacket(boolean joinQueue) {
        this.joinQueue = joinQueue;
    }

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private MatchQueuePacket() {}
}