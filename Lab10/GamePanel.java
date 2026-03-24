import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.*;

public class GamePanel extends JPanel {

    private static final int SPRITE_SIZE = 64;
    private static final int RENDER_SIZE = 96;

    private BufferedImage playerSheet;
    private BufferedImage armorSheet;

    private BufferedImage playerFrame;
    private BufferedImage armorFrame;

    private int armorColumns = 0;
    private int armorRows = 0;
    private int armorCount = 0;

    private int x = 300;
    private int y = 200;

    private Set<Integer> pressedKeys = new HashSet<>();

    public GamePanel() {

        try {
            // Load sprite sheets
            playerSheet = loadFirstExistingImage("player1.png", "player.png", "SpriteSheet.png");
            armorSheet = loadFirstExistingImage("armors.png", "armor.png");

            // Default player frame (top-left sprite)
            if (playerSheet != null
                    && playerSheet.getWidth() >= SPRITE_SIZE
                    && playerSheet.getHeight() >= SPRITE_SIZE) {
                playerFrame = playerSheet.getSubimage(0, 0, SPRITE_SIZE, SPRITE_SIZE);
            }

            if (armorSheet != null) {
                armorColumns = armorSheet.getWidth() / SPRITE_SIZE;
                armorRows = armorSheet.getHeight() / SPRITE_SIZE;
                armorCount = armorColumns * armorRows;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        setFocusable(true);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        // Game loop
        Timer timer = new Timer(60, e -> update());
        timer.start();
    }

    private BufferedImage loadFirstExistingImage(String... fileNames) throws IOException {
        for (String fileName : fileNames) {
            File file = new File(fileName);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        }
        throw new IOException("Could not find any image file: " + Arrays.toString(fileNames));
    }

    private void update() {

        int speed = 5;

        if (pressedKeys.contains(KeyEvent.VK_W)) y -= speed;
        if (pressedKeys.contains(KeyEvent.VK_S)) y += speed;
        if (pressedKeys.contains(KeyEvent.VK_A)) x -= speed;
        if (pressedKeys.contains(KeyEvent.VK_D)) x += speed;

        repaint();
    }

    public void setCustomization(int selection) {

        if (selection == 0 || armorSheet == null) {
            armorFrame = null;
        } else {
            int index = selection - 1;

            if (index >= 0 && index < armorCount) {
                int row = index / armorColumns;
                int col = index % armorColumns;

                int spriteX = col * SPRITE_SIZE;
                int spriteY = row * SPRITE_SIZE;

                armorFrame = armorSheet.getSubimage(spriteX, spriteY, SPRITE_SIZE, SPRITE_SIZE);
            }
        }

        repaint();
        requestFocusInWindow();
    }

    public int getArmorCount() {
        return armorCount;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int playerDrawX = x;
        int playerDrawY = y;

        int armorDrawX = playerDrawX;
        int armorDrawY = playerDrawY;

        if (playerFrame != null) {
            g.drawImage(playerFrame, playerDrawX, playerDrawY, RENDER_SIZE, RENDER_SIZE, null);
        }

        if (armorFrame != null) {
            g.drawImage(armorFrame, armorDrawX, armorDrawY, RENDER_SIZE, RENDER_SIZE, null);
        }
    }
}