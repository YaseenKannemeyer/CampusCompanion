package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.util.List;
import java.util.Map;
import mycput.ac.za.studenttimetable.domain.StudentDomain;
import mycput.ac.za.studenttimetable.domain.NotificationsDomain;
import mycput.ac.za.studenttimetable.dao.StudentNotificationDAO;

public class DashboardPanel extends JPanel {

    private String studentId;
    private String groupId;
    private final Subjects.ConnectionProvider connectionProvider;

    private HeaderBannerPanel headerPanel;
    private final StudentNotificationDAO studentNotificationDAO = new StudentNotificationDAO();

    // ===================== COLORS =====================
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color CTA_PRIMARY = new Color(0xE7404A);
    private static final Color CTA_SECONDARY = new Color(0x1996CC);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    private static final Dimension CARD_SIZE = new Dimension(380, 180); // slightly smaller
    private static final Dimension WIDE_CARD_SIZE = new Dimension(1160, 180); // slightly smaller

    // ===================== CONSTRUCTORS =====================
    public DashboardPanel() {
        this(null, null, null);
    }

    public DashboardPanel(Subjects.ConnectionProvider connectionProvider, String studentId, String groupId) {
        this.connectionProvider = connectionProvider;
        this.studentId = studentId;
        this.groupId = groupId;

        setLayout(new BorderLayout());
        setBackground(PRIMARY_BG);

        headerPanel = new HeaderBannerPanel(connectionProvider, studentId);
        add(headerPanel, BorderLayout.NORTH);

        // Wrap main content in flexible panel
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(PRIMARY_BG);
        wrapper.add(createMainContent(), BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);

        add(createFooterButtons(), BorderLayout.SOUTH);
    }

    // ===================== STUDENT SETTER =====================
    public void setStudent(StudentDomain student) {
        this.studentId = student.getStudentID();
        if (headerPanel != null) headerPanel.setStudent(student);

        removeAll();

        add(headerPanel, BorderLayout.NORTH);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(PRIMARY_BG);
        wrapper.add(createMainContent(), BorderLayout.NORTH);
        add(wrapper, BorderLayout.CENTER);

        add(createFooterButtons(), BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    // ===================== MAIN CONTENT =====================
    private JPanel createMainContent() {
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setBackground(PRIMARY_BG);
        mainContent.setBorder(new EmptyBorder(20, 20, 10, 20)); // bottom padding smaller

        // === ROW 1: Overview, Current Courses, Subject Progress ===
        JPanel row1 = new JPanel(new GridLayout(1, 3, 15, 0));
        row1.setOpaque(false);
        row1.add(createCard("Today's Overview", new String[]{
                "Next class: Applications Development Practice 2 at 10:00 AM",
                "Location: Lab 3B, IT Centre",
                "Lecturer: Mr. Richard Maliwatu"
        }));

        row1.add(createTableCard("Current Courses", new String[][]{
                {"ADF262S", "Applications Development Fundamentals 2", "Mr. Richard Maliwatu"},
                {"ADP262S", "Applications Development Practice 2", "Mr. Ayodeji Afolayan"},
                {"CNF262S", "Communications Networks Fundamentals 2", "Mr. Israel Ngokо"},
                {"INM262S", "Information Management 2", "Ms. Nicole Wessels"},
                {"PRC262S", "Professional Communications 2", "Ms. Boniswa Mafunda"},
                {"PRT262S", "Project 2", "Mr. Kirby America"}
        }, new String[]{"Code", "Course", "Lecturer"}));

        Map<String, Integer> subjectProgress = Map.of(
                "Applications Dev Practice 2", 82,
                "Information Management 2", 74,
                "Project 2", 65
        );
        row1.add(createProgressCard("Subject Progress", subjectProgress));
        mainContent.add(row1);
        mainContent.add(Box.createVerticalStrut(10)); // smaller spacing

        // === ROW 2: Lecture Attendance, Upcoming Assignments, Subjects ===
        JPanel row2 = new JPanel(new GridLayout(1, 3, 15, 0));
        row2.setOpaque(false);

        Map<String, Integer> attendance = Map.of(
                "Attended", 46,
                "Total", 50
        );
        row2.add(createProgressCard("Lecture Attendance", attendance));

        row2.add(createCard("Upcoming Assignments", new String[]{
                "ADP262S: CRUD App – Due 20 Oct (High)",
                "INM262S: SQL Database Design – Due 22 Oct (Medium)",
                "PRT262S: Project Proposal – Due 25 Oct (High)",
                "PRC262S: Presentation Draft – Due 28 Oct (Low)"
        }));

        row2.add(createCard("Subjects", new String[]{
                "Applications Development Fundamentals 2",
                "Applications Development Practice 2",
                "Information Management 2",
                "Communications Networks Fundamentals 2",
                "Professional Communications 2",
        }));

        mainContent.add(row2);
        mainContent.add(Box.createVerticalStrut(10)); // smaller spacing

        // === ROW 3: Notifications ===
        JPanel row3 = new JPanel(new BorderLayout());
        row3.setOpaque(false);
        row3.add(createNotificationsCard(), BorderLayout.CENTER);
        mainContent.add(row3);

        return mainContent;
    }

    // ===================== NOTIFICATIONS CARD =====================
    private JPanel createNotificationsCard() {
        JPanel container = new JPanel(new BorderLayout());
        container.setBackground(PRIMARY_BG);

        JLabel sectionTitle = new JLabel("🔔 Latest Notification");
        sectionTitle.setFont(new Font("Roboto", Font.BOLD, 18));
        sectionTitle.setForeground(CTA_SECONDARY);
        sectionTitle.setBorder(new EmptyBorder(0, 5, 5, 5)); // reduce bottom padding
        container.add(sectionTitle, BorderLayout.NORTH);

        if (studentId == null || studentId.isEmpty()) {
            return wrapCard(container, new JLabel("No student logged in."));
        }

        List<NotificationsDomain> notifications = studentNotificationDAO.getNotificationsByStudentID(studentId);
        if (notifications.isEmpty()) {
            return wrapCard(container, new JLabel("No recent notifications."));
        }

        NotificationsDomain latest = notifications.stream()
                .max((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .orElse(null);

        if (latest == null) {
            return wrapCard(container, new JLabel("No recent notifications."));
        }

        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SHADOW_COLOR, 1, true),
                new EmptyBorder(12, 15, 12, 15)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel titleLabel = new JLabel(getIconForTitle(latest.getTitle()) + " " + latest.getTitle());
        titleLabel.setFont(new Font("Roboto", Font.BOLD, 15));
        titleLabel.setForeground(CTA_SECONDARY);

        JLabel messageLabel = new JLabel("<html><div style='width:350px; color:#333333;'>"
                + latest.getBody() + "</div></html>");
        messageLabel.setFont(new Font("Roboto", Font.PLAIN, 13));

        JLabel timeLabel = new JLabel("📅 " + latest.getCreatedAt().toString());
        timeLabel.setFont(new Font("Roboto", Font.ITALIC, 11));
        timeLabel.setForeground(Color.GRAY);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);
        left.add(titleLabel);
        left.add(Box.createVerticalStrut(5));
        left.add(messageLabel);
        left.add(Box.createVerticalStrut(5)); // smaller spacing
        left.add(timeLabel);

        card.add(left, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { card.setBackground(new Color(245, 245, 245)); }
            @Override
            public void mouseExited(MouseEvent e) { card.setBackground(CARD_BG); }
            @Override
            public void mouseClicked(MouseEvent e) { showNotificationDialog(latest); }
        });

        // Let card size naturally
        card.setPreferredSize(null);
        card.setMinimumSize(null);
        card.setMaximumSize(null);

        container.add(card, BorderLayout.CENTER);
        container.setBorder(new EmptyBorder(0,0,5,0)); // smaller bottom padding
        return container;
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

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(CTA_SECONDARY); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(CTA_PRIMARY); }
        });

        btn.addActionListener(e -> {
            try { Desktop.getDesktop().browse(new URI(url)); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Failed to open link:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        return btn;
    }

    // ===================== CARD HELPERS =====================
    private JPanel createCard(String title, String[] lines) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 3, 3, new Color(150, 180, 210)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("<html><div style='font-size:16px; color:#1996CC; font-weight:bold;'>" 
                + title + "</div></html>");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));

        for (String line : lines) {
            JLabel label = new JLabel("<html>" + line + "</html>");
            label.setFont(new Font("Roboto", Font.PLAIN, 14));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            panel.add(label);
            panel.add(Box.createVerticalStrut(5));
        }

        panel.setPreferredSize(CARD_SIZE);
        panel.setMinimumSize(CARD_SIZE);
        panel.setMaximumSize(CARD_SIZE);

        return panel;
    }

    private JPanel createProgressCard(String title, Map<String, Integer> subjects) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 3, 3, new Color(150, 180, 210)),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("<html><div style='font-size:16px; color:#1996CC; font-weight:bold; text-align:center;'>" 
                + title + "</div></html>");
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(10));

        for (Map.Entry<String, Integer> entry : subjects.entrySet()) {
            JLabel subjLabel = new JLabel(entry.getKey() + " (" + entry.getValue() + "%)");
            subjLabel.setFont(new Font("Roboto", Font.PLAIN, 13));
            subjLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JProgressBar bar = new JProgressBar(0, 100);
            bar.setValue(entry.getValue());
            bar.setStringPainted(true);
            bar.setForeground(CTA_SECONDARY);
            bar.setAlignmentX(Component.CENTER_ALIGNMENT);

            panel.add(subjLabel);
            panel.add(bar);
            panel.add(Box.createVerticalStrut(8));
        }

        panel.setPreferredSize(CARD_SIZE);
        panel.setMinimumSize(CARD_SIZE);
        panel.setMaximumSize(CARD_SIZE);

        return panel;
    }

    private JPanel createTableCard(String title, String[][] data, String[] headers) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 1, 3, 3, new Color(150, 180, 210)),
                new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel lbl = new JLabel("<html><div style='font-size:16px; font-weight:bold; color:#1996CC; text-align:center;'>" 
                + title + "</div></html>", SwingConstants.CENTER);
        lbl.setFont(new Font("Roboto", Font.BOLD, 16));
        panel.add(lbl, BorderLayout.NORTH);

        JTable table = new JTable(new DefaultTableModel(data, headers));
        table.setFont(new Font("Roboto", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Roboto", Font.BOLD, 13));
        table.setRowHeight(24);
        table.setFillsViewportHeight(true);
        table.setEnabled(false);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scroll, BorderLayout.CENTER);

        panel.setPreferredSize(WIDE_CARD_SIZE);
        panel.setMinimumSize(WIDE_CARD_SIZE);
        panel.setMaximumSize(WIDE_CARD_SIZE);

        return panel;
    }

    private JPanel wrapCard(JPanel container, JComponent content) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SHADOW_COLOR, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));
        card.add(content, BorderLayout.CENTER);
        container.add(card, BorderLayout.CENTER);
        return container;
    }

    private String getIconForTitle(String title) {
        title = title.toLowerCase();
        if (title.contains("exam")) return "📌";
        if (title.contains("assignment")) return "📝";
        if (title.contains("class") || title.contains("lecture")) return "📘";
        if (title.contains("system") || title.contains("update")) return "⚙️";
        if (title.contains("grade")) return "📊";
        if (title.contains("meeting") || title.contains("group")) return "👥";
        return "🔔";
    }

    private void showNotificationDialog(NotificationsDomain notification) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Notification", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(CARD_BG);

        JLabel title = new JLabel(getIconForTitle(notification.getTitle()) + " " + notification.getTitle());
        title.setFont(new Font("Roboto", Font.BOLD, 18));
        title.setForeground(CTA_SECONDARY);

        JTextArea message = new JTextArea(notification.getBody() + "\n\nSent: " + notification.getCreatedAt());
        message.setFont(new Font("Roboto", Font.PLAIN, 14));
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
        message.setEditable(false);
        message.setOpaque(false);

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn, CTA_PRIMARY, CTA_SECONDARY, new Dimension(120, 36));
        closeBtn.addActionListener(e -> dialog.dispose());

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(message), BorderLayout.CENTER);
        panel.add(closeBtn, BorderLayout.SOUTH);

        dialog.setContentPane(panel);
        dialog.setVisible(true);
    }

    private void styleButton(JButton btn, Color primary, Color secondary, Dimension size) {
        btn.setFont(new Font("Roboto", Font.BOLD, 13));
        btn.setBackground(primary);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        btn.setOpaque(true);
        btn.setPreferredSize(size);

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { btn.setBackground(secondary); }
            @Override
            public void mouseExited(MouseEvent e) { btn.setBackground(primary); }
        });
    }
}
