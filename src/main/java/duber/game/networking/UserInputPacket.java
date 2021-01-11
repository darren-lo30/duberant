package duber.game.networking;

import duber.engine.KeyboardInput;
import duber.engine.MouseInput;

public class UserInputPacket {
    public KeyboardInput keyboardInput;
    public MouseInput mouseInput;

    public UserInputPacket(KeyboardInput keyboardInput, MouseInput mouseInput) {
        this.keyboardInput = keyboardInput;
        this.mouseInput = mouseInput;
    }

    @SuppressWarnings("unused")
    private UserInputPacket() {}
}