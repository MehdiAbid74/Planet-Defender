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
                // TODO: update player, bullets, enemies, collisions, HUD
                break;
            case LEVEL_COMPLETE:
                // TODO: transition to next level
                break;
            case GAME_OVER:
                // TODO: R to restart
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
                // TODO: draw world, player, enemies, bullets
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
        if (e.getKeyCode() == KeyEvent.VK_ENTER) enterPressed = true;
        // TODO: handle arrows/WASD/space in RUNNING state
    }
    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}
