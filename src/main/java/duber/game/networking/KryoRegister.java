package duber.game.networking;

import com.esotericsoftware.kryo.Kryo;

/**
 * Registers all the Packets used in the Game
 * @author Darren Lo
 * @version 1.0
 */
public class KryoRegister {
    /**
     * Private constructor to discourage instantiation.
     */
    private KryoRegister() {}
    
    /**
     * Registers the packets.
     * @param kryo the Kryo class used to register packets
     */
    public static void registerPackets(Kryo kryo) {
        //Classes from other libraries
        kryo.setReferences(true);
        kryo.setRegistrationRequired(false);
    }
}