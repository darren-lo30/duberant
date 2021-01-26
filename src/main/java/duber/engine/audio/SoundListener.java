package duber.engine.audio;

import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.alListener3f;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_VELOCITY;
import static org.lwjgl.openal.AL10.AL_ORIENTATION;
import static org.lwjgl.openal.AL10.alListenerfv;



/**
 * A listener to sounds in the 3D world
 * @author Darren Lo
 * @version 1.0
 */
public class SoundListener {
    /**
     * Constructs a SoundListener at (0, 0, 0)
     */
    public SoundListener(){
        this(new Vector3f());
    }
    
    /**
     * Constructs a SoundListener at a given position
     * @param position the position
     */
    public SoundListener(Vector3f position){
        alListener3f(AL_POSITION, position.x, position.y, position.z);
        alListener3f(AL_VELOCITY, 0, 0, 0);   
    }

    /**
     * Sets the position.
     * @param position the position
     */
    public void setPosition(Vector3f position){
        alListener3f(AL_POSITION, position.x, position.y, position.z);
    }

    /**
     * Sets the orientation of this SoundListener.
     * @param facing the direction being faced
     * @param up the direction that is upwards
     */
    public void setOrientation(Vector3f facing, Vector3f up){
        float[] data = new float[6];
        data[0] = facing.x;
        data[1] = facing.y;
        data[2] = facing.z;

        data[3] = up.x;
        data[4] = up.y;
        data[5] = up.z;
        alListenerfv(AL_ORIENTATION, data);
    }
}