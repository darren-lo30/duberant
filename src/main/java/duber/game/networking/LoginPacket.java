package duber.game.networking;

public class LoginPacket extends Packet {
    public String username;
    
    @SuppressWarnings("unused")
    private LoginPacket() {}

    public LoginPacket(String username) {
        this.username = username;
    }
}