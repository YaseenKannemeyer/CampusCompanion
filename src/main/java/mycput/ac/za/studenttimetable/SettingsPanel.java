package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("⚙️ Settings");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Center Panel
        JPanel settingsForm = new JPanel();
        settingsForm.setLayout(new BoxLayout(settingsForm, BoxLayout.Y_AXIS));
        settingsForm.setOpaque(false);
        settingsForm.setBorder(new EmptyBorder(20, 40, 20, 40));

        // Theme Toggle
        JCheckBox darkMode = new JCheckBox("Enable Dark Mode");
        darkMode.setFont(new Font("Poppins", Font.PLAIN, 14));
        darkMode.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Notification Toggle
        JCheckBox notifications = new JCheckBox("Enable Notifications");
        notifications.setFont(new Font("Poppins", Font.PLAIN, 14));
        notifications.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Save Button
        JButton saveBtn = new JButton("Save Changes");
        saveBtn.setFont(new Font("Poppins", Font.BOLD, 14));
        saveBtn.setBackground(new Color(60, 120, 250));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        saveBtn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        settingsForm.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsForm.add(darkMode);
        settingsForm.add(Box.createRigidArea(new Dimension(0, 10)));
        settingsForm.add(notifications);
        settingsForm.add(Box.createRigidArea(new Dimension(0, 20)));
        settingsForm.add(saveBtn);

        add(settingsForm, BorderLayout.CENTER);
    }
}
