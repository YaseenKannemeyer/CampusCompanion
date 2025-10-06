package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.sql.SQLException;
import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

public class DashboardPanel extends JPanel {

    private String studentId;
    private String groupId;
    
    private final Subjects.ConnectionProvider connectionProvider;

    private JLabel nameLabel, groupLabel, studentNumberLabel, courseLabel;

    public DashboardPanel() {
        this(null, null, null);
    }

    public DashboardPanel(Subjects.ConnectionProvider connectionProvider, String studentId, String groupId) {
        this.connectionProvider = connectionProvider;
        this.studentId = studentId;
        this.groupId = groupId;

        setLayout(new BorderLayout());
        setBackground(new Color(28, 66, 138));

        add(createHeaderBanner(studentId), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooterButtons(), BorderLayout.SOUTH);
    }

   // ------------------- HEADER -------------------
private JPanel createHeaderBanner(String studentId) {
    FrostedGlassPanel header = new FrostedGlassPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(58, 123, 213, 120),
                    0, getHeight(), new Color(28, 66, 138, 120)
            );
            g2.setPaint(gradient);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
            g2.dispose();
        }
    };
    header.setLayout(new BorderLayout());
    header.setPreferredSize(new Dimension(0, 150));
    header.setBorder(new EmptyBorder(15, 15, 15, 15));

    // Info panel inside header
    JPanel studentInfoPanel = new JPanel();
    studentInfoPanel.setOpaque(false);
    studentInfoPanel.setLayout(new BoxLayout(studentInfoPanel, BoxLayout.Y_AXIS));

    // Class fields for labels
    nameLabel = new JLabel("Name: Loading...");
    studentNumberLabel = new JLabel("Student Number: Loading...");
    groupLabel = new JLabel("Group: Loading...");
    courseLabel = new JLabel("Course: Loading...");

    // Style labels
    for (JLabel lbl : new JLabel[]{nameLabel, studentNumberLabel, groupLabel, courseLabel}) {
        lbl.setFont(new Font("Poppins", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        studentInfoPanel.add(lbl);
        studentInfoPanel.add(Box.createVerticalStrut(5));
    }

    header.add(studentInfoPanel, BorderLayout.WEST);

    // Load student info asynchronously
    if (studentId != null) {
        new SwingWorker<StudentDomain, Void>() {
            @Override
            protected StudentDomain doInBackground() throws Exception {
                StudentDAO studentDAO = new StudentDAO();
                return studentDAO.getStudentProfile(studentId); // should return groupName & courseName too
            }

            @Override
            protected void done() {
                try {
                    StudentDomain student = get();
                    if (student != null) {
                        nameLabel.setText("Name: " + student.getFirstName() + " " + student.getLastName());
                        studentNumberLabel.setText("Student Number: " + student.getStudentID());
                        groupLabel.setText("Group: " + (student.getGroupName() != null ? student.getGroupName() : "-"));
                        courseLabel.setText("Course: " + (student.getCourseName() != null ? student.getCourseName() : "-"));
                    } else {
                        nameLabel.setText("Name: -");
                        studentNumberLabel.setText("Student Number: -");
                        groupLabel.setText("Group: -");
                        courseLabel.setText("Course: -");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    nameLabel.setText("Name: Error");
                    studentNumberLabel.setText("Student Number: Error");
                    groupLabel.setText("Group: Error");
                    courseLabel.setText("Course: Error");
                }
            }
        }.execute();
    } else {
        // studentId is null
        nameLabel.setText("Name: -");
        studentNumberLabel.setText("Student Number: -");
        groupLabel.setText("Group: -");
        courseLabel.setText("Course: -");
    }

    return header;
}




    // ------------------- MAIN CONTENT -------------------
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;

        // Info Overview
        String[] infoLines = {
                "Next class: Software Engineering at 10:00 AM",
                "Location: Room 205, Building B",
                "Lecturer: Dr. Smith"
        };
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.33; gbc.weighty = 0.2;
        mainContent.add(new FrostedGlassCard("Today's Overview", infoLines, 120), gbc);

        // Current Courses
        String[][] courses = {
                {"CS101", "Software Eng", "Dr. Smith"},
                {"CS102", "Database Systems", "Prof. Jones"},
                {"CS103", "Networking", "Dr. Lee"}
        };
        gbc.gridx = 1; gbc.gridy = 0;
        mainContent.add(new FrostedGlassTableCard("Current Courses", courses, new String[]{"Code", "Course", "Instructor"}), gbc);

        // Subject Progress
        String[] subjects = {"Software Eng", "Database", "Networking"};
        int[] progress = {75, 50, 90};
        gbc.gridx = 2; gbc.gridy = 0;
        mainContent.add(new FrostedGlassProgressCard("Subject Progress", subjects, progress), gbc);

        // Attendance
        int attended = 45, total = 50;
        gbc.gridx = 0; gbc.gridy = 1; gbc.weighty = 0.25;
        mainContent.add(new FrostedGlassAttendanceCard("Lecture Attendance", attended, total), gbc);

        // Upcoming Assignments
        String[] assignments = {
                "Database Project - Due 12 Oct (High)",
                "Networking Quiz - Due 10 Oct (Medium)",
                "Software Eng Assignment - Due 15 Oct (Low)"
        };
        gbc.gridx = 1; gbc.gridy = 1;
        mainContent.add(new FrostedGlassCard("Upcoming Assignments", assignments, 150), gbc);

        // Notifications
        String[] notifications = {"Room change for Networking class", "Software Eng lecture canceled"};
        gbc.gridx = 2; gbc.gridy = 1;
        mainContent.add(new FrostedGlassCard("Notifications", notifications, 150), gbc);

        // Academic Stats
        String[] stats = {"Average GPA: 3.5", "Time Studied: 12h/week"};
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3; gbc.weightx = 1;
        mainContent.add(new FrostedGlassCard("Academic Stats", stats, 120), gbc);

        return mainContent;
    }

    // ------------------- FOOTER -------------------
    private JPanel createFooterButtons() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        buttonPanel.setOpaque(false);

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
        btn.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(52, 152, 219)); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(new Color(41, 128, 185)); }
        });
        btn.addActionListener(e -> {
            try { Desktop.getDesktop().browse(new java.net.URI(url)); }
            catch (Exception ex) { JOptionPane.showMessageDialog(DashboardPanel.this, "Failed to open link:\n" + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
        });
        return btn;
    }

    public void setStudent(StudentDomain student) {
    if (student != null) {
        this.studentId = student.getStudentID();
        this.groupId = student.getGroupID();

        nameLabel.setText("Name: " + student.getFirstName() + " " + student.getLastName());
        studentNumberLabel.setText("Student Number: " + student.getStudentID());
        groupLabel.setText("Group: " + (student.getGroupID() != null ? student.getGroupID() : "-"));
        courseLabel.setText("Course: " + (student.getCourseName() != null ? student.getCourseName() : "-"));
    } else {
        nameLabel.setText("Name: -");
        studentNumberLabel.setText("Student Number: -");
        groupLabel.setText("Group: -");
        courseLabel.setText("Course: -");
    }
    revalidate();
    repaint();
}


    // ================= GLASS PANEL BASE =================
    private abstract static class FrostedGlassPanel extends JPanel {
        public FrostedGlassPanel() { setOpaque(false); }
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int arc = 20;
            g2.setColor(new Color(255, 255, 255, 80));
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);

            g2.setColor(new Color(255, 255, 255, 50));
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, arc, arc);

            g2.setColor(new Color(0, 0, 0, 20));
            g2.fillRoundRect(3, 3, getWidth(), getHeight(), arc, arc);

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------------- CARD CLASSES ----------------
    private static class FrostedGlassCard extends FrostedGlassPanel {
        public FrostedGlassCard(String title, String[] lines, int height) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(15, 20, 15, 20));
            setPreferredSize(new Dimension(260, height));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Poppins", Font.BOLD, 15));
            titleLabel.setForeground(new Color(28, 66, 138));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(10));

            for (String line : lines) {
                JLabel label = new JLabel("<html><p style='width:220px'>" + line + "</p></html>");
                label.setFont(new Font("Poppins", Font.PLAIN, 13));
                label.setForeground(new Color(28, 66, 138));
                add(label);
                add(Box.createVerticalStrut(5));
            }
        }
    }

    private static class FrostedGlassProgressCard extends FrostedGlassPanel {
        public FrostedGlassProgressCard(String title, String[] subjects, int[] progress) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(10, 15, 10, 15));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Poppins", Font.BOLD, 15));
            titleLabel.setForeground(new Color(28, 66, 138));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(10));

            Color[] barColors = {new Color(72, 196, 181), new Color(135, 206, 250), new Color(176, 224, 230)};
            for (int i = 0; i < subjects.length; i++) {
                JLabel subjLabel = new JLabel(subjects[i]);
                subjLabel.setFont(new Font("Poppins", Font.PLAIN, 13));
                add(subjLabel);

                JProgressBar bar = new JProgressBar(0, 100);
                bar.setValue(progress[i]);
                bar.setStringPainted(true);
                bar.setForeground(barColors[i % barColors.length]);
                add(bar);
                add(Box.createVerticalStrut(5));
            }
        }
    }

    private static class FrostedGlassAttendanceCard extends FrostedGlassPanel {
        public FrostedGlassAttendanceCard(String title, int attended, int total) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setBorder(new EmptyBorder(10, 15, 10, 15));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Poppins", Font.BOLD, 15));
            titleLabel.setForeground(new Color(28, 66, 138));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(titleLabel);
            add(Box.createVerticalStrut(10));

            JLabel statsLabel = new JLabel("Attended: " + attended + " / " + total);
            statsLabel.setFont(new Font("Poppins", Font.PLAIN, 13));
            statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            add(statsLabel);

            JProgressBar bar = new JProgressBar(0, total);
            bar.setValue(attended);
            bar.setStringPainted(true);
            bar.setForeground(new Color(72, 196, 181));
            add(bar);
        }
    }

    private static class FrostedGlassTableCard extends FrostedGlassPanel {
        public FrostedGlassTableCard(String title, String[][] data, String[] columns) {
            JTable table = new JTable(data, columns);
table.setOpaque(false);  // make table transparent
table.setFillsViewportHeight(true);
table.setShowGrid(false);
table.setIntercellSpacing(new Dimension(0, 0));
table.getTableHeader().setOpaque(false);
table.getTableHeader().setBackground(new Color(255, 255, 255, 120)); // semi-transparent
table.getTableHeader().setForeground(new Color(28, 66, 138));
table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 13));
table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(255,255,255,80)));


// Transparent cell renderer for the table
table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
    @Override
    public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
        if (!isSelected) {
            c.setBackground(new Color(255, 255, 255, 50));  // semi-transparent
        }
        setHorizontalAlignment(SwingConstants.CENTER);
        setForeground(new Color(28, 66, 138));
        return c;
    }
});

            JScrollPane scroll = new JScrollPane(table);
scroll.setOpaque(false);                 // scroll pane transparent
scroll.getViewport().setOpaque(false);   // viewport transparent
scroll.setBorder(BorderFactory.createEmptyBorder());


            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(10, 15, 10, 15));
            setPreferredSize(new Dimension(260, 150));

            JLabel label = new JLabel(title, SwingConstants.CENTER);
            label.setFont(new Font("Poppins", Font.BOLD, 15));
            label.setBorder(new EmptyBorder(10, 0, 10, 0));
            label.setForeground(new Color(28, 66, 138));

            add(label, BorderLayout.NORTH);
            add(scroll, BorderLayout.CENTER);
        }
    }
}
