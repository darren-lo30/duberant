package duber.game.networking;

import duber.game.User;

public class UserConnectedPacket extends Packet {
    public User user;

    @SuppressWarnings("unused")
    private UserConnectedPacket() {}

    public UserConnectedPacket(User user) {
        this.user = user;
    }
    
    public User getUser() {
        return user;
    }
}