package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SidebarPanel extends JPanel {

    private static final Color BG_DEFAULT = new Color(245, 245, 245);
    private static final Color BG_HOVER = new Color(200, 220, 255);
    private static final Color BG_ACTIVE = new Color(220, 230, 255);

    private final Subjects.ConnectionProvider connectionProvider;
    private Subjects subjectsPanel;

    private final List<JPanel> itemPanels = new ArrayList<>();
    private JPanel activeItemPanel = null;

    private String currentStudentId;
    private String currentStudentGroup;

    public SidebarPanel(JPanel contentPanel, JTable table, Subjects.ConnectionProvider connectionProvider) {
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "connectionProvider required");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_DEFAULT);
        setPreferredSize(new Dimension(180, 0));
        setBorder(new EmptyBorder(10, 0, 10, 0));

        String[] items = {"Dashboard", "Timetable", "Subjects", "Notifications", "Settings"};
        for (String item : items) {
            JPanel itemPanel = createSidebarItem(item, contentPanel, table);
            itemPanels.add(itemPanel);
            add(itemPanel);

            if (item.equals("Dashboard")) setActiveItem(itemPanel);
        }

        add(Box.createVerticalGlue());
        add(createLogoutButton());
    }

    public void setCurrentStudent(String studentId, String studentGroup) {
        this.currentStudentId = studentId;
        this.currentStudentGroup = studentGroup;
        System.out.println("SidebarPanel.setCurrentStudent(): " + studentId + ", " + studentGroup);
    }

   public void initSubjectsPanel() {
    if (currentStudentId == null || currentStudentGroup == null) {
        System.out.println("Student info not set yet!");
        return;
    }
    if (subjectsPanel == null) {
        subjectsPanel = new Subjects(connectionProvider, currentStudentId, currentStudentGroup);
    } else {
        subjectsPanel.setStudent(currentStudentId, currentStudentGroup);
    }
}



    // --- Sidebar GUI ---
    private JPanel createSidebarItem(String name, JPanel contentPanel, JTable table) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setMaximumSize(new Dimension(180, 50));
        itemPanel.setBackground(BG_DEFAULT);
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(name);
        label.setFont(new Font("Poppins", Font.PLAIN, 14));
        label.setBorder(new EmptyBorder(5, 10, 5, 5));
        itemPanel.add(label);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { if (itemPanel != activeItemPanel) itemPanel.setBackground(BG_HOVER);}
            @Override public void mouseExited(MouseEvent e) { if (itemPanel != activeItemPanel) itemPanel.setBackground(BG_DEFAULT);}
            @Override public void mouseClicked(MouseEvent e) {
                setActiveItem(itemPanel);
                renderContent(name, contentPanel, table);
            }
        });

        return itemPanel;
    }

    private void setActiveItem(JPanel newActive) {
        if (activeItemPanel != null) activeItemPanel.setBackground(BG_DEFAULT);
        activeItemPanel = newActive;
        activeItemPanel.setBackground(BG_ACTIVE);
    }

    private void renderContent(String item, JPanel contentPanel, JTable table) {
        contentPanel.removeAll();
        switch (item) {
            case "Dashboard" -> contentPanel.add(new DashboardPanel(), BorderLayout.CENTER);
            case "Timetable" -> contentPanel.add(new JScrollPane(table), BorderLayout.CENTER);
          case "Subjects" -> {
    if (subjectsPanel == null) {
        // Always pass the current student info
        subjectsPanel = new Subjects(connectionProvider, currentStudentId, currentStudentGroup);
    } else {
        // Update student info if already created
        subjectsPanel.setStudent(currentStudentId, currentStudentGroup);
    }
    contentPanel.add(subjectsPanel, BorderLayout.CENTER);
}


            case "Settings" -> contentPanel.add(new SettingsPanel(), BorderLayout.CENTER);
            case "Notifications" -> contentPanel.add(new NotificationsPanel(), BorderLayout.CENTER);
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createLogoutButton() {
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setMaximumSize(new Dimension(180, 50));
        logoutPanel.setBackground(BG_DEFAULT);

        JLabel logoutLabel = new JLabel("↩️ Logout");
        logoutLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        logoutLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        logoutPanel.add(logoutLabel);

        logoutPanel.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { logoutPanel.setBackground(BG_HOVER);}
            @Override public void mouseExited(MouseEvent e) { logoutPanel.setBackground(BG_DEFAULT);}
            @Override public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) System.exit(0);
            }
        });

        return logoutPanel;
    }
}
