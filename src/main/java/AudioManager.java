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
    private Random random = new Random();

    public AudioManager() {
        backgroundAudio = new MediaPlayer(new Media(getClass().getResource("/audio/background.wav").toExternalForm()));
        backgroundAudio.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundAudio.setVolume(0.025);

        coinPickupSounds = List.of(
            new AudioClip(getClass().getResource("/audio/coinPickup1.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/coinPickup2.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/coinPickup3.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/coinPickup4.wav").toExternalForm())
        );

        wallBreakSounds = List.of(
            new AudioClip(getClass().getResource("/audio/wallBreak1.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/wallBreak2.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/wallBreak3.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/wallBreak4.wav").toExternalForm())
        );

        footstepSounds = List.of(
            new AudioClip(getClass().getResource("/audio/footstep1.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/footstep2.wav").toExternalForm()),
            new AudioClip(getClass().getResource("/audio/footstep3.wav").toExternalForm())
        );
    }

    public void playBackground() { backgroundAudio.play(); }
    public void stopBackground() { backgroundAudio.stop(); }
    public void pauseBackground() { backgroundAudio.pause(); }
    public void playCoinPickup() { coinPickupSounds.get(random.nextInt(coinPickupSounds.size())).play(); }
    public void playWallBreak() { wallBreakSounds.get(random.nextInt(wallBreakSounds.size())).play(); }

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
