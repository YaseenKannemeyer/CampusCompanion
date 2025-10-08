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

    public DashboardPanel() {
        this(null, null, null);
    }

    public DashboardPanel(Subjects.ConnectionProvider connectionProvider, String studentId, String groupId) {
        this.connectionProvider = connectionProvider;
        this.studentId = studentId;
        this.groupId = groupId;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

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
        mainContent.setBackground(getBackground());
        mainContent.setBorder(new EmptyBorder(10, 15, 10, 15));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // === Row 1 ===
        gbc.gridx = 0; gbc.gridy = 0;
        mainContent.add(createGradientCard("Today's Overview",
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
        mainContent.add(createGradientCard("Upcoming Assignments", new String[]{
                "Database Project - Due 12 Oct (High)",
                "Networking Quiz - Due 10 Oct (Medium)",
                "Software Eng Assignment - Due 15 Oct (Low)"
        }), gbc);

        gbc.gridx = 2;
        mainContent.add(createGradientCard("Subjects", new String[]{
                "Software Engineering",
                "Database Systems",
                "Networking Fundamentals",
                "Operating Systems",
                "ICT Project Practice"
        }), gbc);

        // === Row 3 ===
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        mainContent.add(createGradientCard("Notifications", new String[]{
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
        buttonPanel.setBackground(getBackground());

        buttonPanel.add(createStyledButton("Blackboard", "https://myclassroom.cput.ac.za/"));
        buttonPanel.add(createStyledButton("Outlook", "https://outlook.office.com"));

        return buttonPanel;
    }

    private JButton createStyledButton(String text, String url) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setBackground(new Color(41, 128, 185));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(130, 38));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(52, 152, 219));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(new Color(41, 128, 185));
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
private JPanel createGradientCard(String title, String[] lines) {
    JPanel panel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Smooth blue gradient from top to bottom
            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(41, 128, 185),       // top blue
                    0, getHeight(), new Color(72, 196, 230) // bottom lighter blue
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }
    };

    panel.setOpaque(false);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    panel.setPreferredSize(new Dimension(560, 300));

    // Title
    JLabel titleLabel = new JLabel(title);
    titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
    titleLabel.setForeground(Color.WHITE);
    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    panel.add(titleLabel);
    panel.add(Box.createVerticalStrut(10));

    // Content lines
    for (String line : lines) {
        JLabel lbl = new JLabel("<html><p style='width:300px; color:white'>" + line + "</p></html>");
        lbl.setFont(new Font("Poppins", Font.PLAIN, 14));
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

    table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 14));
    table.getTableHeader().setForeground(Color.WHITE);
    table.getTableHeader().setBackground(new Color(41, 128, 185));

    table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
            if (!isSelected) {
                c.setBackground(row % 2 == 0 ? new Color(72, 196, 230, 60) : new Color(41, 128, 185, 40));
            }
            setHorizontalAlignment(SwingConstants.CENTER);
            setForeground(Color.DARK_GRAY);
            setFont(new Font("Poppins", Font.PLAIN, 13));
            return c;
        }
    });

    JScrollPane scroll = new JScrollPane(table);
    scroll.setBorder(BorderFactory.createEmptyBorder());

    JPanel panel = new JPanel(new BorderLayout()) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gp = new GradientPaint(
                    0, 0, new Color(41, 128, 185),       // top blue
                    0, getHeight(), new Color(72, 196, 230) // bottom lighter blue
            );
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

            g2.dispose();
            super.paintComponent(g);
        }
    };

    panel.setOpaque(false);
    panel.setBorder(new EmptyBorder(12, 12, 12, 12));
    panel.setPreferredSize(new Dimension(560, 300));

    JLabel lbl = new JLabel(title, SwingConstants.CENTER);
    lbl.setFont(new Font("Poppins", Font.BOLD, 18));
    lbl.setForeground(Color.WHITE);
    lbl.setBorder(new EmptyBorder(0, 0, 10, 0));

    panel.add(lbl, BorderLayout.NORTH);
    panel.add(scroll, BorderLayout.CENTER);

    return panel;
}


    private JPanel createProgressCard(String title, String[] subjects, int[] progress) {
        JPanel panel = createGradientCard(title, new String[]{});
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        for (int i = 0; i < subjects.length; i++) {
            JLabel subjLabel = new JLabel(subjects[i]);
            subjLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
            subjLabel.setForeground(Color.DARK_GRAY);
            panel.add(subjLabel);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(progress[i]);
            bar.setStringPainted(true);
            bar.setForeground(new Color(72, 196, 181));
            bar.setPreferredSize(new Dimension(380, 20));
            bar.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
            panel.add(bar);
            panel.add(Box.createVerticalStrut(8));
        }

        return panel;
    }

  
}
