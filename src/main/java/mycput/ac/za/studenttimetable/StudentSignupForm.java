package mycput.ac.za.studenttimetable;

import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Material-styled, two-column Student signup form.
 * - Left: banner/logo
 * - Right: card-style form
 *
 * Keeps all validation logic and stars for required fields.
 */
public class StudentSignupForm extends JPanel {

    private JTextField txtStudentId, txtFirstName, txtLastName, txtEmail, txtPhoneNumber;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbGroupID, cmbYear;
    private JButton btnSignup, btnLogin;
    private StudentTimeTable parent;

    // Material palette (as requested)
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color CTA_PRIMARY = new Color(0xE7404A);
    private static final Color CTA_SECONDARY = new Color(0x1996CC);
    private static final Color TEXT = new Color(0x333333);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    public StudentSignupForm(StudentTimeTable parent) {
        this.parent = parent;
        initComponents();
    }

    private void initComponents() {
    setLayout(new BorderLayout());
    setBackground(PRIMARY_BG);

    // ================= LEFT PANEL =================
    JPanel leftPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(0, 0, PRIMARY_BG, 0, getHeight(), new Color(0xCFEFFF));
            g2.setPaint(gp);
            g2.fillRect(0, 0, getWidth(), getHeight());
        }
    };
    leftPanel.setPreferredSize(new Dimension(250, 0));
    leftPanel.setLayout(new BorderLayout());
leftPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createMatteBorder(0, 0, 0, 10, new Color(0, 0, 0, 5)), // right shadow
        new EmptyBorder(36, 28, 36, 28) // keep your padding
));

    // Centered content (logo + welcome + hint)
    JPanel centerContent = new JPanel();
    centerContent.setOpaque(false);
    centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
    centerContent.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Logo
   // Logo
ImageIcon originalIcon = null;
try {
    originalIcon = new ImageIcon(getClass().getResource("/icons/LogoB.png"));
} catch (Exception ex) { /* ignore */ }

if (originalIcon != null) {
    // Scale the logo cleanly to 800 x 450
    Image scaledImage = getScaledImage(originalIcon.getImage(), 200, 140);
    JLabel logoLabel = new JLabel(new ImageIcon(scaledImage));
    logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
    centerContent.add(Box.createVerticalStrut(24));
    centerContent.add(logoLabel);
}



    // Welcome text
    centerContent.add(Box.createVerticalStrut(18));
    JLabel welcome = new JLabel("<html><div style='text-align:center;'>Welcome to<br><b>CPUT Student Portal</b></div></html>");
    welcome.setFont(new Font("Roboto", Font.PLAIN, 20));
    welcome.setForeground(TEXT);
    welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
    centerContent.add(welcome);

    // Hint text (wrap properly, centered)
    centerContent.add(Box.createVerticalStrut(20));
    JLabel hint = new JLabel("<html><div style='text-align:center;'>Create your student account to view timetables, assignments and announcements. Simple, secure and fast.</div></html>");
    hint.setFont(new Font("Roboto", Font.PLAIN, 13));
    hint.setForeground(TEXT);
    hint.setAlignmentX(Component.CENTER_ALIGNMENT);
    centerContent.add(hint);

    // Wrap in BorderLayout to center vertically
    JPanel glueWrapper = new JPanel(new BorderLayout());
    glueWrapper.setOpaque(false);
    glueWrapper.add(centerContent, BorderLayout.CENTER);

    // Bottom links (Help + Privacy)
    JPanel bottomLinks = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
    bottomLinks.setOpaque(false);
    JLabel help = createLinkLabel("Help", () -> openExternal("https://mycput.cput.ac.za/"));
    JLabel privacy = createLinkLabel("Privacy", () -> openExternal("https://cput.ac.za/privacy/"));
    help.setFont(new Font("Roboto", Font.PLAIN, 12));
    privacy.setFont(new Font("Roboto", Font.PLAIN, 12));
    bottomLinks.add(help);
    bottomLinks.add(privacy);

    leftPanel.add(glueWrapper, BorderLayout.CENTER);
    leftPanel.add(bottomLinks, BorderLayout.SOUTH);

    add(leftPanel, BorderLayout.WEST);

    // ================= RIGHT: FORM CARD =================
    JPanel wrapper = new JPanel(new GridBagLayout());
    wrapper.setOpaque(false);
    wrapper.setBorder(new EmptyBorder(28, 40, 28, 28)); // added extra left margin

    JPanel formCard = new JPanel();
    formCard.setBackground(CARD_BG);
    formCard.setBorder(new CompoundBorder(
            new EmptyBorder(28, 28, 28, 28),
            new MatteDropShadowBorder(SHADOW_COLOR, 12, 20, 0.12f)
    ));
    formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
    formCard.setPreferredSize(new Dimension(720, 740));
    formCard.setMaximumSize(new Dimension(760, 800));
    formCard.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Title & subtitle
    JLabel title = new JLabel("Create an account");
    title.setFont(new Font("Roboto", Font.BOLD, 28));
    title.setForeground(TEXT);
    title.setAlignmentX(Component.CENTER_ALIGNMENT);
    formCard.add(title);

    JLabel subtitle = new JLabel("Register with your student details to access the portal.");
    subtitle.setFont(new Font("Roboto", Font.PLAIN, 14));
    subtitle.setForeground(new Color(0x666666));
    subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
    formCard.add(Box.createVerticalStrut(8));
    formCard.add(subtitle);
    formCard.add(Box.createVerticalStrut(20));

    // Fields container
    JPanel fields = new JPanel();
    fields.setOpaque(false);
    fields.setLayout(new GridBagLayout());
    GridBagConstraints fgbc = new GridBagConstraints();
    fgbc.fill = GridBagConstraints.HORIZONTAL;
    fgbc.insets = new Insets(10, 8, 10, 8);
    fgbc.weightx = 1.0;

    // Year & Group
    cmbYear = new JComboBox<>(new String[]{
            "Select year of study", "First year", "Second year", "Third year"
    });
    cmbYear.setFont(new Font("Roboto", Font.PLAIN, 15));
    cmbYear.setBorder(createMaterialFieldBorder());
    cmbYear.setPreferredSize(new Dimension(320, 46));

    cmbGroupID = new JComboBox<>();
    cmbGroupID.setFont(new Font("Roboto", Font.PLAIN, 15));
    cmbGroupID.setBorder(createMaterialFieldBorder());
    cmbGroupID.setPreferredSize(new Dimension(320, 46));

    Map<String, String[]> yearToGroups = new HashMap<>();
    yearToGroups.put("First year", new String[]{"Select your group","1A","1B","1C","1D","1E","1F","1G","1H","1I","1J","1K","1L","1M","1N","1O","1P"});
    yearToGroups.put("Second year", new String[]{"Select your group","2A","2B","2C","2D","2E","2F","2G","2H","2I","2J","2K"});
    yearToGroups.put("Third year", new String[]{"Select your group","3A","3B","3C","3D","3E","3F","3G","3H","3I","3J","3K"});

    cmbYear.addActionListener(e -> {
        String selectedYear = (String) cmbYear.getSelectedItem();
        cmbGroupID.setModel(new DefaultComboBoxModel<>(
                yearToGroups.getOrDefault(selectedYear, new String[]{})
        ));
    });
    cmbYear.setSelectedIndex(0);

    JPanel yearPanel = createLabeledField("Year", cmbYear);
    JPanel groupPanel = createLabeledField("Group", cmbGroupID);

    fgbc.gridx = 0; fgbc.gridy = 0;
    fields.add(yearPanel, fgbc);
    fgbc.gridx = 1;
    fields.add(groupPanel, fgbc);

    // Student ID full width
    txtStudentId = new JTextField();
    txtStudentId.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtStudentId.setBorder(createMaterialFieldBorder());
    txtStudentId.setPreferredSize(new Dimension(680, 46));
    JPanel studentIdPanel = createLabeledField("Student ID", txtStudentId);
    fgbc.gridx = 0; fgbc.gridy = 1; fgbc.gridwidth = 2;
    fields.add(studentIdPanel, fgbc);

    // First + Last
    txtFirstName = new JTextField();
    txtFirstName.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtFirstName.setBorder(createMaterialFieldBorder());
    txtFirstName.setPreferredSize(new Dimension(320, 46));
    txtLastName = new JTextField();
    txtLastName.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtLastName.setBorder(createMaterialFieldBorder());
    txtLastName.setPreferredSize(new Dimension(320, 46));

    JPanel firstPanel = createLabeledField("First Name", txtFirstName);
    JPanel lastPanel = createLabeledField("Last Name", txtLastName);
    fgbc.gridx = 0; fgbc.gridy = 2; fgbc.gridwidth = 1;
    fields.add(firstPanel, fgbc);
    fgbc.gridx = 1;
    fields.add(lastPanel, fgbc);

    // Email + Phone
    txtEmail = new JTextField();
    txtEmail.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtEmail.setBorder(createMaterialFieldBorder());
    txtEmail.setPreferredSize(new Dimension(320, 46));
    txtPhoneNumber = new JTextField();
    txtPhoneNumber.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtPhoneNumber.setBorder(createMaterialFieldBorder());
    txtPhoneNumber.setPreferredSize(new Dimension(320, 46));

    JPanel emailPanel = createLabeledField("Email", txtEmail);
    JPanel phonePanel = createLabeledField("Phone Number", txtPhoneNumber);
    fgbc.gridx = 0; fgbc.gridy = 3;
    fields.add(emailPanel, fgbc);
    fgbc.gridx = 1;
    fields.add(phonePanel, fgbc);

    // Password + Confirm
    txtPassword = new JPasswordField();
    txtPassword.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtPassword.setBorder(createMaterialFieldBorder());
    txtPassword.setPreferredSize(new Dimension(320, 46));
    txtConfirmPassword = new JPasswordField();
    txtConfirmPassword.setFont(new Font("Roboto", Font.PLAIN, 15));
    txtConfirmPassword.setBorder(createMaterialFieldBorder());
    txtConfirmPassword.setPreferredSize(new Dimension(320, 46));

    JPanel passPanel = createLabeledPasswordField("Password", txtPassword);
    JPanel confirmPanel = createLabeledPasswordField("Confirm Password", txtConfirmPassword);
    fgbc.gridx = 0; fgbc.gridy = 4;
    fields.add(passPanel, fgbc);
    fgbc.gridx = 1;
    fields.add(confirmPanel, fgbc);

    formCard.add(fields);
    formCard.add(Box.createVerticalStrut(18));

    // Signup button
    btnSignup = new JButton("Create account");
    btnSignup.setFont(new Font("Roboto", Font.BOLD, 16));
    btnSignup.setBackground(CTA_PRIMARY);
    btnSignup.setForeground(Color.WHITE);
    btnSignup.setFocusPainted(false);
    btnSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnSignup.setBorder(new EmptyBorder(12, 28, 12, 28));
    btnSignup.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnSignup.setOpaque(true);
    btnSignup.setPreferredSize(new Dimension(220, 52));
    btnSignup.addMouseListener(new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) { btnSignup.setBackground(CTA_PRIMARY.darker()); }
        @Override
        public void mouseExited(MouseEvent e) { btnSignup.setBackground(CTA_PRIMARY); }
    });
    btnSignup.addActionListener(e -> handleSignup());
    formCard.add(btnSignup);
    formCard.add(Box.createVerticalStrut(12));

    // Login link
    btnLogin = new JButton("Already have an account? Log in");
    btnLogin.setFont(new Font("Roboto", Font.BOLD, 14));
    btnLogin.setForeground(CTA_SECONDARY);
    btnLogin.setContentAreaFilled(false);
    btnLogin.setBorderPainted(false);
    btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    btnLogin.setAlignmentX(Component.CENTER_ALIGNMENT);
    btnLogin.addActionListener(e -> { if (parent != null) parent.slideToLogin(); });
    formCard.add(btnLogin);
    formCard.add(Box.createVerticalStrut(10));

    // Terms
    JLabel terms = new JLabel("<html><div style='text-align:center; width:560px;'>By creating an account you agree to the <span style='color:#1996CC;'>Terms &amp; Privacy</span>.</div></html>");
    terms.setFont(new Font("Roboto", Font.PLAIN, 12));
    terms.setForeground(new Color(0x666666));
    terms.setAlignmentX(Component.CENTER_ALIGNMENT);
    formCard.add(terms);

    // Validation
    wireValidation();

    // Add card to wrapper
    wrapper.add(formCard);
    add(wrapper, BorderLayout.CENTER);
}

    // Utility method to scale an image with high quality
private Image getScaledImage(Image src, int w, int h) {
    BufferedImage resized = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2 = resized.createGraphics();

    // High-quality rendering hints
    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
    g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    g2.drawImage(src, 0, 0, w, h, null);
    g2.dispose();

    return resized;
}


    // ---------- Helpers & UI factories ----------

    private void wireValidation() {
        // replicate same validation logic with stars as originally requested
        // use the createLabeledField / createPassword variants, they set up listeners internally
        // ensure student ID, names, email, phone, password rules are enforced on submit too
    }

    // Create a label + component with the required red star and live validation wiring
    private JPanel createLabeledField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 6));

        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        labelRow.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Roboto", Font.PLAIN, 14));
        label.setForeground(TEXT);
        JLabel star = new JLabel("*");
        star.setFont(new Font("Roboto", Font.BOLD, 14));
        star.setForeground(Color.RED);
        labelRow.add(label);
        labelRow.add(star);

        panel.add(labelRow, BorderLayout.NORTH);

        // container for the field so borders align consistently
        JPanel fieldWrapper = new JPanel(new BorderLayout());
        fieldWrapper.setOpaque(false);
        field.setFont(new Font("Roboto", Font.PLAIN, 15));
        fieldWrapper.add(field, BorderLayout.CENTER);
        panel.add(fieldWrapper, BorderLayout.CENTER);

        // Wire specific live validation rules (mimic original logic)
        if (field instanceof JTextField && "Student ID".equals(labelText)) {
            JTextField tf = (JTextField) field;
            tf.getDocument().addDocumentListener(new SimpleDocumentListener() {
                @Override public void update() {
                    String text = tf.getText().trim();
                    boolean valid = text.matches("\\d{9}");
                    star.setVisible(!valid);
                }
            });
        } else if (field instanceof JTextField && ("First Name".equals(labelText) || "Last Name".equals(labelText))) {
            JTextField tf = (JTextField) field;
            tf.getDocument().addDocumentListener(new SimpleDocumentListener() {
                @Override public void update() {
                    String text = tf.getText().trim();
                    boolean valid = text.matches("[a-zA-Z]+");
                    star.setVisible(!valid);
                }
            });
        } else if (field instanceof JTextField && "Email".equals(labelText)) {
            JTextField tf = (JTextField) field;
            tf.getDocument().addDocumentListener(new SimpleDocumentListener() {
                @Override public void update() {
                    String text = tf.getText().trim();
                    boolean valid = text.matches("^[a-zA-Z0-9._%+-]+@(mycput\\.ac\\.za|cput\\.ac\\.za|gmail\\.com|outlook\\.com|hotmail\\.com|yahoo\\.com)$");
                    star.setVisible(!valid);
                }
            });
        } else if (field instanceof JTextField && "Phone Number".equals(labelText)) {
            JTextField tf = (JTextField) field;
            tf.getDocument().addDocumentListener(new SimpleDocumentListener() {
                @Override public void update() {
                    String text = tf.getText().trim();
                    boolean valid = text.matches("\\d{10,12}");
                    star.setVisible(!valid);
                }
            });
        } else if (field instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>) field;
            cb.addActionListener(e -> {
                boolean valid = cb.getSelectedItem() != null && !cb.getSelectedItem().toString().isEmpty();
                star.setVisible(!valid);
            });
            // initial visibility
            boolean valid = cb.getSelectedItem() != null && !cb.getSelectedItem().toString().isEmpty();
            star.setVisible(!valid);
        }

        return panel;
    }

    private JPanel createLabeledPasswordField(String labelText, JPasswordField field) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BorderLayout(0, 6));

        JPanel labelRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        labelRow.setOpaque(false);
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Roboto", Font.PLAIN, 14));
        label.setForeground(TEXT);
        JLabel star = new JLabel("*");
        star.setFont(new Font("Roboto", Font.BOLD, 14));
        star.setForeground(Color.RED);
        labelRow.add(label);
        labelRow.add(star);

        panel.add(labelRow, BorderLayout.NORTH);

        field.setFont(new Font("Roboto", Font.PLAIN, 15));
        panel.add(field, BorderLayout.CENTER);

        // Password rules as original
        field.getDocument().addDocumentListener(new SimpleDocumentListener() {
            @Override
            public void update() {
                String password = new String(field.getPassword());
                boolean hasUpper = password.matches(".*[A-Z].*");
                boolean hasLower = password.matches(".*[a-z].*");
                boolean hasDigit = password.matches(".*\\d.*");
                boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");
                star.setVisible(!(hasUpper && hasLower && hasDigit && hasSpecial));
                // if confirm exists, sync (handled elsewhere)
            }
        });

        // Confirm field syncing (if this is the confirm field)
        if (labelText.equals("Confirm Password")) {
            field.getDocument().addDocumentListener(new SimpleDocumentListener() {
                @Override
                public void update() {
                    String pwd = txtPassword == null ? "" : new String(txtPassword.getPassword());
                    String confirm = new String(field.getPassword());
                    star.setVisible(!pwd.equals(confirm));
                }
            });
        }

        return panel;
    }

    private Border createMaterialFieldBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD9EAF3), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        );
    }

    private JLabel createLinkLabel(String text, Runnable action) {
        JLabel lbl = new JLabel("<html><u>" + text + "</u></html>");
        lbl.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        lbl.setForeground(CTA_SECONDARY);
        lbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { action.run(); }
            @Override
            public void mouseEntered(MouseEvent e) { lbl.setForeground(CTA_SECONDARY.darker()); }
            @Override
            public void mouseExited(MouseEvent e) { lbl.setForeground(CTA_SECONDARY); }
        });
        lbl.setFont(new Font("Roboto", Font.PLAIN, 12));
        return lbl;
    }

    private void openExternal(String url) {
        try {
            if (Desktop.isDesktopSupported()) Desktop.getDesktop().browse(new URI(url));
        } catch (Exception ex) {
            // ignore
        }
    }

    // ---------- Signup handling (preserves original validation + DB save) ----------

    private void handleSignup() {
    // --- Gather input values ---
    String studentId = txtStudentId.getText().trim();
    String groupId = (String) cmbGroupID.getSelectedItem();
    String year = (String) cmbYear.getSelectedItem();
    if (groupId == null) groupId = "";
    if (year == null) year = "";
    String firstName = txtFirstName.getText().trim();
    String lastName = txtLastName.getText().trim();
    String email = txtEmail.getText().trim();
    String phone = txtPhoneNumber.getText().trim();
    String password = new String(txtPassword.getPassword());
    String confirmPassword = new String(txtConfirmPassword.getPassword());

    // --- Validation ---

    // Check required fields including combo boxes
    if (studentId.isEmpty() || year.isEmpty() || "Select year of study".equals(year) ||
        groupId.isEmpty() || groupId.startsWith("Select") ||
        firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || phone.isEmpty() ||
        password.isEmpty() || confirmPassword.isEmpty()) {
        showToast("All fields are required!", true);
        return;
    }

    // Student ID: exactly 9 digits
    if (!studentId.matches("\\d{9}")) {
        showToast("Student ID must be exactly 9 digits!", true);
        return;
    }

    // Names: letters only
    if (!firstName.matches("[a-zA-Z]+")) {
        showToast("First name can only contain letters!", true);
        return;
    }
    if (!lastName.matches("[a-zA-Z]+")) {
        showToast("Last name can only contain letters!", true);
        return;
    }

    // Email validation
    if (!email.matches("^[a-zA-Z0-9._%+-]+@(mycput\\.ac\\.za|cput\\.ac\\.za|gmail\\.com|outlook\\.com|hotmail\\.com|yahoo\\.com)$")) {
        showToast("Invalid email address! Must be a valid academic or major email.", true);
        return;
    }

    // Phone validation
    if (!phone.matches("\\d{10,12}")) {
        showToast("Phone number must contain only digits and be 10 to 12 digits long!", true);
        return;
    }

    // Password validation
    boolean hasUpper = password.matches(".*[A-Z].*");
    boolean hasLower = password.matches(".*[a-z].*");
    boolean hasDigit = password.matches(".*\\d.*");
    boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

    StringBuilder pwdError = new StringBuilder();
    if (!hasUpper) pwdError.append("Password must contain at least 1 capital letter.\n");
    if (!hasLower) pwdError.append("Password must contain at least 1 lowercase letter.\n");
    if (!hasDigit) pwdError.append("Password must contain at least 1 number.\n");
    if (!hasSpecial) pwdError.append("Password must contain at least 1 special character.\n");

    if (pwdError.length() > 0) {
        showToast(pwdError.toString(), true);
        return;
    }

    if (!password.equals(confirmPassword)) {
        showToast("Passwords do not match!", true);
        return;
    }

    // --- Create StudentDomain object ---
    StudentDomain student = new StudentDomain(
            studentId,
            "",
            groupId,
            firstName,
            lastName,
            phone,
            email
    );

    // --- Save to DB ---
    try {
        StudentDAO dao = new StudentDAO();
        boolean success = dao.saveStudent(student, password);

        if (success) {
            showToast("Signup successful! Welcome, " + firstName, false);
            clearForm();
            if (parent != null) parent.slideToLogin();
        } else {
            showToast("Signup failed. Try again.", true);
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
        showToast("Database error: " + ex.getMessage(), true);
    }
}

private void clearForm() {
    txtStudentId.setText("");
    cmbYear.setSelectedIndex(0);
    cmbGroupID.setSelectedIndex(0);
    txtFirstName.setText("");
    txtLastName.setText("");
    txtEmail.setText("");
    txtPhoneNumber.setText("");
    txtPassword.setText("");
    txtConfirmPassword.setText("");
}


    // ---------- Small utility classes ----------

    // Lightweight DocumentListener
    private interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update();
        @Override default void insertUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override default void removeUpdate(javax.swing.event.DocumentEvent e) { update(); }
        @Override default void changedUpdate(javax.swing.event.DocumentEvent e) { update(); }
    }
    
    private void showToast(String message, boolean isError) {
    // Use a modal dialog
    JDialog dialog = new JDialog((Frame) null, isError ? "Error" : "Success", true);
    dialog.setSize(400, 180);
    dialog.setLocationRelativeTo(this); // center on parent
    dialog.setResizable(false);

    JPanel panel = new JPanel(new BorderLayout(15, 15));
    panel.setBorder(new EmptyBorder(15, 15, 15, 15));
    panel.setBackground(CARD_BG);

    // Title
    JLabel title = new JLabel(isError ? "❌ Error" : "✅ Success");
    title.setFont(new Font("Roboto", Font.BOLD, 18));
    title.setForeground(isError ? new Color(0xE74C3C) : new Color(0x2ECC71));
    panel.add(title, BorderLayout.NORTH);

    // Message
    JTextArea messageArea = new JTextArea(message);
    messageArea.setFont(new Font("Roboto", Font.PLAIN, 14));
    messageArea.setWrapStyleWord(true);
    messageArea.setLineWrap(true);
    messageArea.setEditable(false);
    messageArea.setOpaque(false);
    panel.add(messageArea, BorderLayout.CENTER);

    // Continue / Close button
    JButton closeBtn = new JButton("Continue");
    styleButton(closeBtn, isError ? new Color(0xE74C3C) : new Color(0x2ECC71),
                       CTA_SECONDARY, new Dimension(140, 38));
    closeBtn.addActionListener(e -> dialog.dispose());
    JPanel btnWrapper = new JPanel();
    btnWrapper.setOpaque(false);
    btnWrapper.add(closeBtn);
    panel.add(btnWrapper, BorderLayout.SOUTH);

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


    /**
     * A matte-ish drop shadow border (subtle elevation).
     * Keeps the UI material-like without relying on external libraries.
     */
    private static class MatteDropShadowBorder extends AbstractBorder {
        private final Color shadow;
        private final int thickness;
        private final int radius;
        private final float alpha;

        public MatteDropShadowBorder(Color shadow, int thickness, int radius, float alpha) {
            this.shadow = shadow;
            this.thickness = thickness;
            this.radius = radius;
            this.alpha = alpha;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            for (int i = 0; i < thickness; i++) {
                float a = alpha * (1.0f - (float) i / (float) (thickness + 1));
                g2.setColor(new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), Math.round(255 * a)));
                g2.drawRoundRect(x + i, y + i, w - i * 2 - 1, h - i * 2 - 1, radius, radius);
            }
            g2.dispose();
        }
    }
}
