package duber.game.client.match;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

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
    
    private void addPlayer(Player player) {        
        SoundSource playerSoundSource = new SoundSource(true, false);
        addSoundSource(getPlayerSoundSourceName(player), playerSoundSource);

        SoundSource gunSoundSource = new SoundSource(false, false);
        addSoundSource(getGunSoundSourceName(player), gunSoundSource);
    }

    public void playSounds() {
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
            
            //Play moving sound effects
            MovementState playerMovement = player.getPlayerData().getPlayerMovement();
            if(playerMovement != MovementState.RUNNING) {
                playerSoundSource.stop();
            } else {
                SoundData.playLoopSound(soundManager, playerSoundSource, SoundFiles.RUNNING);
            }

            //Play gun sound effects
            if(player.getPlayerData().isShooting()) {
                playGunSoundEffect(gunSoundSource, player.getWeaponsInventory().getEquippedGun());
            }
        }
    }

    public void playGunSoundEffect(SoundSource gunSoundSource, Gun gun) {
        if(GunTypes.RIFLE.isGunType(gun)) {

        } else if(GunTypes.PISTOL.isGunType(gun)) {
            SoundData.playSound(soundManager, gunSoundSource, SoundFiles.PISTOL);
        } else if(GunTypes.LMG.isGunType(gun)) {

        }
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
        return "Player Gun" + player.getId();
    }

    public SoundSource getGunSoundSource(Player player) {
        return soundManager.getSoundSource(getGunSoundSourceName(player));
    }
}