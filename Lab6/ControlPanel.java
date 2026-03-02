import javax.swing.*;

public class ControlPanel extends JPanel {

    public ControlPanel(GamePanel gamePanel) {

        JLabel label = new JLabel("Choose Customization:");

        String[] options = {
                "Hat",
                "Glasses",
                "Shirt",
                "Sword",
                "Cape"
        };

        JComboBox<String> comboBox = new JComboBox<>(options);

        comboBox.addActionListener(e -> {
            int selectedIndex = comboBox.getSelectedIndex();
            gamePanel.setCustomization(selectedIndex);
        });

        add(label);
        add(comboBox);
    }
}
