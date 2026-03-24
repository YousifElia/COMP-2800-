import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class CharacterServer extends JFrame {

    private final GamePanel gamePanel;
    private final int port;

    private volatile boolean running = true;
    private ServerSocket serverSocket;
    private final ExecutorService clientPool = Executors.newCachedThreadPool();

    public CharacterServer(int port) {
        this.port = port;

        setTitle("Character Server");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        gamePanel = new GamePanel();
        JLabel infoLabel = new JLabel("Server running on port " + port + " (WASD to move)");

        add(gamePanel, BorderLayout.CENTER);
        add(infoLabel, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdownServer();
            }
        });

        setVisible(true);
        gamePanel.requestFocusInWindow();

        startServer();
    }

    private void startServer() {
        clientPool.submit(() -> {
            try (ServerSocket localServerSocket = new ServerSocket(port)) {
                serverSocket = localServerSocket;

                while (running) {
                    try {
                        Socket clientSocket = localServerSocket.accept();
                        clientPool.submit(new ClientHandler(clientSocket));
                    } catch (IOException acceptError) {
                        if (running) {
                            acceptError.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private void shutdownServer() {
        running = false;

        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException ignored) {
                // Ignore shutdown errors
            }
        }

        clientPool.shutdownNow();
    }

    private class ClientHandler implements Runnable {

        private final Socket socket;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (
                Socket localSocket = socket;
                BufferedReader reader = new BufferedReader(new InputStreamReader(localSocket.getInputStream()));
                PrintWriter writer = new PrintWriter(localSocket.getOutputStream(), true)
            ) {
                writer.println("ARMOR_COUNT " + gamePanel.getArmorCount());

                String line;
                while ((line = reader.readLine()) != null) {
                    handleCommand(line.trim());
                }
            } catch (IOException e) {
                // Client disconnected or network error
            }
        }

        private void handleCommand(String command) {
            if (command.startsWith("SET ")) {
                String value = command.substring(4).trim();
                try {
                    int selection = Integer.parseInt(value);
                    SwingUtilities.invokeLater(() -> gamePanel.setCustomization(selection));
                } catch (NumberFormatException ignored) {
                    // Ignore invalid commands
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = 5000;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {
                // Use default port
            }
        }

        final int selectedPort = port;
        SwingUtilities.invokeLater(() -> new CharacterServer(selectedPort));
    }
}
