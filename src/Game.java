import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import java.io.InputStream;
import java.io.BufferedInputStream; // <— pour le son
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sound.sampled.*;

public class Game extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(Game::new);
    }

    public Game() {
        setTitle("Planet Defender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        Assets.load();   // charge Gun.png

        panel.start();
    }
}

enum GameState { START, RUNNING, LEVEL_COMPLETE, GAME_OVER }

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final Timer timer = new Timer(16, this); // ~60 FPS
    private GameState state = GameState.START;
    private boolean enterPressed = false;

    private final Player player = new Player(380, 500);

    // --- bullets ---
    private final List<Bullet> bullets = new ArrayList<>();
    private long lastShotTimeMs = 0;
    private final long shotCooldownMs = 180; // délai entre 2 tirs

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
    }

    public void start() { timer.start(); }

    @Override public void actionPerformed(ActionEvent e) {
        updateGame();
        repaint();
    }

    private void updateGame() {
        switch (state) {
            case START:
                if (enterPressed) state = GameState.RUNNING;
                break;
            case RUNNING:
                player.update();

                // update bullets + nettoyage
                Iterator<Bullet> it = bullets.iterator();
                while (it.hasNext()) {
                    Bullet b = it.next();
                    b.update();
                    if (!b.isOnScreen(getWidth(), getHeight())) it.remove();
                }
                break;
            case LEVEL_COMPLETE:
            case GAME_OVER:
                break;
        }
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);
        switch (state) {
            case START:
                drawCentered(g, "PRESS ENTER TO START");
                break;
            case RUNNING:
                g.drawString("HUD: score=0 lives=3 level=1", 10, 20);
                // draw bullets
                for (Bullet b : bullets) b.draw(g);
                // draw player
                player.draw(g);
                break;
            case LEVEL_COMPLETE:
                drawCentered(g, "LEVEL COMPLETE!");
                break;
            case GAME_OVER:
                drawCentered(g, "GAME OVER - PRESS R");
                break;
        }
    }

    private void drawCentered(Graphics g, String text) {
        FontMetrics fm = g.getFontMetrics();
        int x = (getWidth() - fm.stringWidth(text)) / 2;
        int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, x, y);
    }

    private void tryShoot() {
        long now = System.currentTimeMillis();
        if (now - lastShotTimeMs < shotCooldownMs) return; // cooldown
        lastShotTimeMs = now;

        // bullet part du haut-centre du joueur
        int bx = player.getCenterX() - 2;   // 4px de large
        int by = player.getY() - 10;        // juste au-dessus
        bullets.add(new Bullet(bx, by, -10)); // vers le haut

        // --- SON DU TIR ---
        Assets.playSound("/assets/shoot.wav");
        // System.out.println("pew"); // debug si besoin
    }

    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER) enterPressed = true;

        if (state == GameState.RUNNING) {
            if (code == KeyEvent.VK_SPACE) tryShoot();
            player.keyPressed(code);
        }
    }

    @Override public void keyReleased(KeyEvent e) {
        if (state == GameState.RUNNING) {
            player.keyReleased(e.getKeyCode());
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
}

// ===================
// Player
// ===================
class Player {
    private int x, y, width, height, speed;
    private boolean up, down, left, right;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.width = 80;   // ajuste selon Gun.png
        this.height = 80;
        this.speed = 5;
    }

    public void update() {
        if (up) y -= speed;
        if (down) y += speed;
        if (left) x -= speed;
        if (right) x += speed;

        // limites écran 800x600
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > 800) x = 800 - width;
        if (y + height > 600) y = 600 - height;
    }

    public void draw(Graphics g) {
        g.drawImage(Assets.PLAYER, x, y, width, height, null);
    }

    // getters utiles
    public int getX(){ return x; }
    public int getY(){ return y; }
    public int getCenterX(){ return x + width/2; }

    public void keyPressed(int keyCode) {
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP)    up = true;
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)  down = true;
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT)  left = true;
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) right = true;
    }
    public void keyReleased(int keyCode) {
        if (keyCode == KeyEvent.VK_W || keyCode == KeyEvent.VK_UP)    up = false;
        if (keyCode == KeyEvent.VK_S || keyCode == KeyEvent.VK_DOWN)  down = false;
        if (keyCode == KeyEvent.VK_A || keyCode == KeyEvent.VK_LEFT)  left = false;
        if (keyCode == KeyEvent.VK_D || keyCode == KeyEvent.VK_RIGHT) right = false;
    }
}

// ===================
// Bullet (tir du joueur)
// ===================
class Bullet {
    private int x, y;
    private final int speedY;       // négatif = vers le haut
    private final int width = 4;
    private final int height = 12;

    public Bullet(int x, int y, int speedY) {
        this.x = x;
        this.y = y;
        this.speedY = speedY;
    }

    public void update() { y += speedY; }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect(x, y, width, height);
    }

    public boolean isOnScreen(int w, int h) {
        return y + height >= 0 && y <= h && x + width >= 0 && x <= w;
    }

    // pour collisions plus tard
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
}

// ===================
// Assets
// ===================
class Assets {
    public static Image PLAYER;

    public static void load() {
        try (InputStream in = Assets.class.getResourceAsStream("/assets/Gun.png")) {
            if (in == null) throw new IllegalStateException("Gun.png introuvable");
            BufferedImage img = ImageIO.read(in);
            PLAYER = img;
        } catch (Exception e) {
            System.err.println("Erreur image: " + e.getMessage());
            PLAYER = new BufferedImage(40, 40, BufferedImage.TYPE_INT_ARGB);
            Graphics g = ((BufferedImage) PLAYER).getGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, 40, 40);
            g.dispose();
        }
    }

    // --- lecteur de son robuste (WAV PCM) ---
    public static void playSound(String path) {
        new Thread(() -> {
            try (InputStream raw = Assets.class.getResourceAsStream(path)) {
                if (raw == null) throw new IllegalStateException("Son introuvable: " + path);
                try (BufferedInputStream buf = new BufferedInputStream(raw);
                     AudioInputStream audioIn = AudioSystem.getAudioInputStream(buf)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioIn);
                    clip.start(); // joue une fois
                }
            } catch (Exception e) {
                System.err.println("Erreur son: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}
