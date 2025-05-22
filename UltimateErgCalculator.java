package src;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class UltimateErgCalculator {
    private static CardLayout cardLayout = new CardLayout();
    private static JPanel mainPanel = new JPanel(cardLayout);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Ultimate Erg Calculator");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Main Menu Panel
        JPanel menuPanel = new JPanel() {
            private Image backgroundImage = new ImageIcon("src/src/resources/RowingBackground.png").getImage();

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false); // ensures transparency so image shows

        JLabel title = new JLabel("Ultimate Erg Calculator", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 64));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 100)));
        menuPanel.add(title);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 80)));

        String[] options = { "Watts ↔ Split", "Time/Distance Predictor", "Boat Time Estimator" };

        for (String option : options) {
            JButton btn = createMenuButton(option);
            btn.addActionListener(e -> cardLayout.show(mainPanel, option));
            menuPanel.add(Box.createRigidArea(new Dimension(0, 40)));
            menuPanel.add(btn);
        }

        mainPanel.add(menuPanel, "Main");
        mainPanel.add(createWattsSplitPanel(), "Watts ↔ Split");
        mainPanel.add(createPaulLawPanel(), "Paul's Law Predictor");
        mainPanel.add(createBoatTimePanel(), "Boat Time Estimator");

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private static JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setBackground(new Color(0, 102, 204));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setFont(new Font("SansSerif", Font.BOLD, 48));
        btn.setMaximumSize(new Dimension(600, 100));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25));
        return btn;
    }

    private static JPanel createWattsSplitPanel() {
        JPanel panel = basePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Watts ↔ Split Converter", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instruction = new JLabel("Enter either watts (e.g. 200) or split (e.g. 1:45.0)");
        instruction.setFont(new Font("SansSerif", Font.PLAIN, 24));
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        inputPanel.setOpaque(false);

        JTextField input = new JTextField(15);
        input.setFont(new Font("SansSerif", Font.PLAIN, 24));
        input.setMaximumSize(new Dimension(300, 50));

        JButton convert = new JButton("Convert");
        convert.setFont(new Font("SansSerif", Font.BOLD, 24));
        convert.setBackground(new Color(100, 153, 76));
        convert.setForeground(Color.WHITE);

        JTextArea result = new JTextArea(3, 30);
        result.setFont(new Font("Monospaced", Font.PLAIN, 24));
        result.setEditable(false);
        result.setBackground(new Color(240, 240, 240));
        result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        convert.addActionListener(e -> {
            String val = input.getText();
            try {
                if (val.contains(":")) {
                    double splitSec = parseTime(val);
                    double watts = 2.8 / Math.pow(splitSec / 500.0, 3);
                    result.setText(String.format("Watts: %.1f\n(Conversion formula: 2.8/(split/500)^3)", watts));
                } else {
                    double watts = Double.parseDouble(val);
                    double split = 500 * Math.pow(2.8 / watts, 1.0 / 3.0);
                    result.setText(String.format("Split: %s\n(Conversion formula: 500*(2.8/watts)^(1/3))", formatTime(split)));
                }
            } catch (Exception ex) {
                result.setText("Invalid input format.\nFor watts: enter a number (e.g. 200)\nFor split: use mm:ss.s format (e.g. 1:45.0)");
            }
        });

        inputPanel.add(input);
        inputPanel.add(convert);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(instruction);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(inputPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(new JScrollPane(result));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(backButton());
        
        return panel;
    }

    private static JPanel createPaulLawPanel() {
        JPanel panel = basePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Paul's Law Predictor", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instruction = new JLabel("Select calculation type and enter required values");
        instruction.setFont(new Font("SansSerif", Font.PLAIN, 24));
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);

        String[] choices = { 
            "Calculate split for a target distance",
            "Calculate time for a target split", 
            "Calculate distance from time and split" 
        };
        JComboBox<String> choiceBox = new JComboBox<>(choices);
        choiceBox.setFont(new Font("SansSerif", Font.PLAIN, 20));
        choiceBox.setMaximumSize(new Dimension(500, 50));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        inputPanel.setOpaque(false);
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create input fields with labels
        JPanel distancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        distancePanel.setOpaque(false);
        JLabel distanceLabel = new JLabel("Known Distance (m):");
        distanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JTextField distanceField = new JTextField(10);
        distanceField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        distancePanel.add(distanceLabel);
        distancePanel.add(distanceField);

        JPanel knownValuePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        knownValuePanel.setOpaque(false);
        JLabel knownValueLabel = new JLabel("Known Split (mm:ss.s):");
        knownValueLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JTextField knownValueField = new JTextField(10);
        knownValueField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        knownValuePanel.add(knownValueLabel);
        knownValuePanel.add(knownValueField);

        JPanel targetPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        targetPanel.setOpaque(false);
        JLabel targetLabel = new JLabel("Target Distance/Split:");
        targetLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JTextField targetField = new JTextField(10);
        targetField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        targetPanel.add(targetLabel);
        targetPanel.add(targetField);

        inputPanel.add(distancePanel);
        inputPanel.add(knownValuePanel);
        inputPanel.add(targetPanel);

        JTextArea output = new JTextArea(4, 40);
        output.setFont(new Font("Monospaced", Font.PLAIN, 20));
        output.setEditable(false);
        output.setBackground(new Color(240, 240, 240));
        output.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton compute = new JButton("Calculate");
        compute.setFont(new Font("SansSerif", Font.BOLD, 24));
        compute.setBackground(new Color(0, 153, 76));
        compute.setForeground(Color.WHITE);

        // Update UI based on selection
        choiceBox.addActionListener(e -> {
            int selection = choiceBox.getSelectedIndex();
            distanceLabel.setText(selection == 2 ? "Total Time (mm:ss.s):" : "Known Distance (m):");
            knownValueLabel.setText(selection == 0 ? "Known Split (mm:ss.s):" : 
                                  selection == 1 ? "Known Time (mm:ss.s):" : "Split (mm:ss.s):");
            targetLabel.setText(selection == 0 ? "Target Distance (m):" : 
                              selection == 1 ? "Target Split (mm:ss.s):" : "");
            targetPanel.setVisible(selection != 2);
        });

        compute.addActionListener(e -> {
            try {
                int selection = choiceBox.getSelectedIndex();
                if (selection == 0) {
                    // Split for given distance
                    double knownDistance = Double.parseDouble(distanceField.getText());
                    double knownSplit = parseTime(knownValueField.getText());
                    double targetDistance = Double.parseDouble(targetField.getText());
                    double targetSplit = knownSplit * Math.pow(targetDistance / knownDistance, 0.067);
                    output.setText(String.format("Estimated Split for %,.0fm: %s\n(Paul's Law formula: split × (distance/new_distance)^0.067)", 
                            targetDistance, formatTime(targetSplit)));
                } else if (selection == 1) {
                    // Time for given split
                    double knownDistance = Double.parseDouble(distanceField.getText());
                    double knownTime = parseTime(knownValueField.getText());
                    double targetSplit = parseTime(targetField.getText());
                    double knownSplit = knownTime / (knownDistance / 500);
                    double targetTime = (targetSplit / knownSplit) * knownTime;
                    output.setText(String.format("Estimated Time: %s\n(Formula: (target_split/known_split) × known_time)", 
                            formatTime(targetTime)));
                } else {
                    // Distance for time + split
                    double totalTime = parseTime(distanceField.getText());
                    double split = parseTime(knownValueField.getText());
                    double distance = (totalTime / split) * 500;
                    output.setText(String.format("Estimated Distance: %,.0f meters\n(Formula: (total_time/split) × 500)", distance));
                }
            } catch (Exception ex) {
                output.setText("Invalid input format.\nFor distances: enter a number (e.g. 2000)\nFor times/splits: use mm:ss.s format (e.g. 7:20.0)");
            }
        });

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(instruction);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(choiceBox);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(inputPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(compute);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(new JScrollPane(output));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(backButton());
        
        return panel;
    }

    private static JPanel createBoatTimePanel() {
        JPanel panel = basePanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Boat Time Estimator", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 36));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instruction = new JLabel("Enter erg times for all rowers and select boat type");
        instruction.setFont(new Font("SansSerif", Font.PLAIN, 24));
        instruction.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Boat type selection
        JPanel boatTypePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        boatTypePanel.setOpaque(false);
        JLabel boatLabel = new JLabel("Boat Type:");
        boatLabel.setFont(new Font("SansSerif", Font.PLAIN, 20));
        JComboBox<String> boatChoice = new JComboBox<>(new String[] {
            "1x (Single)", "2- (Pair)", "2x (Double)", "4- (Four)", "4x (Quad)", "8+ (Eight)"
        });
        boatChoice.setFont(new Font("SansSerif", Font.PLAIN, 20));
        boatTypePanel.add(boatLabel);
        boatTypePanel.add(boatChoice);

        // Time input panels - will be dynamically shown/hidden
        JPanel singleInputPanel = createSingleInputPanel();
        JPanel pairInputPanel = createMultiInputPanel(2, "Pair Times (MM:SS):");
        JPanel fourInputPanel = createMultiInputPanel(4, "Four Times (MM:SS):");
        JPanel eightInputPanel = createMultiInputPanel(8, "Eight Times (MM:SS):");

        // Container for dynamic input panels
        JPanel dynamicInputContainer = new JPanel();
        dynamicInputContainer.setLayout(new BoxLayout(dynamicInputContainer, BoxLayout.Y_AXIS));
        dynamicInputContainer.setOpaque(false);
        dynamicInputContainer.add(singleInputPanel);

        // Result display
        JTextArea result = new JTextArea(5, 40);
        result.setFont(new Font("Monospaced", Font.PLAIN, 20));
        result.setEditable(false);
        result.setBackground(new Color(240, 240, 240));
        result.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Calculate button
        JButton compute = new JButton("Calculate Race Time");
        compute.setFont(new Font("SansSerif", Font.BOLD, 24));
        compute.setBackground(new Color(0, 153, 76));
        compute.setForeground(Color.WHITE);

        // Boat type selection listener
        boatChoice.addActionListener(e -> {
            dynamicInputContainer.removeAll();
            int selected = boatChoice.getSelectedIndex();
            
            switch (selected) {
                case 0: // 1x
                    dynamicInputContainer.add(singleInputPanel);
                    break;
                case 1: // 2-
                case 2: // 2x
                    dynamicInputContainer.add(pairInputPanel);
                    break;
                case 3: // 4-
                case 4: // 4x
                    dynamicInputContainer.add(fourInputPanel);
                    break;
                case 5: // 8+
                    dynamicInputContainer.add(eightInputPanel);
                    break;
            }
            
            dynamicInputContainer.revalidate();
            dynamicInputContainer.repaint();
        });

        // Calculation logic
        compute.addActionListener(e -> {
            try {
                int boatType = boatChoice.getSelectedIndex() + 1; // Convert to 1-6
                double avgTime = calculateAverageTime(boatType, singleInputPanel, pairInputPanel, fourInputPanel, eightInputPanel);
                
                double factor = getBoatFactor(boatType);
                double boatTime = avgTime * factor;
                double raceTime = boatTime * 0.75 - 6.2;
                
                String boatName = boatChoice.getSelectedItem().toString();
                result.setText(String.format(
                    "Boat Type: %s\n" +
                    "Average Split: %s\n" +
                    "Adjusted Time: %s\n" +
                    "Estimated Race Time: %s\n" +
                    "(Formula: (average_time × %.3f × 0.75) - 6.2 seconds)",
                    boatName, formatTime(avgTime), formatTime(boatTime), formatTime(raceTime), factor
                ));
            } catch (Exception ex) {
                result.setText("Invalid input format.\nPlease enter all times in MM:SS format (e.g. 7:20.0)");
            }
        });

        // Add components to main panel
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(title);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(instruction);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(boatTypePanel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(dynamicInputContainer);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(compute);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(new JScrollPane(result));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(backButton());
        
        return panel;
    }

    private static JPanel createSingleInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        panel.setOpaque(false);
        
        JLabel label = new JLabel("Single Sculler Time (MM:SS):");
        label.setFont(new Font("SansSerif", Font.PLAIN, 20));
        
        JTextField timeField = new JTextField(10);
        timeField.setFont(new Font("SansSerif", Font.PLAIN, 20));
        timeField.setName("singleTime");
        
        panel.add(label);
        panel.add(timeField);
        
        return panel;
    }

    private static JPanel createMultiInputPanel(int numRowers, String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JPanel fieldsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        fieldsPanel.setOpaque(false);
        fieldsPanel.setMaximumSize(new Dimension(600, numRowers * 40));
        
        for (int i = 1; i <= numRowers; i++) {
            JLabel label = new JLabel("Rower " + i + ":");
            label.setFont(new Font("SansSerif", Font.PLAIN, 18));
            
            JTextField field = new JTextField(10);
            field.setFont(new Font("SansSerif", Font.PLAIN, 18));
            field.setName("rower" + i);
            
            fieldsPanel.add(label);
            fieldsPanel.add(field);
        }
        
        panel.add(fieldsPanel);
        return panel;
    }

    private static double calculateAverageTime(int boatType, JPanel... inputPanels) throws Exception {
        List<Double> times = new ArrayList<>();
        
        for (JPanel panel : inputPanels) {
            Component[] components = panel.getComponents();
            for (Component comp : components) {
                if (comp instanceof JTextField) {
                    JTextField field = (JTextField) comp;
                    if (!field.getText().isEmpty()) {
                        times.add(parseTime(field.getText()));
                    }
                } else if (comp instanceof Container) {
                    // Check nested components for GridLayout panels
                    Component[] subComps = ((Container) comp).getComponents();
                    for (Component subComp : subComps) {
                        if (subComp instanceof JTextField) {
                            JTextField field = (JTextField) subComp;
                            if (!field.getText().isEmpty()) {
                                times.add(parseTime(field.getText()));
                            }
                        }
                    }
                }
            }
        }
        
        if (times.isEmpty()) {
            throw new Exception("No times entered");
        }
        
        // Calculate average
        double sum = 0;
        for (double time : times) {
            sum += time;
        }
        
        return sum / times.size();
    }

    private static double getBoatFactor(int boatType) {
        switch (boatType) {
            case 1: return 1.154;  // 1x
            case 2: return 1.077;  // 2-
            case 3: return 1.053;  // 2x
            case 5: return 0.975;  // 4x
            case 6: return 0.937;  // 8+
            default: return 1.0;   // 4- (case 4)
        }
    }

    private static JPanel basePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(200, 125, 110));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }

    private static JButton backButton() {
        JButton back = new JButton("Back to Main Menu");
        back.setFont(new Font("SansSerif", Font.PLAIN, 24));
        back.setBackground(new Color(200, 50, 50));
        back.setForeground(Color.WHITE);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.addActionListener(e -> cardLayout.show(mainPanel, "Main"));
        return back;
    }

    private static double parseTime(String time) throws Exception {
        if (time == null || time.trim().isEmpty()) {
            throw new Exception("Empty time value");
        }
        
        String[] parts = time.split(":");
        if (parts.length != 2) {
            throw new Exception("Invalid time format");
        }
        
        try {
            int minutes = Integer.parseInt(parts[0]);
            double seconds = Double.parseDouble(parts[1]);
            return minutes * 60 + seconds;
        } catch (NumberFormatException e) {
            throw new Exception("Invalid number format");
        }
    }

    private static String formatTime(double seconds) {
        int min = (int) (seconds / 60);
        double sec = seconds % 60;
        return String.format("%d:%04.1f", min, sec);
    }
}