package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private String studentId;
    private String groupId;
    private final Subjects.ConnectionProvider connectionProvider;

    // Components that need updating
    private JLabel nameLabel;
    private JLabel groupLabel;

    public DashboardPanel() {
        this(null, null, null);
    }

    public DashboardPanel(Subjects.ConnectionProvider connectionProvider, String studentId, String groupId) {
        this.connectionProvider = connectionProvider;
        this.studentId = studentId;
        this.groupId = groupId;

        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8F9FB"));

        // 🔹 Header with gradient banner
        add(createHeaderBanner(), BorderLayout.NORTH);

        // 🔹 Main Content
        add(createMainContent(), BorderLayout.CENTER);

        // 🔹 Footer (Quick Links)
        add(createFooterButtons(), BorderLayout.SOUTH);
    }

    // ------------------- HEADER BANNER -------------------
    private JPanel createHeaderBanner() {
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradient = new GradientPaint(0, 0,
                        new Color(52, 143, 80),
                        getWidth(), getHeight(),
                        new Color(86, 180, 211));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight() + 40, 0, 40);
            }
        };

        header.setLayout(new BorderLayout());
        header.setPreferredSize(new Dimension(0, 130));
        header.setBorder(new EmptyBorder(20, 30, 15, 30));
        header.setOpaque(false);

        // Student info area
        JPanel studentInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        studentInfoPanel.setOpaque(false);

        // Avatar
        JLabel avatar = new JLabel("\uD83D\uDC64"); // simple emoji avatar
        avatar.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 50));
        avatar.setForeground(Color.WHITE);

        // Student text info
        nameLabel = new JLabel("Welcome, " + (studentId != null ? studentId : "Student"));
        nameLabel.setFont(new Font("Poppins", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);

        groupLabel = new JLabel("Group: " + (groupId != null ? groupId : "-"));
        groupLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        groupLabel.setForeground(Color.WHITE);

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(nameLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(groupLabel);

        studentInfoPanel.add(avatar);
        studentInfoPanel.add(textPanel);

        // Title on right side
        JLabel title = new JLabel("Student Dashboard");
        title.setFont(new Font("Poppins", Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(studentInfoPanel, BorderLayout.WEST);
        header.add(title, BorderLayout.EAST);

        return header;
    }

    // ------------------- MAIN CONTENT -------------------
    private JPanel createMainContent() {

        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(30, 30, 30, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;

        // 🔸 Info Card
        String[] infoLines = {
                "Your next class is Software Engineering at 10:00 AM.",
                "Location: Room 205, Building B.",
                "Lecturer: Dr. Smith"
        };
        JPanel infoCard = createInfoCard("Today’s Overview", infoLines, 130);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.35;
        gbc.weighty = 0.4;
        mainContent.add(infoCard, gbc);

        // 🔸 Timetable Card
        String[][] data = {
                {"8:00", "Networking"},
                {"10:00", "Software Engineering"},
                {"13:00", "Database Systems"}
        };
        String[] columns = {"Time", "Subject"};
        JPanel timetableCard = createTableCard("Today’s Timetable", data, columns);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.65;
        gbc.weighty = 0.4;
        mainContent.add(timetableCard, gbc);

        // 🔸 Notifications Card
        String[] notifications = {"No notifications at this time"};
        JPanel notificationsCard = createInfoCard("Notifications", notifications, 150);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.weighty = 0.25;
        mainContent.add(notificationsCard, gbc);

        return mainContent;
    }

    // ------------------- FOOTER BUTTONS -------------------
    private JPanel createFooterButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setOpaque(false);

        JButton blackboardBtn = createStyledButton("Blackboard", "https://myclassroom.cput.ac.za/");
        JButton outlookBtn = createStyledButton("Outlook", "https://outlook.office.com");

        buttonPanel.add(blackboardBtn);
        buttonPanel.add(outlookBtn);
        return buttonPanel;
    }

    private JButton createStyledButton(String text, String url) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setBackground(Color.decode("#34495E"));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        // Hover animation
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.decode("#2E86C1"));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(Color.decode("#34495E"));
            }
        });

        btn.addActionListener(e -> openWebpage(url));
        return btn;
    }

    // ------------------- HELPER METHODS -------------------
    private JPanel createInfoCard(String title, String[] lines, int preferredHeight) {
        JPanel panel = createCardPanel();
        panel.setPreferredSize(new Dimension(260, preferredHeight));
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 15));
        titleLabel.setForeground(Color.decode("#2C3E50"));

        JPanel linePanel = new JPanel();
        linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.Y_AXIS));
        linePanel.setOpaque(false);

        for (String line : lines) {
            JLabel label = new JLabel("<html><p style='width:220px'>" + line + "</p></html>");
            label.setFont(new Font("Poppins", Font.PLAIN, 13));
            label.setForeground(Color.decode("#566573"));
            label.setBorder(new EmptyBorder(5, 0, 5, 0));
            linePanel.add(label);
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(linePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTableCard(String title, String[][] data, String[] columns) {
        JTable table = new JTable(data, columns);
        styleTable(table);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel panel = createCardPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel label = new JLabel(title, SwingConstants.CENTER);
        label.setFont(new Font("Poppins", Font.BOLD, 15));
        label.setBorder(new EmptyBorder(10, 0, 10, 0));
        label.setForeground(Color.decode("#2C3E50"));

        panel.add(label, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createCardPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.WHITE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                // subtle shadow
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };
    }

    private void styleTable(JTable table) {
        table.setFont(new Font("Poppins", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);

        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
        table.getTableHeader().setBackground(Color.decode("#ECF0F1"));
        table.getTableHeader().setForeground(Color.decode("#2C3E50"));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.setDefaultRenderer(Object.class, center);
    }

    private void openWebpage(String urlString) {
        try {
            Desktop.getDesktop().browse(new java.net.URI(urlString));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to open link:\n" + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ------------------- NEW METHOD -------------------
    public void setStudent(String studentId, String groupId) {
        this.studentId = studentId;
        this.groupId = groupId;

        // Update header labels dynamically
        if (nameLabel != null) nameLabel.setText("Welcome, " + (studentId != null ? studentId : "Student"));
        if (groupLabel != null) groupLabel.setText("Group: " + (groupId != null ? groupId : "-"));

        revalidate();
        repaint();
    }
}
