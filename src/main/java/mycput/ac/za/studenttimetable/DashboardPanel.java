package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class DashboardPanel extends JPanel {

    private final String studentId;
    private final String groupId;
    private final Subjects.ConnectionProvider connectionProvider;
    
     public DashboardPanel() {
        this(null, null, null);
    }

    // ✅ New constructor with parameters
    public DashboardPanel(Subjects.ConnectionProvider connectionProvider, String studentId, String groupId) {
        this.connectionProvider = connectionProvider;
        this.studentId = studentId;
        this.groupId = groupId;

        setLayout(new BorderLayout());
        setBackground(Color.decode("#F8F9FB"));

        // 🔹 Header
        JLabel dashboardTitle = new JLabel("Dashboard - Student: " + studentId + " | Group: " + groupId);
        dashboardTitle.setFont(new Font("Poppins", Font.BOLD, 26));
        dashboardTitle.setForeground(Color.decode("#2C3E50"));
        dashboardTitle.setBorder(new EmptyBorder(25, 30, 15, 0));
        add(dashboardTitle, BorderLayout.NORTH);

        // 🔹 Main Content Panel
        JPanel mainContent = new JPanel(new BorderLayout());
        mainContent.setOpaque(false);
        mainContent.setBorder(new EmptyBorder(10, 20, 20, 20));

        // 🔹 Info Card
        String[] infoLines = {
            "Your next class is Software Engineering at 10:00 AM.",
            "Location: Room 205, Building B.",
            "Lecturer: Dr. Smith"
        };
        JPanel infoCard = createInfoCard("Today’s Overview", infoLines, 130);
        JPanel leftWrapper = new JPanel(new BorderLayout());
        leftWrapper.setOpaque(false);
        leftWrapper.add(infoCard, BorderLayout.NORTH);
        mainContent.add(leftWrapper, BorderLayout.WEST);

        // 🔹 Timetable table
        String[][] data = {
            {"8:00", "Networking"},
            {"10:00", "Software Engineering"},
            {"13:00", "Database Systems"}
        };
        String[] columns = {"Time", "Subject"};
        JTable table = new JTable(data, columns);
        styleTable(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(500, 300));
        mainContent.add(scrollPane, BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    // 🔹 Your helper methods remain unchanged
    private JPanel createInfoCard(String title, String[] lines, int preferredHeight) {
        JPanel panel = createRoundedPanel(Color.WHITE);
        panel.setPreferredSize(new Dimension(240, preferredHeight));
        panel.setLayout(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Poppins", Font.BOLD, 14));
        titleLabel.setForeground(Color.decode("#2C3E50"));

        JPanel linePanel = new JPanel();
        linePanel.setLayout(new BoxLayout(linePanel, BoxLayout.Y_AXIS));
        linePanel.setOpaque(false);

        for (String line : lines) {
            JLabel label = new JLabel("<html><p style='width:210px'>" + line + "</p></html>");
            label.setFont(new Font("Poppins", Font.PLAIN, 12));
            label.setForeground(Color.decode("#566573"));
            label.setBorder(new EmptyBorder(5, 0, 5, 0));
            linePanel.add(label);
        }

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(linePanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createRoundedPanel(Color bg) {
        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setOpaque(false);
        panel.setBackground(bg);
        return panel;
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
}
