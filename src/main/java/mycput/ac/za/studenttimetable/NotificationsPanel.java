package mycput.ac.za.studenttimetable;

import mycput.ac.za.studenttimetable.dao.StudentNotificationDAO;
import mycput.ac.za.studenttimetable.domain.NotificationsDomain;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationsPanel extends JPanel {

    private JTextField searchField;
    private JPanel listPanel;
    private List<Notification> notifications;
    private List<Notification> filteredList;
    private String studentID;
    private HeaderBannerPanel headerPanel;

    private final StudentNotificationDAO studentNotificationDAO = new StudentNotificationDAO();

    // ================= COLORS =================
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color CTA_PRIMARY = new Color(0xE7404A);
    private static final Color CTA_SECONDARY = new Color(0x1996CC);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public NotificationsPanel(String studentID) {
        this.studentID = studentID;
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(15, 15));
        setBackground(PRIMARY_BG);
        setBorder(new EmptyBorder(15, 20, 15, 20));

        // Load notifications
        notifications = loadNotificationsFromDB();
        filteredList = new ArrayList<>(notifications);

        // ===== Header =====
        headerPanel = new HeaderBannerPanel(null, studentID);
        add(headerPanel, BorderLayout.NORTH);

        // ===== Search + Title =====
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setOpaque(false);

        JLabel title = new JLabel("🔔 Notifications (" + notifications.size() + ")");
        title.setFont(new Font("Roboto", Font.BOLD, 20));
        title.setForeground(CTA_SECONDARY);
        topPanel.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(new BorderLayout(8, 5));
        searchPanel.setOpaque(false);

        searchField = new JTextField();
        searchField.setFont(new Font("Roboto", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        ));
        searchPanel.add(searchField, BorderLayout.CENTER);

        JButton refreshBtn = new JButton("⟳ Refresh");
        styleButton(refreshBtn, CTA_SECONDARY, CTA_PRIMARY, new Dimension(140, 38));
        refreshBtn.addActionListener(e -> {
            searchField.setText("");
            notifications = loadNotificationsFromDB();
            filteredList = new ArrayList<>(notifications);
            refreshList();
        });
        searchPanel.add(refreshBtn, BorderLayout.EAST);

        topPanel.add(searchPanel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.CENTER);

        // ===== Notification list =====
        listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setOpaque(false);
        refreshList();

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(14);
        scrollPane.getViewport().setBackground(PRIMARY_BG);
        add(scrollPane, BorderLayout.CENTER);

        // ===== Footer buttons =====
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        footer.setOpaque(false);

        JButton markReadBtn = new JButton("✓ Mark as Read");
        JButton deleteBtn = new JButton("🗑 Delete Selected");
        styleButton(markReadBtn, CTA_PRIMARY, CTA_SECONDARY, new Dimension(145, 38));
        styleButton(deleteBtn, CTA_PRIMARY, CTA_SECONDARY, new Dimension(145, 38));

        footer.add(markReadBtn);
        footer.add(deleteBtn);
        add(footer, BorderLayout.SOUTH);

        // ===== Button functionalities =====
        deleteBtn.addActionListener(e -> {
            notifications.removeIf(n -> n.checkBox != null && n.checkBox.isSelected());
            filteredList = new ArrayList<>(notifications);
            refreshList();
        });

        markReadBtn.addActionListener(e -> {
            for (Notification n : notifications) {
                if (n.checkBox != null && n.checkBox.isSelected() && !n.isRead) {
                    n.isRead = true;
                    studentNotificationDAO.markNotificationAsRead(studentID, n.id);
                }
            }
            refreshList();
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String query = searchField.getText().toLowerCase();
                filteredList = notifications.stream()
                        .filter(n -> n.title.toLowerCase().contains(query))
                        .collect(Collectors.toList());
                refreshList();
            }
        });
    }

    public void setStudent(StudentDomain student) {
        this.studentID = student.getStudentID();
        if (headerPanel != null) {
            headerPanel.setStudent(student);
        }
        notifications = loadNotificationsFromDB();
        filteredList = new ArrayList<>(notifications);
        refreshList();
    }

    private void refreshList() {
        listPanel.removeAll();

        if (filteredList.isEmpty()) {
            JLabel noResults = new JLabel("No notifications found.");
            noResults.setFont(new Font("Roboto", Font.ITALIC, 14));
            noResults.setForeground(Color.GRAY);
            noResults.setAlignmentX(Component.CENTER_ALIGNMENT);
            listPanel.add(Box.createVerticalGlue());
            listPanel.add(noResults);
            listPanel.add(Box.createVerticalGlue());
        } else {
            for (Notification n : filteredList) {
                listPanel.add(createNotificationCard(n));
                listPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        listPanel.revalidate();
        listPanel.repaint();
    }

    private JPanel createNotificationCard(Notification n) {
        JPanel card = new JPanel(new BorderLayout(10, 5));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createLineBorder(SHADOW_COLOR, 1, true)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JCheckBox checkBox = new JCheckBox();
        checkBox.setOpaque(false);
        n.checkBox = checkBox;

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setOpaque(false);

        JLabel title = new JLabel(n.icon + " " + n.title);
        title.setFont(new Font("Roboto", n.isRead ? Font.PLAIN : Font.BOLD, 15));
        title.setForeground(CTA_SECONDARY);

        JLabel body = new JLabel("<html><div style='width:350px; color:#333333;'>" + n.message + "</div></html>");
        body.setFont(new Font("Roboto", Font.PLAIN, 13));
        body.setForeground(Color.DARK_GRAY);

        JLabel time = new JLabel(n.time);
        time.setFont(new Font("Roboto", Font.ITALIC, 11));
        time.setForeground(Color.GRAY);

        left.add(title);
        left.add(body);
        left.add(Box.createVerticalStrut(5));
        left.add(time);

        card.add(checkBox, BorderLayout.WEST);
        card.add(left, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(245, 245, 245));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(n.isRead ? new Color(250, 250, 250) : CARD_BG);
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!checkBox.isSelected()) {
                    showNotificationDialog(n);
                    if (!n.isRead) {
                        n.isRead = true;
                        studentNotificationDAO.markNotificationAsRead(studentID, n.id);
                        refreshList();
                    }
                }
            }
        });

        return card;
    }

    private void showNotificationDialog(Notification n) {
        JDialog dialog = new JDialog((Frame) null, "Notification", true);
        dialog.setSize(420, 280);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setBackground(CARD_BG);

        JLabel title = new JLabel(n.icon + " " + n.title);
        title.setFont(new Font("Roboto", Font.BOLD, 18));
        title.setForeground(CTA_SECONDARY);

        JTextArea message = new JTextArea(n.message + "\n\nSent: " + n.time);
        message.setFont(new Font("Roboto", Font.PLAIN, 14));
        message.setWrapStyleWord(true);
        message.setLineWrap(true);
        message.setEditable(false);
        message.setOpaque(false);

        panel.add(title, BorderLayout.NORTH);
        panel.add(new JScrollPane(message), BorderLayout.CENTER);

        JButton closeBtn = new JButton("Close");
styleButton(closeBtn, CTA_PRIMARY, CTA_SECONDARY, new Dimension(140, 38));
        closeBtn.addActionListener(e -> dialog.dispose());
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

    // Hover effect
    btn.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            btn.setBackground(secondary);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            btn.setBackground(primary);
        }
    });
}


    private List<Notification> loadNotificationsFromDB() {
        List<Notification> list = new ArrayList<>();
        if (studentID == null) return list;

        List<NotificationsDomain> dbNotifications = studentNotificationDAO.getNotificationsByStudentID(studentID);
        for (NotificationsDomain n : dbNotifications) {
            list.add(new Notification(
                    n.getNotificationID(),
                    n.getTitle(),
                    n.getBody(),
                    n.getCreatedAt().toString(),
                    getIconForTitle(n.getTitle()),
                    false
            ));
        }

        return list;
    }

    private String getIconForTitle(String title) {
        title = title.toLowerCase();
        if (title.contains("exam")) return "📌";
        if (title.contains("assignment")) return "📝";
        if (title.contains("class") || title.contains("lecture")) return "📘";
        if (title.contains("system") || title.contains("update")) return "⚙️";
        if (title.contains("grade")) return "📊";
        if (title.contains("meeting") || title.contains("group")) return "👥";
        return "🔔"; // default
    }

    private static class Notification {
        private final int id;
        private final String title, message, time, icon;
        private boolean isRead;
        private JCheckBox checkBox;

        public Notification(String idStr, String title, String message, String time, String icon, boolean isRead) {
            this.id = Integer.parseInt(idStr);
            this.title = title;
            this.message = message;
            this.time = time;
            this.icon = icon;
            this.isRead = isRead;
        }
    }
}
