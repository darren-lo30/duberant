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
import duber.game.SoundData.SoundFiles;
import duber.game.gameobjects.Gun;
import duber.game.gameobjects.Player;
import duber.game.gameobjects.GunBuilder.GunTypes;
import duber.game.gameobjects.Player.MovementState;

public class MatchSounds  {
    private Match match;
    private SoundManager soundManager;

    List<String> addedSoundSources = new ArrayList<>();

    public MatchSounds(Match match, SoundManager soundManager) {
        this.match = match;
        this.soundManager = soundManager;
    }

    public void addMatchPlayers() {
        for(Player player : match.getPlayers()) {
            addPlayer(player);
        }
    }

    public void configureSettings() {
        soundManager.setAttenuationModel(AL11.AL_LINEAR_DISTANCE);    
    }
    
    private void addPlayer(Player player) {        
        SoundSource playerSoundSource = createMatchSoundSource(true, false);
        addSoundSource(getPlayerSoundSourceName(player), playerSoundSource);
        
        SoundData.setSourceSound(soundManager, playerSoundSource, SoundFiles.RUNNING);

        SoundSource gunSoundSource = createMatchSoundSource(false, false);
        addSoundSource(getGunSoundSourceName(player), gunSoundSource);
    }

    private SoundSource createMatchSoundSource(boolean looping, boolean relative) {
        SoundSource soundSource = new SoundSource(looping, relative);

        AL10.alSourcef(soundSource.getId(), AL10.AL_REFERENCE_DISTANCE, 40.0f);
        AL10.alSourcef(soundSource.getId(), AL10.AL_MAX_DISTANCE, 200.0f);

        //AL10.alSourcef(soundSource.getId(), AL10.AL_MAX_DISTANCE, 200.0f);
        return soundSource;
    }

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

    public void playMovementSounds() {
        for(Player player : match.getPlayers()) {
            SoundSource playerSoundSource = getPlayerSoundSource(player);
            
            MovementState playerMovement = player.getPlayerData().getPlayerMovement();
            if(playerMovement != MovementState.RUNNING) {
                playerSoundSource.pause();
            } else {
                if(!playerSoundSource.isPlaying()) {
                    playerSoundSource.play();
                }
            }
        }
    }

    public void playGunSounds(Player player) {
        playGunSoundEffect(getGunSoundSource(player), player.getWeaponsInventory().getEquippedGun());
    }

    public void playGunSoundEffect(SoundSource gunSoundSource, Gun gun) {
        if(GunTypes.RIFLE.isGunType(gun)) {

        } else if(GunTypes.PISTOL.isGunType(gun)) {
            SoundData.setSourceSound(soundManager, gunSoundSource, SoundFiles.PISTOL);
        } else if(GunTypes.LMG.isGunType(gun)) {

        }

        gunSoundSource.play();
    }

    public void addSoundSource(String name, SoundSource soundSource) {
        addedSoundSources.add(name);
        soundManager.addSoundSource(name, soundSource);
    }

    public void clear() {
        for(String soundSourceName : addedSoundSources) {
            soundManager.removeSoundSource(soundSourceName);
        }
    }

    public String getPlayerSoundSourceName(Player player) {
        return "Player" + player.getId();
    }

    public SoundSource getPlayerSoundSource(Player player) {
        return soundManager.getSoundSource(getPlayerSoundSourceName(player));
    }

    public String getGunSoundSourceName(Player player) {
        return "PlayerGun" + player.getId();
    }

    public SoundSource getGunSoundSource(Player player) {
        return soundManager.getSoundSource(getGunSoundSourceName(player));
    }
}