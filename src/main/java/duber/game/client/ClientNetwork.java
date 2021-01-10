package duber.game.client;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import duber.game.networking.KryoRegister;


/**
 * A class that encapsulates a client connection to a DuberantServer
 */
public class ClientNetwork extends Listener {
    private Client client;
    private Connection connection;
    private String ipAddress;
    private int port;
    
    private final BlockingQueue<Object> packets = new LinkedBlockingQueue<>();
    
    public ClientNetwork() {
        this("localhost", 5000);
    }

    public ClientNetwork(String ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
        
        client = new Client();
        
        //Register packets
        KryoRegister.registerPackets(client.getKryo());

        client.start();
        client.addListener(this);        
    }

    public String getIpAddres() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void connect(int timeout) throws IOException {
        client.connect(timeout, ipAddress, port, port);
    }

    @Override
    public void connected(Connection connection) {
        System.out.println("Connected!");
        this.connection = connection;
    }

    public boolean isConnected() {
        return connection != null && connection.isConnected();
    }

    public Client getClient() {
        return client;
    }

    public Connection getConnection() {
        return connection;
    }

    public BlockingQueue<Object> getPackets() {
        return packets;
    }
    
    @Override
    public void received(Connection clientConnection, Object packet) {
        packets.add(packet);
    }
}