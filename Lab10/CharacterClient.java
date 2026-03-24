import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class CharacterClient extends JFrame {

    private final String host;
    private final int port;

    private Socket socket;
    private PrintWriter writer;

    public CharacterClient(String host, int port) {
        this.host = host;
        this.port = port;

        setTitle("Character Client");
        setSize(350, 140);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                disconnect();
            }
        });

        connectAndBuildUi();
        setVisible(true);
    }

    private void connectAndBuildUi() {
        try {
            socket = new Socket(host, port);
            writer = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            int armorCount = readArmorCount(reader);

            JLabel label = new JLabel("Choose Customization:");
            String[] options = buildOptions(armorCount);
            JComboBox<String> comboBox = new JComboBox<>(options);

            comboBox.addActionListener(e -> {
                int selectedIndex = comboBox.getSelectedIndex();
                writer.println("SET " + selectedIndex);
            });

            add(label);
            add(comboBox);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                this,
                "Could not connect to server at " + host + ":" + port,
                "Connection Error",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private int readArmorCount(BufferedReader reader) throws IOException {
        String firstLine = reader.readLine();
        if (firstLine == null || !firstLine.startsWith("ARMOR_COUNT ")) {
            return 0;
        }

        String value = firstLine.substring("ARMOR_COUNT ".length()).trim();
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String[] buildOptions(int armorCount) {
        String[] options = new String[armorCount + 1];
        options[0] = "None";

        for (int i = 1; i <= armorCount; i++) {
            options[i] = "Armor " + i;
        }

        return options;
    }

    private void disconnect() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException ignored) {
                // Ignore close errors
            }
        }
    }

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 5000;

        if (args.length > 0) {
            host = args[0];
        }
        if (args.length > 1) {
            try {
                port = Integer.parseInt(args[1]);
            } catch (NumberFormatException ignored) {
                // Use default port
            }
        }

        final String selectedHost = host;
        final int selectedPort = port;
        SwingUtilities.invokeLater(() -> new CharacterClient(selectedHost, selectedPort));
    }
}
