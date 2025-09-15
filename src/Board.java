import java.awt.*;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Klasa Board reprezentuje planszę do gry w węża.
 * Implementuje interfejs ActionListener, który obsługuje zdarzenia związane z ruchem węża.
 */
public class Board extends JPanel implements ActionListener {

    // Stałe reprezentujące różne parametry gry
    private final int B_WIDTH = 300;
    private final int B_HEIGHT = B_WIDTH;
    private final int DOT_SIZE = 10; // Rozmiar elementów na mapie (w pixelach - 10x10 w tym przypadku).
    private final int ALL_body_length = B_WIDTH*B_HEIGHT; // Maksymalny rozmiar węża - logicznie jest to powierzchnia całej mapy.
    private final int RAND_POS = B_WIDTH/DOT_SIZE; // Maksymalna losowa pozycja na planszy dla jabłka.
    private final int DELAY = 140; // Opóźnienie (w ms) ruchu węża - nim większe tym wąż wolniejszy (Zalecane 140).

    // Tablice przechowujące współrzędne części ciała węża
    private final int x[] = new int[ALL_body_length];
    private final int y[] = new int[ALL_body_length];

    // Zmienne reprezentujące stan gry
    private int body_length;
    private int apple_x;
    private int apple_y;

    // Kierunek węża
    private boolean leftDirection = false;
    private boolean rightDirection = true;
    private boolean upDirection = false;
    private boolean downDirection = false;
    private boolean inGame = true;

    // Timer do obsługi ruchu węża
    private Timer timer;
    
    // Obrazy reprezentujące różne elementy gry
    private Image ball;
    private Image apple;
    private Image headUp;
    private Image headDown;
    private Image headLeft;
    private Image headRight;


    /**
     * Konstruktor klasy Board. Inicjalizuje planszę gry.
     * @param userName Nazwa użytkownika.
     * @param scores Mapa wyników.
     */
    private String userName;
    private Map<String, Integer> scores;

    public Board(String userName, Map<String, Integer> scores) {
        this.userName = userName;
        this.scores = scores;
        initBoard();
    }

    /**
     * Metoda inicjalizująca planszę gry.
     */
    private void initBoard() {

        addKeyListener(new TAdapter());
        setBackground(Color.black);
        setFocusable(true);

        setPreferredSize(new Dimension(B_WIDTH, B_HEIGHT));
        loadImages();
        initGame();
    }

    /**
     * Metoda wczytująca obrazy z plików.
     */
    private void loadImages() {

        ImageIcon iid = new ImageIcon(Board.class.getResource("/resources/body.png"));
        ball = iid.getImage();

        ImageIcon iia = new ImageIcon(Board.class.getResource("/resources/apple.png"));
        apple = iia.getImage();

        ImageIcon iihU = new ImageIcon(Board.class.getResource("/resources/headUp.png"));
        headUp = iihU.getImage();

        ImageIcon iihD = new ImageIcon(Board.class.getResource("/resources/headDown.png"));
        headDown = iihD.getImage();

        ImageIcon iihL = new ImageIcon(Board.class.getResource("/resources/headLeft.png"));
        headLeft = iihL.getImage();

        ImageIcon iihR = new ImageIcon(Board.class.getResource("/resources/headRight.png"));
        headRight = iihR.getImage();

    }

    /**
     * Metoda inicjalizująca grę.
     */
    private void initGame() {

        body_length = 3;

        for (int z = 0; z < body_length; z++) {
            x[z] = 50 - z * 10;
            y[z] = 50;
        }
        
        locateApple();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    /**
     * Metoda rysująca elementy gry.
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        doDrawing(g);
    }
    
    /**
     * Metoda rysująca elementy gry.
     */
    private void doDrawing(Graphics g) {
    
        if (inGame) {
    
            g.drawImage(apple, apple_x, apple_y, this);
    
            for (int z = 0; z < body_length; z++) {
                if (z == 0) {
                    if (leftDirection) {
                        g.drawImage(headLeft, x[z], y[z], this);
                    } else if (rightDirection) {
                        g.drawImage(headRight, x[z], y[z], this);
                    } else if (upDirection) {
                        g.drawImage(headUp, x[z], y[z], this);
                    } else if (downDirection) {
                        g.drawImage(headDown, x[z], y[z], this);
                    }
                } else {
                    g.drawImage(ball, x[z], y[z], this);
                }
            }
    
            Toolkit.getDefaultToolkit().sync();
    
        } else {
    
            gameOver(g);
        }        
    }

    /**
     * Metoda wyświetlająca komunikat o końcu gry i zapisująca wynik.
     */
    private void gameOver(Graphics g) {

        System.out.println("Game over, saving score...");
        String msg = "Game Over";
        Font small = new Font("Helvetica", Font.BOLD, 14);
        FontMetrics metr = getFontMetrics(small);
    
        g.setColor(Color.white);
        g.setFont(small);
        g.drawString(msg, (B_WIDTH - metr.stringWidth(msg)) / 2, B_HEIGHT / 2);
    
        saveScore();  // Zapisz wynik po zakończeniu gry
    }
    
    /**
     * Metoda zapisująca wynik gry.
     */
    private void saveScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("scores.txt", true))) {
            int applesEaten = body_length - 3;
            writer.write(userName + ": " + applesEaten);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
       

    /**
     * Metoda sprawdzająca, czy wąż zjadł jabłko.
     */
    private void checkApple() {

        if ((x[0] == apple_x) && (y[0] == apple_y)) {

            body_length++;
            locateApple();
        }
    }

    /**
     * Metoda obsługująca ruch węża.
     */
    private void move() {

        for (int z = body_length; z > 0; z--) {
            x[z] = x[(z - 1)];
            y[z] = y[(z - 1)];
        }

        if (leftDirection) {
            x[0] -= DOT_SIZE;
        }

        if (rightDirection) {
            x[0] += DOT_SIZE;
        }

        if (upDirection) {
            y[0] -= DOT_SIZE;
        }

        if (downDirection) {
            y[0] += DOT_SIZE;
        }
    }

    /**
     * Metoda sprawdzająca, czy doszło do kolizji.
     */
    private void checkCollision() {

        for (int z = body_length; z > 0; z--) {
    
            if ((x[0] == x[z]) && (y[0] == y[z])) {
                inGame = false;
            }
        }
    
        if (y[0] >= B_HEIGHT) {
            inGame = false;
        }
    
        if (y[0] < 0) {
            inGame = false;
        }
    
        if (x[0] >= B_WIDTH) {
            inGame = false;
        }
    
        if (x[0] < 0) {
            inGame = false;
        }
        
        if (!inGame) {
            timer.stop();
        }
    }
    

    /**
     * Metoda umieszczająca jabłko na planszy w losowym miejscu.
     */
    private void locateApple() {

        int r = (int) (Math.random() * RAND_POS);
        apple_x = ((r * DOT_SIZE));

        r = (int) (Math.random() * RAND_POS);
        apple_y = ((r * DOT_SIZE));
    }

    /**
     * Klasa TAdapter obsługująca zdarzenia związane z klawiaturą.
     */
    private class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if ((key == KeyEvent.VK_LEFT) && (!rightDirection)) {
                leftDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_RIGHT) && (!leftDirection)) {
                rightDirection = true;
                upDirection = false;
                downDirection = false;
            }

            if ((key == KeyEvent.VK_UP) && (!downDirection)) {
                upDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if ((key == KeyEvent.VK_DOWN) && (!upDirection)) {
                downDirection = true;
                rightDirection = false;
                leftDirection = false;
            }

            if (key == KeyEvent.VK_O) {
                body_length = body_length + 1; // Maly cheat który zwiększa długość węża (jakby zjadł jabłko) po kliknięciu przycisku "o".
            }
        }
    }

    /**
     * Metoda obsługująca zdarzenia związane z ruchem węża.
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        if (inGame) {

            checkApple();
            checkCollision();
            move();
        }

        repaint();
    }

    /**
     * Metoda ustawiająca nazwę użytkownika.
     * @param userName Nazwa użytkownika.
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Metoda zwracająca nazwę użytkownika.
     * @return Nazwa użytkownika.
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Główna metoda programu.
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Snake Game");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            Board board = new Board("DefaultUserName", new HashMap<>()); // Utwórz instancję Board
            frame.add(board);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}