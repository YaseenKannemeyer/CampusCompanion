package mycput.ac.za.studenttimetable;

import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;

public class StudentSignupForm extends JPanel {

    private JTextField txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhoneNumber;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbGroupID;
    private JButton btnSignup, btnLogin;
    private StudentTimeTable parent;

    public StudentSignupForm(StudentTimeTable parent) {
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // --- Left panel (gradient welcome) ---
        JPanel leftPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180), 0, getHeight(), new Color(100, 180, 220));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        leftPanel.setPreferredSize(new Dimension(300, 0));
        leftPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel("<html><center>Welcome to<br>Student Portal</center></html>");
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        leftPanel.add(welcomeLabel, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);

        // --- Right panel (form) ---
        JPanel formPanel = new JPanel();
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(new CompoundBorder(
                new EmptyBorder(40, 40, 40, 40),
                new DropShadowBorder()
        ));
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Title
        JLabel title = new JLabel("Sign Up");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(50, 50, 50));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Fields
        cmbGroupID = new JComboBox<>(new String[]{"1G", "1F", "2G", "2E"});
        cmbGroupID.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtStudentId = createInputField("Student ID");
        txtFirstName = createInputField("First Name");
        txtLastName = createInputField("Last Name");
        txtEmail = createInputField("Email");
        txtPhoneNumber = createInputField("Phone Number");
        txtPassword = createPasswordField("Password");
        txtConfirmPassword = createPasswordField("Confirm Password");

        // Add fields with spacing
        formPanel.add(cmbGroupID);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtStudentId);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtFirstName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtLastName);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtPhoneNumber);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtConfirmPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Signup button
        btnSignup = new JButton("Sign Up");
        btnSignup.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnSignup.setBackground(new Color(70, 130, 180));
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFocusPainted(false);
        btnSignup.setBorder(new RoundedBorder(20));
        btnSignup.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Hover effect
        btnSignup.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnSignup.setBackground(new Color(60, 110, 170)); }
            public void mouseExited(MouseEvent e) { btnSignup.setBackground(new Color(70, 130, 180)); }
        });

        btnSignup.addActionListener(e -> handleSignup());
        formPanel.add(btnSignup);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // "Already have an account? Login" button
        btnLogin = new JButton("Already have an account? Login");
        btnLogin.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnLogin.setForeground(new Color(70, 130, 180));
        btnLogin.setContentAreaFilled(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addActionListener(e -> {
            if (parent != null) parent.slideToLogin();
        });

        formPanel.add(btnLogin);

        add(formPanel, BorderLayout.CENTER);
    }

    private JTextField createInputField(String placeholder) {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                placeholder
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return field;
    }

    private JPasswordField createPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        field.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1, true),
                placeholder
        ));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return field;
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
    if (studentId.isEmpty() || groupId.isEmpty() || firstName.isEmpty() ||
        lastName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
        password.isEmpty() || confirmPassword.isEmpty()) {
        JOptionPane.showMessageDialog(this, "All fields are required!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (!password.equals(confirmPassword)) {
        JOptionPane.showMessageDialog(this, "Passwords do not match!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // --- Create StudentDomain object ---
    // UserID will be generated automatically inside DAO, so we pass an empty string
    StudentDomain student = new StudentDomain(
        studentId,   // StudentID
        "",          // UserID will be auto-generated
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
            if (parent != null) parent.slideToLogin();
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
        public RoundedBorder(int radius) { this.radius = radius; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(new Color(70, 130, 180));
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    // Drop shadow for form panel
    private static class DropShadowBorder extends AbstractBorder {
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setColor(new Color(0, 0, 0, 30));
            for (int i = 0; i < 5; i++) {
                g2.drawRoundRect(x+i, y+i, w-2*i-1, h-2*i-1, 20, 20);
            }
            g2.dispose();
        }
    }
}
