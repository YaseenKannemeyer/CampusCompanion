package mycput.ac.za.studenttimetable;

import mycput.ac.za.studenttimetable.dao.StudentDAO;



import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

public class LoginForm extends JPanel {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnSignupLink;
    private StudentTimeTable parent;
    private final Subjects.ConnectionProvider connectionProvider;


    public LoginForm(StudentTimeTable parent, Subjects.ConnectionProvider connectionProvider) {
        this.parent = parent;
        this.connectionProvider = connectionProvider;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(240, 240, 240));

        // --- Left panel ---
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

        JLabel welcomeLabel = new JLabel("<html><center>Welcome Back!<br>Login to Your Account</center></html>");
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

        JLabel title = new JLabel("Login");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(50, 50, 50));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.add(title);
        formPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // Fields
        txtEmail = createInputField("Email");
        txtPassword = createPasswordField("Password");

        formPanel.add(txtEmail);
        formPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        formPanel.add(txtPassword);
        formPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Login button
        btnLogin = new JButton("Login");
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setBackground(new Color(70, 130, 180));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(new RoundedBorder(20));
        btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnLogin.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnLogin.setBackground(new Color(60, 110, 170)); }
            public void mouseExited(MouseEvent e) { btnLogin.setBackground(new Color(70, 130, 180)); }
        });

        btnLogin.addActionListener(e -> handleLogin());
        formPanel.add(btnLogin);
        formPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Sign up link
        btnSignupLink = new JButton("Don't have an account? Sign Up");
        btnSignupLink.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btnSignupLink.setForeground(new Color(70, 130, 180));
        btnSignupLink.setContentAreaFilled(false);
        btnSignupLink.setBorderPainted(false);
        btnSignupLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        btnSignupLink.addActionListener(e -> {
            if (parent != null) parent.slideToSignup();
        });

        formPanel.add(btnSignupLink);

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

  private void handleLogin() {
    JOptionPane.showMessageDialog(this, "Login clicked!");
    String email = txtEmail.getText().trim();
    String password = new String(txtPassword.getPassword());

    if (email.isEmpty() || password.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Please enter both email and password", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        StudentDAO dao = new StudentDAO();

        // Attempt login and fetch student info
        StudentDomain student = dao.loginStudent(email, password);

        if (student == null) {
            JOptionPane.showMessageDialog(this, "Invalid email or password", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // ✅ Store correct studentId and groupId
        Session.setStudent(student.getStudentID(), student.getGroupID());

        // Show success
        JOptionPane.showMessageDialog(this, "Login successful! Welcome, " + student.getFirstName() + ".");

        if (parent != null) {
            // ✅ Load Dashboard first, not Subjects
            DashboardPanel dashboard = new DashboardPanel(connectionProvider, student.getStudentID(), student.getGroupID());
            parent.showMainDashboard(dashboard);

            // ✅ Pass into sidebar
            parent.getSidebar().setCurrentStudent(student.getStudentID(), student.getGroupID());
        }

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}







    // Rounded border
    private static class RoundedBorder extends AbstractBorder {
        private int radius;
        public RoundedBorder(int radius) { this.radius = radius; }
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            g.setColor(new Color(70, 130, 180));
            g.drawRoundRect(x, y, width-1, height-1, radius, radius);
        }
    }

    // Drop shadow
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
