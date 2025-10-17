package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;
import mycput.ac.za.studenttimetable.domain.SubjectDomain;

public class SettingsPanel extends JPanel {

    private final Subjects.ConnectionProvider connectionProvider;
    private HeaderBannerPanel headerPanel;

    private DefaultListModel<SubjectDomain> subjectListModel;
    private JList<SubjectDomain> subjectList;

    private StudentDAO studentDAO;
    private String studentId;
    private String studentGroup;

    // ================= COLORS =================
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color CTA_PRIMARY = new Color(0xE7404A);
    private static final Color CTA_SECONDARY = new Color(0x1996CC);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    // ================= ACCOUNT LABELS =================
    private JLabel lblStudentID, lblFullName, lblEmail, lblGroup, lblCourse;

    public SettingsPanel(Subjects.ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
        setLayout(new BorderLayout());
        setBackground(PRIMARY_BG);

        // Header
        headerPanel = new HeaderBannerPanel(connectionProvider, null);
        add(headerPanel, BorderLayout.NORTH);

        // Initialize DAO
        try {
            studentDAO = new StudentDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to connect to database: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }

        add(createMainContent(), BorderLayout.CENTER);
    }

    // ================= SET STUDENT =================
    public void setStudent(String studentId, String studentGroup) {
        this.studentId = studentId;
        this.studentGroup = studentGroup;

        headerPanel.setStudentId(studentId);

        if (studentId != null) {
            refreshAccountInfo();
            if (subjectListModel != null) {
                loadStudentSubjects();
            }
        } else {
            clearAccountInfo();
            if (subjectListModel != null) {
                subjectListModel.clear();
            }
        }
    }

    // ================= MAIN CONTENT =================
    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(PRIMARY_BG);
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);

        // ================= LEFT COLUMN (Account + Subjects) =================
        JPanel leftColumn = new JPanel(new GridLayout(2, 1, 0, 15));
        leftColumn.setOpaque(false);
        leftColumn.add(createAccountCard());
        leftColumn.add(createSubjectsCard());

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;           // takes all extra space
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH; // expand fully
        main.add(leftColumn, gbc);

        // ================= RIGHT COLUMN (App Info) =================
        JPanel rightColumn = createAboutCard();

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0;           // only as wide as preferred
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.VERTICAL; // only stretch vertically
        main.add(rightColumn, gbc);

        return main;
    }

    // ================= ACCOUNT CARD =================
    private JPanel createAccountCard() {
        JPanel panel = createCard("Account Management");

        lblStudentID = createInfoLabel("Student ID: Loading...");
        lblFullName = createInfoLabel("Full Name: Loading...");
        lblEmail = createInfoLabel("Email: Loading...");
        lblGroup = createInfoLabel("Group: Loading...");
        lblCourse = createInfoLabel("Course: Loading...");

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(lblStudentID);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblFullName);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblEmail);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblGroup);
        infoPanel.add(Box.createVerticalStrut(5));
        infoPanel.add(lblCourse);

        // Wrap infoPanel to prevent text clipping
        JScrollPane scroll = new JScrollPane(infoPanel);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(0, 160)); // fixed height for consistency
        panel.add(scroll);

        panel.add(Box.createVerticalStrut(15));

        // Buttons
        panel.add(createButtonPanel(
                new String[]{"Change Password", "Delete Account"},
                new Color[]{CTA_PRIMARY, new Color(192, 57, 43)},
                new Color[]{CTA_SECONDARY, new Color(231, 76, 60)},
                new Runnable[]{this::handleChangePassword, this::handleDeleteAccount}
        ));

        return panel;
    }

    // ================= SUBJECT CARD =================
    private JPanel createSubjectsCard() {
        JPanel panel = createCard("Subject Management");

        subjectListModel = new DefaultListModel<>();
        subjectList = new JList<>(subjectListModel);
        subjectList.setFont(new Font("Roboto", Font.PLAIN, 13));
        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        subjectList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof SubjectDomain s) {
                    label.setText(s.getSubjectCode() + " - " + s.getSubjectName());
                }
                return label;
            }
        });

        JScrollPane scroll = new JScrollPane(subjectList);
        scroll.setPreferredSize(new Dimension(0, 160)); // same height as account card
        scroll.setBorder(null);
        panel.add(scroll);

        return panel;
    }

    // ================= ABOUT CARD =================
   private JPanel createAboutCard() {
    JPanel panel = createCard("Application Info");
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setAlignmentX(Component.CENTER_ALIGNMENT);

    // --- About Text ---
    JTextArea aboutText = new JTextArea(
            "Student Timetable App v1.0\n"
            + "Purpose: Manage student accounts, subjects, and application settings.\n"
            + "2025 © MyCPUT"
    );
    aboutText.setFont(new Font("Roboto", Font.PLAIN, 13));
    aboutText.setEditable(false);
    aboutText.setOpaque(false);
    aboutText.setLineWrap(true);
    aboutText.setWrapStyleWord(true);
    aboutText.setAlignmentX(Component.CENTER_ALIGNMENT); // horizontal center
    aboutText.setMaximumSize(new Dimension(350, 100));   // optional max width
    aboutText.setMinimumSize(new Dimension(350, 60));    // optional min height
    aboutText.setPreferredSize(new Dimension(350, 80));
    aboutText.setHighlighter(null); // remove text selection highlight
    aboutText.setMargin(new Insets(0, 0, 0, 0));
    aboutText.setFocusable(false);

    panel.add(Box.createVerticalGlue()); // push content toward center vertically
    panel.add(aboutText);
    panel.add(Box.createVerticalStrut(12));

    // --- Logo below About Text ---
    try {
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/icons/Logo.png"));
        Image scaledLogo = logoIcon.getImage().getScaledInstance(150, 100, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledLogo));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // horizontal center
        panel.add(logoLabel);
        panel.add(Box.createVerticalStrut(12));
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    // --- Developers Section ---
    String[] developers = {
        "MOGAMAT YASEEN KANNEMEYER",
        "JOSHUA REID ADAMS",
        "AIDAN BARENDS",
        "MATTHEW BARRON",
        "WILL BRYAN KOERIES",
        "ALPHONSINE NINGABIYE"
    };

    JPanel devPanel = new JPanel();
    devPanel.setLayout(new BoxLayout(devPanel, BoxLayout.Y_AXIS));
    devPanel.setOpaque(false);
    devPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // center panel horizontally

    // Developers title
    JLabel devTitle = new JLabel("Developers");
    devTitle.setFont(new Font("Roboto", Font.BOLD, 15));
    devTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    devPanel.add(devTitle);
    devPanel.add(Box.createVerticalStrut(8));

    for (String dev : developers) {
        JLabel devLabel = new JLabel(dev);
        devLabel.setFont(new Font("Roboto", Font.PLAIN, 13));
        devLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        devPanel.add(devLabel);
    }
    panel.add(devPanel);

    panel.add(Box.createVerticalGlue()); // push everything to center vertically

    // --- GitHub Section ---
JLabel githubMessage = new JLabel("Click below to see updates and star if you love the app!");
githubMessage.setFont(new Font("Roboto", Font.ITALIC, 13));
githubMessage.setAlignmentX(Component.CENTER_ALIGNMENT); // centers horizontally
githubMessage.setHorizontalAlignment(SwingConstants.CENTER); // ensures label text is centered
panel.add(Box.createVerticalStrut(12));
panel.add(githubMessage);
panel.add(Box.createVerticalStrut(6));

JLabel githubLabel = createLinkLabel("GitHub Repository", () -> {
    try {
        Desktop.getDesktop().browse(new URI("https://github.com/YaseenKannemeyer/CampusCompanion"));
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(null, "Unable to open GitHub repository.");
    }
});
githubLabel.setFont(new Font("Roboto", Font.BOLD, 14));
githubLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // centers horizontally
githubLabel.setHorizontalAlignment(SwingConstants.CENTER); // ensures label text is centered
panel.add(githubLabel);
panel.add(Box.createVerticalStrut(12));

    // --- Links (Terms, Help, Contact Us) ---
    JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    linksPanel.setOpaque(false);

    String basePath = "/Users/mogamatyaseenkannemeyer/Documents/CPUT 3rd YEAR/PRT2/StudentTimeTable/src/main/resources/";

    JLabel termsLabel = createLinkLabel("Terms & Privacy", () -> {
        try {
            Desktop.getDesktop().browse(new File(basePath + "Terms&Privacy.html").toURI());
        } catch (IOException ex) { ex.printStackTrace(); }
    });
    JLabel helpLabel = createLinkLabel("Help", () -> {
        try {
            Desktop.getDesktop().browse(new File(basePath + "Help.html").toURI());
        } catch (IOException ex) { ex.printStackTrace(); }
    });
    JLabel contactLabel = createLinkLabel("Contact Us", () -> {
        try {
            Desktop.getDesktop().browse(new File(basePath + "Contact.html").toURI());
        } catch (IOException ex) { ex.printStackTrace(); }
    });

    termsLabel.setFont(new Font("Roboto", Font.PLAIN, 13));
    helpLabel.setFont(new Font("Roboto", Font.PLAIN, 13));
    contactLabel.setFont(new Font("Roboto", Font.PLAIN, 13));

    linksPanel.add(termsLabel);
    linksPanel.add(helpLabel);
    linksPanel.add(contactLabel);
    panel.add(linksPanel);

    panel.setPreferredSize(new Dimension(400, 0));
    panel.setMinimumSize(new Dimension(400, 0));

    return panel;
}


    private JLabel createLinkLabel(String text, Runnable action) {
        JLabel lbl = new JLabel("<html><u>" + text + "</u></html>");
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.setForeground(CTA_SECONDARY);
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                action.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lbl.setForeground(CTA_SECONDARY.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lbl.setForeground(CTA_SECONDARY);
            }
        });
        lbl.setFont(new Font("Roboto", Font.PLAIN, 12));
        return lbl;
    }

    private void openExternal(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
            }
        } catch (Exception ex) {
            // ignore
        }
    }

    // ================= ACCOUNT REFRESH =================
    private void refreshAccountInfo() {
        if (studentId == null) {
            return;
        }

        new SwingWorker<StudentDomain, Void>() {
            @Override
            protected StudentDomain doInBackground() throws Exception {
                return studentDAO.getStudentProfile(studentId);
            }

            @Override
            protected void done() {
                try {
                    StudentDomain student = get();
                    if (student != null) {
                        lblStudentID.setText("Student ID: " + student.getStudentID());
                        lblFullName.setText("Full Name: " + student.getFirstName() + " " + student.getLastName());
                        lblEmail.setText("Email: " + student.getEmail());
                        lblGroup.setText("Group: " + (student.getGroupID() != null ? student.getGroupID() : "-"));
                        lblCourse.setText("Course: " + (student.getCourseName() != null ? student.getCourseName() : "-"));
                    } else {
                        clearAccountInfo();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    clearAccountInfo();
                }
            }
        }.execute();
    }

    private void clearAccountInfo() {
        lblStudentID.setText("Student ID: -");
        lblFullName.setText("Full Name: -");
        lblEmail.setText("Email: -");
        lblGroup.setText("Group: -");
        lblCourse.setText("Course: -");
    }

    // ================= CHANGE PASSWORD =================
    private void handleChangePassword() {
        if (studentId == null) {
            return;
        }

        String currentPass = JOptionPane.showInputDialog(this,
                "Enter your current password for Student ID: " + studentId);
        if (currentPass == null || currentPass.isEmpty()) {
            return;
        }

        try {
            if (!studentDAO.checkPassword(studentId, currentPass)) {
                JOptionPane.showMessageDialog(this, "Current password incorrect.");
                return;
            }

            JPanel passwordPanel = new JPanel(new GridLayout(2, 2, 5, 5));
            JPasswordField newPassField = new JPasswordField();
            JPasswordField confirmPassField = new JPasswordField();
            passwordPanel.add(new JLabel("New Password:"));
            passwordPanel.add(newPassField);
            passwordPanel.add(new JLabel("Confirm Password:"));
            passwordPanel.add(confirmPassField);

            int result = JOptionPane.showConfirmDialog(this, passwordPanel,
                    "Enter New Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String newPass = new String(newPassField.getPassword());
                String confirmPass = new String(confirmPassField.getPassword());
                if (!newPass.equals(confirmPass)) {
                    JOptionPane.showMessageDialog(this, "Passwords do not match.");
                    return;
                }
                studentDAO.updatePassword(studentId, newPass);
                JOptionPane.showMessageDialog(this, "Password updated successfully.");
                refreshAccountInfo();
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update password: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= DELETE ACCOUNT =================
    private void handleDeleteAccount() {
        if (studentId == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            studentDAO.deleteStudent(studentId);
            JOptionPane.showMessageDialog(this, "Account deleted successfully.");
            clearAccountInfo();
            if (subjectListModel != null) {
                subjectListModel.clear();
            }
            studentId = null;
            studentGroup = null;
            headerPanel.setStudentId(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete account: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ================= HELPERS =================
    private JLabel createInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Roboto", Font.PLAIN, 14));
        lbl.setForeground(Color.DARK_GRAY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setHorizontalAlignment(SwingConstants.LEFT);
        return lbl;
    }

    private JPanel createCard(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(CARD_BG);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(15, 15, 15, 15),
                BorderFactory.createLineBorder(SHADOW_COLOR, 1, true)
        ));
        panel.setOpaque(true);

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Roboto", Font.BOLD, 18));
        lbl.setForeground(CTA_SECONDARY);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(10));
        return panel;
    }

    private JPanel createButtonPanel(String[] texts, Color[] primary, Color[] hover, Runnable[] actions) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        for (int i = 0; i < texts.length; i++) {
            final int index = i;
            JButton btn = createStyledButton(texts[i], primary[i], hover[i]);
            btn.addActionListener(e -> actions[index].run());
            panel.add(btn);
            panel.add(Box.createVerticalStrut(8));
        }
        return panel;
    }

    private JButton createStyledButton(String text, Color primary, Color hover) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Roboto", Font.PLAIN, 14));
        btn.setBackground(primary);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setOpaque(true);
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Rounded corners
        btn.setUI(new javax.swing.plaf.basic.BasicButtonUI() {
            @Override
            public void installUI(JComponent c) {
                super.installUI(c);
                c.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
                c.setBackground(primary);
                c.setForeground(Color.WHITE);
            }
        });

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(hover);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(primary);
            }
        });

        return btn;
    }

    // ================= LOAD SUBJECTS =================
    private void loadStudentSubjects() {
        subjectListModel.clear();
        if (studentGroup == null) {
            return;
        }

        List<SubjectDomain> subjects = new ArrayList<>();
        String courseId = null;

        try (Connection conn = connectionProvider.get(); PreparedStatement ps = conn.prepareStatement("SELECT CourseID FROM StudentGroup WHERE GroupID=?")) {
            ps.setString(1, studentGroup);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    courseId = rs.getString("CourseID");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (courseId == null) {
            return;
        }

        int yearLevel = 1;
        try {
            yearLevel = Integer.parseInt(studentGroup.substring(0, 1));
        } catch (Exception ignored) {
        }

        try (Connection conn = connectionProvider.get(); PreparedStatement ps = conn.prepareStatement(
                "SELECT s.SubjectCode, s.SubjectName FROM Subject s "
                + "JOIN SubjectCourse sc ON s.SubjectCode = sc.SubjectCode "
                + "WHERE sc.CourseID=? AND s.YearLevel=? ORDER BY s.SubjectName")) {
            ps.setString(1, courseId);
            ps.setInt(2, yearLevel);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    subjects.add(new SubjectDomain(rs.getString("SubjectCode"), rs.getString("SubjectName"), yearLevel));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (subjects.isEmpty()) {
            subjectListModel.addElement(new SubjectDomain("", "<No subjects found>", 0));
        } else {
            subjects.forEach(subjectListModel::addElement);
        }
    }
}
