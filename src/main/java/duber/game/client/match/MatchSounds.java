package duber.game.client.match;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;

import duber.engine.audio.SoundManager;
import duber.engine.audio.SoundSource;
import duber.engine.entities.components.Transform;
import duber.game.SoundData;
import duber.game.SoundData.SoundFile;
import duber.game.gameobjects.Gun;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.GunType;
import duber.game.gameobjects.Player.PlayerData.MovementState;

/**
 * A class that manages the sounds during a match.
 * @author Darren Lo
 * @version 1.0
 */
public class MatchSounds  {
    /** The match. */
    private Match match;

    /** The sound manager used to play the audio. */
    private SoundManager soundManager;

    /** All the sound sources that were added during the match */
    List<String> addedSoundSources = new ArrayList<>();

    /**
     * Constructs MatchSounds.
     * @param match the match
     * @param soundManager the sound manager to play sounds
     */
    public MatchSounds(Match match, SoundManager soundManager) {
        this.match = match;
        this.soundManager = soundManager;
    }

    /**
     * Adds all the Players in the match.
     */
    public void addMatchPlayers() {
        for(Player player : match.getPlayers()) {
            SoundSource playerSoundSource = createMatchSoundSource(true, false);
            addSoundSource(getPlayerSoundSourceName(player), playerSoundSource);
            
            SoundData.setSourceSound(soundManager, playerSoundSource, SoundFile.RUNNING);
    
            SoundSource gunSoundSource = createMatchSoundSource(false, false);
            addSoundSource(getGunSoundSourceName(player), gunSoundSource);
        }
    }

    /**
     * Configures the match sounds settings.
     */
    public void configureSettings() {
        soundManager.setAttenuationModel(AL11.AL_LINEAR_DISTANCE);    
    }

    /**
     * Creates a sound source for the match.
     * @param looping if the sound source loops
     * @param relative if the sound source is relative
     * @return the created SoundSource
     */
    private SoundSource createMatchSoundSource(boolean looping, boolean relative) {
        SoundSource soundSource = new SoundSource(looping, relative);

        AL10.alSourcef(soundSource.getId(), AL10.AL_REFERENCE_DISTANCE, 40.0f);
        AL10.alSourcef(soundSource.getId(), AL10.AL_MAX_DISTANCE, 200.0f);

        return soundSource;
    }

    /**
     * Updates the location of the SoundSources.
     */
    public void updateSoundSources() {
        //Update main player listener
        Transform mainPlayerTransform = match.getMainPlayer().getComponent(Transform.class);
        soundManager.updateListenerPosition(mainPlayerTransform);
        
        //Update sound sources for each user
        for(Player player : match.getPlayers()) {
            SoundSource playerSoundSource = getPlayerSoundSource(player);
            SoundSource gunSoundSource = getGunSoundSource(player);

            //Set sound source position
            Vector3f playerPosition = player.getComponent(Transform.class).getPosition();
            playerSoundSource.setPosition(playerPosition);
            gunSoundSource.setPosition(playerPosition);
        }
    }

    /**
     * Plays movement sounds for the Players.
     */
    public void playMovementSounds() {
        for(Player player : match.getPlayers()) {
            SoundSource playerSoundSource = getPlayerSoundSource(player);
            
            MovementState playerMovement = player.getPlayerData().getPlayerMovement();
            if (playerMovement != MovementState.RUNNING) {
                playerSoundSource.pause();
            } else {
                if (!playerSoundSource.isPlaying()) {
                    playerSoundSource.play();
                }
            }
        }
    }

    /**
     * Plays the gun shot sound for a Player.
     * @param player the shooting Player
     */
    public void playGunSounds(Player player) {
        playGunSoundEffect(getGunSoundSource(player), player.getWeaponsInventory().getEquippedGun());
    }

    /**
     * Plays the sound effect for a gun.
     * @param gunSoundSource the sound source of the gun
     * @param gun the Gun that is shooting
     */
    public void playGunSoundEffect(SoundSource gunSoundSource, Gun gun) {
        if (GunType.RIFLE.isGunType(gun)) {
            SoundData.setSourceSound(soundManager, gunSoundSource, SoundFile.RIFLE);
        } else if (GunType.PISTOL.isGunType(gun)) {
            SoundData.setSourceSound(soundManager, gunSoundSource, SoundFile.PISTOL);
        } else if (GunType.LMG.isGunType(gun)) {
            SoundData.setSourceSound(soundManager, gunSoundSource, SoundFile.LMG);
        }

        gunSoundSource.play();
    }

    /**
     * Adds a sound source to the match.
     * @param name the name
     * @param soundSource the SoundSource
     */
    public void addSoundSource(String name, SoundSource soundSource) {
        addedSoundSources.add(name);
        soundManager.addSoundSource(name, soundSource);
    }

    /**
     * Clears the match sounds.
     */
    public void clear() {
        for(String soundSourceName : addedSoundSources) {
            soundManager.removeSoundSource(soundSourceName);
        }
    }

    /**
     * Gets the sound source name for a Player.
     * @param player the Player.
     * @return the Player's SoundSource name
     */
    public String getPlayerSoundSourceName(Player player) {
        return "Player" + player.getId();
    }

    /**
     * Gets the SoundSource for a Player.
     * @param player the Player
     * @return the SoundSource for the Player
     */
    public SoundSource getPlayerSoundSource(Player player) {
        return soundManager.getSoundSource(getPlayerSoundSourceName(player));
    }

    /**
     * Gets the SoundSource name for a Player's gun.
     * @param player the Player
     * @return the gun's SoundSource name
     */
    public String getGunSoundSourceName(Player player) {
        return "PlayerGun" + player.getId();
    }

    /**
     * Gets hte SoundSource for a Player's gun.
     * @param player the Player
     * @return the SoundSource for the Player's gun
     */
    public SoundSource getGunSoundSource(Player player) {
        return soundManager.getSoundSource(getGunSoundSourceName(player));
    }
}