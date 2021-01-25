package duber.game.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import duber.engine.exceptions.LWJGLException;
import duber.engine.utilities.Timer;
import duber.game.MatchData;
import duber.game.User;
import duber.game.networking.LoginPacket;
import duber.game.networking.MatchFoundPacket;
import duber.game.networking.MatchQueuePacket;
import duber.game.networking.LoginConfirmationPacket;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;


/**
 * A server that connects players.
 * @author Darren Lo
 * @version 1.0
 */
public class DuberantServer {
    /**
     * The number of loops per second to run.
     */
    private static final int TARGET_UPS = 10;

    /**
     * Whether or not the server is running.
     */
    private volatile boolean running;

    /**
     * The server network used to connect to clients with.
     */
    private ServerNetwork serverNetwork;

    /**
     * The Users seraching for a match.
     */
    private Set<User> usersSearchingForMatch = new LinkedHashSet<>();

    /**
     * The Users in a match.
     */
    private Set<User> usersInMatch = new HashSet<>();

    /**
     * The users that are connected and logged.
     */
    private Map<Connection, User> connectedUsers = new ConcurrentHashMap<>();

    /**
     * The ongoing matches.
     */
    private Set<MatchManager> matchManagers = new HashSet<>();

    /**
     * Constructs a new server listening on port 5000.
     * @param port the port to listen on
     * @throws IOException if the ServerNetwork could not start
     */
    public DuberantServer(int port) throws IOException {
        serverNetwork = new ServerNetwork(port);
    }

    /**
     * Starts the server.
     */
    public void start() {
        running = true;
        serverNetwork.start();

        //Remove user if they are disconnected
        serverNetwork.addListener(new Listener() {
            @Override
            public void disconnected(Connection connection) {
                connectedUsers.remove(connection);
            }
        });

        //Start server loop
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

    /**
     * Stops the server.
     */
    public void stop() {
        running = false;
    }

    /**
     * Listens for client packets and responds to them.
     * @throws InterruptedException if the Thread is interupted
     */
    public void serverLoop() throws InterruptedException {
        Timer serverLoopTimer = new Timer();

        float elapsedTime;
        float interval = 1.0f / TARGET_UPS;
        
        while(running && serverNetwork.isRunning()) {
            initializeMatches();
            cleanupMatches();

            //Handle connections
            for(Connection connection : serverNetwork.getConnections()) {                
                Queue<Object> packets = serverNetwork.getPackets(connection);
                
                if (packets != null && !usersInMatch.contains(getUser(connection))) {
                    processAllPackets(connection, packets);
                }
            }


            //Ensure that a maximum of 10 updates per second happens
            //It is fine if less than 10 updates per second happens
            elapsedTime = serverLoopTimer.getElapsedTimeAndUpdate();
            if (elapsedTime < interval) {
                Thread.sleep((long) (interval - elapsedTime) * 1_000_000L);
            }
        } 
    }

    /**
     * Gets the User associated with a connection.
     * @param connection the connection to qeury
     * @return the associated User
     */
    private User getUser(Connection connection) {
        return connectedUsers.get(connection);
    }

    /**
     * Removes any disconnected users from the Users searching for a match.
     */
    private void cleanUsersSearchingMatches() {
        for(Iterator<User> i = usersSearchingForMatch.iterator(); i.hasNext();) {
            User user = i.next();
            if (!user.isLoggedIn()) {
                i.remove();
            }
        }
    }

    /**
     * Initializes a match when enough Users are in the match queue.
     */
    private void initializeMatches() {
        cleanUsersSearchingMatches();
        Iterator<User> userIterator = usersSearchingForMatch.iterator();
        while(usersSearchingForMatch.size() >= MatchData.NUM_PLAYERS_IN_MATCH) {
            System.out.println("Initializing a match");

            List<User> newMatchUsers = new ArrayList<>(MatchData.NUM_PLAYERS_IN_MATCH);
            for(int i = 0; i<MatchData.NUM_PLAYERS_IN_MATCH; i++) {
                User nextUser = userIterator.next();
                nextUser.getConnection().sendTCP(new MatchFoundPacket());

                usersInMatch.add(nextUser);
                newMatchUsers.add(nextUser);
                userIterator.remove();

            }

            try {
                MatchManager matchManager = new MatchManager(serverNetwork, newMatchUsers);
                matchManagers.add(matchManager);
                //Start a new thread with the match manager
                new Thread(matchManager).start();
            } catch (LWJGLException le) {
                System.out.println("Error while starting match");
                le.printStackTrace();
            }
        }
    }

    /**
     * Removes any matches that have ended from the ongoing matches.
     */
    private void cleanupMatches() {
        for(Iterator<MatchManager> i = matchManagers.iterator(); i.hasNext();) {
            MatchManager match = i.next();
            if (!match.isRunning()) {
                System.out.println("Match ended");
                i.remove();

                for(User user : match.getUsers()) {
                    usersInMatch.remove(user);
                }
            }
        }
    }

    /**
     * Processes all the packets from a connection.
     * @param connection the connection
     * @param packets the packets they sent
     */
    private void processAllPackets(Connection connection, Queue<Object> packets) {
        //Only process certain requests, leave other requests up to the match manager
        while(!packets.isEmpty()) {
            Object packet = packets.poll();
            if (packet instanceof LoginPacket) {
                processPacket(connection, (LoginPacket) packet);
            } 

            User connectedUser = getUser(connection);
            if (connectedUser != null && packet instanceof MatchQueuePacket) {
                System.out.println("Player sent match queue request");
                processPacket(connectedUser, (MatchQueuePacket) packet);
            }
        }
    }

    /**
     * Processes a login packet. 
     * @param connection the Connection that sent the packet
     * @param userConnectPacket the data required to login
     */
    private void processPacket(Connection connection, LoginPacket userConnectPacket) {
        String username = userConnectPacket.username;
        System.out.println("Connected with username: " + username);

        //Send user account back to client
        User registeredUser = new User(connection.getID(), userConnectPacket.username);
        connection.sendTCP(new LoginConfirmationPacket(registeredUser));

        registeredUser.setConnection(connection);
        
        //Add user
        connectedUsers.put(connection, registeredUser);
    }

    /**
     * Processes a match queue packet.
     * @param user the User that sent the packet
     * @param matchQueuePacket the data about the Users action with the match queue
     */
    private void processPacket(User user, MatchQueuePacket matchQueuePacket) {
        if (matchQueuePacket.joinQueue) {
            //Join match queue
            usersSearchingForMatch.add(user);
        } else {
            //Leave match queue
            usersSearchingForMatch.remove(user);
        }
    }

    /**
     * Runs a server.
     * @param args the args from the command line
     */
    public static void main(String[] args) {
        try {
            DuberantServer server = new DuberantServer(5000);
            server.start();
        } catch (IOException ioe) {
            System.out.println("IO error occured while starting server");
            ioe.printStackTrace();
        }
    }
}