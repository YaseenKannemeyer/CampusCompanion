package mycput.ac.za.studenttimetable;

import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class StudentSignupForm extends JPanel {

    private JTextField txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhoneNumber;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbGroupID, cmbYear;
    private JButton btnSignup, btnLogin;
    private StudentTimeTable parent;

    public StudentSignupForm(StudentTimeTable parent) {
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
    setLayout(new BorderLayout());
    setBackground(new Color(245, 245, 245));

    // --- Left panel (gradient + logo + welcome text) ---
    JPanel leftPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, new Color(60, 120, 200), 0, getHeight(), new Color(90, 180, 230));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    leftPanel.setPreferredSize(new Dimension(350, 0));
    leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

    // Load original logo
ImageIcon originalIcon = new ImageIcon(getClass().getResource("/icons/Logo.png"));

// Scale it to a max width/height (e.g., 120x120)
Image scaledImage = originalIcon.getImage().getScaledInstance(300, 150, Image.SCALE_SMOOTH);
ImageIcon logoIcon = new ImageIcon(scaledImage);

// Add to label
JLabel logoLabel = new JLabel(logoIcon);
logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
leftPanel.add(Box.createVerticalStrut(60));
leftPanel.add(logoLabel);


    // Welcome text
    JLabel welcomeLabel = new JLabel("<html><center>Welcome to<br>Student Portal</center></html>");
    welcomeLabel.setForeground(Color.WHITE);
    welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
    welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
    welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    leftPanel.add(Box.createVerticalStrut(20));
    leftPanel.add(welcomeLabel);
    leftPanel.add(Box.createVerticalGlue());

    add(leftPanel, BorderLayout.WEST);

    // --- Right panel (form card) ---
    JPanel formPanel = new JPanel();
    formPanel.setBackground(Color.WHITE);
    formPanel.setBorder(new CompoundBorder(
            new EmptyBorder(50, 50, 50, 50),
            new DropShadowBorder()
    ));
    formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

    // Title
    JLabel title = new JLabel("Sign Up");
    title.setFont(new Font("Segoe UI", Font.BOLD, 32));
    title.setForeground(new Color(40, 40, 40));
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    formPanel.add(title);
    formPanel.add(Box.createRigidArea(new Dimension(0, 30)));

    // --- Fields ---
    cmbYear = new JComboBox<>(new String[]{
            "Select year of study", "First year", "Second year", "Third year"
    });
    cmbYear.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
    cmbGroupID = new JComboBox<>();
    cmbGroupID.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));

    Map<String, String[]> yearToGroups = new HashMap<>();
    yearToGroups.put("First year", new String[]{
            "Select your group", "1A", "1B", "1C", "1D", "1E", "1F", "1G",
            "1H", "1I", "1J", "1K", "1L", "1M", "1N", "1O", "1P"
    });
    yearToGroups.put("Second year", new String[]{
            "Select your group", "2A", "2B", "2C", "2D", "2E", "2F", "2G",
            "2H", "2I", "2J", "2K"
    });
    yearToGroups.put("Third year", new String[]{
            "Select your group", "3A", "3B", "3C", "3D", "3E", "3F", "3G",
            "3H", "3I", "3J", "3K"
    });

    cmbYear.addActionListener(e -> {
        String selectedYear = (String) cmbYear.getSelectedItem();
        cmbGroupID.setModel(new DefaultComboBoxModel<>(
                yearToGroups.getOrDefault(selectedYear, new String[]{})
        ));
    });
    cmbYear.setSelectedIndex(0);

    txtStudentId = new JTextField();
    txtFirstName = new JTextField();
    txtLastName = new JTextField();
    txtEmail = new JTextField();
    txtPhoneNumber = new JTextField();
    txtPassword = new JPasswordField();
    txtConfirmPassword = new JPasswordField();

    // Add fields with spacing
    formPanel.add(createComboBoxFieldWithStar("Year", cmbYear));
    formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
    formPanel.add(createComboBoxFieldWithStar("Group", cmbGroupID));
    formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
    formPanel.add(createInputFieldWithStar("Student ID", txtStudentId));
    formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
    formPanel.add(createInputFieldWithStar("First Name", txtFirstName));
    formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
    formPanel.add(createInputFieldWithStar("Last Name", txtLastName));
    formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
    formPanel.add(createInputFieldWithStar("Email", txtEmail));
    formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
    formPanel.add(createInputFieldWithStar("Phone Number", txtPhoneNumber));
    formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
    formPanel.add(createPasswordFieldWithStar("Password", txtPassword, null));
    formPanel.add(Box.createRigidArea(new Dimension(0, 12)));
    formPanel.add(createPasswordFieldWithStar("Confirm Password", txtConfirmPassword, txtPassword));

    // Signup button
    btnSignup = new JButton("Sign Up");
    btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 16));
    btnSignup.setBackground(new Color(60, 130, 210));
    btnSignup.setForeground(Color.WHITE);
    btnSignup.setFocusPainted(false);
    btnSignup.setBorder(new RoundedBorder(22));
    btnSignup.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnSignup.addMouseListener(new MouseAdapter() {
        public void mouseEntered(MouseEvent e) {
            btnSignup.setBackground(new Color(50, 110, 190));
        }
        public void mouseExited(MouseEvent e) {
            btnSignup.setBackground(new Color(60, 130, 210));
        }
    });
    btnSignup.addActionListener(e -> handleSignup());
    formPanel.add(Box.createVerticalStrut(20));
    formPanel.add(btnSignup);

    // Login button
    btnLogin = new JButton("Already have an account? Login");
    btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    btnLogin.setForeground(new Color(60, 130, 210));
    btnLogin.setContentAreaFilled(false);
    btnLogin.setBorderPainted(false);
    btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnLogin.addActionListener(e -> {
        if (parent != null) parent.slideToLogin();
    });
    formPanel.add(Box.createVerticalStrut(15));
    formPanel.add(btnLogin);

    add(formPanel, BorderLayout.CENTER);
}


    // --- Input field with red star ---
    private JPanel createInputFieldWithStar(String placeholder, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // space for label + field
        panel.setBackground(Color.WHITE);

        // Label panel (placeholder + red star)
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(placeholder);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(50, 50, 50));

        JLabel star = new JLabel("*");
        star.setFont(new Font("Segoe UI", Font.BOLD, 14));
        star.setForeground(Color.RED);

        labelPanel.add(label);
        labelPanel.add(star);
        panel.add(labelPanel, BorderLayout.NORTH);

        // Input field
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));
        panel.add(field, BorderLayout.CENTER);

        // Live validation
        field.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                boolean valid = false;
                String text = field.getText().trim();
                switch (placeholder) {
                    case "Student ID":
                        valid = text.matches("\\d{9}");
                        break;
                    case "First Name":
                    case "Last Name":
                        valid = text.matches("[a-zA-Z]+");
                        break;
                    case "Email":
                        valid = text.matches("^[a-zA-Z0-9._%+-]+@(mycput\\.ac\\.za|cput\\.ac\\.za|gmail\\.com|outlook\\.com|hotmail\\.com|yahoo\\.com)$");
                        break;
                    case "Phone Number":
                        valid = text.matches("\\d{10,12}");
                        break;
                    default:
                        valid = !text.isEmpty();
                }
                star.setVisible(!valid);
            }
        });

        return panel;
    }

// --- Password field with smart validation ---
    private JPanel createPasswordFieldWithStar(String placeholder, JPasswordField field, JPasswordField confirmField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setBackground(Color.WHITE);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(placeholder);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(50, 50, 50));

        JLabel star = new JLabel("*");
        star.setFont(new Font("Segoe UI", Font.BOLD, 14));
        star.setForeground(Color.RED);

        labelPanel.add(label);
        labelPanel.add(star);
        panel.add(labelPanel, BorderLayout.NORTH);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));
        panel.add(field, BorderLayout.CENTER);

        // Password validation rules
        field.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                String password = new String(field.getPassword());
                StringBuilder message = new StringBuilder();

                boolean hasUpper = password.matches(".*[A-Z].*");
                boolean hasLower = password.matches(".*[a-z].*");
                boolean hasDigit = password.matches(".*\\d.*");
                boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

                if (!hasUpper) {
                    message.append("Missing capital letter. ");
                }
                if (!hasLower) {
                    message.append("Missing lowercase letter. ");
                }
                if (!hasDigit) {
                    message.append("Missing number. ");
                }
                if (!hasSpecial) {
                    message.append("Missing special character. ");
                }

                // Only hide star if all conditions are met
                star.setVisible(!(hasUpper && hasLower && hasDigit && hasSpecial));

                // Optionally show error tooltip
                field.setToolTipText(message.length() > 0 ? message.toString() : null);

                // Update confirm password star too if provided
                if (confirmField != null) {
                    String confirmPassword = new String(confirmField.getPassword());
                    boolean match = password.equals(confirmPassword);
                    confirmField.putClientProperty("star", match); // we'll use this later in confirmField listener
                }
            }
        });

        // --- Confirm password live validation ---
        if (confirmField != null) {
            confirmField.getDocument().addDocumentListener(new SimpleDocumentListener() {
                @Override
                public void update() {
                    String password = new String(field.getPassword());
                    String confirm = new String(confirmField.getPassword());
                    star.setVisible(!password.equals(confirm));
                }
            });
        }

        return panel;
    }

// --- ComboBox with red star ---
    private JPanel createComboBoxFieldWithStar(String placeholder, JComboBox<String> comboBox) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        panel.setBackground(Color.WHITE);

        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        labelPanel.setBackground(Color.WHITE);

        JLabel label = new JLabel(placeholder);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(50, 50, 50));

        JLabel star = new JLabel("*");
        star.setFont(new Font("Segoe UI", Font.BOLD, 14));
        star.setForeground(Color.RED);

        labelPanel.add(label);
        labelPanel.add(star);
        panel.add(labelPanel, BorderLayout.NORTH);

        comboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        comboBox.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true));
        panel.add(comboBox, BorderLayout.CENTER);

        // Live validation: must select an item
        comboBox.addActionListener(e -> {
            boolean valid = comboBox.getSelectedItem() != null && !comboBox.getSelectedItem().toString().isEmpty();
            star.setVisible(!valid);
        });

        return panel;
    }

// --- Simple Document Listener interface ---
    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {

        void update();

        default void insertUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        default void removeUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }

        default void changedUpdate(javax.swing.event.DocumentEvent e) {
            update();
        }
    }

    private void handleSignup() {
        // --- Gather input values ---
        String studentId = txtStudentId.getText().trim();
        String groupId = (String) cmbGroupID.getSelectedItem();
        String firstName = txtFirstName.getText().trim();
        String lastName = txtLastName.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhoneNumber.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());

        // --- Validation ---
        if (studentId.isEmpty() || groupId.isEmpty() || firstName.isEmpty()
                || lastName.isEmpty() || email.isEmpty() || phone.isEmpty()
                || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- New Validations ---
        // 1. Student ID: exactly 9 digits
        if (!studentId.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(this, "Student ID must be exactly 9 digits!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

// 2. Names: only letters
        if (!firstName.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this, "First name can only contain letters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!lastName.matches("[a-zA-Z]+")) {
            JOptionPane.showMessageDialog(this, "Last name can only contain letters!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

// 3. Email validation
        if (!email.matches("^[a-zA-Z0-9._%+-]+@(mycput\\.ac\\.za|cput\\.ac\\.za|gmail\\.com|outlook\\.com|hotmail\\.com|yahoo\\.com)$")) {
            JOptionPane.showMessageDialog(this, "Invalid email address! Must be a valid academic or major email.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

// 4. Phone number validation: digits only, 10-12 digits
        if (!phone.matches("\\d{10,12}")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain only digits and be 10 to 12 digits long!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
// --- Password validation ---

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        StringBuilder pwdError = new StringBuilder();
        if (!hasUpper) {
            pwdError.append("Password must contain at least 1 capital letter.\n");
        }
        if (!hasLower) {
            pwdError.append("Password must contain at least 1 lowercase letter.\n");
        }
        if (!hasDigit) {
            pwdError.append("Password must contain at least 1 number.\n");
        }
        if (!hasSpecial) {
            pwdError.append("Password must contain at least 1 special character.\n");
        }

        if (pwdError.length() > 0) {
            JOptionPane.showMessageDialog(this, pwdError.toString(), "Invalid Password", JOptionPane.ERROR_MESSAGE);
            return;
        }

// --- Confirm password ---
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // --- Create StudentDomain object ---
        StudentDomain student = new StudentDomain(
                studentId, // StudentID
                "", // UserID will be auto-generated
                groupId,
                firstName,
                lastName,
                phone,
                email
        );

        // --- Save to database ---
        try {
            StudentDAO dao = new StudentDAO();
            boolean success = dao.saveStudent(student, password); // pass password separately

            if (success) {
                JOptionPane.showMessageDialog(this, "Signup successful! Welcome, " + firstName);
                clearForm();
                if (parent != null) {
                    parent.slideToLogin();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Signup failed. Try again.", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearForm() {
        txtStudentId.setText("");
        cmbGroupID.setSelectedIndex(0);
        txtFirstName.setText("");
        txtLastName.setText("");
        txtEmail.setText("");
        txtPhoneNumber.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
    }

    // Rounded border for button
    private static class RoundedBorder extends AbstractBorder {

        private int radius;

        public RoundedBorder(int radius) {
            this.radius = radius;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(new Color(70, 130, 180));
            g.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    // Drop shadow for form panel
    private static class DropShadowBorder extends AbstractBorder {

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 30));
            for (int i = 0; i < 5; i++) {
                g2.drawRoundRect(x + i, y + i, w - 2 * i - 1, h - 2 * i - 1, 20, 20);
            }
            g2.dispose();
        }
    }
}
