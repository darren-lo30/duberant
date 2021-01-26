package duber.game.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import duber.game.networking.KryoRegister;


/**
 * A class that encapsulates a client connection to a DuberantServer.
 * @author Darren Lo
 * @version 1.0
 */
public class ClientNetwork extends Listener {
    /** The Client used to connect to the server. */
    private Client client;

    /** The connection to the server. */
    private Connection connection;

    /** The ip address of the server. */
    private String ipAddress;

    /** The port to connect to the server on. */
    private int port;
    
    /** The packets received from the server. */
    private final BlockingQueue<Object> packets = new LinkedBlockingQueue<>();
    
    /** 
     * Constructs a ClientNetwork defaulting to local host and on port 5000
     */
    public ClientNetwork() {
        this("localhost", 5000);
    }

    /**
     * Constructs a ClientNetwork with a given ip address and port.
     * @param ipAddress the ip address of the server
     * @param port the port of the server
     */
    public ClientNetwork(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        
        client = new Client(50000, 50000);
        
        //Register packets
        KryoRegister.registerPackets(client.getKryo());

        client.start();
        client.addListener(this);        
    }

    /**
     * Connects the ClientNetwork to the server.
     * @param timeout the time to wait for a server response before failing
     * @throws IOException if the server could not be connected to
     */
    public void connect(int timeout) throws IOException {
        client.connect(timeout, ipAddress, port, port);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void connected(Connection connection) {
        this.connection = connection;
    }

    /**
     * Determines if there is a connection to the server.
     * @return if there is a connection to the server
     */
    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    /**
     * Gets the Client used to connect to the server.
     * @return the client used to connect to the server
     */
    public Client getClient() {
        return client;
    }

    /**
     * Gets the Connection used to connect to the server.
     * @return the connection used to connect to the server
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Gets a BlockingQueue of packets that were received from the server.
     * @return the BlockingQueue of packets
     */
    public BlockingQueue<Object> getPackets() {
        return packets;
    }
    
    /**
     * {@inheritDoc}
     * Adds the packet to the BlockingQueue of packets.
     */
    @Override
    public void received(Connection clientConnection, Object packet) {
        packets.add(packet);
    }

    /**
     * Closes the network.
     */
    public void close() {
        if (client != null) {
            client.close();
        }
        if (connection != null) {
            connection.close();
        }
    }
    
}