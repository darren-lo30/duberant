package duber.game.server;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import duber.game.networking.KryoRegister;

/**
 * A class to encapsulate all the server network data for the game
 * @author Darren Lo
 * @version 1.0
 */
public class ServerNetwork extends Listener {    
    /**
     * Whether or not the server is running.
     */
    private boolean running;

    /**
     * The Server used to communicate with the client.
     */
    private Server server;

    /**
     * All packets received from each Connection.
     */
    private Map<Connection, ConcurrentLinkedQueue<Object>> packets = new ConcurrentHashMap<>();

    /**
     * A Set of all active Connections.
     */
    private Set<Connection> connections = ConcurrentHashMap.newKeySet();
    
    /**
     * Constructs a server listening on a given port.
     * @param port the port to listen to
     * @throws IOException if the server could not be started
     */
    public ServerNetwork(int port) throws IOException {
        server = new Server(50000, 50000);
        server.bind(port, port);

        KryoRegister.registerPackets(server.getKryo());
        server.addListener(this);
    }

    /**
     * Gets all the packets received from a given Connection.
     * @param connection the Connection to query from
     * @return a Queue of all received packets from the Connection
     */
    public Queue<Object> getPackets(Connection connection) {
        Queue<Object> connectionPackets = packets.get(connection);
        if (connectionPackets == null) {
            return new ConcurrentLinkedQueue<>();
        }
        
        return connectionPackets;
    }

    /**
     * Starts the server.
     */
    public void start() {
        System.out.println("Starting server network");
        running = true;
        server.start();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        running = false;
        server.stop();
    }

    /**
     * Determines if the server is running.
     * @return whether or not the server is running
     */
    public boolean isRunning() {
        return running;
    }   

    /**
     * Gets the Server that connects to the client.
     * @return the Server that connects to the client
     */
    public Server getServer() {
        return server;
    }

    /**
     * A method to run when a packet is received.
     * @param connection the Connection that sent the packet
     * @param packet the packet that was sent
     */
    @Override
    public void received(Connection connection, Object packet) {
        if (!(packet instanceof FrameworkMessage.KeepAlive)) {
            packets.get(connection).add(packet);
        }
    }

    /**
     * A method to run when a Connection connects.
     * @param connection the connected Connection
     */
    @Override
    public void connected(Connection connection) {
        System.out.println("Connected with: " + connection);
        packets.put(connection, new ConcurrentLinkedQueue<>());
        connections.add(connection);
    }

    /**
     * A method that runs when a Connection disconnects.
     * @param connection the disconnected Connection
     */
    @Override
    public void disconnected(Connection connection) {
        System.out.println("Disconnected with: " + connection);
        connections.remove(connection);
        packets.remove(connection);
    }

    /**
     * Adds a Listener to the Server.
     * @param listener the Listener to add
     */
    public void addListener(Listener listener) {
        server.addListener(listener);
    }

    /**
     * Removes a Listener from the Server.
     * @param listener the Listener to remove
     */
    public void removeListener(Listener listener) {
        server.removeListener(listener);
    }

    /**
     * Gets a Set of all connected Connections.
     * @return all the connected Connections
     */
    public Set<Connection> getConnections() {
        return connections;
    }
}



    