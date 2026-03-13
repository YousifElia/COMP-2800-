import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener {
    
    //board dimensions
    int tileSize = 32;
    int rows = 16;
    int cols = 16;

    int boarderwidth = tileSize * cols;
    int boarderheight = tileSize * rows;

    Image shipImg;
    Image alienImg;
    Image alienCyanImg;
    Image alienPinkImg;
    Image alienYellowImg;
    ArrayList<Image> alienImgArray;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive;
        boolean used;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
            this.alive = true;
            this.used = false;
        }
    }

    // player ship

    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * cols / 2 - tileSize;
    int shipY = tileSize * rows - tileSize * 2;
    int shipVelocity = tileSize;
    Block ship;

    // aliens
    ArrayList<Block> alienArray;
    int alienWidth = tileSize * 2;
    int alienHeight = tileSize;
    int alienX = tileSize;
    int alienY = tileSize;

    int alienRows = 2;
    int alienCols = 3;
    int alienCount = 0;
    int alienVelocity = 1;

    // bullets
    ArrayList<Block> bulletArray;
    int bulletWidth = tileSize / 8;
    int bulletHeight = tileSize / 2;
    int bulletVelocity = -10;

    Timer gameLoop;
    boolean gameOver = false;
    int score = 0;

    public SpaceInvaders() {
        setPreferredSize(new Dimension(boarderwidth, boarderheight));
        setBackground(Color.BLACK);
        setFocusable(true);
        setupKeyBindings();

        shipImg = new ImageIcon("ship.png").getImage();
        alienImg = new ImageIcon("alien.png").getImage();
        alienCyanImg = new ImageIcon("alien_cyan.png").getImage();
        alienPinkImg = new ImageIcon("alien_pink.png").getImage();
        alienYellowImg = new ImageIcon("alien_yellow.png").getImage();

        alienImgArray = new ArrayList<>();
        alienImgArray.add(alienImg);
        alienImgArray.add(alienCyanImg);
        alienImgArray.add(alienPinkImg);
        alienImgArray.add(alienYellowImg);

        ship = new Block(shipX, shipY, shipWidth, shipHeight, shipImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        gameLoop = new Timer(16, this); // ~60 FPS
        createAliens();
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        // draw ship
        g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);

        // draw aliens
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            }
        }

        // draw bullets
        g.setColor(Color.white);
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            if (!bullet.used) {
                g.drawRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }

        // draw score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 32));
        if (gameOver) {
            g.drawString("Game Over! Final Score: " + score, boarderwidth / 2 - 100, boarderheight / 2);
        } else {
            g.drawString("Score: " + score, 10, 35);
        }
    }

    public void move() {

        // move aliens
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (alien.alive) {
                alien.x += alienVelocity;
                if (alien.x + alien.width >= boarderwidth || alien.x <= 0) {
                    alienVelocity *= -1;

                    // move all aliens down
                    for (int j = 0; j < alienArray.size(); j++) {
                        alienArray.get(j).y += alienHeight;
                    }
                }

                if (alien.y >= ship.y) {
                    gameOver = true;
                }
            }
        }

        // move bullets
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocity;

            for (int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    alien.alive = false;
                    bullet.used = true;
                    alienCount--;
                    score += 10;
                }
            }
        }
        while (!bulletArray.isEmpty() && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0); 
        }

        if (alienCount == 0) {
            score += alienCols * alienRows * 10; // bonus for clearing all aliens
            alienCols = Math.min(alienCols + 1, cols / 2 - 2);
            alienRows = Math.min(alienRows + 1, rows - 6);
            alienArray.clear();
            bulletArray.clear();
            createAliens();
        }
    }

    private void createAliens() {
        Random random = new Random();
        for (int c = 0; c < alienCols; c++) {
            for (int r = 0; r < alienRows; r++) {
                int randomImgIndex = random.nextInt(alienImgArray.size());
                Block alien = new Block( alienX + c * alienWidth, alienY + r * alienHeight, alienWidth, alienHeight, alienImgArray.get(randomImgIndex));
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    private boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private void setupKeyBindings() {
        bindKey("LEFT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver && ship.x - shipVelocity >= 0) {
                    ship.x -= shipVelocity;
                }
            }
        });

        bindKey("RIGHT", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!gameOver && ship.x + shipVelocity + ship.width <= boarderwidth) {
                    ship.x += shipVelocity;
                }
            }
        });

        bindKey("SPACE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (gameOver) {
                    restartGame();
                    return;
                }
                fireBullet();
            }
        });
    }

    private void bindKey(String keyStroke, Action action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), keyStroke);
        actionMap.put(keyStroke, action);
    }

    private void restartGame() {
        ship.x = shipX;
        ship.y = shipY;
        bulletArray.clear();
        alienArray.clear();
        gameOver = false;
        score = 0;
        alienCols = 3;
        alienRows = 2;
        alienVelocity = 1;
        createAliens();
        gameLoop.start();
    }

    private void fireBullet() {
        Block bullet = new Block(ship.x + shipWidth * 15 / 32, ship.y, bulletWidth, bulletHeight, null);
        bulletArray.add(bullet);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

}
