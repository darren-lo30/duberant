package duber.game;

import java.io.IOException;

import duber.engine.audio.SoundBuffer;
import duber.engine.audio.SoundManager;
import duber.engine.audio.SoundSource;

public class SoundData {
    public enum SoundFiles {
        RUNNING ("/sounds/running.ogg"),
        PISTOL  ("/sounds/pistol.ogg");

        private String fileName;
        private SoundFiles(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public String getBufferName() {
            return this.name();
        }

        public int getBufferId(SoundManager soundManager) {
            return soundManager.getSoundBufferId(this.getBufferName());
        }
    }

    public static void loadSounds(SoundManager soundManager) throws IOException {
        for(SoundFiles soundFile : SoundFiles.values()) {
            addSound(soundFile.getBufferName(), soundFile.getFileName(), soundManager);
        }
    }

    public static void addSound(String bufferName, String fileName, SoundManager soundManager) throws IOException {
        SoundBuffer soundBuffer = new SoundBuffer(fileName);
        soundManager.addSoundBuffer(bufferName, soundBuffer);
    }
    
    public static void playSound(SoundManager soundManager, SoundSource soundSource, SoundFiles soundFile) {
        int newSoundBuffer = soundFile.getBufferId(soundManager);
        soundSource.setBuffer(newSoundBuffer);
        soundSource.play();
    }

    public static void playLoopSound(SoundManager soundManager, SoundSource soundSource, SoundFiles soundFile) {
        int newSoundBuffer = soundFile.getBufferId(soundManager);

        if(soundSource.getBuffer() != newSoundBuffer) {
            soundSource.setBuffer(newSoundBuffer);
        }
        
        if(!soundSource.isPlaying()) {
            soundSource.play();
        }
    }
}