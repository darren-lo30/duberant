package duber.engine.audio;

import org.joml.Vector3f;

import duber.engine.Cleansable;

import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alSourcei;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcePause;
import static org.lwjgl.openal.AL10.alSource3f;

import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_POSITION;
import static org.lwjgl.openal.AL10.AL_LOOPING;
import static org.lwjgl.openal.AL10.AL_SOURCE_RELATIVE;
import static org.lwjgl.openal.AL10.AL_TRUE;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.alDeleteSources;


/**
 * A source of sound
 * @author Darren Lo
 * @version 1.0
 */
public class SoundSource implements Cleansable {
    /** This SoundSource's id. */
    private final int id;

    /**
     * Constructs a SoundSource.
     * @param loop if it should loop
     * @param relative if its relative audio
     */
    public SoundSource(boolean loop, boolean relative){
        id = alGenSources();

        if (loop){
            alSourcei(id, AL_LOOPING, AL_TRUE);
        }

        if (relative){
            alSourcei(id, AL_SOURCE_RELATIVE, AL_TRUE);
        }
    }

    /**
     * Gets the id.
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Sets a property.
     * @param property the property
     * @param value the value to set for the property
     */
    public void setProperty(int property, float value){
        alSourcef(id, property, value);
    }

    /**
     * Plays the sound source.
     */
    public void play(){
        alSourcePlay(id);
    }
    
    /**
     * Stops the sound source.
     */
    public void stop(){
        alSourceStop(id);
    }

    /**
     * Pauses the sound source.
     */
    public void pause(){
        alSourcePause(id);
    }

    /**
     * Determines if this SoundSource is playing.
     * @return whether or not this SoundSource is playing
     */
    public boolean isPlaying(){
        return alGetSourcei(id, AL_SOURCE_STATE) == AL_PLAYING;
    }

    /**
     * Sets the position of this SoundSource.
     * @param position the position
     */
    public void setPosition(Vector3f position){
        alSource3f(id, AL_POSITION, position.x, position.y, position.z);
    }

    /**
     * Sets the buffer playing in this SoundSource.
     * @param bufferId the id of the sound buffer to play
     */
    public void setBuffer(int bufferId){
        stop();
        alSourcei(id, AL_BUFFER, bufferId);
    }

    /**
     * Gets the id of the sound buffer that is currently playing.
     * @return the id of the sound buffer
     */
    public int getBuffer() {
        return alGetSourcei(id, AL_BUFFER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cleanup(){
        stop();
        alDeleteSources(id);
    }
}