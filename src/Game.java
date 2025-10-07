import javax.swing.*;

public class Game extends JFrame {
    public static void main(String[] args) { SwingUtilities.invokeLater(Game::new); }

    public Game() {
        setTitle("Planet Defender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        Assets.load();      // charge Gun.png + Enemies.png
        panel.start();
    }
}
