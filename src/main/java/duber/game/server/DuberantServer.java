package duber.game.server;

import java.util.List;

import com.esotericsoftware.kryonet.Server;

public class DuberantServer {
    private Server server;
    private List<Integer> connectedUserIds;

    public DuberantServer() {
        server = new Server();
    }
    
}