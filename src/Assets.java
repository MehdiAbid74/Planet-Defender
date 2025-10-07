import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.InputStream;

public class Assets {
    public static Image PLAYER;
    public static Image ENEMY;

    public static void load() {
        // Player
        try (InputStream in = Assets.class.getResourceAsStream("/assets/Gun.png")) {
            if (in == null) throw new IllegalStateException("Gun.png not found");
            PLAYER = ImageIO.read(in);
        } catch (Exception e) {
            System.err.println("Error loading Gun.png: " + e.getMessage());
            PLAYER = fallback(40, 40, Color.WHITE);
        }

        // Enemy
        try (InputStream in = Assets.class.getResourceAsStream("/assets/Enemies.png")) {
            if (in == null) throw new IllegalStateException("Enemies.png not found");
            ENEMY = ImageIO.read(in);
        } catch (Exception e) {
            System.err.println("Error loading Enemies.png: " + e.getMessage());
            ENEMY = fallback(50, 35, Color.RED);
        }
    }

    private static BufferedImage fallback(int w, int h, Color c) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = img.getGraphics();
        g.setColor(c); g.fillRect(0, 0, w, h); g.dispose();
        return img;
    }

    public static void playSound(String path) {
        new Thread(() -> {
            try (InputStream raw = Assets.class.getResourceAsStream(path)) {
                if (raw == null) throw new IllegalStateException("Sound not found: " + path);
                try (BufferedInputStream buf = new BufferedInputStream(raw);
                     AudioInputStream audioIn = AudioSystem.getAudioInputStream(buf)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start();
                }
            } catch (Exception e) {
                System.err.println("Sound error: " + e.getMessage());
            }
        }).start();
    }
}
