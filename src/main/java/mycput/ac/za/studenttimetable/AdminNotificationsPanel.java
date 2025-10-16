package mycput.ac.za.studenttimetable;

import mycput.ac.za.studenttimetable.dao.*;
import mycput.ac.za.studenttimetable.domain.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminNotificationsPanel extends JPanel {

    // === UI COMPONENTS ===
    private final JRadioButton groupRadio = new JRadioButton("Group", true);
    private final JRadioButton studentRadio = new JRadioButton("Student");
    private final JComboBox<String> recipientCombo = new JComboBox<>();
private final JTextField searchField = new JTextField(15);
private final JButton searchButton = new JButton("Search");

    private final JTextField adminField = new JTextField("A001", 15);
    private final JTextField titleField = new JTextField();
    private final JTextArea messageArea = new JTextArea(6, 40);
    private final JLabel statusLabel = new JLabel(" ");
    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> notificationsList = new JList<>(listModel);

    // === DATA ===
    private List<String> students;
    private List<String> groups;

    // === DAOs ===
    private StudentDAO studentDAO;
    private StudentGroupDAO groupDAO;
    private NotificationsDAO notificationDAO;
    private StudentNotificationDAO studentNotifDAO;
    private GroupNotificationsDAO groupNotifDAO;

    // === COLOR SCHEME (same palette as Dashboard) ===
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color ACCENT_PRIMARY = new Color(0x1996CC);
    private static final Color ACCENT_ERROR = new Color(0xE7404A);
    private static final Color BORDER_COLOR = new Color(0, 0, 0, 30);

    public AdminNotificationsPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(PRIMARY_BG);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        try {
            studentDAO = new StudentDAO();
            groupDAO = new StudentGroupDAO();
            notificationDAO = new NotificationsDAO();
            studentNotifDAO = new StudentNotificationDAO();
            groupNotifDAO = new GroupNotificationsDAO();
            loadRecipientsFromDB();
        } catch (SQLException e) {
            e.printStackTrace();
            students = new ArrayList<>();
            groups = new ArrayList<>();
            JOptionPane.showMessageDialog(this, "Database error:\n" + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // === LAYOUT SECTIONS ===
        add(buildHeader(), BorderLayout.NORTH);
        add(buildMainContent(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        groupRadio.addActionListener(e -> toggleAudience());
        studentRadio.addActionListener(e -> toggleAudience());
        toggleAudience();
    }

    private void loadRecipientsFromDB() throws SQLException {
        students = studentDAO.getAllStudentIDs();
        groups = groupDAO.getAllGroupIDs();
    }

    // ====================== HEADER ======================
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(ACCENT_PRIMARY);
        header.setBorder(new EmptyBorder(15, 20, 15, 20));

        JLabel icon = new JLabel("📢");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 32));
        icon.setForeground(Color.WHITE);

        JLabel title = new JLabel("Admin Notifications Center");
        title.setFont(new Font("Roboto", Font.BOLD, 20));
        title.setForeground(Color.WHITE);

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        left.setBackground(ACCENT_PRIMARY);
        left.add(icon);
        left.add(title);

        header.add(left, BorderLayout.WEST);
        return header;
    }

    // ====================== MAIN CONTENT ======================
    private JPanel buildMainContent() {
        JPanel content = new JPanel(new GridLayout(1, 2, 20, 0));
        content.setOpaque(false);

        // Left side: Compose message
        JPanel composeCard = new JPanel(new BorderLayout(15, 15));
        composeCard.setBackground(CARD_BG);
        composeCard.setBorder(createCardBorder("Compose Notification"));

        composeCard.add(buildAudiencePanel(), BorderLayout.NORTH);
        composeCard.add(buildMessagePanel(), BorderLayout.CENTER);

        // Right side: Sent notifications
        JPanel sentCard = new JPanel(new BorderLayout(10, 10));
        sentCard.setBackground(CARD_BG);
        sentCard.setBorder(createCardBorder("Sent Notifications"));

        notificationsList.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        notificationsList.setFixedCellHeight(28);
        sentCard.add(new JScrollPane(notificationsList), BorderLayout.CENTER);

        content.add(composeCard);
        content.add(sentCard);
        return content;
    }

    private TitledBorder createCardBorder(String title) {
        return BorderFactory.createTitledBorder(
                new CompoundBorder(new LineBorder(BORDER_COLOR, 1, true),
                        new EmptyBorder(12, 12, 12, 12)),
                title,
                TitledBorder.LEADING,
                TitledBorder.TOP,
                new Font("Roboto", Font.BOLD, 16),
                ACCENT_PRIMARY
        );
    }

    // ====================== PANELS ======================
   private JPanel buildAudiencePanel() {
    JPanel panel = new JPanel(new GridBagLayout());
    panel.setOpaque(false);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(5, 8, 5, 8);
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;

    ButtonGroup bg = new ButtonGroup();
    bg.add(groupRadio);
    bg.add(studentRadio);

    JLabel lblSendTo = new JLabel("Send to:");
    lblSendTo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    gbc.gridx = 0; gbc.gridy = 0;
    panel.add(lblSendTo, gbc);

    JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
    radios.setOpaque(false);
    radios.add(groupRadio);
    radios.add(studentRadio);
    gbc.gridx = 1;
    panel.add(radios, gbc);

    JLabel lblRecipient = new JLabel("Recipient:");
    lblRecipient.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    gbc.gridx = 0; gbc.gridy = 1;
    panel.add(lblRecipient, gbc);

    // === Recipient + Search Row ===
    JPanel comboSearchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
    comboSearchPanel.setOpaque(false);
    recipientCombo.setPreferredSize(new Dimension(160, 28));
    searchField.setPreferredSize(new Dimension(100, 28));
    searchButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    searchButton.setFocusPainted(false);
    searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    searchButton.setBackground(new Color(230, 230, 230));

    comboSearchPanel.add(recipientCombo);
    comboSearchPanel.add(searchField);
    comboSearchPanel.add(searchButton);

    gbc.gridx = 1;
    panel.add(comboSearchPanel, gbc);

    // === Action Listeners ===
    groupRadio.addActionListener(e -> toggleAudience());
    studentRadio.addActionListener(e -> toggleAudience());

    searchButton.addActionListener(e -> searchStudent());

    return panel;
}
private void searchStudent() {
    String query = searchField.getText().trim().toLowerCase();
    recipientCombo.removeAllItems();

    if (query.isEmpty()) {
        for (String s : students) recipientCombo.addItem(s);
        statusLabel.setText("Showing all students");
        return;
    }

    boolean found = false;
    for (String s : students) {
        if (s.toLowerCase().contains(query)) {
            recipientCombo.addItem(s);
            found = true;
        }
    }

    if (found) {
        statusLabel.setText("Search results for: " + query);
    } else {
        recipientCombo.addItem("No results found");
        statusLabel.setText("No matching students found for: " + query);
    }
}


    private JPanel buildMessagePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);

        JPanel fields = new JPanel(new GridLayout(2, 2, 10, 10));
        fields.setOpaque(false);
        fields.add(new JLabel("Admin ID:"));
        fields.add(adminField);
        fields.add(new JLabel("Title:"));
        fields.add(titleField);
        panel.add(fields, BorderLayout.NORTH);

        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                new EmptyBorder(8, 8, 8, 8)
        ));
        panel.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        return panel;
    }

    // ====================== FOOTER ======================
    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 5));
        btnPanel.setOpaque(false);

        JButton resetBtn = createStyledButton("Reset", new Color(220, 220, 220), Color.DARK_GRAY);
        JButton sendBtn = createStyledButton("Send ➤", ACCENT_PRIMARY, Color.WHITE);
        JButton delBtn = createStyledButton("🗑 Delete Selected", ACCENT_ERROR, Color.WHITE);

        resetBtn.addActionListener(e -> resetForm());
        sendBtn.addActionListener(e -> sendNotification());
        delBtn.addActionListener(e -> deleteNotification());

        btnPanel.add(resetBtn);
        btnPanel.add(sendBtn);
        btnPanel.add(delBtn);

        footer.add(btnPanel, BorderLayout.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        statusLabel.setBorder(new EmptyBorder(5, 10, 10, 10));
        footer.add(statusLabel, BorderLayout.SOUTH);
        return footer;
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Roboto", Font.BOLD, 13));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(bg.darker());
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(bg);
            }
        });
        return btn;
    }

    // ====================== LOGIC (unchanged) ======================
   private void toggleAudience() {
    recipientCombo.removeAllItems();
    boolean isGroup = groupRadio.isSelected();

    searchField.setVisible(!isGroup);
    searchButton.setVisible(!isGroup);

    if (isGroup) {
        for (String g : groups) recipientCombo.addItem(g);
        statusLabel.setText("Viewing groups");
    } else {
        for (String s : students) recipientCombo.addItem(s);
        statusLabel.setText("Viewing students");
    }
}


    private void resetForm() {
        adminField.setText("A001");
        titleField.setText("");
        messageArea.setText("");
        groupRadio.setSelected(true);
        toggleAudience();
        statusLabel.setText("Form reset ✔");
    }

    private void sendNotification() {
        // (same as your existing logic)
    }

    private void deleteNotification() {
        // (same as your existing logic)
    }
}
