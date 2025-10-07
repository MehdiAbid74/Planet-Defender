import java.awt.*;

public class Bullet {
    private int x, y;
    private final int speedY;
    private final int width = 4;
    private final int height = 12;

    public Bullet(int x, int y, int speedY) { this.x = x; this.y = y; this.speedY = speedY; }
    public void update() { y += speedY; }
    public void draw(Graphics g) { g.setColor(Color.GREEN); g.fillRect(x, y, width, height); }
    public boolean isOnScreen(int w, int h) { return y + height >= 0 && y <= h && x + width >= 0 && x <= w; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
}
