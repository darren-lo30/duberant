package duber.engine.audio;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.openal.AL;
import static org.lwjgl.openal.AL10.*;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import static org.lwjgl.openal.ALC10.*;

import duber.engine.entities.Camera;
import duber.engine.entities.components.Transform;
import duber.engine.graphics.MatrixTransformer;

import static org.lwjgl.system.MemoryUtil.NULL;


public class SoundManager {
    private long deviceHandle;
    
    private long context;

    private SoundListener listener;

    List<SoundBuffer> soundBuffers;

    Map<String, SoundSource> soundSources;

    private final Matrix4f cameraMatrix;

    public SoundManager(){
        soundBuffers = new ArrayList<>();
        soundSources = new HashMap<>();
        cameraMatrix = new Matrix4f();
    }

    public void init() {
        deviceHandle = alcOpenDevice((ByteBuffer) null);

        if(deviceHandle == NULL){
            throw new IllegalStateException("Could not open deviceHandle");
        }
        ALCCapabilities deviceCapabilities = ALC.createCapabilities(deviceHandle);
        context = alcCreateContext(deviceHandle, (IntBuffer) null);
        if(context == NULL){
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

    public void playSoundSource(String name){
        SoundSource soundSource = soundSources.get(name);
        if(soundSource != null && !soundSource.isPlaying()){
            soundSource.play();
        }
    }

    public void removeSoundSource(String name){
        soundSources.remove(name);
    }

    public void addSoundBuffer(SoundBuffer soundBuffer){
        soundBuffers.add(soundBuffer);
    }

    public SoundListener getListener(){
        return listener;
    }

    public void setListener(SoundListener listener){
        this.listener = listener;
    }

    public void updateListenerPosition(Camera camera){
        Transform cameraTransform = camera.getComponent(Transform.class);
        MatrixTransformer.updateGeneralViewMatrix(cameraTransform.getPosition(), cameraTransform.getRotation(), cameraMatrix);

        listener.setPosition(cameraTransform.getPosition());

        //Direction that listener is facing
        Vector3f facing = new Vector3f();
        cameraMatrix.positiveZ(facing).negate();
        
        //Direction that is up
        Vector3f up = new Vector3f();
        cameraMatrix.positiveY(up);
        
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

        for(SoundBuffer soundBuffer: soundBuffers){
            soundBuffer.cleanup();
        }
        soundBuffers.clear();

        if(context != NULL){
            alcDestroyContext(context);
        }
        if(deviceHandle != NULL){
            alcCloseDevice(deviceHandle);
        }
    }
}