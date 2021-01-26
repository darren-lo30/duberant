package duber.game.networking;

import duber.game.User;

/**
 * A Packet used by Kryonet that notifies the client that they succesfully logged in.
 * @author Darren Lo
 * @version 1.0
 */
public class LoginConfirmationPacket extends Packet {
    /** The User that was created */
    public User user;

    /**
     * Constructs a LoginConfirmationPacket.
     * @param user the user that logged in
     */
    public LoginConfirmationPacket(User user) {
        this.user = user;
    }

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private LoginConfirmationPacket() {}
}