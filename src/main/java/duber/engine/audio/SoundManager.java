package duber.engine.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import static org.lwjgl.openal.ALC10.*;

import duber.engine.Cleansable;
import duber.engine.entities.components.Transform;
import duber.engine.graphics.MatrixTransformer;

import static org.lwjgl.system.MemoryUtil.NULL;


public class SoundManager implements Cleansable {
    private final long deviceHandle;
    
    private final long context;

    private SoundListener listener;

    Map<String, SoundBuffer> soundBuffers;

    Map<String, SoundSource> soundSources;

    private final Matrix4f listenerMatrix;

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

    public void addSoundSource(String name, SoundSource soundSource){
        soundSources.put(name, soundSource);
    }

    public SoundSource getSoundSource(String name){
        return soundSources.get(name);
    }

    public boolean hasSoundSource(String name) {
        return soundSources.containsKey(name);
    }

    public void playSoundSource(String name){
        SoundSource soundSource = soundSources.get(name);
        if (soundSource != null && !soundSource.isPlaying()){
            soundSource.play();
        }
    }

    public void removeSoundSource(String name){
        soundSources.remove(name);
    }

    public void addSoundBuffer(String name, SoundBuffer soundBuffer){
        soundBuffers.put(name, soundBuffer);
    }

    public SoundBuffer getSoundBuffer(String name) {
        return soundBuffers.get(name);
    }

    public int getSoundBufferId(String name) {
        return soundBuffers.get(name).getId();
    }

    public SoundListener getListener(){
        return listener;
    }

    public void setListener(SoundListener listener){
        this.listener = listener;
    }

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

    public void setAttenuationModel(int model){
        alDistanceModel(model);
    }

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