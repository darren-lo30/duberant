package duber.game;

import java.io.IOException;

import duber.engine.audio.SoundBuffer;
import duber.engine.audio.SoundManager;
import duber.engine.audio.SoundSource;

/**
 * Stores all the SoundData used in the game.
 * @author Darren Lo
 * @version 1.0
 */
public class SoundData {
    
    /**
     * Stores all the sound file paths.
     */
    public enum SoundFile {
        RUNNING ("/sounds/running.ogg"),
        PISTOL  ("/sounds/pistol.ogg"),
        RIFLE   ("/sounds/rifle.ogg"),
        LMG     ("/sounds/lmg.ogg");

        /**
         * The path to the sound file.
         */
        private String filePath;

        /**
         * Constructs a new SoundFile enum with a given path.
         * @param filePath the path to the sound file
         */
        private SoundFile(String filePath) {
            this.filePath = filePath;
        }

        /**
         * Gets the file path for the SoundFile.
         * @return the file path
         */
        public String getFilePath() {
            return filePath;
        }

        /**
         * Gets the SoundBuffer name associated with it.
         * @return the name of the SoundBuffer
         */
        public String getBufferName() {
            return this.name();
        }

        /**
         * Gets the SoundBuffer id associated with it.
         * @param soundManager the SoundManager that stores all the sounds
         * @return the id of the SoundBuffer
         */
        public int getBufferId(SoundManager soundManager) {
            return soundManager.getSoundBufferId(this.getBufferName());
        }
    }

    /**
     * Loads a sound into the SoundManager.
     * @param soundManager the SoundManager that stores the sounds
     * @throws IOException if the sound could not be loaded
     */
    public static void loadSounds(SoundManager soundManager) throws IOException {
        for(SoundFile soundFile : SoundFile.values()) {
            SoundBuffer soundBuffer = new SoundBuffer(soundFile.getFilePath());
            soundManager.addSoundBuffer(soundFile.getBufferName(), soundBuffer);
        }
    }

    /**
     * Sets a SoundSource to a given SoundFile.
     * @param soundManager the SoundManager that stores the sounds
     * @param soundSource the SoundSource that plays the sounds
     * @param soundFile the SoundFile to load
     */
    public static void setSourceSound(SoundManager soundManager, SoundSource soundSource, SoundFile soundFile) {
        int newSoundBuffer = soundFile.getBufferId(soundManager);
        soundSource.setBuffer(newSoundBuffer);
    }     
}
