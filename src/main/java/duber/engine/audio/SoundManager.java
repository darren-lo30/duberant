package duber.engine.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;


import duber.engine.Cleansable;
import duber.engine.entities.components.Transform;
import duber.engine.graphics.MatrixTransformer;

import static org.lwjgl.system.MemoryUtil.NULL;

import static org.lwjgl.openal.AL10.alDistanceModel;

import static org.lwjgl.openal.ALC10.alcOpenDevice;
import static org.lwjgl.openal.ALC10.alcCreateContext;
import static org.lwjgl.openal.ALC10.alcMakeContextCurrent;
import static org.lwjgl.openal.ALC10.alcDestroyContext;
import static org.lwjgl.openal.ALC10.alcCloseDevice;


/**
 * A manager for all the sounds in a game.
 * @author Darren Lo
 * @version 1.0
 */
public class SoundManager implements Cleansable {
    /** The device handle used to play audio. */
    private final long deviceHandle;
    
    /** The OpenAL context. */
    private final long context;

    /** The SoundListener. */
    private SoundListener listener;

    /** All the sound buffers mapped with names. */
    private Map<String, SoundBuffer> soundBuffers;

    /** All the sound sources mapped with names. */
    private Map<String, SoundSource> soundSources;

    /** The matrix of the SoundListener */
    private final Matrix4f listenerMatrix;

    /**
     * Constructs a SoundManager.
     */
    public SoundManager(){
        soundBuffers = new HashMap<>();
        soundSources = new HashMap<>();
        listenerMatrix = new Matrix4f();

        deviceHandle = alcOpenDevice((ByteBuffer) null);

        if (deviceHandle == NULL){
            throw new IllegalStateException("Could not open deviceHandle");
        }
        ALCCapabilities deviceCapabilities = ALC.createCapabilities(deviceHandle);
        context = alcCreateContext(deviceHandle, (IntBuffer) null);
        if (context == NULL){
            throw new IllegalStateException("Failed to create OpenAL context");
        }

        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCapabilities);
    }

    /**
     * Adds a SoundSource with a name.
     * @param name the name
     * @param soundSource the SoundSource
     */
    public void addSoundSource(String name, SoundSource soundSource){
        soundSources.put(name, soundSource);
    }

    /**
     * Gets a SoundSource from a name.
     * @param the name
     * @return the SoundSource with the name
     */
    public SoundSource getSoundSource(String name){
        return soundSources.get(name);
    }

    /**
     * Plays a SoundSource with a name.
     * @param name the name of the SoundSource
     */
    public void playSoundSource(String name){
        SoundSource soundSource = soundSources.get(name);
        if (soundSource != null && !soundSource.isPlaying()){
            soundSource.play();
        }
    }

    /**
     * Removes a SoundSource with a name.
     * @param name the name of the SoundSource
     */
    public void removeSoundSource(String name){
        soundSources.remove(name);
    }

    /**
     * Adds a sound buffer with a name.
     * @param name the name
     * @param soundBuffer the SoundBuffer
     */
    public void addSoundBuffer(String name, SoundBuffer soundBuffer){
        soundBuffers.put(name, soundBuffer);
    }

    /**
     * Gets a SoundBuffer from a name.
     * @param name the name
     * @return the SoundBuffer with the name
     */
    public SoundBuffer getSoundBuffer(String name) {
        return soundBuffers.get(name);
    }

    /**
     * Gets the id of a SoundBuffer with a name.
     * @param name the name
     * @return the id of the SoundBuffer
     */
    public int getSoundBufferId(String name) {
        return soundBuffers.get(name).getId();
    }

    /**
     * Gets the listener.
     * @return the listener
     */
    public SoundListener getListener(){
        return listener;
    }

    /**
     * Sets the listener.
     * @param listener the listener
     */
    public void setListener(SoundListener listener){
        this.listener = listener;
    }

    /**
     * Updates the listener's position.
     * @param transform the Transform to update with
     */
    public void updateListenerPosition(Transform transform){
        MatrixTransformer.updateGeneralViewMatrix(transform.getPosition(), transform.getRotation(), listenerMatrix);

        listener.setPosition(transform.getPosition());

        //Direction that listener is facing
        Vector3f facing = new Vector3f();
        listenerMatrix.positiveZ(facing).negate();
        
        //Direction that is up
        Vector3f up = new Vector3f();
        listenerMatrix.positiveY(up);
        
        listener.setOrientation(facing, up);
    }

    /**
     * Sets the attenuation model being used.
     * @param model the model to use
     */
    public void setAttenuationModel(int model){
        alDistanceModel(model);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup(){
        for(SoundSource soundSource: soundSources.values()){
            soundSource.cleanup();
        }
        soundSources.clear();

        for(SoundBuffer soundBuffer: soundBuffers.values()){
            soundBuffer.cleanup();
        }
        soundBuffers.clear();

        if (context != NULL){
            alcDestroyContext(context);
        }
        if (deviceHandle != NULL){
            alcCloseDevice(deviceHandle);
        }
    }
}