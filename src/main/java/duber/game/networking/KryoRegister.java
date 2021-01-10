package duber.game.networking;

import com.esotericsoftware.kryo.Kryo;

public class KryoRegister {
    private KryoRegister() {}
    
    public static void registerPackets(Kryo kryo) {
        //Classes from other libraries
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
    }
}