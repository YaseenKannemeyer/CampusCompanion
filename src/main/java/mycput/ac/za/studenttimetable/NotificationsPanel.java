package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class NotificationsPanel extends JPanel {

    public NotificationsPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // Title
        JLabel title = new JLabel("🔔 Notifications");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(title, BorderLayout.NORTH);

        // Notification list panel (scrollable)
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);

        // Example Notifications
        List<Notification> notifications = loadNotifications();
        for (Notification n : notifications) {
            listPanel.add(createNotificationCard(n));
            listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setBackground(Color.WHITE);

        add(scrollPane, BorderLayout.CENTER);
    }

    // Fake data for now
    private List<Notification> loadNotifications() {
        List<Notification> list = new ArrayList<>();
        list.add(new Notification("New timetable uploaded.", "2 hours ago", "📅"));
        list.add(new Notification("Upcoming test: ISA260S", "Yesterday", "📝"));
        list.add(new Notification("Grade update in ICT Electives 2", "2 days ago", "📊"));
        list.add(new Notification("Reminder: Group project due this Friday.", "3 days ago", "⏰"));
        list.add(new Notification("New subject resources available.", "Last week", "📚"));
        return list;
    }

    private JPanel createNotificationCard(Notification n) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(250, 250, 250));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 15, 10, 15)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        // Icon + Title
        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        left.setOpaque(false);
        JLabel icon = new JLabel(n.icon());
        icon.setFont(new Font("SansSerif", Font.PLAIN, 18));
        JLabel message = new JLabel(n.message());
        message.setFont(new Font("Poppins", Font.PLAIN, 14));
        left.add(icon);
        left.add(message);

        // Timestamp
        JLabel time = new JLabel(n.time());
        time.setFont(new Font("Poppins", Font.PLAIN, 12));
        time.setForeground(new Color(120, 120, 120));
        time.setBorder(new EmptyBorder(0, 10, 0, 0));

        card.add(left, BorderLayout.WEST);
        card.add(time, BorderLayout.EAST);

        return card;
    }

    // Inner class to hold notification data
    private static class Notification {
        private final String message, time, icon;

        public Notification(String message, String time, String icon) {
            this.message = message;
            this.time = time;
            this.icon = icon;
        }

        public String message() { return message; }
        public String time() { return time; }
        public String icon() { return icon; }
    }
}
