package duber.game.networking;

import duber.game.client.match.Crosshair;

public class UserConnectPacket {
    public String username;
    public Crosshair crosshair;
    
    private UserConnectPacket() {}

    public UserConnectPacket(String username, Crosshair crosshair) {
        this.username = username;
        this.crosshair = crosshair;
    }
}