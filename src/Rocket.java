import java.awt.*;

public class Rocket {
    private int x, y;
    private int vy = 6; // falling speed
    private int w = 6, h = 16;

    public Rocket(int x, int y) { this.x = x; this.y = y; }

    public void update() { y += vy; }

    public void draw(Graphics g) {
        if (Assets.ROCKET != null) g.drawImage(Assets.ROCKET, x - 8, y, 16, 24, null);
        else { g.setColor(Color.ORANGE); g.fillRect(x, y, w, h); }
    }

    public Rectangle getBounds() { return new Rectangle(x, y, w, h); }
    public boolean isOnScreen(int W, int H) { return y <= H && y + h >= 0 && x >= 0 && x <= W; }
}
