import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SpaceInvaders extends JPanel implements ActionListener {

    static final int REGULAR_UFO = 1;
    static final int ELITE_UFO = 2;

    // board dimensions
    int tileSize = 32;
    int rows = 16;
    int cols = 16;

    int boarderwidth = tileSize * cols;
    int boarderheight = tileSize * rows;

    Image playerDefaultImg;
    Image playerLeftImg;
    Image playerRightImg;
    Image regularEnemyImg;
    Image eliteEnemyImg;
    Image eliteEnemyDamagedImg;

    class Block {
        int x;
        int y;
        int width;
        int height;
        Image img;
        boolean alive;
        boolean used;
        int health;
        int maxHealth;
        int enemyType;

        Block(int x, int y, int width, int height, Image img) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.img = img;
            this.alive = true;
            this.used = false;
            this.health = 1;
            this.maxHealth = 1;
            this.enemyType = REGULAR_UFO;
        }
    }

    // player ship
    int shipWidth = tileSize * 2;
    int shipHeight = tileSize;
    int shipX = tileSize * cols / 2 - tileSize;
    int shipY = tileSize * rows - tileSize * 2;
    int shipVelocity = 6;
    Block ship;
    boolean movingLeft = false;
    boolean movingRight = false;

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
    int level = 1;

    // bullets
    ArrayList<Block> bulletArray;
    int bulletWidth = Math.max(4, tileSize / 8);
    int bulletHeight = tileSize / 2;
    int bulletVelocity = -10;

    Timer gameLoop;
    boolean gameOver = false;
    int score = 0;
    Random random = new Random();

    public SpaceInvaders() {
        setPreferredSize(new Dimension(boarderwidth, boarderheight));
        setBackground(Color.BLACK);
        setFocusable(true);
        setupKeyBindings();

        playerDefaultImg = loadImage("player.png");
        playerLeftImg = loadImage("playerleft.png", "playerLeft.png");
        playerRightImg = loadImage("playerright.png", "playerRight.png");
        regularEnemyImg = loadImage("enemy1.png", "enemyUFO.png");
        eliteEnemyImg = loadImage("enemy2.png", "enemyShip.png");
        eliteEnemyDamagedImg = loadImage("enemy2_damaged.png");

        ship = new Block(shipX, shipY, shipWidth, shipHeight, playerDefaultImg);
        alienArray = new ArrayList<>();
        bulletArray = new ArrayList<>();

        gameLoop = new Timer(16, this);
        startLevel(1);
        gameLoop.start();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        drawShip(g);
        drawAliens(g);
        drawBullets(g);
        drawHud(g);
    }

    private void drawShip(Graphics g) {
        if (ship.img != null) {
            g.drawImage(ship.img, ship.x, ship.y, ship.width, ship.height, null);
            return;
        }

        g.setColor(Color.GREEN);
        g.fillRect(ship.x, ship.y, ship.width, ship.height);
    }

    private void drawAliens(Graphics g) {
        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (!alien.alive) {
                continue;
            }

            if (alien.img != null) {
                g.drawImage(alien.img, alien.x, alien.y, alien.width, alien.height, null);
            } else {
                g.setColor(getAlienFallbackColor(alien));
                g.fillRect(alien.x, alien.y, alien.width, alien.height);
            }
        }
    }

    private void drawBullets(Graphics g) {
        g.setColor(Color.WHITE);
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            if (!bullet.used) {
                g.fillRect(bullet.x, bullet.y, bullet.width, bullet.height);
            }
        }
    }

    private void drawHud(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 22));
        g.drawString("Score: " + score, 10, 28);
        g.drawString("Level: " + level, 10, 54);

        if (gameOver) {
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Game Over", boarderwidth / 2 - 80, boarderheight / 2 - 10);
            g.drawString("Press Space to restart", boarderwidth / 2 - 150, boarderheight / 2 + 28);
        }
    }

    public void move() {
        updateShipMovement();
        moveAliens();
        moveBullets();
        cleanupBullets();

        if (alienCount == 0 && !gameOver) {
            startNextLevel();
        }
    }

    private void updateShipMovement() {
        if (movingLeft && !movingRight) {
            ship.x = Math.max(0, ship.x - shipVelocity);
            ship.img = playerLeftImg != null ? playerLeftImg : playerDefaultImg;
        } else if (movingRight && !movingLeft) {
            ship.x = Math.min(boarderwidth - ship.width, ship.x + shipVelocity);
            ship.img = playerRightImg != null ? playerRightImg : playerDefaultImg;
        } else {
            ship.img = playerDefaultImg;
        }
    }

    private void moveAliens() {
        boolean hitEdge = false;

        for (int i = 0; i < alienArray.size(); i++) {
            Block alien = alienArray.get(i);
            if (!alien.alive) {
                continue;
            }

            alien.x += alienVelocity;
            if (alien.x + alien.width >= boarderwidth || alien.x <= 0) {
                hitEdge = true;
            }

            if (alien.y + alien.height >= ship.y) {
                gameOver = true;
            }
        }

        if (hitEdge) {
            alienVelocity *= -1;
            for (int i = 0; i < alienArray.size(); i++) {
                alienArray.get(i).x += alienVelocity;
                alienArray.get(i).y += alienHeight;
            }
        }
    }

    private void moveBullets() {
        for (int i = 0; i < bulletArray.size(); i++) {
            Block bullet = bulletArray.get(i);
            bullet.y += bulletVelocity;

            for (int j = 0; j < alienArray.size(); j++) {
                Block alien = alienArray.get(j);
                if (!bullet.used && alien.alive && detectCollision(bullet, alien)) {
                    applyBulletHit(alien, bullet);
                }
            }
        }
    }

    private void applyBulletHit(Block alien, Block bullet) {
        bullet.used = true;
        alien.health--;

        if (alien.health <= 0) {
            alien.alive = false;
            alienCount--;
            score += alien.enemyType == ELITE_UFO ? 25 : 10;
            return;
        }

        if (alien.enemyType == ELITE_UFO && eliteEnemyDamagedImg != null) {
            alien.img = eliteEnemyDamagedImg;
        }
    }

    private void cleanupBullets() {
        while (!bulletArray.isEmpty() && (bulletArray.get(0).used || bulletArray.get(0).y < 0)) {
            bulletArray.remove(0);
        }
    }

    private void startNextLevel() {
        score += alienCols * alienRows * 10;
        level = Math.min(3, level + 1);
        alienCols = Math.min(alienCols + 1, cols / 2 - 2);
        alienRows = Math.min(alienRows + 1, rows - 6);
        alienVelocity = alienVelocity < 0 ? -(Math.abs(alienVelocity) + 1) : Math.abs(alienVelocity) + 1;
        bulletArray.clear();
        createAliens();
    }

    private void startLevel(int targetLevel) {
        level = Math.max(1, Math.min(3, targetLevel));
        alienVelocity = Math.max(1, level);
        bulletArray.clear();
        createAliens();
    }

    private void createAliens() {
        alienArray.clear();

        for (int c = 0; c < alienCols; c++) {
            for (int r = 0; r < alienRows; r++) {
                Block alien = createAlienForLevel(c, r);
                alienArray.add(alien);
            }
        }
        alienCount = alienArray.size();
    }

    private Block createAlienForLevel(int col, int row) {
        int enemyType = pickEnemyTypeForLevel();
        Image enemyImage = enemyType == ELITE_UFO ? eliteEnemyImg : regularEnemyImg;
        Block alien = new Block(alienX + col * alienWidth, alienY + row * alienHeight, alienWidth, alienHeight, enemyImage);
        alien.enemyType = enemyType;
        alien.maxHealth = enemyType == ELITE_UFO ? 2 : 1;
        alien.health = alien.maxHealth;
        return alien;
    }

    private int pickEnemyTypeForLevel() {
        if (level <= 1) {
            return REGULAR_UFO;
        }
        if (level == 2) {
            return random.nextBoolean() ? REGULAR_UFO : ELITE_UFO;
        }
        return ELITE_UFO;
    }

    private boolean detectCollision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x && a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private Color getAlienFallbackColor(Block alien) {
        if (alien.enemyType == ELITE_UFO) {
            return alien.health == 1 ? Color.ORANGE : Color.RED;
        }
        return Color.CYAN;
    }

    private Image loadImage(String... fileNames) {
        for (String fileName : fileNames) {
            File directFile = new File(fileName);
            if (directFile.exists()) {
                return new ImageIcon(directFile.getPath()).getImage();
            }

            File imageDirFile = new File("images", fileName);
            if (imageDirFile.exists()) {
                return new ImageIcon(imageDirFile.getPath()).getImage();
            }
        }
        return null;
    }

    private void setupKeyBindings() {
        bindKey("pressed LEFT", "moveLeftPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movingLeft = true;
            }
        });

        bindKey("released LEFT", "moveLeftReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movingLeft = false;
            }
        });

        bindKey("pressed RIGHT", "moveRightPressed", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movingRight = true;
            }
        });

        bindKey("released RIGHT", "moveRightReleased", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                movingRight = false;
            }
        });

        bindKey("SPACE", "fireOrRestart", new AbstractAction() {
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

    private void bindKey(String keyStroke, String actionKey, Action action) {
        InputMap inputMap = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        inputMap.put(KeyStroke.getKeyStroke(keyStroke), actionKey);
        actionMap.put(actionKey, action);
    }

    private void restartGame() {
        ship.x = shipX;
        ship.y = shipY;
        ship.img = playerDefaultImg;
        movingLeft = false;
        movingRight = false;
        bulletArray.clear();
        gameOver = false;
        score = 0;
        alienCols = 3;
        alienRows = 2;
        startLevel(1);
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Space Invaders");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);

            SpaceInvaders game = new SpaceInvaders();
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            game.requestFocusInWindow();
        });
    }
}
