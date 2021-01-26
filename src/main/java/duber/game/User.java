package duber.game;

import com.esotericsoftware.kryonet.Connection;

import duber.game.client.match.Crosshair;

/**
 * A User in the game.
 * @author Darren Lo
 * @version 1.0
 */
public class User {
    /**
     * The Users unique identification number.
     */
    private int id;

    /**
     * The Users name
     */
    private String username;

    /**
     * The crosshair that they have while in a match.
     */
    private Crosshair crosshair;

    /**
     * The Users connection to server or client.
     */
    private transient Connection connection;

    /**
     * Constructs a User with a given id.
     * @param id the id of the User
     * @param username the Users name
     */
    public User(int id, String username) {
        this.id = id;
        this.username = username;
        crosshair = new Crosshair();
    }

    /**
     * Gets the Users id.
     * @return the Users id
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the Users username.
     * @return the Users username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the Users crosshair.
     * @return the Users crosshair
     */
    public Crosshair getCrosshair() {
        return crosshair;
    }   

    /**
     * Gets the Users connection.
     * @return the Users connection
     */
    public Connection getConnection() {
        return connection;
    }
    
    /**
     * Sets the users connection.
     * @param connection the Users connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * Determines whether the User is logged in.
     * @return whether or not the User is logged in
     */
    public boolean isLoggedIn() {
        return connection != null && connection.isConnected();
    }

    /**
     * Calculates the Users hashcode which is the Users id
     * @return the Users hashcode
     */
    @Override
    public int hashCode() {
        return id;
    }

    /**
     * Determines whether a User is the same as another Object
     * @param o the Object to compare to
     */
    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof User)) {
            return false;
        }
        
        User user = (User) o;
        return id == user.getId();
    }

    /**
     * A constructor Used by Kryonet
     */
    @SuppressWarnings("unused")
    private User() {}    
}