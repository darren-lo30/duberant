package duber.game.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import duber.game.networking.KryoRegister;

public class ServerNetwork extends Listener {
    private final int port;
    private boolean running;
    private Server server;
    private Map<Connection, ConcurrentLinkedQueue<Object>> packets = new HashMap<>();
    
    public ServerNetwork(int port) throws IOException {
        this.port = port;
        server = new Server();
        server.bind(port, port);

        KryoRegister.registerPackets(server.getKryo());
        server.addListener(this);
    }

    public int getPort() {
        return port;
    }

    public Map<Connection, ConcurrentLinkedQueue<Object>> getPackets() {
        return packets;
    }

    public void start() {
        System.out.println("Starting server network");
        running = true;
        server.start();
    }

    public void stop() {
        running = false;
        server.stop();
    }

    public boolean isRunning() {
        return running;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void received(Connection connection, Object packet) {
        System.out.println("Received");
        packets.computeIfAbsent(connection, k -> new ConcurrentLinkedQueue<>()).add(packet);
    }
    
    @Override
    public void connected(Connection connection) {
        System.out.println("Connected with: " + connection);
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected with: " + connection);
        packets.remove(connection);
    }
    
    public Optional<Connection> getConnectionById(int id) {
        return Arrays.stream(server.getConnections())
                     .filter(connection -> connection.getID() == id)
                     .findFirst();
    }

    public void addListener(Listener listener) {
        server.addListener(listener);
    }

    public void removeListener(Listener listener) {
        server.removeListener(listener);
    }
    
}



    