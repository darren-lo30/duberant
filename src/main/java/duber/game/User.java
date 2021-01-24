package duber.game;

import com.esotericsoftware.kryonet.Connection;

import duber.game.client.match.Crosshair;

public class User {
    private int id;
    private String username;
    private Crosshair crosshair;
    private transient Connection connection;

    public User(int id, String username) {
        this.id = id;
        this.username = username;
        crosshair = new Crosshair();
    }
    
    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Crosshair getCrosshair() {
        return crosshair;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public boolean isLoggedIn() {
        return connection != null && connection.isConnected();
    }

    @Override
    public int hashCode() {
        return id;
    }

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

    @SuppressWarnings("unused")
    private User() {}    
}