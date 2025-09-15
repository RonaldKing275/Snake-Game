import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import javax.swing.*;

// Klasa StartMenu reprezentuje menu startowe gry.
public class StartMenu extends JFrame {
    private JTextField userNameField;
    private JButton startButton;
    private JButton scoresButton;
    public static String userName;

    // Konstruktor klasy StartMenu. Inicjalizuje interfejs użytkownika.
    public StartMenu() {
        createUI();
    }

    // Klasa wyjątku reprezentująca błąd gry.
    public class GameException extends Exception {
        public GameException(String message) {
            super(message);
        }
    }

    // Metoda tworząca interfejs użytkownika. Ustawia tytuł, rozmiar okna, 
    // pola tekstowe, przyciski i ich funkcje.
    private void createUI() {
        setTitle("Snake Game"); // Ustawienie tytułu okna.
        setDefaultCloseOperation(EXIT_ON_CLOSE); // Ustawienie operacji domyślnej przy zamknięciu okna.
        setSize(300, 100); // Ustawienie rozmiaru okna.

        userNameField = new JTextField(15); // Inicjalizacja pola tekstowego do wprowadzania nazwy użytkownika.
        startButton = new JButton("Start"); // Inicjalizacja przycisku start.
        scoresButton = new JButton("Scores"); // Inicjalizacja przycisku wyników.

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                userName = userNameField.getText();
                try {
                    if (userName == null || userName.trim().isEmpty()) {
                        throw new GameException("Username cannot be empty or only contain white spaces.");
                    } else {
                        dispose();
                        Board board = new Board(userName, new HashMap<String, Integer>()); // Utwórz nową plansze gry.
                        JFrame gameFrame = new JFrame("Snake Game");
                        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        gameFrame.setResizable(false);
                        gameFrame.add(board);
                        gameFrame.pack();
                        gameFrame.setLocationRelativeTo(null);
                        gameFrame.setVisible(true);
                    }
                } catch (GameException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage());
                }
            }
        });  

        scoresButton.addActionListener(e -> {
            try (BufferedReader reader = new BufferedReader(new FileReader("scores.txt"))) {
                String line;
                StringBuilder scores = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    scores.append(line).append("\n");
                }
                JOptionPane.showMessageDialog(null, scores.toString());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Username:"));
        panel.add(userNameField);
        panel.add(startButton);
        panel.add(scoresButton);
        add(panel);

        setLocationRelativeTo(null);
    }

    // Główna metoda programu. Uruchamia menu startowe gry.
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new StartMenu().setVisible(true));
    }
}
