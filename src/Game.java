import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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
        panel.start();
    }
}

enum GameState { START, RUNNING, LEVEL_COMPLETE, GAME_OVER }

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final Timer timer = new Timer(16, this); // ~60 FPS
    private GameState state = GameState.START;
    private boolean enterPressed = false;

    // === NOUVEAU : joueur ===
    private final Player player = new Player(380, 500);

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
                // === NOUVEAU : mise à jour du joueur ===
                player.update();
                break;
            case LEVEL_COMPLETE:
                break;
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
                // === NOUVEAU : dessin du joueur ===
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

    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER) enterPressed = true;

        // === NOUVEAU : contrôles quand le jeu tourne ===
        if (state == GameState.RUNNING) {
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
// Classe Player simple
// ===================
class Player {
    private int x, y, width, height, speed;
    private boolean up, down, left, right;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
        this.width = 40;
        this.height = 40;
        this.speed = 5;
    }

    public void update() {
        if (up) y -= speed;
        if (down) y += speed;
        if (left) x -= speed;
        if (right) x += speed;

        // Limites écran 800x600
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > 800) x = 800 - width;
        if (y + height > 600) y = 600 - height;
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        g.fillRect(x, y, width, height);
    }

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
