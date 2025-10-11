package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.Desktop;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

public class DashboardPanel extends JPanel {

    private String studentId;
    private String groupId;

    private final Subjects.ConnectionProvider connectionProvider;

    private HeaderBannerPanel headerPanel;

    // ================= COLORS =================
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color CTA_PRIMARY = new Color(0xE7404A);
    private static final Color CTA_SECONDARY = new Color(0x1996CC);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public DashboardPanel() {
        this(null, null, null);
    }

    public DashboardPanel(Subjects.ConnectionProvider connectionProvider, String studentId, String groupId) {
        this.connectionProvider = connectionProvider;
        this.studentId = studentId;
        this.groupId = groupId;

        setLayout(new BorderLayout());
        setBackground(PRIMARY_BG);

        // Header
        headerPanel = new HeaderBannerPanel(connectionProvider, studentId);
        add(headerPanel, BorderLayout.NORTH);

        // Main content
        add(createMainContent(), BorderLayout.CENTER);

        // Footer buttons
        add(createFooterButtons(), BorderLayout.SOUTH);
    }

    public void setStudent(StudentDomain student) {
        if (headerPanel != null) {
            headerPanel.setStudent(student);
        }
    }

    // ===================== MAIN CONTENT =====================
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(PRIMARY_BG);
        mainContent.setBorder(new EmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // === Row 1 ===
        gbc.gridx = 0; gbc.gridy = 0;
        mainContent.add(createCard("Today's Overview",
                new String[]{"Next class: Software Engineering at 10:00 AM",
                        "Location: Room 205, Building B",
                        "Lecturer: Dr. Smith"}), gbc);

        gbc.gridx = 1;
        mainContent.add(createTableCard("Current Courses", new String[][]{
                {"CS101", "Software Eng", "Dr. Smith"},
                {"CS102", "Database Systems", "Prof. Jones"},
                {"CS103", "Networking", "Dr. Lee"}
        }, new String[]{"Code", "Course", "Instructor"}), gbc);

        gbc.gridx = 2;
        mainContent.add(createProgressCard("Subject Progress",
                new String[]{"Software Eng", "Database", "Networking"},
                new int[]{75, 50, 90}), gbc);

        // === Row 2 ===
        gbc.gridx = 0; gbc.gridy = 1;
        mainContent.add(createProgressCard("Lecture Attendance", new String[]{"Attended", "Total"}, new int[]{45, 50}), gbc);

        gbc.gridx = 1;
        mainContent.add(createCard("Upcoming Assignments", new String[]{
                "Database Project - Due 12 Oct (High)",
                "Networking Quiz - Due 10 Oct (Medium)",
                "Software Eng Assignment - Due 15 Oct (Low)"
        }), gbc);

        gbc.gridx = 2;
        mainContent.add(createCard("Subjects", new String[]{
                "Software Engineering",
                "Database Systems",
                "Networking Fundamentals",
                "Operating Systems",
                "ICT Project Practice"
        }), gbc);

        // === Row 3 ===
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        mainContent.add(createCard("Notifications", new String[]{
                "Room change for Networking class",
                "Software Eng lecture canceled",
                "Timetable update available",
                "Exam schedule released"
        }), gbc);

        return mainContent;
    }

    // ===================== FOOTER BUTTONS =====================
    private JPanel createFooterButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setBackground(PRIMARY_BG);

        buttonPanel.add(createStyledButton("Blackboard", "https://myclassroom.cput.ac.za/"));
        buttonPanel.add(createStyledButton("Outlook", "https://outlook.office.com"));

        return buttonPanel;
    }

    private JButton createStyledButton(String text, String url) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Roboto", Font.BOLD, 13));
        btn.setBackground(CTA_PRIMARY);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder());
        btn.setOpaque(true);

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(CTA_SECONDARY);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(CTA_PRIMARY);
            }
        });

        btn.addActionListener(e -> {
            try { Desktop.getDesktop().browse(new java.net.URI(url)); }
            catch (Exception ex) { JOptionPane.showMessageDialog(DashboardPanel.this,
                    "Failed to open link:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });

        return btn;
    }

    // ===================== CARDS =====================
    private JPanel createCard(String title, String[] lines) {
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                BorderFactory.createLineBorder(SHADOW_COLOR, 1, true)
        ));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(300, 200));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 18));
        titleLabel.setForeground(CTA_SECONDARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));

        for (String line : lines) {
            JLabel lbl = new JLabel("<html><p style='width:280px; color:#333333'>" + line + "</p></html>");
            lbl.setFont(new Font("Roboto", Font.PLAIN, 14));
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lbl);
            panel.add(Box.createVerticalStrut(5));
        }

        return panel;
    }

    private JPanel createTableCard(String title, String[][] data, String[] columns) {
        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setRowHeight(28);

        table.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 14));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setBackground(CTA_SECONDARY);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? new Color(220, 245, 255) : new Color(200, 235, 255));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(Color.DARK_GRAY);
                setFont(new Font("Roboto", Font.PLAIN, 13));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(true);
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createLineBorder(SHADOW_COLOR, 1, true)
        ));
        panel.setPreferredSize(new Dimension(300, 200));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Roboto", Font.BOLD, 18));
        lbl.setForeground(CTA_SECONDARY);
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createProgressCard(String title, String[] subjects, int[] progress) {
        JPanel panel = createCard(title, new String[]{});
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = 0; i < subjects.length; i++) {
            JLabel subjLabel = new JLabel(subjects[i]);
            subjLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
            subjLabel.setForeground(Color.DARK_GRAY);
            panel.add(subjLabel);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(progress[i]);
            bar.setStringPainted(true);
            bar.setForeground(CTA_SECONDARY);
            bar.setPreferredSize(new Dimension(260, 20));
            bar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
            panel.add(bar);
            panel.add(Box.createVerticalStrut(8));
        }

        return panel;
    }
}
