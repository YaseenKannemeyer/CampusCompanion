package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
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

    // ===================== ACCOUNT LABELS =====================
    private JLabel lblStudentID, lblFullName, lblEmail, lblGroup, lblCourse;

    public SettingsPanel(Subjects.ConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;

        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));

        // Header placeholder
        headerPanel = new HeaderBannerPanel(connectionProvider, null);
        add(headerPanel, BorderLayout.NORTH);

        // Initialize DAOs
        try {
            studentDAO = new StudentDAO();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to connect to database: " + e.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }

        // Main content
        add(createMainContent(), BorderLayout.CENTER);
    }

    // ================== SET CURRENT STUDENT ==================
    public void setStudent(String studentId, String studentGroup) {
        this.studentId = studentId;
        this.studentGroup = studentGroup;

        if (studentId != null) {
            headerPanel.setStudentId(studentId);
            refreshAccountInfo();  // load/update labels
            if (subjectListModel != null) loadStudentSubjects();
        } else {
            clearAccountInfo();
            if (subjectListModel != null) subjectListModel.clear();
        }
    }

    // ===================== MAIN CONTENT =====================
    private JPanel createMainContent() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(getBackground());
        main.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;

        gbc.gridx = 0; gbc.gridy = 0;
        main.add(createAccountCard(), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        main.add(createSubjectsCard(), gbc);

        return main;
    }

    // ===================== ACCOUNT CARD =====================
    private JPanel createAccountCard() {
        JPanel panel = createGradientCard("Account Settings");

        // ===================== INFO LABELS =====================
        lblStudentID = createInfoLabel("Student ID: Loading...");
        lblFullName  = createInfoLabel("Full Name: Loading...");
        lblEmail     = createInfoLabel("Email: Loading...");
        lblGroup     = createInfoLabel("Group: Loading...");
        lblCourse    = createInfoLabel("Course: Loading...");

        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.add(lblStudentID);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(lblFullName);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(lblEmail);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(lblGroup);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(lblCourse);

        // ===================== CHANGE PASSWORD =====================
        JButton changePasswordBtn = createStyledButton("Change Password");
        changePasswordBtn.setPreferredSize(new Dimension(200, 40));
        changePasswordBtn.addActionListener(e -> handleChangePassword());

        // ===================== DELETE ACCOUNT =====================
        JButton deleteBtn = createStyledButton("Delete Account");
        deleteBtn.setBackground(new Color(192, 57, 43));
        deleteBtn.setForeground(Color.WHITE);
        deleteBtn.addActionListener(e -> handleDeleteAccount());

        // ===================== ADD COMPONENTS =====================
        panel.add(Box.createVerticalStrut(15));
        panel.add(infoPanel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(changePasswordBtn);
        panel.add(Box.createVerticalStrut(15));
        panel.add(deleteBtn);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    // ===================== REFRESH ACCOUNT INFO =====================
    private void refreshAccountInfo() {
        if (studentId == null) return;

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

    // ===================== CLEAR ACCOUNT INFO =====================
    private void clearAccountInfo() {
        lblStudentID.setText("Student ID: -");
        lblFullName.setText("Full Name: -");
        lblEmail.setText("Email: -");
        lblGroup.setText("Group: -");
        lblCourse.setText("Course: -");
    }

    // ===================== HANDLE CHANGE PASSWORD =====================
    private void handleChangePassword() {
        if (studentId == null) return;

        String currentPass = JOptionPane.showInputDialog(this,
                "Enter your current password for Student ID: " + studentId);
        if (currentPass == null || currentPass.isEmpty()) return;

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
                refreshAccountInfo(); // refresh info in case you want to reload any dependent UI
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to update password: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ===================== HANDLE DELETE ACCOUNT =====================
    private void handleDeleteAccount() {
        if (studentId == null) return;

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete your account?",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            studentDAO.deleteStudent(studentId);
            JOptionPane.showMessageDialog(this, "Account deleted successfully.");
            clearAccountInfo();
            if (subjectListModel != null) subjectListModel.clear();
            studentId = null;
            studentGroup = null;
            headerPanel.setStudentId(null);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Failed to delete account: " + ex.getMessage(),
                    "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Helper: clean label style
    private JLabel createInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.BOLD, 16));
        lbl.setForeground(Color.WHITE);
        lbl.setOpaque(false);
        return lbl;
    }

    // ===================== SUBJECTS CARD =====================
    private JPanel createSubjectsCard() {
        JPanel panel = createGradientCard("Manage Subjects");

        subjectListModel = new DefaultListModel<>();
        subjectList = new JList<>(subjectListModel);
        subjectList.setFont(new Font("Poppins", Font.PLAIN, 13));
        subjectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Display subject nicely
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
        scroll.setPreferredSize(new Dimension(400, 280));
        scroll.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(scroll);

        return panel;
    }

    // ===================== LOAD STUDENT-SPECIFIC SUBJECTS =====================
    private void loadStudentSubjects() {
        subjectListModel.clear();
        if (studentGroup == null) return;

        List<SubjectDomain> subjects = new ArrayList<>();
        String courseId = null;

        // Get courseId from studentGroup
        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement("SELECT CourseID FROM StudentGroup WHERE GroupID=?")) {
            ps.setString(1, studentGroup);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) courseId = rs.getString("CourseID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (courseId == null) return;

        // Get year from group ID
        int yearLevel = 1;
        try { yearLevel = Integer.parseInt(studentGroup.substring(0, 1)); } catch (Exception ignored) {}

        // Fetch subjects
        try (Connection conn = connectionProvider.get();
             PreparedStatement ps = conn.prepareStatement(
                     "SELECT s.SubjectCode, s.SubjectName FROM Subject s " +
                             "JOIN SubjectCourse sc ON s.SubjectCode = sc.SubjectCode " +
                             "WHERE sc.CourseID=? AND s.YearLevel=? ORDER BY s.SubjectName")) {
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

        // Populate JList
        if (subjects.isEmpty()) {
            subjectListModel.addElement(new SubjectDomain("", "<No subjects found>", 0));
        } else {
            subjects.forEach(subjectListModel::addElement);
        }
    }

    // ===================== HELPERS =====================
    private JPanel createGradientCard(String title) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(41, 128, 185), 0, getHeight(), new Color(72, 196, 230));
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2.dispose();
            }
        };
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(420, 280));

        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Poppins", Font.BOLD, 18));
        lbl.setForeground(Color.WHITE);
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(lbl);
        panel.add(Box.createVerticalStrut(10));

        return panel;
    }

    private JButton createStyledButton(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Poppins", Font.BOLD, 13));
        btn.setBackground(new Color(41, 128, 185));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 38));

        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(new Color(52, 152, 219)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(new Color(41, 128, 185)); }
        });

        return btn;
    }
}
