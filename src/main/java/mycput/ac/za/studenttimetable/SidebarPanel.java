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

    // ================= COLORS & FONTS =================
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color SECONDARY_CTA = new Color(0x1996CC);
    private static final Color PRIMARY_CTA = new Color(0xE7404A);
    private static final Color ITEM_TEXT = Color.BLACK;
    private static final Font ROBOTO_BOLD = new Font("Roboto", Font.BOLD, 14);
    private static final Font ROBOTO_MEDIUM = new Font("Roboto", Font.PLAIN, 13);

    private final Subjects.ConnectionProvider connectionProvider;
    private final JPanel contentPanel;
    private JLayeredPane layeredPane;
    private ChatBotPanel chatOverlay;

    private DashboardPanel dashboardPanel;
    private JPanel timetablePanel;
    private Subjects subjectsPanel;
    private SettingsPanel settingsPanel;
    private NotificationsPanel notificationsPanel;

    private final List<MaterialCardPanel> itemPanels = new ArrayList<>();
    private MaterialCardPanel activeItemPanel = null;

    private String currentStudentId;
    private String currentStudentGroup;

    // ================= CONSTRUCTOR =================
    public SidebarPanel(JPanel contentPanel, JTable table, Subjects.ConnectionProvider connectionProvider) {
        this.contentPanel = contentPanel;
        this.connectionProvider = Objects.requireNonNull(connectionProvider);

        setLayout(new BorderLayout());
        setBackground(PRIMARY_BG);
        setBorder(new EmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(220, 0));

        // Main items panel
        JPanel itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setOpaque(false);

        initContentPanels(table);

        // Define sidebar items
        SidebarItem[] items = {
            new SidebarItem("Dashboard", "/icons/dashboard.png"),
            new SidebarItem("Timetable", "/icons/timetable.png"),
            new SidebarItem("Subjects", "/icons/subjects.png"),
            new SidebarItem("Notifications", "/icons/notifications.png"),
            new SidebarItem("Settings", "/icons/settings.png")
        };

        // Add items
        for (SidebarItem item : items) {
            MaterialCardPanel panel = createSidebarItem(item.name, item.iconPath);
            itemPanels.add(panel);
            itemsPanel.add(panel);
            itemsPanel.add(createSeparator());

            if (item.name.equals("Dashboard")) {
                setActiveItem(panel);
                renderContent(item.name);
            }
        }

        itemsPanel.add(Box.createVerticalGlue());

       // Bottom buttons: Chat and Logout
JPanel bottomPanel = new JPanel();
bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
bottomPanel.setOpaque(false);

// Chat button
bottomPanel.add(createSidebarItem("Chat", "/icons/chat.png"));
bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

// Separator between Chat and Logout
bottomPanel.add(createSeparator());
bottomPanel.add(Box.createRigidArea(new Dimension(0, 10)));

// Logout button
bottomPanel.add(createLogoutButton());

add(itemsPanel, BorderLayout.CENTER);
add(bottomPanel, BorderLayout.SOUTH);

    }

    // ================= INIT CONTENT PANELS =================
    private void initContentPanels(JTable table) {
    contentPanel.setLayout(new CardLayout());

    dashboardPanel = new DashboardPanel(connectionProvider, null, null);

    // === Timetable Panel (with Header) ===
    timetablePanel = new JPanel(new BorderLayout());
    timetablePanel.setBackground(new Color(0xD6EEFF)); // match style
    
    // Header panel — styled like DashboardPanel
    HeaderBannerPanel header = new HeaderBannerPanel(connectionProvider, currentStudentId);
    timetablePanel.add(header, BorderLayout.NORTH);

    // Scrollable timetable table
    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
    timetablePanel.add(scroll, BorderLayout.CENTER);

    // === Other Panels ===
    subjectsPanel = new Subjects(connectionProvider, "", "");
    settingsPanel = new SettingsPanel(connectionProvider);
    notificationsPanel = new NotificationsPanel(currentStudentId);

    contentPanel.add(dashboardPanel, "Dashboard");
    contentPanel.add(timetablePanel, "Timetable");
    contentPanel.add(subjectsPanel, "Subjects");
    contentPanel.add(settingsPanel, "Settings");
    contentPanel.add(notificationsPanel, "Notifications");
}

    // ================= SET STUDENT =================
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
                settingsPanel.setStudent(studentId, studentGroup);

            if (notificationsPanel != null)
                notificationsPanel.setStudent(student);
            if (timetablePanel != null) {
    for (Component comp : timetablePanel.getComponents()) {
        if (comp instanceof HeaderBannerPanel headerPanel) {
            headerPanel.setStudent(student);
        }
    }
}
            


        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Failed to load student info: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= SIDEBAR ITEM =================
    private MaterialCardPanel createSidebarItem(String name, String iconPath) {
        MaterialCardPanel itemPanel = new MaterialCardPanel();
        itemPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        itemPanel.setMaximumSize(new Dimension(200, 50));
        itemPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel label = new JLabel(name);
        label.setFont(ROBOTO_BOLD);
        label.setForeground(ITEM_TEXT);

        if (iconPath != null) {
            try {
                ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource(iconPath)));
                Image scaled = icon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
                label.setIcon(new ImageIcon(scaled));
                label.setIconTextGap(10);
            } catch (Exception ignored) {}
        }

        itemPanel.add(label);

        itemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (itemPanel != activeItemPanel)
                    itemPanel.setBackground(SECONDARY_CTA);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (itemPanel != activeItemPanel)
                    itemPanel.setBackground(PRIMARY_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                setActiveItem(itemPanel);
                renderContent(name);

                if (name.equals("Chat") && layeredPane != null) {
                    if (chatOverlay == null) {
                        chatOverlay = new ChatBotPanel();
                        layeredPane.add(chatOverlay, JLayeredPane.PALETTE_LAYER);
                        chatOverlay.setLocation(layeredPane.getWidth() - 500, layeredPane.getHeight() - 400);
                        chatOverlay.setVisible(true);
                    } else {
                        chatOverlay.setVisible(!chatOverlay.isVisible());
                    }
                }
            }
        });

        return itemPanel;
    }

    private void setActiveItem(MaterialCardPanel newActive) {
        if (activeItemPanel != null) activeItemPanel.setBackground(PRIMARY_BG);
        activeItemPanel = newActive;
        if (activeItemPanel != null) activeItemPanel.setBackground(SECONDARY_CTA);
    }

    public void setLayeredPane(JLayeredPane layeredPane) { this.layeredPane = layeredPane; }

    public void renderContent(String item) {
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, item);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    // ================= LOGOUT BUTTON =================
    private MaterialCardPanel createLogoutButton() {
        MaterialCardPanel logoutPanel = new MaterialCardPanel();
        logoutPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
        logoutPanel.setMaximumSize(new Dimension(200, 50));
        logoutPanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutPanel.setBackground(PRIMARY_CTA);

        JLabel label = new JLabel("↩️ Logout");
        label.setFont(ROBOTO_MEDIUM);
        label.setForeground(Color.WHITE);
        logoutPanel.add(label);

        logoutPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { logoutPanel.setBackground(SECONDARY_CTA); }
            @Override
            public void mouseExited(MouseEvent e) { logoutPanel.setBackground(PRIMARY_CTA); }
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame topFrame = (JFrame) SwingUtilities.getWindowAncestor(SidebarPanel.this);
                int confirm = JOptionPane.showConfirmDialog(topFrame,
                        "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
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

    // ================= SEPARATOR =================
    private JSeparator createSeparator() {
        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(new Color(180, 180, 180));
        sep.setMaximumSize(new Dimension(200, 2)); // thicker separator
        return sep;
    }

    // ================= MATERIAL CARD =================
    private static class MaterialCardPanel extends JPanel {
        public MaterialCardPanel() { setOpaque(true); setBackground(PRIMARY_BG); }
    }

    // ================= SIDEBAR ITEM CLASS =================
    private static class SidebarItem {
        String name;
        String iconPath;
        public SidebarItem(String name, String iconPath) { this.name = name; this.iconPath = iconPath; }
    }

    // Optional: paint subtle right shadow
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        int shadowWidth = 6;
        Color shadowColor = new Color(0, 0, 0, 30);
        GradientPaint gp = new GradientPaint(getWidth() - shadowWidth, 0, shadowColor,
                                             getWidth(), 0, new Color(0, 0, 0, 0));
        g2.setPaint(gp);
        g2.fillRect(getWidth() - shadowWidth, 0, shadowWidth, getHeight());
        g2.dispose();
    }
}
