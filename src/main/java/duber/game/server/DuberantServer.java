package duber.game.server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import duber.engine.utilities.Timer;
import duber.game.networking.UserConnectPacket;

import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryonet.Connection;


public class DuberantServer {
    private static final int TARGET_UPS = 10;


    private volatile boolean running;
    private ServerNetwork serverNetwork;

    private Set<Integer> connectionsInMatch = new HashSet<>();
    private Set<MatchManager> ongoingMatches = new HashSet<>();


    public DuberantServer() throws IOException {
        serverNetwork = new ServerNetwork(5000);
    }

    public void start() {
        running = true;
        serverNetwork.start();

        try {
            serverLoop();
        } catch (InterruptedException ie) {
            ie.printStackTrace();
            Thread.currentThread().interrupt();
        } finally {
            serverNetwork.stop();
            running = false;
        }
    }

    public void stop() {
        running = false;
    }

    private boolean inMatch(Connection connection) {
        return connectionsInMatch.contains(connection.getID());
    }

    public void serverLoop() throws InterruptedException {
        Timer serverLoopTimer = new Timer();

        float elapsedTime;
        float interval = 1.0f / TARGET_UPS;
        
        while(running && serverNetwork.isRunning()) {
            for(Map.Entry<Connection, ConcurrentLinkedQueue<Object>> connectionPackets : serverNetwork.getPackets().entrySet()) {
                Connection connection = connectionPackets.getKey();
                ConcurrentLinkedQueue<Object> packets = connectionPackets.getValue();              

                if(!inMatch(connection)) {
                    processAllPackets(connection, packets);
                }
            }

            
            //Ensure that a maximum of 10 updates per second happens
            //It is fine if less than 10 updates per second happens
            elapsedTime = serverLoopTimer.getElapsedTime();
            if(elapsedTime < interval) {
                Thread.sleep((long) (interval - elapsedTime) * 1_000_000L);
            }
        } 
    }

    private void processAllPackets(Connection connection, ConcurrentLinkedQueue<Object> packets) {
        //Only process certain requests, leave other requests up to the match manager
        for(Object packet : packets) {
            packets.poll();
            if(packet instanceof UserConnectPacket) {
                processPacket(connection, (UserConnectPacket) packet);
            }
        }
    }

    private void processPacket(Connection connection, UserConnectPacket userConnectPacket) {
        String username = userConnectPacket.username;
        System.out.println("Connected with username: " + username);
        System.out.println("Crosshair width: " + userConnectPacket.crosshair.getWidth());
    }


    public static void main(String[] args) {
        try {
            DuberantServer server = new DuberantServer();
            server.start();
        } catch (IOException ioe) {
            System.out.println("IO error occured while starting server");
            ioe.printStackTrace();
        }
    }
}