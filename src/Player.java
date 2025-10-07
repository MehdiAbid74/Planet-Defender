import java.awt.*;
import java.awt.event.KeyEvent;

public class Player {
    private int x, y, width, height, speed;
    private boolean up, down, left, right;

    public Player(int startX, int startY) {
        this.x = startX; this.y = startY;
        this.width = 80; this.height = 80;
        this.speed = 5;
    }

    public void update() {
        if (up) y -= speed;
        if (down) y += speed;
        if (left) x -= speed;
        if (right) x += speed;

        // screen limits (800x600 fixed canvas)
        if (x < 0) x = 0;
        if (y < 0) y = 0;
        if (x + width > 800) x = 800 - width;
        if (y + height > 600) y = 600 - height;
    }

    public void draw(Graphics g) { g.drawImage(Assets.PLAYER, x, y, width, height, null); }

    public int getY(){ return y; }
    public int getCenterX(){ return x + width/2; }
    public Rectangle getBounds(){ return new Rectangle(x, y, width, height); }

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
