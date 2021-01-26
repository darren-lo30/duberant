package duber.game.networking;

/**
 * A Packet used by Kryonet that notifies the server that a client is trying to login.
 * @author Darren Lo
 * @version 1.0
 */
public class LoginPacket extends Packet {
    /**
     * The username of the User.
     */
    public String username;

    /**
     * Constructs a LoginPacket
     * @param username the name of the User trying to log in
     */
    public LoginPacket(String username) {
        this.username = username;
    }

    /**
     * Used by Kryonet
     */
    @SuppressWarnings("unused")
    private LoginPacket() {}
}