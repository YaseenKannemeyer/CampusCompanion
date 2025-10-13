package mycput.ac.za.studenttimetable;

import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import java.awt.Desktop;
import java.net.URI;

public class LoginForm extends JPanel {

    private JTextField txtEmail;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnSignupLink;
    private final StudentTimeTable parent;
    private final Subjects.ConnectionProvider connectionProvider;

    // ================== COLORS ==================
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color CTA_PRIMARY = new Color(0xE7404A);
    private static final Color CTA_SECONDARY = new Color(0x1996CC);
    private static final Color TEXT = new Color(0x333333);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public LoginForm(StudentTimeTable parent, Subjects.ConnectionProvider connectionProvider) {
        this.parent = parent;
        this.connectionProvider = connectionProvider;
        initComponents();
    }

  private void initComponents() {
    // Use GridBagLayout for main panel to center topPanel + formCard
    setLayout(new GridBagLayout());
    setBackground(PRIMARY_BG);

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.fill = GridBagConstraints.NONE;
    gbc.anchor = GridBagConstraints.CENTER;
    gbc.insets = new Insets(0, 0, 10, 0);

    // ================= TOP PANEL (Logo + Welcome + Hint) =================
    JPanel topPanel = new JPanel();
    topPanel.setOpaque(false);
    topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));

    // --- Logo ---
    ImageIcon originalIcon = null;
    try {
        originalIcon = new ImageIcon(getClass().getResource("/icons/LogoB.png"));
    } catch (Exception ex) { /* ignore */ }

    if (originalIcon != null) {
        Image scaledImage = originalIcon.getImage().getScaledInstance(200, 140, Image.SCALE_SMOOTH);
        JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(logoLabel);
    } else {
        JLabel logoText = new JLabel("Student Portal");
        logoText.setFont(new Font("Roboto", Font.BOLD, 28));
        logoText.setForeground(TEXT);
        logoText.setAlignmentX(Component.CENTER_ALIGNMENT);
        topPanel.add(logoText);
    }
topPanel.add(Box.createVerticalStrut(20));

JLabel welcome = new JLabel("<html><div style='text-align:center;'>Welcome Back!<br><b>Login to Your Account</b></div></html>");
welcome.setFont(new Font("Roboto", Font.PLAIN, 20));
welcome.setForeground(TEXT);
welcome.setAlignmentX(Component.CENTER_ALIGNMENT);   // BoxLayout centering
welcome.setHorizontalAlignment(SwingConstants.CENTER); // Text horizontal centering
welcome.setVerticalAlignment(SwingConstants.CENTER);   // Text vertical centering
topPanel.add(welcome);


//    // --- Hint text ---
//    topPanel.add(Box.createVerticalStrut(12));
//    JLabel hint = new JLabel("<html><div style='text-align:center;'>Access your timetables, assignments and announcements quickly and securely.</div></html>");
//    hint.setFont(new Font("Roboto", Font.PLAIN, 13));
//    hint.setForeground(TEXT);
//    hint.setAlignmentX(Component.CENTER_ALIGNMENT);
//    topPanel.add(hint);
//
//    // Add topPanel to main panel (GridBagLayout centers it)
   add(topPanel, gbc);

    // ================= FORM CARD =================
    gbc.gridy = 1;
    gbc.insets = new Insets(20, 40, 40, 40);

    JPanel wrapper = new JPanel(new GridBagLayout());
    wrapper.setOpaque(false);

    JPanel formCard = new JPanel();
    formCard.setBackground(CARD_BG);
    formCard.setBorder(new CompoundBorder(
            new EmptyBorder(28, 28, 28, 28),
            new MatteDropShadowBorder(SHADOW_COLOR, 12, 20, 0.12f)
    ));
    formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
    formCard.setMaximumSize(new Dimension(760, 800));
    formCard.setAlignmentX(Component.CENTER_ALIGNMENT);

    // --- Title ---
    JLabel title = new JLabel("Login");
    title.setFont(new Font("Roboto", Font.BOLD, 28));
    title.setForeground(TEXT);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    formCard.add(title);

    JLabel subtitle = new JLabel("Enter your student details to access the portal.");
    subtitle.setFont(new Font("Roboto", Font.PLAIN, 14));
    subtitle.setForeground(new Color(0x666666));
    subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    formCard.add(Box.createVerticalStrut(8));
    formCard.add(subtitle);
    formCard.add(Box.createVerticalStrut(20));

    // --- Fields ---
    JPanel fields = new JPanel(new GridBagLayout());
    fields.setOpaque(false);
    GridBagConstraints fgbc = new GridBagConstraints();
    fgbc.fill = GridBagConstraints.HORIZONTAL;
    fgbc.insets = new Insets(10, 8, 10, 8);
    fgbc.weightx = 1.0;

    txtEmail = new JTextField();
    txtEmail.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtEmail.setBorder(createMaterialFieldBorder());
    txtEmail.setPreferredSize(new Dimension(680, 46));
    JPanel emailPanel = createLabeledField("Email Address", txtEmail);
    fgbc.gridx = 0; fgbc.gridy = 0;
    fields.add(emailPanel, fgbc);

    txtPassword = new JPasswordField();
    txtPassword.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtPassword.setBorder(createMaterialFieldBorder());
    txtPassword.setPreferredSize(new Dimension(680, 46));
    JPanel passPanel = createLabeledPasswordField("Password", txtPassword);
    fgbc.gridy = 1;
    fields.add(passPanel, fgbc);

    formCard.add(fields);
    formCard.add(Box.createVerticalStrut(30));

    // --- Login Button ---
    btnLogin = new JButton("Login");
    btnLogin.setFont(new Font("Roboto", Font.BOLD, 16));
    btnLogin.setBackground(CTA_PRIMARY);
    btnLogin.setForeground(Color.WHITE);
    btnLogin.setFocusPainted(false);
    btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnLogin.setBorder(new EmptyBorder(12, 28, 12, 28));
    btnLogin.setOpaque(true);
    btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnLogin.setPreferredSize(new Dimension(220, 52));
    btnLogin.addMouseListener(new MouseAdapter() {
        @Override public void mouseEntered(MouseEvent e) { btnLogin.setBackground(CTA_PRIMARY.darker()); }
        @Override public void mouseExited(MouseEvent e) { btnLogin.setBackground(CTA_PRIMARY); }
    });
    btnLogin.addActionListener(e -> handleLogin());
    formCard.add(btnLogin);
    formCard.add(Box.createVerticalStrut(12));

    // --- Signup Link ---
    btnSignupLink = new JButton("Don't have an account? Sign Up");
    btnSignupLink.setFont(new Font("Roboto", Font.BOLD, 14));
    btnSignupLink.setForeground(CTA_SECONDARY);
    btnSignupLink.setContentAreaFilled(false);
    btnSignupLink.setBorderPainted(false);
    btnSignupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnSignupLink.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnSignupLink.addActionListener(e -> { if (parent != null) parent.slideToSignup(); });
    formCard.add(btnSignupLink);
    formCard.add(Box.createVerticalStrut(10));

    // --- Terms ---
    JLabel terms = new JLabel("<html><div style='text-align:center; width:560px;'>By logging in, you agree to the <span style='color:#1996CC;'>Terms & Privacy</span>.</div></html>");
    terms.setFont(new Font("Roboto", Font.PLAIN, 12));
    terms.setForeground(new Color(0x666666));
    terms.setAlignmentX(Component.CENTER_ALIGNMENT);
    formCard.add(terms);

    wrapper.add(formCard);
    add(wrapper, gbc);
}






    // ---------- Helpers ----------
    private void handleLogin() {
        String email = txtEmail.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
    showErrorDialog("Incomplete Fields", "Please enter both email and password.");
            return;
        }

        try {
            StudentDAO dao = new StudentDAO();
            StudentDomain student = dao.loginStudent(email, password);

            if (student == null) {
    showErrorDialog("Login Failed", "Invalid email or password.");
                return;
            }

            Session.setStudent(student.getStudentID(), student.getGroupID());
            showLoginSuccessDialog(student.getFirstName());


            if (parent != null) {
                parent.showMainDashboard();
                parent.getSidebar().setCurrentStudent(student.getStudentID(), student.getGroupID());
                parent.getSidebar().renderContent("Dashboard");
            }

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void showLoginSuccessDialog(String studentName) {
    JDialog dialog = new JDialog((Frame) null, "Login Successful", true);
    dialog.setSize(400, 220);
    dialog.setLocationRelativeTo(this); // center on parent

    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    panel.setBackground(CARD_BG);

    // Title
    JLabel title = new JLabel("✅ Login Successful");
    title.setFont(new Font("Roboto", Font.BOLD, 18));
    title.setForeground(CTA_SECONDARY);

    // Message
    JTextArea message = new JTextArea("Welcome, " + studentName + "!\nYou are now logged in.");
    message.setFont(new Font("Roboto", Font.PLAIN, 14));
    message.setWrapStyleWord(true);
    message.setLineWrap(true);
    message.setEditable(false);
    message.setOpaque(false);

    panel.add(title, BorderLayout.NORTH);
    panel.add(message, BorderLayout.CENTER);

    // Close button
    JButton closeBtn = new JButton("Continue");
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
    
    private void showErrorDialog(String title, String message) {
    JDialog dialog = new JDialog((Frame) null, title, true);
    dialog.setSize(400, 180);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    panel.setBackground(CARD_BG);

    JLabel titleLabel = new JLabel("❌ " + title);
    titleLabel.setFont(new Font("Roboto", Font.BOLD, 18));
    titleLabel.setForeground(CTA_PRIMARY);

    JTextArea msg = new JTextArea(message);
    msg.setFont(new Font("Roboto", Font.PLAIN, 14));
    msg.setWrapStyleWord(true);
    msg.setLineWrap(true);
    msg.setEditable(false);
    msg.setOpaque(false);

    panel.add(titleLabel, BorderLayout.NORTH);
    panel.add(msg, BorderLayout.CENTER);

    JButton closeBtn = new JButton("Close");
    styleButton(closeBtn, CTA_PRIMARY, CTA_SECONDARY, new Dimension(140, 38));
    closeBtn.addActionListener(e -> dialog.dispose());
    panel.add(closeBtn, BorderLayout.SOUTH);

    dialog.setContentPane(panel);
    dialog.setVisible(true);
}




    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 4));
        panel.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Roboto", Font.PLAIN, 13));
        label.setForeground(TEXT);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

  private JPanel createLabeledPasswordField(String labelText, JPasswordField field) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setOpaque(false);

    // Label
    JLabel label = new JLabel(labelText);
    label.setFont(new Font("Roboto", Font.PLAIN, 13));
    label.setForeground(TEXT);
    label.setAlignmentX(Component.LEFT_ALIGNMENT); // align left
    panel.add(label);
    panel.add(Box.createVerticalStrut(4));

    // Password field
    field.setFont(new Font("Roboto", Font.PLAIN, 15));
    field.setPreferredSize(new Dimension(680, 46));
    field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46)); // allow stretching
    field.setAlignmentX(Component.LEFT_ALIGNMENT); // align left
    panel.add(field);
    panel.add(Box.createVerticalStrut(4));

    // Show password checkbox (LEFT aligned)
    JCheckBox showPassword = new JCheckBox("Show Password");
    showPassword.setOpaque(false);
    showPassword.setFont(new Font("Roboto", Font.PLAIN, 12));
    showPassword.addActionListener(e -> {
        if (showPassword.isSelected()) {
            field.setEchoChar((char) 0); // show
        } else {
            field.setEchoChar('•'); // mask
        }
    });

    // Wrap checkbox in left-aligned panel
    JPanel checkboxWrapper = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
    checkboxWrapper.setOpaque(false);
    checkboxWrapper.add(showPassword);
    checkboxWrapper.setAlignmentX(Component.LEFT_ALIGNMENT); // align wrapper to left
    panel.add(checkboxWrapper);

    return panel;
}



    private Border createMaterialFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
                new EmptyBorder(6, 10, 6, 10)
        );
    }

    private JLabel createLinkLabel(String text, Runnable action) {
        JLabel lbl = new JLabel("<html><u>" + text + "</u></html>");
        lbl.setForeground(CTA_SECONDARY);
        lbl.setFont(new Font("Roboto", Font.PLAIN, 13));
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) { action.run(); }
            @Override public void mouseEntered(MouseEvent e) { lbl.setForeground(CTA_SECONDARY.darker()); }
            @Override public void mouseExited(MouseEvent e) { lbl.setForeground(CTA_SECONDARY); }
        });
        return lbl;
    }

    private void openExternal(String url) {
        try { Desktop.getDesktop().browse(new URI(url)); }
        catch (Exception ex) { ex.printStackTrace(); }
    }

    // ---------- Custom Shadow ----------
    private static class MatteDropShadowBorder extends AbstractBorder {
        private final Color color;
        private final int size;
        private final int cornerRadius;
        private final float opacity;

        public MatteDropShadowBorder(Color color, int size, int cornerRadius, float opacity) {
            this.color = color;
            this.size = size;
            this.cornerRadius = cornerRadius;
            this.opacity = opacity;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setComposite(AlphaComposite.SrcOver.derive(opacity));
            g2.setColor(color);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < size; i++) {
                g2.drawRoundRect(x + i, y + i, width - i * 2 - 1, height - i * 2 - 1, cornerRadius, cornerRadius);
            }
            g2.dispose();
        }
    }
}
