import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import javax.swing.*;

public class COMP2800Lab3YousifElia110170930 extends JFrame implements ActionListener {

    private JTextArea display;
    private HashMap<String, JButton> keyMap = new HashMap<>();
    private Color defaultColor;

    public COMP2800Lab3YousifElia110170930() {

        setTitle("TypingTutor");
        setSize(900, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top text area
        display = new JTextArea(
                "Type text using your keyboard. The key you press will be highlighted and text will be displayed.\n\n"
        );
        display.setEditable(false);
        display.setFont(new Font("Arial", Font.PLAIN, 14));
        display.setLineWrap(true);
        display.setWrapStyleWord(true);
        add(display, BorderLayout.NORTH);

        // Keyboard panel
        JPanel keyboardPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1;
        gbc.weighty = 1;

        // Row 0
        addKey(keyboardPanel, gbc, "~", 0, 0, 1);
        addKey(keyboardPanel, gbc, "1", 1, 0, 1);
        addKey(keyboardPanel, gbc, "2", 2, 0, 1);
        addKey(keyboardPanel, gbc, "3", 3, 0, 1);
        addKey(keyboardPanel, gbc, "4", 4, 0, 1);
        addKey(keyboardPanel, gbc, "5", 5, 0, 1);
        addKey(keyboardPanel, gbc, "6", 6, 0, 1);
        addKey(keyboardPanel, gbc, "7", 7, 0, 1);
        addKey(keyboardPanel, gbc, "8", 8, 0, 1);
        addKey(keyboardPanel, gbc, "9", 9, 0, 1);
        addKey(keyboardPanel, gbc, "0", 10, 0, 1);
        addKey(keyboardPanel, gbc, "-", 11, 0, 1);
        addKey(keyboardPanel, gbc, "+", 12, 0, 1);
        addKey(keyboardPanel, gbc, "Backspace", 13, 0, 2);

        // Row 1
        addKey(keyboardPanel, gbc, "Tab", 0, 1, 2);
        addKey(keyboardPanel, gbc, "Q", 2, 1, 1);
        addKey(keyboardPanel, gbc, "W", 3, 1, 1);
        addKey(keyboardPanel, gbc, "E", 4, 1, 1);
        addKey(keyboardPanel, gbc, "R", 5, 1, 1);
        addKey(keyboardPanel, gbc, "T", 6, 1, 1);
        addKey(keyboardPanel, gbc, "Y", 7, 1, 1);
        addKey(keyboardPanel, gbc, "U", 8, 1, 1);
        addKey(keyboardPanel, gbc, "I", 9, 1, 1);
        addKey(keyboardPanel, gbc, "O", 10, 1, 1);
        addKey(keyboardPanel, gbc, "P", 11, 1, 1);
        addKey(keyboardPanel, gbc, "[", 12, 1, 1);
        addKey(keyboardPanel, gbc, "]", 13, 1, 1);
        addKey(keyboardPanel, gbc, "\\", 14, 1, 1);

        // Row 2
        addKey(keyboardPanel, gbc, "CAPS", 0, 2, 2);
        addKey(keyboardPanel, gbc, "A", 2, 2, 1);
        addKey(keyboardPanel, gbc, "S", 3, 2, 1);
        addKey(keyboardPanel, gbc, "D", 4, 2, 1);
        addKey(keyboardPanel, gbc, "F", 5, 2, 1);
        addKey(keyboardPanel, gbc, "G", 6, 2, 1);
        addKey(keyboardPanel, gbc, "H", 7, 2, 1);
        addKey(keyboardPanel, gbc, "J", 8, 2, 1);
        addKey(keyboardPanel, gbc, "K", 9, 2, 1);
        addKey(keyboardPanel, gbc, "L", 10, 2, 1);
        addKey(keyboardPanel, gbc, ":", 11, 2, 1);
        addKey(keyboardPanel, gbc, "*", 12, 2, 1);
        addKey(keyboardPanel, gbc, "Enter", 13, 2, 2);

        // Row 3
        addKey(keyboardPanel, gbc, "SHIFT", 0, 3, 2);
        addKey(keyboardPanel, gbc, "Z", 2, 3, 1);
        addKey(keyboardPanel, gbc, "X", 3, 3, 1);
        addKey(keyboardPanel, gbc, "C", 4, 3, 1);
        addKey(keyboardPanel, gbc, "V", 5, 3, 1);
        addKey(keyboardPanel, gbc, "B", 6, 3, 1);
        addKey(keyboardPanel, gbc, "N", 7, 3, 1);
        addKey(keyboardPanel, gbc, "M", 8, 3, 1);
        addKey(keyboardPanel, gbc, ",", 9, 3, 1);
        addKey(keyboardPanel, gbc, ".", 10, 3, 1);
        addKey(keyboardPanel, gbc, "?", 11, 3, 1);
        addKey(keyboardPanel, gbc, "^", 12, 3, 1);

        // Row 4
        addKey(keyboardPanel, gbc, "Space", 4, 4, 6);
        addKey(keyboardPanel, gbc, "<", 11, 4, 1);
        addKey(keyboardPanel, gbc, "v", 12, 4, 1);
        addKey(keyboardPanel, gbc, ">", 13, 4, 1);

        add(keyboardPanel, BorderLayout.CENTER);
        setupKeyBindings(keyboardPanel);

        setVisible(true);
    }

    private void addKey(JPanel panel, GridBagConstraints gbc, String text, int x, int y, int width) {

        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.addActionListener(this);

        if (defaultColor == null)
            defaultColor = button.getBackground();

        keyMap.put(text.toUpperCase(), button);
        panel.add(button, gbc);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();
        highlight(button);

        if (button.getText().equals("Backspace")) {
            backspace();
        } else if (button.getText().equals("Space")) {
            display.append(" ");
        } else if (button.getText().equals("Enter")) {
            display.append("\n");
        } else {
            display.append(button.getText());
        }
    }

    private void setupKeyBindings(JPanel panel) {

        InputMap im = panel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = panel.getActionMap();

        for (String key : keyMap.keySet()) {

            if (key.equals("BACKSPACE")) continue;

            KeyStroke ks = KeyStroke.getKeyStroke(key);
            if (ks == null) continue;

            im.put(ks, key);
            am.put(key, new AbstractAction() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JButton btn = keyMap.get(key);
                    highlight(btn);

                    if (key.equals("SPACE"))
                        display.append(" ");
                    else
                        display.append(key.toLowerCase());
                }
            });
        }

        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_BACK_SPACE, 0), "BACKSPACE");
        am.put("BACKSPACE", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JButton btn = keyMap.get("BACKSPACE");
                if (btn != null)
                    highlight(btn);

                backspace();
            }
        });
    }

    private void backspace() {
        String text = display.getText();
        if (text.length() > 0) {
            display.setText(text.substring(0, text.length() - 1));
        }
    }

    private void highlight(JButton button) {
        button.setBackground(new Color(120, 170, 255));

        Timer t = new Timer(150, e -> button.setBackground(defaultColor));
        t.setRepeats(false);
        t.start();
    }

    public static void main(String[] args) {
        new COMP2800Lab3YousifElia110170930();
    }
}
