import java.awt.*;

public class Enemy {
    private int x, y, width = 64, height = 48;
    private int speedY;
    private double driftPhase;

    private Enemy(int x, int y, int speedY) {
        this.x = x; this.y = y; this.speedY = speedY;
        this.driftPhase = Math.random() * Math.PI * 2; // small sine drift
    }

    public static Enemy randomTop(int screenW) {
        int x = (int)(Math.random() * (screenW - 70)) + 5;
        int speed = 2 + (int)(Math.random() * 3); // 2..4 px/frame
        return new Enemy(x, -50, speed);
    }

    public void update() {
        y += speedY;
        // light horizontal wave so they "move"
        x += (int)(Math.sin((y + driftPhase) * 0.04) * 2);
    }

    public void draw(Graphics g) {
        g.drawImage(Assets.ENEMY, x, y, width, height, null);
    }

    public int getY() { return y; }
    public Rectangle getBounds() { return new Rectangle(x, y, width, height); }
}
