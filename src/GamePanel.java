import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final Timer timer = new Timer(16, this); // ~60 FPS
    private GameState state = GameState.START;
    private boolean enterPressed = false;

    private final Player player = new Player(380, 500);

    // --- Level and Boss Fight ---
    private int currentLevel = 1;          // 1 = normal enemies, 2 = boss fight
    private Boss boss = null;              // created when level 2 starts
    private final List<Rocket> rockets = new ArrayList<>(); // boss projectiles

    // bullets (player)
    private final List<Bullet> bullets = new ArrayList<>();
    private long lastShotTimeMs = 0;
    private final long shotCooldownMs = 180;

    // enemies (level 1)
    private final List<Enemy> enemies = new ArrayList<>();
    private long lastSpawnMs = 0;
    private long spawnEveryMs = 800; // spawn interval (ms)

    // game rules
    private int lives = 3;
    private int kills = 0;
    private final int targetKills = 15; // after 15 kills -> start boss fight

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.black);
        setFocusable(true);
        addKeyListener(this);
    }

    public void start() { timer.start(); }

    @Override public void actionPerformed(ActionEvent e) { updateGame(); repaint(); }

    private void updateGame() {
        switch (state) {
            case START:
                if (enterPressed) state = GameState.RUNNING;
                break;

            case RUNNING:
                long now = System.currentTimeMillis();

                // update player movement
                player.update();

                // --- LEVEL 1: spawn and update normal enemies ---
                if (currentLevel == 1) {
                    // spawn enemies on a fixed cadence
                    if (now - lastSpawnMs >= spawnEveryMs) {
                        lastSpawnMs = now;
                        enemies.add(Enemy.randomTop(getWidth()));
                    }

                    // move enemies & handle collisions (bottom leak / player hit)
                    Iterator<Enemy> eit = enemies.iterator();
                    while (eit.hasNext()) {
                        Enemy en = eit.next();
                        en.update();

                        // enemy passed bottom -> lose life
                        if (en.getY() > getHeight()) {
                            lives--;
                            eit.remove();
                            if (lives <= 0) { state = GameState.GAME_OVER; }
                            continue;
                        }
                        // enemy hits player
                        if (en.getBounds().intersects(player.getBounds())) {
                            lives--;
                            eit.remove();
                            if (lives <= 0) { state = GameState.GAME_OVER; }
                        }
                    }
                }

                // --- player bullets update + off-screen cleanup ---
                Iterator<Bullet> bit = bullets.iterator();
                while (bit.hasNext()) {
                    Bullet b = bit.next();
                    b.update();
                    if (!b.isOnScreen(getWidth(), getHeight())) bit.remove();
                }

                // --- bullets collisions ---
                // Level 1: bullets vs enemies    |   Level 2: bullets vs boss
                bit = bullets.iterator();
                while (bit.hasNext()) {
                    Bullet b = bit.next();
                    Rectangle br = b.getBounds();
                    boolean hit = false;

                    if (currentLevel == 1) {
                        Iterator<Enemy> eit = enemies.iterator();
                        while (eit.hasNext()) {
                            Enemy en = eit.next();
                            if (br.intersects(en.getBounds())) {
                                eit.remove();
                                hit = true;
                                kills++;
                                break;
                            }
                        }
                    } else if (currentLevel == 2 && boss != null) {
                        if (br.intersects(boss.getBounds())) {
                            boss.hit(1); // 1 damage per bullet
                            hit = true;
                        }
                    }

                    if (hit) bit.remove();
                }

                // --- check transition from Level 1 -> Level 2 (Boss Fight) ---
                if (currentLevel == 1 && kills >= targetKills) {
                    startBossFight();
                }

                // --- LEVEL 2: update boss + rockets + collisions ---
                if (currentLevel == 2 && boss != null) {
                    // boss movement + shooting cadence
                    boss.update(getWidth());
                    if (boss.canShoot()) {
                        rockets.add(new Rocket(boss.getCenterX(), boss.getBottomY()));
                        Assets.playSound("/assets/shoot.wav"); // optional: different sound for boss
                    }

                    // update rockets + collision with player
                    Iterator<Rocket> rit = rockets.iterator();
                    while (rit.hasNext()) {
                        Rocket r = rit.next();
                        r.update();
                        if (!r.isOnScreen(getWidth(), getHeight())) { rit.remove(); continue; }

                        if (r.getBounds().intersects(player.getBounds())) {
                            lives--;
                            rit.remove();
                            if (lives <= 0) { state = GameState.GAME_OVER; }
                        }
                    }

                    // boss defeated -> full victory
                    if (boss.isDead()) {
                        state = GameState.LEVEL_COMPLETE;
                    }
                }

                break;

            case LEVEL_COMPLETE:
                // wait for ENTER to restart
                break;

            case GAME_OVER:
                // wait for R to restart
                break;
        }
    }

    // start Level 2 (Boss Fight)
    private void startBossFight() {
        currentLevel = 2;
        enemies.clear();
        bullets.clear();
        rockets.clear();
        boss = new Boss(getWidth() / 2 - 100, -120); // enter from the top
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.white);

        switch (state) {
            case START:
                drawCentered(g, "PRESS ENTER TO START");
                g.drawString("Move: WASD/Arrows  |  Shoot: SPACE", 10, 40);
                break;

            case RUNNING:
                // HUD
                g.drawString("HUD: kills=" + kills + "/" + targetKills + "   lives=" + lives, 10, 20);
                if (currentLevel == 2) g.drawString("BOSS FIGHT!", 10, 36);

                // draw world
                for (Enemy en : enemies) en.draw(g); // only active in level 1
                for (Bullet b : bullets) b.draw(g);
                player.draw(g);

                // draw boss + rockets if level 2
                if (currentLevel == 2 && boss != null) {
                    boss.draw(g);
                    for (Rocket r : rockets) r.draw(g);
                }
                break;

            case LEVEL_COMPLETE:
                drawCentered(g, "YOU WIN!  PRESS ENTER TO PLAY AGAIN");
                break;

            case GAME_OVER:
                drawCentered(g, "GAME OVER  -  PRESS R TO RESTART");
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
        if (now - lastShotTimeMs < shotCooldownMs) return;
        lastShotTimeMs = now;

        int bx = player.getCenterX() - 2;
        int by = player.getY() - 10;
        bullets.add(new Bullet(bx, by, -10));

        Assets.playSound("/assets/shoot.wav");
    }

    @Override public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_ENTER) {
            enterPressed = true;
            if (state == GameState.LEVEL_COMPLETE) resetGame();
        }

        if (state == GameState.RUNNING) {
            if (code == KeyEvent.VK_SPACE) tryShoot();
            player.keyPressed(code);
        }

        if (state == GameState.GAME_OVER && code == KeyEvent.VK_R) {
            resetGame();
        }
    }

    private void resetGame() {
        bullets.clear();
        enemies.clear();
        rockets.clear();
        boss = null;
        lives = 3;
        kills = 0;
        lastSpawnMs = 0;

        // reset level back to 1 defaults
        currentLevel = 1;
        spawnEveryMs = 800;

        state = GameState.START;
        enterPressed = false;
    }

    @Override public void keyReleased(KeyEvent e) {
        if (state == GameState.RUNNING) player.keyReleased(e.getKeyCode());
    }
    @Override public void keyTyped(KeyEvent e) {}
}
