import java.awt.*;

public class Boss {
    private int x, y, w = 200, h = 100;
    private int vx = 3;            // horizontal movement speed
    private int hp = 25;           // boss hit points
    private long lastShotMs = 0;   // last time the boss shot
    private long shotCooldownMs = 900; // shoot every 900 ms

    public Boss(int startX, int startY) {
        this.x = startX; this.y = startY;
    }

    public void update(int panelW) {
        // move down until a certain height, then oscillate horizontally
        if (y < 60) y += 2;
        else {
            x += vx;
            if (x < 10 || x + w > panelW - 10) vx = -vx;
        }
    }

    public boolean canShoot() {
        long now = System.currentTimeMillis();
        if (now - lastShotMs >= shotCooldownMs) {
            lastShotMs = now;
            return true;
        }
        return false;
    }

    public void draw(Graphics g) {
        if (Assets.BOSS != null) g.drawImage(Assets.BOSS, x, y, w, h, null);
        else {
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, w, h);
        }
        // HP bar
        g.setColor(Color.RED);
        g.fillRect(10, 40, hp * 8, 12);
        g.setColor(Color.WHITE);
        g.drawRect(10, 40, 25 * 8, 12);
        g.drawString("BOSS HP: " + hp, 10, 66);
    }

    public Rectangle getBounds() { return new Rectangle(x, y, w, h); }
    public int getCenterX() { return x + w / 2; }
    public int getBottomY() { return y + h; }

    public void hit(int dmg) { hp = Math.max(0, hp - dmg); }
    public boolean isDead() { return hp <= 0; }
}
