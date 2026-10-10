import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import javax.imageio.ImageIO;

final class Assets {
    private static final String IMAGE_DIR = "/assets/images/";

    private Assets() {}

    static BufferedImage image(String fileName) {
        String path = IMAGE_DIR + fileName;
        URL url = Assets.class.getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing image resource: " + path);
        }
        try {
            return ImageIO.read(url);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read image: " + path, e);
        }
    }
}
