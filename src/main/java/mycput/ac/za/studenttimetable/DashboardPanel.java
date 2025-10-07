package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.Desktop;
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
        setBackground(new Color(245, 245, 245));

        add(createHeaderBanner(studentId), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
        add(createFooterButtons(), BorderLayout.SOUTH);
    }

   private JPanel createHeaderBanner(String studentId) {
    // Header container
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(new Color(245, 245, 245)); // light gray background
    header.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1), // subtle border
            new EmptyBorder(25, 25, 25, 25) // increased padding
    ));
    header.setPreferredSize(new Dimension(0, 180)); // taller for full content

    // Left: Student info
    JPanel studentInfoPanel = new JPanel();
    studentInfoPanel.setOpaque(false);
    studentInfoPanel.setLayout(new BoxLayout(studentInfoPanel, BoxLayout.Y_AXIS));

    nameLabel = new JLabel("Name: Loading...");
    studentNumberLabel = new JLabel("Student Number: Loading...");
    groupLabel = new JLabel("Group: Loading...");
    courseLabel = new JLabel("Course: Loading...");

    for (JLabel lbl : new JLabel[]{nameLabel, studentNumberLabel, groupLabel, courseLabel}) {
        lbl.setFont(new Font("Poppins", Font.BOLD, 18));
        lbl.setForeground(new Color(33, 37, 41)); // dark gray text
        studentInfoPanel.add(lbl);
        studentInfoPanel.add(Box.createVerticalStrut(10)); // extra spacing
    }

    header.add(studentInfoPanel, BorderLayout.WEST);

    // Right: Optional student avatar/icon
    JLabel avatar = new JLabel();
    avatar.setPreferredSize(new Dimension(100, 100)); // slightly larger
    avatar.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true)); // rounded border
    avatar.setIcon(new ImageIcon(new ImageIcon("resources/avatar.png").getImage()
            .getScaledInstance(100, 100, Image.SCALE_SMOOTH))); // placeholder
    header.add(avatar, BorderLayout.EAST);

    // Load student info asynchronously
    if (studentId != null) {
        new SwingWorker<StudentDomain, Void>() {
            @Override
            protected StudentDomain doInBackground() throws Exception {
                StudentDAO studentDAO = new StudentDAO();
                return studentDAO.getStudentProfile(studentId);
            }

            @Override
            protected void done() {
                try {
                    StudentDomain student = get();
                    setStudent(student != null ? student : null);
                } catch (Exception e) {
                    e.printStackTrace();
                    setStudent(null);
                }
            }
        }.execute();
    } else {
        setStudent(null);
    }

    return header;
}



    // ------------------- MAIN CONTENT -------------------
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(getBackground());
        mainContent.setBorder(new EmptyBorder(5, 8, 5, 8));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        // === Row 1 ===
        String[] infoLines = {
            "Next class: Software Engineering at 10:00 AM",
            "Location: Room 205, Building B",
            "Lecturer: Dr. Smith"
        };
        gbc.gridx = 0; gbc.gridy = 0;
        mainContent.add(createCard("Today's Overview", infoLines), gbc);

        String[][] courses = {
            {"CS101", "Software Eng", "Dr. Smith"},
            {"CS102", "Database Systems", "Prof. Jones"},
            {"CS103", "Networking", "Dr. Lee"}
        };
        gbc.gridx = 1;
        mainContent.add(createTableCard("Current Courses", courses, new String[]{"Code", "Course", "Instructor"}), gbc);

        String[] subjects = {"Software Eng", "Database", "Networking"};
        int[] progress = {75, 50, 90};
        gbc.gridx = 2;
        mainContent.add(createProgressCard("Subject Progress", subjects, progress), gbc);

        // === Row 2 ===
        int attended = 45, total = 50;
        gbc.gridx = 0; gbc.gridy = 1;
        mainContent.add(createAttendanceCard("Lecture Attendance", attended, total), gbc);

        String[] assignments = {
            "Database Project - Due 12 Oct (High)",
            "Networking Quiz - Due 10 Oct (Medium)",
            "Software Eng Assignment - Due 15 Oct (Low)"
        };
        gbc.gridx = 1;
        mainContent.add(createCard("Upcoming Assignments", assignments), gbc);

        String[] subjectList = {
            "Software Engineering",
            "Database Systems",
            "Networking Fundamentals",
            "Operating Systems",
            "ICT Project Practice"
        };
        gbc.gridx = 2;
        mainContent.add(createCard("Subjects", subjectList), gbc);

        // === Row 3 ===
        String[] notifications = {
            "Room change for Networking class",
            "Software Eng lecture canceled",
            "Timetable update available",
            "Exam schedule released"
        };
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 3;
        mainContent.add(createCard("Notifications", notifications), gbc);

        return mainContent;
    }

    // ------------------- FOOTER -------------------
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

    // ======================= CARDS =======================
    private JPanel createCard(String title, String[] lines) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 12, 10, 12)
        ));
        panel.setPreferredSize(new Dimension(560, 300));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 18));
        titleLabel.setForeground(new Color(28, 66, 138));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));

        for (String line : lines) {
            JLabel lbl = new JLabel("<html><p style='width:300px'>" + line + "</p></html>");
            lbl.setFont(new Font("Poppins", Font.PLAIN, 14));
            lbl.setForeground(Color.DARK_GRAY);
            lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(lbl);
            panel.add(Box.createVerticalStrut(5));
        }

        return panel;
    }

    private JPanel createProgressCard(String title, String[] subjects, int[] progress) {
        JPanel panel = createCard(title, new String[]{});
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
            panel.add(bar);
            panel.add(Box.createVerticalStrut(5));
        }

        return panel;
    }

    private JPanel createAttendanceCard(String title, int attended, int total) {
        JPanel panel = createCard(title, new String[]{});

        JLabel statsLabel = new JLabel("Attended: " + attended + " / " + total);
        statsLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
        statsLabel.setForeground(Color.DARK_GRAY);
        statsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(statsLabel);
        panel.add(Box.createVerticalStrut(10));

        JProgressBar bar = new JProgressBar(0, total);
        bar.setValue(attended);
        bar.setStringPainted(true);
        bar.setForeground(new Color(72, 196, 181));
        bar.setPreferredSize(new Dimension(380, 20));
        panel.add(bar);

        return panel;
    }

    private JPanel createTableCard(String title, String[][] data, String[] columns) {
        JTable table = new JTable(data, columns);
        table.setFillsViewportHeight(true);
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setRowHeight(25);

        table.getTableHeader().setFont(new Font("Poppins", Font.BOLD, 14));
        table.getTableHeader().setForeground(new Color(28, 66, 138));
        table.getTableHeader().setBackground(new Color(230, 230, 230));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, column);
                if (!isSelected) c.setBackground(Color.WHITE);
                setHorizontalAlignment(SwingConstants.CENTER);
                setForeground(Color.DARK_GRAY);
                setFont(new Font("Poppins", Font.PLAIN, 13));
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        panel.setPreferredSize(new Dimension(560, 300));

        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(new Font("Poppins", Font.BOLD, 18));
        lbl.setForeground(new Color(28, 66, 138));
        lbl.setBorder(new EmptyBorder(0, 0, 10, 0));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }
}
