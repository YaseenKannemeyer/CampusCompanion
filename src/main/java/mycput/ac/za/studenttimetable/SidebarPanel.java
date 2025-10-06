package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

public class SidebarPanel extends JPanel {

    private static final Color BG_DEFAULT = new Color(245, 245, 245);
    private static final Color BG_HOVER = new Color(200, 220, 255);
    private static final Color BG_ACTIVE = new Color(220, 230, 255);

    private final Subjects.ConnectionProvider connectionProvider;
    private final JPanel contentPanel;   // will now use CardLayout

    // Panels created once and reused
    private DashboardPanel dashboardPanel;
    private JPanel timetablePanel;
    private Subjects subjectsPanel;
    private SettingsPanel settingsPanel;
    private NotificationsPanel notificationsPanel;

    private final List<JPanel> itemPanels = new ArrayList<>();
    private JPanel activeItemPanel = null;

    private String currentStudentId;
    private String currentStudentGroup;

    public SidebarPanel(JPanel contentPanel, JTable table, Subjects.ConnectionProvider connectionProvider) {
        this.contentPanel = contentPanel;
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "connectionProvider required");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(BG_DEFAULT);
        setPreferredSize(new Dimension(180, 0));
        setBorder(new EmptyBorder(10, 0, 10, 0));

        add(createLogoPanel());
        add(Box.createRigidArea(new Dimension(0, 20)));

        initContentPanels(table); // initialize panels with CardLayout

        // Sidebar items
        String[] items = {"Dashboard", "Timetable", "Subjects", "Notifications", "Settings"};
        for (String item : items) {
            JPanel itemPanel = createSidebarItem(item);
            itemPanels.add(itemPanel);
            add(itemPanel);

            if (item.equals("Dashboard")) {
                setActiveItem(itemPanel);
                renderContent("Dashboard");
            }
        }

        add(Box.createVerticalGlue());
        add(createLogoutButton());
    }

    private void initContentPanels(JTable table) {
        // Use CardLayout for contentPanel
        contentPanel.setLayout(new CardLayout());

        dashboardPanel = new DashboardPanel();

        timetablePanel = new JPanel(new BorderLayout());
        timetablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        subjectsPanel = new Subjects(connectionProvider, "", "");
        settingsPanel = new SettingsPanel();
        notificationsPanel = new NotificationsPanel();

        // Add all panels to CardLayout with unique name
        contentPanel.add(dashboardPanel, "Dashboard");
        contentPanel.add(timetablePanel, "Timetable");
        contentPanel.add(subjectsPanel, "Subjects");
        contentPanel.add(settingsPanel, "Settings");
        contentPanel.add(notificationsPanel, "Notifications");
    }

    public void setCurrentStudent(String studentId, String studentGroup) {
    this.currentStudentId = studentId;
    this.currentStudentGroup = studentGroup;

    try {
        // Fetch StudentDomain from DAO
        StudentDAO studentDAO = new StudentDAO();
        StudentDomain student = studentDAO.getStudentProfile(studentId);

        if (dashboardPanel != null && student != null) {
            dashboardPanel.setStudent(student); // pass StudentDomain
        }

        if (subjectsPanel != null) { subjectsPanel.setStudent(studentId, studentGroup); }

        System.out.println("SidebarPanel.setCurrentStudent(): " + studentId + ", " + studentGroup);

    } catch (SQLException e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Failed to load student info: " + e.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }
}


    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(BG_DEFAULT);
        logoPanel.setMaximumSize(new Dimension(180, 80));
        logoPanel.setLayout(new BorderLayout());

        try {
            ImageIcon logoIcon = new ImageIcon(getClass().getResource("/icons/Logo.png"));
            Image scaled = logoIcon.getImage().getScaledInstance(120, 60, Image.SCALE_SMOOTH);
            JLabel logoLabel = new JLabel(new ImageIcon(scaled));
            logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            logoPanel.add(logoLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            JLabel fallback = new JLabel("Student Timetable", SwingConstants.CENTER);
            fallback.setFont(new Font("Poppins", Font.BOLD, 16));
            logoPanel.add(fallback, BorderLayout.CENTER);
        }

        return logoPanel;
    }

    private JPanel createSidebarItem(String name) {
        JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setMaximumSize(new Dimension(180, 50));
        itemPanel.setBackground(BG_DEFAULT);
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(name);
        label.setFont(new Font("Poppins", Font.PLAIN, 14));
        label.setBorder(new EmptyBorder(5, 10, 5, 5));
        itemPanel.add(label);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (itemPanel != activeItemPanel) itemPanel.setBackground(BG_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (itemPanel != activeItemPanel) itemPanel.setBackground(BG_DEFAULT);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveItem(itemPanel);
                renderContent(name);
            }
        });

        return itemPanel;
    }

    private void setActiveItem(JPanel newActive) {
        if (activeItemPanel != null) activeItemPanel.setBackground(BG_DEFAULT);
        activeItemPanel = newActive;
        if (activeItemPanel != null) activeItemPanel.setBackground(BG_ACTIVE);
    }

    public void renderContent(String item) {
    CardLayout cl = (CardLayout) contentPanel.getLayout();

    try {
        // Update student info if needed
        if (currentStudentId != null) {
            StudentDAO studentDAO = new StudentDAO();
            StudentDomain student = studentDAO.getStudentProfile(currentStudentId);

            switch (item) {
                case "Dashboard" -> {
                    if (dashboardPanel != null && student != null) {
                        dashboardPanel.setStudent(student);
                    }
                }
                case "Subjects" -> subjectsPanel.setStudent(currentStudentId, currentStudentGroup);
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }

    cl.show(contentPanel, item); // switch visible panel
    contentPanel.revalidate();
    contentPanel.repaint();
}

    private JPanel createLogoutButton() {
        JPanel logoutPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setMaximumSize(new Dimension(180, 50));
        logoutPanel.setBackground(BG_DEFAULT);
        logoutPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel logoutLabel = new JLabel("↩️ Logout");
        logoutLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        logoutLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
        logoutPanel.add(logoutLabel);

        logoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { logoutPanel.setBackground(BG_HOVER); }

            @Override
            public void mouseExited(MouseEvent e) { logoutPanel.setBackground(BG_DEFAULT); }

            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(SidebarPanel.this);
                int confirm = JOptionPane.showConfirmDialog(topFrame, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION && topFrame instanceof StudentTimeTable mainFrame) {
                    Session.setStudent(null, null);
                    LoginForm loginForm = new LoginForm(mainFrame, connectionProvider);
                    mainFrame.showLoginPanel(loginForm);
                    setActiveItem(null);
                }
            }
        });

        return logoutPanel;
    }
}
