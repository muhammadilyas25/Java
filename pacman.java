import java.awt.*;
import java.awt.event.*;
import java.util.HashSet;
import java.util.Random;
import javax.swing.*;

public class pacman extends JPanel implements ActionListener, KeyListener {
    class Block {
        int x, y, width, height;
        Image image;
        int startX, startY;
        char direction = 'U';
        int velocityX = 0, velocityY = 0;

        Block(Image image, int x, int y, int width, int height) {
            this.image = image;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.startX = x;
            this.startY = y;
        }

        void updateDirection(char dir) {
            char prevDirection = this.direction;
            this.direction = dir;
            updateVelocity();
            this.x += this.velocityX;
            this.y += this.velocityY;
            for (Block wall : walls) {
                if (collision(this, wall)) {
                    this.x -= this.velocityX;
                    this.y -= this.velocityY;
                    this.direction = prevDirection;
                    updateVelocity();
                }
            }
        }

        void updateVelocity() {
            velocityX = 0; velocityY = 0;
            if (direction == 'U') velocityY = -tileSize / 4;
            if (direction == 'D') velocityY = tileSize / 4;
            if (direction == 'L') velocityX = -tileSize / 4;
            if (direction == 'R') velocityX = tileSize / 4;
        }

        void reset() {
            x = startX;
            y = startY;
        }
    }

    private int tileSize = 32;
    private int rowCount, columnCount;
    private int boardWidth, boardHeight;

    private Image wallImage, blueGhostImage, orangeGhostImage, pinkGhostImage, redGhostImage;
    private Image pacmanUpImage, pacmanDownImage, pacmanLeftImage, pacmanRightImage;

    private String[] tileMap;
    private HashSet<Block> walls = new HashSet<>();
    private HashSet<Block> foods = new HashSet<>();
    private HashSet<Block> ghosts = new HashSet<>();
    private Block pacman;

    private Timer gameLoop;
    private char[] directions = {'U', 'D', 'L', 'R'};
    private Random random = new Random();
    private int score = 0, lives = 3;
    private boolean gameOver = false;
    private int level;

    public pacman(int level) {
        this.level = level;
        loadImages();
        setupMap();
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);
        loadMap();

        gameLoop = new Timer(50, this);
        gameLoop.start();
    }

    private void loadImages() {
        wallImage = new ImageIcon(getClass().getResource("./wall.png")).getImage();
        blueGhostImage = new ImageIcon(getClass().getResource("./blueGhost.png")).getImage();
        orangeGhostImage = new ImageIcon(getClass().getResource("./orangeGhost.png")).getImage();
        pinkGhostImage = new ImageIcon(getClass().getResource("./pinkGhost.png")).getImage();
        redGhostImage = new ImageIcon(getClass().getResource("./redGhost.png")).getImage();
        pacmanUpImage = new ImageIcon(getClass().getResource("./pacmanUp.png")).getImage();
        pacmanDownImage = new ImageIcon(getClass().getResource("./pacmanDown.png")).getImage();
        pacmanLeftImage = new ImageIcon(getClass().getResource("./pacmanLeft.png")).getImage();
        pacmanRightImage = new ImageIcon(getClass().getResource("./pacmanRight.png")).getImage();
    }

    private void setupMap() {
        if (level == 1) {
            tileMap = new String[]{
                "XXXXXXXXXXXXXXXXXXX",
                "X        X        X",
                "X XX XXX X XXX XX X",
                "X                 X",
                "X XX X XXXXX X XX X",
                "X    X       X    X",
                "XXXX XXXX XXXX XXXX",
                "OOOX X       X XOOO",
                "XXXX X XXrXX X XXXX",
                "X       bpo       X",
                "XXXX X XXXXX X XXXX",
                "OOOX X       X XOOO",
                "XXXX X XXXXX X XXXX",
                "X        X        X",
                "X XX XXX X XXX XX X",
                "X  X     P     X  X",
                "XX X X XXXXX X X XX",
                "X    X   X   X    X",
                "X XXXXXX X XXXXXX X",
                "X                 X",
                "XXXXXXXXXXXXXXXXXXX"
            };
        } else {
            tileMap = new String[]{
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
                "X        X        X X        X        X",
                "X XX XXX X XXX XX X X XX XXX X XXX XX X",
                "X                 X X                 X",
                "X XX X XXXXX X XX X X XX X XXXXX X XX X",
                "X    X       X    X X    X       X    X",
                "XXXX XXXX XXXX XXXX XXXX XXXX XXXX XXXX",
                "OOOX X       X X       X X       X XOOO",
                "XXXX X XXXXX X XXXX XXXX X XXXXX X XXXX",
                "X       bp                   or       X",
                "XXXX X XXXXX X XXXX XXXX X XXXXX X XXXX",
                "OOOX X       X X       X X       X XOOO",
                "XXXX X XXXXX X XXXX XXXX X XXXXX X XXXX",
                "X                 X X        X        X",
                "X XX XXX X XXX XX X X XX XXX X XXX XX X",
                "X  X           X   P   X           X  X",
                "XX X X XXXXX X X XX XX X X XXXXX X X XX",
                "X    X   X   X    X X    X   X   X    X",
                "X XXXXXX X XXXXXX X X XXXXXX X XXXXXX X",
                "X                 X X                 X",
                "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"
            };
        }

        rowCount = tileMap.length;
        columnCount = tileMap[0].length();
        boardWidth = columnCount * tileSize;
        boardHeight = rowCount * tileSize;
    }

    private void loadMap() {
        walls.clear(); foods.clear(); ghosts.clear();

        for (int r = 0; r < rowCount; r++) {
            for (int c = 0; c < columnCount; c++) {
                char ch = tileMap[r].charAt(c);
                int x = c * tileSize;
                int y = r * tileSize;

                if (ch == 'X') walls.add(new Block(wallImage, x, y, tileSize, tileSize));
                else if (ch == 'b') ghosts.add(new Block(blueGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'o') ghosts.add(new Block(orangeGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'p') ghosts.add(new Block(pinkGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'r') ghosts.add(new Block(redGhostImage, x, y, tileSize, tileSize));
                else if (ch == 'P') pacman = new Block(pacmanRightImage, x, y, tileSize, tileSize);
                else if (ch == ' ') foods.add(new Block(null, x + 14, y + 14, 4, 4));
            }
        }

        for (Block ghost : ghosts) {
            char dir = directions[random.nextInt(4)];
            ghost.updateDirection(dir);
        }
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        g.drawImage(pacman.image, pacman.x, pacman.y, tileSize, tileSize, null);
        for (Block ghost : ghosts) g.drawImage(ghost.image, ghost.x, ghost.y, tileSize, tileSize, null);
        for (Block wall : walls) g.drawImage(wall.image, wall.x, wall.y, tileSize, tileSize, null);
        g.setColor(Color.WHITE);
        for (Block food : foods) g.fillRect(food.x, food.y, food.width, food.height);

        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(gameOver ? "Game Over! Score: " + score : "x" + lives + " Score: " + score, 10, 20);

        // tombol kembali ke menu
        JButton backButton = new JButton("Kembali ke Menu");
        backButton.setBounds(boardWidth - 180, 10, 160, 30);
        this.setLayout(null);
        this.add(backButton);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                gameLoop.stop();
                app.showMainMenu();
            }
        });
    }

    private boolean collision(Block a, Block b) {
        return a.x < b.x + b.width && a.x + a.width > b.x &&
               a.y < b.y + b.height && a.y + a.height > b.y;
    }

    private void move() {
        pacman.x += pacman.velocityX;
        pacman.y += pacman.velocityY;
        for (Block wall : walls)
            if (collision(pacman, wall)) {
                pacman.x -= pacman.velocityX;
                pacman.y -= pacman.velocityY;
                break;
            }

        for (Block ghost : ghosts) {
            if (collision(pacman, ghost)) {
                lives--;
                if (lives == 0) {
                    gameOver = true;
                    return;
                }
                resetPositions();
            }
            ghost.x += ghost.velocityX;
            ghost.y += ghost.velocityY;
            for (Block wall : walls)
                if (collision(ghost, wall)) {
                    ghost.x -= ghost.velocityX;
                    ghost.y -= ghost.velocityY;
                    ghost.updateDirection(directions[random.nextInt(4)]);
                }
        }

        Block eaten = null;
        for (Block food : foods)
            if (collision(pacman, food)) {
                eaten = food;
                score += 10;
            }
        if (eaten != null) foods.remove(eaten);

        if (foods.isEmpty()) {
            loadMap();
            resetPositions();
        }
    }

    private void resetPositions() {
        pacman.reset();
        pacman.velocityX = 0;
        pacman.velocityY = 0;
        for (Block ghost : ghosts) {
            ghost.reset();
            ghost.updateDirection(directions[random.nextInt(4)]);
        }
    }

    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) gameLoop.stop();
    }

    public void keyTyped(KeyEvent e) {}
    public void keyPressed(KeyEvent e) {}
    public void keyReleased(KeyEvent e) {
        if (gameOver) {
            loadMap();
            resetPositions();
            score = 0; lives = 3;
            gameOver = false;
            gameLoop.start();
        }
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_UP) pacman.updateDirection('U');
        else if (code == KeyEvent.VK_DOWN) pacman.updateDirection('D');
        else if (code == KeyEvent.VK_LEFT) pacman.updateDirection('L');
        else if (code == KeyEvent.VK_RIGHT) pacman.updateDirection('R');

        if (pacman.direction == 'U') pacman.image = pacmanUpImage;
        else if (pacman.direction == 'D') pacman.image = pacmanDownImage;
        else if (pacman.direction == 'L') pacman.image = pacmanLeftImage;
        else if (pacman.direction == 'R') pacman.image = pacmanRightImage;
    }
}
