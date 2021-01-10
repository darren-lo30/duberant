package duber.game.networking;

public class UserConnectPacket extends Packet {
    public String username;
    
    @SuppressWarnings("unused")
    private UserConnectPacket() {}

    public UserConnectPacket(String username) {
        this.username = username;
    }
}