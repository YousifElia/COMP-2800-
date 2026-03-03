import javax.swing.*;

public class ControlPanel extends JPanel {

    public ControlPanel(GamePanel gamePanel) {

        JLabel label = new JLabel("Choose Customization:");

        String[] options = {
                "None",
                "Hat",
                "Glasses",
                "Armor",
                "Sword",
                "Cape"
        };

        JComboBox<String> comboBox = new JComboBox<>(options);

        comboBox.addActionListener(e -> {
        int selectedIndex = comboBox.getSelectedIndex();
        gamePanel.setCustomization(selectedIndex);

        // GIVE FOCUS BACK TO GAME PANEL
        gamePanel.requestFocusInWindow();
        });

        add(label);
        add(comboBox);
    }
}
