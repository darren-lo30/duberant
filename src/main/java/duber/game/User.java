package duber.game;

import duber.game.client.match.Crosshair;

public class User {
    private int id;
    private String username;
    private Crosshair crosshair;

    private User() {}
    
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
}