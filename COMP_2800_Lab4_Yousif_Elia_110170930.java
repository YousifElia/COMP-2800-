import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class COMP_2800_Lab4_Yousif_Elia_110170930 extends JPanel implements ChangeListener {
    private final JSlider redSlider, greenSlider, blueSlider;
    private final JTextField redField, greenField, blueField;
    private final JPanel colorPreviewPanel;

    public COMP_2800_Lab4_Yousif_Elia_110170930() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Initialize components
        redSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        greenSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
        blueSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);

        redField = new JTextField("0", 4);
        greenField = new JTextField("0", 4);
        blueField = new JTextField("0", 4);

        colorPreviewPanel = new JPanel();
        colorPreviewPanel.setPreferredSize(new Dimension(400, 100));

        redSlider.addChangeListener(this);
        greenSlider.addChangeListener(this);
        blueSlider.addChangeListener(this);

        // Sync text fields with sliders
        redField.getDocument().addDocumentListener(new SliderDocumentListener(redField, redSlider));
        greenField.getDocument().addDocumentListener(new SliderDocumentListener(greenField, greenSlider));
        blueField.getDocument().addDocumentListener(new SliderDocumentListener(blueField, blueSlider));

        JPanel controlsPanel = new JPanel(new GridLayout(3, 3, 5, 5));
        controlsPanel.add(new JLabel("Red:"));
        controlsPanel.add(redSlider);
        controlsPanel.add(redField);

        controlsPanel.add(new JLabel("Green:"));
        controlsPanel.add(greenSlider);
        controlsPanel.add(greenField);

        controlsPanel.add(new JLabel("Blue:"));
        controlsPanel.add(blueSlider);
        controlsPanel.add(blueField);

        add(controlsPanel, BorderLayout.CENTER); // controls in the middle
        add(colorPreviewPanel, BorderLayout.NORTH); // rectangle at the top
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        if (source == redSlider) {
            redField.setText(String.valueOf(source.getValue()));
        } else if (source == greenSlider) {
            greenField.setText(String.valueOf(source.getValue()));
        } else if (source == blueSlider) {
            blueField.setText(String.valueOf(source.getValue()));
        }
        updateColor();
    }

    private void updateColor() {
        try {
            int r = Integer.parseInt(redField.getText());
            int g = Integer.parseInt(greenField.getText());
            int b = Integer.parseInt(blueField.getText());

            r = Math.max(0, Math.min(255, r));
            g = Math.max(0, Math.min(255, g));
            b = Math.max(0, Math.min(255, b));

            Color newColor = new Color(r, g, b);
            colorPreviewPanel.setBackground(newColor);
        } catch (NumberFormatException ex) {
            colorPreviewPanel.setBackground(Color.BLACK);
        }
    }

    private class SliderDocumentListener implements DocumentListener { // Syncs text field changes to slider
        private final JTextField textField;
        private final JSlider slider;
        private boolean updating = false;

        public SliderDocumentListener(JTextField textField, JSlider slider) {
            this.textField = textField;
            this.slider = slider;
        }

        private void updateSlider() {
            if (updating) return;
            try {
                int value = Integer.parseInt(textField.getText());
                if (value >= 0 && value <= 255) {
                    updating = true;
                    slider.setValue(value);
                    updating = false;
                    updateColor(); // Update color preview when text changes
                }
            } catch (NumberFormatException e) {
                // Ignore if text field is empty or contains invalid input
            }
        }

        @Override
        public void insertUpdate(DocumentEvent e) {
            updateSlider();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateSlider();
        }

        @Override
        public void changedUpdate(DocumentEvent e) { 
            // Not used for plain text fields
        }
    }

    // Main method to run the program and test the ColorChooser component
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Custom Color Chooser Demo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(400, 300);
            frame.add(new COMP_2800_Lab4_Yousif_Elia_110170930());
            frame.setVisible(true);
        });
    }
}
