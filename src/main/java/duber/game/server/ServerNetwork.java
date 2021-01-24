package duber.game.server;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import duber.game.networking.KryoRegister;

public class ServerNetwork extends Listener {
    private final int port;
    private boolean running;
    private Server server;
    private Map<Connection, ConcurrentLinkedQueue<Object>> packets = new ConcurrentHashMap<>();
    private Set<Connection> connections = ConcurrentHashMap.newKeySet();
    
    public ServerNetwork(int port) throws IOException {
        this.port = port;
        server = new Server(50000, 50000);
        server.bind(port, port);

        KryoRegister.registerPackets(server.getKryo());
        server.addListener(this);
    }

    public int getPort() {
        return port;
    }

    public Queue<Object> getPackets(Connection connection) {
        Queue<Object> connectionPackets = packets.get(connection);
        if (connectionPackets == null) {
            return new ConcurrentLinkedQueue<>();
        }
        
        return connectionPackets;
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
        if (!(packet instanceof FrameworkMessage.KeepAlive)) {
            packets.get(connection).add(packet);
        }
    }
    
    @Override
    public void connected(Connection connection) {
        System.out.println("Connected with: " + connection);
        packets.put(connection, new ConcurrentLinkedQueue<>());
        connections.add(connection);
    }

    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected with: " + connection);
        connections.remove(connection);
        packets.remove(connection);
    }

    public void addListener(Listener listener) {
        server.addListener(listener);
    }

    public void removeListener(Listener listener) {
        server.removeListener(listener);
    }

    public Optional<Connection> getConnectionById(int id) {
        return connections.stream()
                .filter(connection -> connection.getID() == id)
                .findFirst();
    }

    public Set<Connection> getConnections() {
        return connections;
    }
}



    