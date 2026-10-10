import java.net.URL;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

final class Sound {
    private static final String SOUND_DIR = "/assets/sounds/";
    private static final float VOLUME = 0.7f;

    private final Clip clip; 

    private Sound(Clip clip) {
        this.clip = clip;
    }

    static Sound load(String fileName) {
        URL url = Sound.class.getResource(SOUND_DIR + fileName);
        if (url == null) {
            System.err.println("Missing sound resource: " + fileName);
            return new Sound(null);
        }
        try (AudioInputStream in = AudioSystem.getAudioInputStream(url)) {
            Clip clip = AudioSystem.getClip();
            clip.open(in);
            setVolume(clip);
            return new Sound(clip);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load sound: " + fileName, e);
        }
    }

    private static void setVolume(Clip clip) {
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gain.setValue((float) (20 * Math.log10(VOLUME)));
        }
    }

    void play() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.start();
        }
    }

    void loop() {
        if (clip != null) {
            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }

    void stop() {
        if (clip != null) {
            clip.stop();
        }
    }
}