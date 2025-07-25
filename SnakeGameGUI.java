import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGameGUI extends JPanel implements ActionListener, KeyListener {
    static final int WIDTH = 600;
    static final int HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (WIDTH * HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 100;

    final LinkedList<GamePoint> snake = new LinkedList<>();
    GamePoint food;
    char direction = 'R';
    boolean running = false;
    Timer timer;
    Random random;

    SnakeGameGUI() {
        this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(this);
        random = new Random();
        startGame();
    }

    public void startGame() {
        snake.clear();
        snake.add(new GamePoint(0, 0));
        newFood();
        direction = 'R';
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            // Draw food
            g.setColor(Color.red);
            g.fillOval(food.x * UNIT_SIZE, food.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);

            // Draw snake
            for (int i = 0; i < snake.size(); i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                } else {
                    g.setColor(new Color(45, 180, 0));
                }
                GamePoint p = snake.get(i);
                g.fillRect(p.x * UNIT_SIZE, p.y * UNIT_SIZE, UNIT_SIZE, UNIT_SIZE);
            }
        } else {
            gameOver(g);
        }
    }

    public void newFood() {
        int x = random.nextInt(WIDTH / UNIT_SIZE);
        int y = random.nextInt(HEIGHT / UNIT_SIZE);
        food = new GamePoint(x, y);
    }

    public void move() {
        GamePoint head = new GamePoint(snake.getFirst().x, snake.getFirst().y);

        switch (direction) {
            case 'U': head.y -= 1; break;
            case 'D': head.y += 1; break;
            case 'L': head.x -= 1; break;
            case 'R': head.x += 1; break;
        }

        snake.addFirst(head);

        // Check food
        if (head.equals(food)) {
            newFood();
        } else {
            snake.removeLast();
        }
    }

    public void checkCollisions() {
        GamePoint head = snake.getFirst();

        // Check wall
        if (head.x < 0 || head.x >= WIDTH / UNIT_SIZE || head.y < 0 || head.y >= HEIGHT / UNIT_SIZE) {
            running = false;
        }

        // Check self collision
        for (int i = 1; i < snake.size(); i++) {
            if (head.equals(snake.get(i))) {
                running = false;
                break;
            }
        }

        if (!running) timer.stop();
    }

    public void gameOver(Graphics g) {
        g.setColor(Color.red);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Game Over", (WIDTH - metrics.stringWidth("Game Over")) / 2, HEIGHT / 2);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkCollisions();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direction != 'R') direction = 'L';
                break;
            case KeyEvent.VK_RIGHT:
                if (direction != 'L') direction = 'R';
                break;
            case KeyEvent.VK_UP:
                if (direction != 'D') direction = 'U';
                break;
            case KeyEvent.VK_DOWN:
                if (direction != 'U') direction = 'D';
                break;
        }
    }

    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGameGUI gamePanel = new SnakeGameGUI();
        frame.add(gamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}

class GamePoint {
    int x, y;
    GamePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object o) {
        if (o instanceof GamePoint p) {
            return this.x == p.x && this.y == p.y;
        }
        return false;
    }
}
