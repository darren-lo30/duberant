package duber.game.networking;

import duber.engine.KeyboardInput;
import duber.engine.MouseInput;

/**
 * A Packet used by Kryonet that sends user input data to the server from the client.
 * @author Darren Lo
 * @version 1.0
 */
public class UserInputPacket extends Packet{
    /** The client's keyboard input. */
    public KeyboardInput keyboardInput;

    /** The clients mouse input. */
    public MouseInput mouseInput;
    
    /**
     * Constructs a UserInputPacket.
     * @param keyboardInput the keyboard input
     * @param mouseInput the mouse input
     */
    public UserInputPacket(KeyboardInput keyboardInput, MouseInput mouseInput) {
        this.keyboardInput = keyboardInput;
        this.mouseInput = mouseInput;
    }

    /**
     * Used by Kryonet.
     */
    @SuppressWarnings("unused")
    private UserInputPacket() {}
}