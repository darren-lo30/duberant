package duber.engine.audio;

import org.joml.Vector3f;
import static org.lwjgl.openal.AL10.*;

public class SoundSource {
    private final int id;

    public SoundSource(boolean loop, boolean relative){
        id = alGenSources();

        if(loop){
            alSourcei(id, AL_LOOPING, AL_TRUE);
        }

        if(relative){
            alSourcei(id, AL_SOURCE_RELATIVE, AL_TRUE);
        }
    }

    public void setProperty(int property, float value){
        alSourcef(id, property, value);
    }

    public void play(){
        alSourcePlay(id);
    }

    public void stop(){
        alSourceStop(id);
    }

    public void pause(){
        alSourcePause(id);
    }

    public boolean isPlaying(){
        return alGetSourcei(id, AL_SOURCE_STATE) == AL_PLAYING;
    }

    public void setPosition(Vector3f position){
        alSource3f(id, AL_POSITION, position.x, position.y, position.z);
    }

    public void setSpeed(Vector3f speed){
        alSource3f(id, AL_VELOCITY, speed.x, speed.y, speed.z);
    }

    public void setGain(float gain){
        alSourcef(id, AL_GAIN, gain);
    }

    public void setBuffer(int bufferId){
        stop();
        alSourcei(id, AL_BUFFER, bufferId);
    }

    public int getBuffer() {
        return alGetSourcei(id, AL_BUFFER);
    }

    public void cleanup(){
        stop();
        alDeleteSources(id);
    }
}