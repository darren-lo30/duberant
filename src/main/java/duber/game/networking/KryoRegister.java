package duber.game.networking;

import com.esotericsoftware.kryo.Kryo;

import org.joml.Vector4f;

import duber.game.User;
import duber.game.client.match.Crosshair;

public class KryoRegister {
    private KryoRegister() {}
    
    public static void registerPackets(Kryo kryo) {
        kryo.register(UserConnectPacket.class);
        kryo.register(Crosshair.class);
        kryo.register(Vector4f.class);
        kryo.register(User.class);

    }
}