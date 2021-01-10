package duber.game.networking;

import duber.game.User;

public class LoginConfirmationPacket extends Packet {
    public User user;

    @SuppressWarnings("unused")
    private LoginConfirmationPacket() {}

    public LoginConfirmationPacket(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}