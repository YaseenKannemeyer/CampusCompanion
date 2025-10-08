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
    private static final Color SIDEBAR_GRADIENT_START = new Color(70, 130, 180, 230); // top (steel blue)
    private static final Color SIDEBAR_GRADIENT_END = new Color(100, 180, 220, 230);  // bottom (lighter cyan-blue)
    private static final Color SIDEBAR_HOVER = new Color(90, 160, 230, 240);  // slightly brighter blue
    private static final Color SIDEBAR_ACTIVE = new Color(40, 120, 200, 255); // deeper active blue
    private static final Color ITEM_TEXT = Color.WHITE;
    private static final Color INNER_SHADOW = new Color(0, 0, 0, 50);
    private static final Color RIGHT_BORDER = new Color(0, 0, 0, 50);

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
        setPreferredSize(new Dimension(220, 0));
        setBorder(new EmptyBorder(15, 15, 15, 15));

        add(createLogoPanel());
        add(Box.createRigidArea(new Dimension(0, 25)));

        initContentPanels(table);

        String[] items = {"Dashboard", "Timetable", "Subjects", "Notifications", "Settings"};
        for (String item : items) {
            FrostedGlassPanel panel = createSidebarItem(item);
            itemPanels.add(panel);
            add(panel);

            if (item.equals("Dashboard")) {
                setActiveItem(panel);
                renderContent("Dashboard");
            }

            add(Box.createRigidArea(new Dimension(0, 10)));
        }

        add(Box.createVerticalGlue());
        add(createLogoutButton());
    }

    private void initContentPanels(JTable table) {
        contentPanel.setLayout(new CardLayout());

        dashboardPanel = new DashboardPanel(connectionProvider, null, null);
        timetablePanel = new JPanel(new BorderLayout());
        timetablePanel.add(new JScrollPane(table), BorderLayout.CENTER);

        subjectsPanel = new Subjects(connectionProvider, "", "");
        settingsPanel = new SettingsPanel(connectionProvider);
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
        StudentDAO dao = new StudentDAO();
        StudentDomain student = dao.getStudentProfile(studentId);

        if (dashboardPanel != null && student != null)
            dashboardPanel.setStudent(student);

        if (subjectsPanel != null)
            subjectsPanel.setStudent(studentId, studentGroup);

        if (settingsPanel != null)
            settingsPanel.setStudent(studentId, studentGroup); // <-- ADD THIS LINE

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

        int panelWidth = 200;
        int panelHeight = 120;

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/icons/Logo.png"));
            int logoHeight = 90;
            int logoWidth = (icon.getIconWidth() * logoHeight) / icon.getIconHeight();
            Image scaled = icon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);

            JLabel logo = new JLabel(new ImageIcon(scaled));
            logo.setHorizontalAlignment(SwingConstants.CENTER);
            logoPanel.add(logo, BorderLayout.CENTER);

        } catch (Exception e) {
            JLabel fallback = new JLabel("Student Timetable", SwingConstants.CENTER);
            fallback.setFont(new Font("Poppins", Font.BOLD, 20));
            fallback.setForeground(ITEM_TEXT);
            logoPanel.add(fallback, BorderLayout.CENTER);
        }

        logoPanel.setPreferredSize(new Dimension(panelWidth, panelHeight));
        logoPanel.setMaximumSize(new Dimension(panelWidth, panelHeight));
        logoPanel.setMinimumSize(new Dimension(panelWidth, panelHeight));

        return logoPanel;
    }

    private FrostedGlassPanel createSidebarItem(String name) {
        FrostedGlassPanel itemPanel = new FrostedGlassPanel();
        itemPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        itemPanel.setMaximumSize(new Dimension(200, 50));
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        itemPanel.setInactive(); // gradient inactive

        JLabel label = new JLabel(name);
        label.setFont(new Font("Poppins", Font.BOLD, 15));
        label.setForeground(ITEM_TEXT);
        label.setBorder(new EmptyBorder(5, 15, 5, 5));
        itemPanel.add(label);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (itemPanel != activeItemPanel) itemPanel.animateBackground(itemPanel.getBackground(), SIDEBAR_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (itemPanel != activeItemPanel) itemPanel.setInactive();
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
        if (activeItemPanel != null) activeItemPanel.setInactive();
        activeItemPanel = newActive;
        if (activeItemPanel != null) activeItemPanel.animateBackground(activeItemPanel.getBackground(), SIDEBAR_ACTIVE);
    }

    public void renderContent(String item) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, item);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private FrostedGlassPanel createLogoutButton() {
        FrostedGlassPanel logoutPanel = new FrostedGlassPanel();
        logoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        logoutPanel.setMaximumSize(new Dimension(200, 50));
        logoutPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutPanel.setInactive();

        JLabel label = new JLabel("↩️ Logout");
        label.setFont(new Font("Poppins", Font.BOLD, 15));
        label.setForeground(ITEM_TEXT);
        label.setBorder(new EmptyBorder(5, 15, 5, 5));
        logoutPanel.add(label);

        logoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { logoutPanel.animateBackground(logoutPanel.getBackground(), SIDEBAR_HOVER); }
            @Override
            public void mouseExited(MouseEvent e) { logoutPanel.setInactive(); }
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

    private static class FrostedGlassPanel extends JPanel {

        private boolean inactive = true;

        public FrostedGlassPanel() { setOpaque(false); }

        public void setInactive() { inactive = true; repaint(); }
        public void setActive() { inactive = false; }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 20;

            if (inactive) {
                GradientPaint gp = new GradientPaint(0, 0, SIDEBAR_GRADIENT_START, 0, getHeight(), SIDEBAR_GRADIENT_END);
                g2.setPaint(gp);
            } else {
                g2.setColor(getBackground());
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(INNER_SHADOW);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(2, 2, getWidth() - 4, getHeight() - 4, arc, arc);

            g2.setColor(RIGHT_BORDER);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());

            g2.dispose();
            super.paintComponent(g);
        }

        public void animateBackground(Color start, Color end) {
            inactive = false; // hover/active override gradient
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
