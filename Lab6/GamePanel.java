import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel {

    private BufferedImage character;
    private BufferedImage[] customizations;

    private int x = 300;
    private int y = 200;

    private int selectedCustomization = -1;
    
    private Set<Integer> pressedKeys = new HashSet<>();

    public GamePanel() {
        try {
            character = ImageIO.read(new File("player.png"));

            customizations = new BufferedImage[6];
            customizations[0] = ImageIO.read(new File("player.png"));
            customizations[1] = ImageIO.read(new File("hat.png"));
            customizations[2] = ImageIO.read(new File("glasses.png"));
            customizations[3] = ImageIO.read(new File("armor.png"));
            customizations[4] = ImageIO.read(new File("sword.png"));
            customizations[5] = ImageIO.read(new File("cape.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }

        setFocusable(true);
        requestFocusInWindow();

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }

            public void keyReleased(java.awt.event.KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });
        
        Timer gameLoop = new Timer(50, e -> update());
        gameLoop.start();
    }
    
    private void update() {
        int speed = 5;
        
        if (pressedKeys.contains(java.awt.event.KeyEvent.VK_W)) y -= speed;
        if (pressedKeys.contains(java.awt.event.KeyEvent.VK_S)) y += speed;
        if (pressedKeys.contains(java.awt.event.KeyEvent.VK_A)) x -= speed;
        if (pressedKeys.contains(java.awt.event.KeyEvent.VK_D)) x += speed;
        
        repaint();
    }

    public void setCustomization(int index) {
        selectedCustomization = index;
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (selectedCustomization >= 0)
            g.drawImage(customizations[selectedCustomization], x, y, null);
        else if (character != null)
            g.drawImage(character, x, y, null);
    }
}
