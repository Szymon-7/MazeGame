import java.util.List;
import java.util.Random;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioManager {

    private MediaPlayer backgroundAudio;
    private List<AudioClip> coinPickupSounds;
    private List<AudioClip> wallBreakSounds;
    private List<AudioClip> footstepSounds;
    private int lastFootstep = -1;
    private List<AudioClip> ladderSounds;
    private List<AudioClip> shopSounds;
    private AudioClip startSound;
    private Random random = new Random();

    public AudioManager() {
        backgroundAudio = new MediaPlayer(new Media(getClass().getResource("/audio/game/background.wav").toExternalForm()));
        backgroundAudio.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundAudio.setVolume(0.1);

        startSound = new AudioClip(getClass().getResource("/audio/game/start.wav").toExternalForm());

        coinPickupSounds = List.of(
            new AudioClip(getClass().getResource("/audio/coin/coinPickup1.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/coin/coinPickup2.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/coin/coinPickup3.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/coin/coinPickup4.wav").toExternalForm())
        );

        wallBreakSounds = List.of(
            new AudioClip(getClass().getResource("/audio/maze/wallBreak1.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/maze/wallBreak2.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/maze/wallBreak3.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/maze/wallBreak4.wav").toExternalForm())
        );

        footstepSounds = List.of(
            new AudioClip(getClass().getResource("/audio/player/footstep1.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/player/footstep2.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/player/footstep3.wav").toExternalForm())
        );

        ladderSounds = List.of(
            new AudioClip(getClass().getResource("/audio/maze/ladder1.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/maze/ladder2.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/maze/ladder3.wav").toExternalForm())
        );

        shopSounds = List.of(
            new AudioClip(getClass().getResource("/audio/shop/shopErr.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/shop/shopBuy1.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/shop/shopBuy2.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/shop/shopBuy3.wav").toExternalForm())
        );
    }

    public void playBackground() { backgroundAudio.play(); }
    public void stopBackground() { backgroundAudio.stop(); }
    public void pauseBackground() { backgroundAudio.pause(); }
    public void playStart() { startSound.play(); }
    public void playCoinPickup() { coinPickupSounds.get(random.nextInt(coinPickupSounds.size())).play(); }
    public void playWallBreak() { wallBreakSounds.get(random.nextInt(wallBreakSounds.size())).play(); }
    public void playLadder() { ladderSounds.get(random.nextInt(ladderSounds.size())).play(); }
    public void playShopBuy() { shopSounds.get(random.nextInt(ladderSounds.size()) + 1).play(); }
    public void playShopErr() { shopSounds.get(0).play(); }

    // Random but not the last one played
    public void playFootstep() {
        int index;

        do {
            index = random.nextInt(footstepSounds.size());
        } while (index == lastFootstep);

        lastFootstep = index;
        footstepSounds.get(index).play();
    }
}
