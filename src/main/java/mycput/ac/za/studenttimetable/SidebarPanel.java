package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

public class SidebarPanel extends JPanel {

    // ================= COLORS =================
    private static final Color SIDEBAR_BG = new Color(255, 255, 255, 60); // translucent white
    private static final Color SIDEBAR_HOVER = new Color(72, 196, 255, 180); // blue hover
    private static final Color SIDEBAR_ACTIVE = new Color(72, 196, 255, 220); // blue active
    private static final Color ITEM_TEXT = new Color(28, 66, 138); // dark blue text
    private static final Color INNER_SHADOW = new Color(0, 0, 0, 30);

    private final Subjects.ConnectionProvider connectionProvider;
    private final JPanel contentPanel;

    private DashboardPanel dashboardPanel;
    private JPanel timetablePanel;
    private Subjects subjectsPanel;
    private SettingsPanel settingsPanel;
    private NotificationsPanel notificationsPanel;

    private final List<FrostedGlassPanel> itemPanels = new ArrayList<>();
    private FrostedGlassPanel activeItemPanel = null;

    private String currentStudentId;
    private String currentStudentGroup;

    public SidebarPanel(JPanel contentPanel, JTable table, Subjects.ConnectionProvider connectionProvider) {
        this.contentPanel = contentPanel;
        this.connectionProvider = Objects.requireNonNull(connectionProvider, "ConnectionProvider required");

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        setPreferredSize(new Dimension(200, 0));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        add(createLogoPanel());
        add(Box.createRigidArea(new Dimension(0, 20)));

        initContentPanels(table);

        // Sidebar items
        String[] items = {"Dashboard", "Timetable", "Subjects", "Notifications", "Settings"};
        for (String item : items) {
            FrostedGlassPanel panel = createSidebarItem(item);
            itemPanels.add(panel);
            add(panel);

            if (item.equals("Dashboard")) {
                setActiveItem(panel);
                renderContent("Dashboard");
            }
        }

        add(Box.createVerticalGlue());
        add(createLogoutButton());
    }

    // ================= INIT CONTENT PANELS =================
    private void initContentPanels(JTable table) {
        contentPanel.setLayout(new CardLayout());

        dashboardPanel = new DashboardPanel(connectionProvider, null, null);

        timetablePanel = new JPanel(new BorderLayout());
        timetablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        subjectsPanel = new Subjects(connectionProvider, "", "");
        settingsPanel = new SettingsPanel();
        notificationsPanel = new NotificationsPanel();

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
            StudentDAO studentDAO = new StudentDAO();
            StudentDomain student = studentDAO.getStudentProfile(studentId);

            if (dashboardPanel != null && student != null) dashboardPanel.setStudent(student);
            if (subjectsPanel != null) subjectsPanel.setStudent(studentId, studentGroup);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load student info: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= LOGO =================
private FrostedGlassPanel createLogoPanel() {
    FrostedGlassPanel logoPanel = new FrostedGlassPanel();
    logoPanel.setLayout(new BorderLayout());

    try {
        ImageIcon icon = new ImageIcon(getClass().getResource("/icons/Logo.png"));
        Image scaled = icon.getImage().getScaledInstance(120, 60, Image.SCALE_SMOOTH);
        JLabel logo = new JLabel(new ImageIcon(scaled));
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        logoPanel.add(logo, BorderLayout.CENTER);

        // Set panel size exactly to the logo
        logoPanel.setPreferredSize(new Dimension(200, 90));
        logoPanel.setMaximumSize(new Dimension(200, 90));
        logoPanel.setMinimumSize(new Dimension(200, 90));

    } catch (Exception e) {
        JLabel fallback = new JLabel("Student Timetable", SwingConstants.CENTER);
        fallback.setFont(new Font("Poppins", Font.BOLD, 16));
        fallback.setForeground(ITEM_TEXT);
        logoPanel.add(fallback, BorderLayout.CENTER);

        // fallback size
        logoPanel.setPreferredSize(new Dimension(150, 30));
        logoPanel.setMaximumSize(new Dimension(150, 30));
        logoPanel.setMinimumSize(new Dimension(150, 30));
    }

    return logoPanel;
}

    // ================= SIDEBAR ITEM =================
    private FrostedGlassPanel createSidebarItem(String name) {
        FrostedGlassPanel itemPanel = new FrostedGlassPanel();
        itemPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setMaximumSize(new Dimension(180, 50));
        itemPanel.setBackground(SIDEBAR_BG);
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(name);
        label.setFont(new Font("Poppins", Font.PLAIN, 14));
        label.setForeground(ITEM_TEXT);
        label.setBorder(new EmptyBorder(5, 10, 5, 5));
        itemPanel.add(label);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (itemPanel != activeItemPanel) itemPanel.animateBackground(itemPanel.getBackground(), SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (itemPanel != activeItemPanel) itemPanel.animateBackground(itemPanel.getBackground(), SIDEBAR_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveItem(itemPanel);
                renderContent(name);
            }
        });

        return itemPanel;
    }

    private void setActiveItem(FrostedGlassPanel newActive) {
        if (activeItemPanel != null) activeItemPanel.animateBackground(activeItemPanel.getBackground(), SIDEBAR_BG);
        activeItemPanel = newActive;
        if (activeItemPanel != null) activeItemPanel.animateBackground(activeItemPanel.getBackground(), SIDEBAR_ACTIVE);
    }

    public void renderContent(String item) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();

        try {
            if (currentStudentId != null) {
                StudentDAO studentDAO = new StudentDAO();
                StudentDomain student = studentDAO.getStudentProfile(currentStudentId);

                if ("Dashboard".equals(item) && dashboardPanel != null && student != null)
                    dashboardPanel.setStudent(student);
                else if ("Subjects".equals(item) && subjectsPanel != null)
                    subjectsPanel.setStudent(currentStudentId, currentStudentGroup);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        cl.show(contentPanel, item);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= LOGOUT =================
    private FrostedGlassPanel createLogoutButton() {
        FrostedGlassPanel logoutPanel = new FrostedGlassPanel();
        logoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setMaximumSize(new Dimension(180, 50));
        logoutPanel.setBackground(SIDEBAR_BG);
        logoutPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel("↩️ Logout");
        label.setFont(new Font("Poppins", Font.PLAIN, 14));
        label.setForeground(ITEM_TEXT);
        label.setBorder(new EmptyBorder(5, 10, 5, 5));
        logoutPanel.add(label);

        logoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { logoutPanel.animateBackground(logoutPanel.getBackground(), SIDEBAR_HOVER); }

            @Override
            public void mouseExited(MouseEvent e) { logoutPanel.animateBackground(logoutPanel.getBackground(), SIDEBAR_BG); }

            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(SidebarPanel.this);
                int confirm = JOptionPane.showConfirmDialog(topFrame, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION && topFrame instanceof StudentTimeTable mainFrame) {
                    Session.setStudent(null, null); // clear session
                    LoginForm loginForm = new LoginForm(mainFrame, connectionProvider);
                    mainFrame.showLoginPanel(loginForm);
                    setActiveItem(null);
                }
            }
        });

        return logoutPanel;
    }

    // ================= GLASS PANEL BASE =================
    private static class FrostedGlassPanel extends JPanel {
        public FrostedGlassPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 20;

            g2.setColor(SIDEBAR_BG);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(INNER_SHADOW);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }

        public void animateBackground(Color start, Color end) {
            Timer timer = new Timer(10, null);
            final int steps = 15;
            final float[] startRGB = start.getRGBComponents(null);
            final float[] endRGB = end.getRGBComponents(null);
            final int[] count = {0};

            timer.addActionListener(e -> {
                float ratio = (float) count[0] / steps;
                Color next = new Color(
                        startRGB[0] + ratio * (endRGB[0] - startRGB[0]),
                        startRGB[1] + ratio * (endRGB[1] - startRGB[1]),
                        startRGB[2] + ratio * (endRGB[2] - startRGB[2]),
                        startRGB[3] + ratio * (endRGB[3] - startRGB[3])
                );
                setBackground(next);
                repaint();
                count[0]++;
                if (count[0] > steps) timer.stop();
            });
            timer.start();
        }
    }
}
