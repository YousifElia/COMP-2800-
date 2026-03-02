import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class GamePanel extends JPanel {

    private BufferedImage character;
    private BufferedImage[] customizations;

    private int x = 300;
    private int y = 200;

    private int selectedCustomization = -1;

    public GamePanel() {
        try {
            character = ImageIO.read(new File("character.png"));

            customizations = new BufferedImage[5];
            customizations[0] = ImageIO.read(new File("hat.png"));
            customizations[1] = ImageIO.read(new File("glasses.png"));
            customizations[2] = ImageIO.read(new File("shirt.png"));
            customizations[3] = ImageIO.read(new File("sword.png"));
            customizations[4] = ImageIO.read(new File("cape.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {

                int key = e.getKeyCode();

                if (key == java.awt.event.KeyEvent.VK_W) y -= 10;
                if (key == java.awt.event.KeyEvent.VK_S) y += 10;
                if (key == java.awt.event.KeyEvent.VK_A) x -= 10;
                if (key == java.awt.event.KeyEvent.VK_D) x += 10;

                repaint();
            }
        });
    }

    public void setCustomization(int index) {
        selectedCustomization = index;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (character != null)
            g.drawImage(character, x, y, null);

        if (selectedCustomization >= 0)
            g.drawImage(customizations[selectedCustomization], x, y, null);
    }
}
