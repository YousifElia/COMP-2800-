import javax.swing.*;

public class ControlPanel extends JPanel {

    public ControlPanel(GamePanel gamePanel) {

        JLabel label = new JLabel("Choose Customization:");

        int armorCount = gamePanel.getArmorCount();
        String[] options = new String[armorCount + 1];
        options[0] = "None";

        for (int i = 1; i <= armorCount; i++) {
            options[i] = "Armor " + i;
        }

        JComboBox<String> comboBox = new JComboBox<>(options);

        comboBox.addActionListener(e -> {
            int selectedIndex = comboBox.getSelectedIndex();
            gamePanel.setCustomization(selectedIndex);
        });

        add(label);
        add(comboBox);
    }
}
