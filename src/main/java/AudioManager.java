import java.util.List;
import java.util.Random;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioManager {

    private MediaPlayer backgroundAudio;
    private List<AudioClip> coinPickupSounds;
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
    }

    public void playBackground() { backgroundAudio.play(); }
    public void stopBackground() { backgroundAudio.stop(); }
    public void pauseBackground() { backgroundAudio.pause(); }
    public void playCoinPickup() { coinPickupSounds.get(random.nextInt(coinPickupSounds.size())).play(); }
}
