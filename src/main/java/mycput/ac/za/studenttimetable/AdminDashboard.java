package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import mycput.ac.za.studenttimetable.dao.CrudHelper;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    // Color palette
    private static final Color PRIMARY_BG = Color.decode("#D6EEFF");
    private static final Color SECONDARY_BG = Color.WHITE;
    private static final Color PRIMARY_ACTION = Color.decode("#E7404A");
    private static final Color SECONDARY_ACTION = Color.decode("#1996CC");
    private static final Font FONT_BODY = new Font("Roboto", Font.PLAIN, 14);
    private static final Font FONT_BOLD = new Font("Roboto", Font.BOLD, 16);

    public AdminDashboard() {
        try {
            UIManager.setLookAndFeel(new NimbusLookAndFeel());
        } catch (UnsupportedLookAndFeelException ignored) {}

        setTitle("Admin Dashboard");
        setSize(1350, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // === HEADER ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(SECONDARY_BG);
        headerPanel.setBorder(new EmptyBorder(15, 25, 15, 25));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setOpaque(true);
       

        JLabel title = new JLabel("🧭 Admin Dashboard");
        title.setFont(new Font("Roboto", Font.BOLD, 22));
        title.setForeground(Color.DARK_GRAY);

        JLabel subtitle = new JLabel("Manage users, courses, lecturers, and more");
        subtitle.setFont(new Font("Roboto", Font.PLAIN, 14));
        subtitle.setForeground(new Color(90, 90, 90));

        JPanel titleContainer = new JPanel(new GridLayout(2, 1));
        titleContainer.setOpaque(false);
        titleContainer.add(title);
        titleContainer.add(subtitle);

        JPanel timePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        timePanel.setBackground(SECONDARY_BG);
        JLabel clock = new JLabel();
        clock.setFont(FONT_BODY);
        clock.setForeground(Color.GRAY);

        new Timer(1000, e -> {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("EEEE, dd MMM yyyy | HH:mm:ss");
            clock.setText(LocalDateTime.now().format(fmt));
        }).start();

        timePanel.add(clock);
        headerPanel.add(titleContainer, BorderLayout.WEST);
        headerPanel.add(timePanel, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // === MAIN CONTENT (Tabbed Pane) ===
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(FONT_BODY);
        tabbedPane.setBorder(new EmptyBorder(15, 15, 15, 15));
        tabbedPane.setBackground(PRIMARY_BG);
        tabbedPane.setOpaque(true);

        // Add tabs
        addTab("Users / Roles", "UserAccount", new String[]{"UserID", "Email", "PasswordHash", "Role", "CreatedAt"}, new String[]{"CreatedAt"}, "UserID");
        addTab("Courses", "Course", new String[]{"CourseID", "CourseName"}, new String[]{}, "CourseID");
        addTab("Subjects", "Subject", new String[]{"SubjectCode", "SubjectName", "YearLevel"}, new String[]{}, "SubjectCode");
        addTab("Students", "Student", new String[]{"StudentID", "UserID", "GroupID", "FirstName", "LastName", "PhoneNumber", "Email"}, new String[]{}, "StudentID");
        addTab("Admins", "Admin", new String[]{"AdminID", "UserID", "FirstName", "LastName", "PhoneNumber"}, new String[]{}, "AdminID");
        addTab("Lecturers", "Lecturer", new String[]{"LecturerID", "FirstName", "LastName", "Email", "PhoneNumber"}, new String[]{}, "LecturerID");
        addTab("Lecture Rooms", "LectureRoom", new String[]{"RoomID", "RoomType"}, new String[]{}, "RoomID");
        addTab("Timetable", "TimeTable", new String[]{"TimeTableID", "SubjectCode", "GroupID", "LecturerID", "RoomID", "ClassType", "ClassDate", "StartTime", "EndTime"}, new String[]{"TimeTableID"}, "TimeTableID");
        tabbedPane.addTab("Notifications", new AdminNotificationsPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // === FOOTER ===
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footer.setBackground(PRIMARY_BG);
        footer.setBorder(new EmptyBorder(10, 0, 10, 0));
        JLabel lblFooter = new JLabel("© 2025 Student Timetable Management System | Designed with ♥ by Yaseen Kannemeyer");
        lblFooter.setFont(new Font("Roboto", Font.PLAIN, 12));
        lblFooter.setForeground(new Color(80, 80, 80));
        footer.add(lblFooter);
        add(footer, BorderLayout.SOUTH);
    }

    private void addTab(String title, String tableName, String[] columns, String[] skipOnAdd, String keyColumn) {
        tabbedPane.addTab(title, createDbCrudPanel(tableName, columns, skipOnAdd, keyColumn));
    }

 private JPanel createDbCrudPanel(String tableName, String[] columns, String[] skipOnAdd, String keyColumn) {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBackground(PRIMARY_BG);
    panel.setBorder(new EmptyBorder(15, 15, 15, 15));

    // --- Top Toolbar ---
    JPanel topPanel = new JPanel(new BorderLayout(10, 10));
    topPanel.setBackground(SECONDARY_BG);
    JButton btnRefresh = createStyledButton("⟳ Refresh", SECONDARY_ACTION, Color.WHITE);

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    searchPanel.setBackground(SECONDARY_BG);
    JLabel lblSearch = new JLabel("🔍 Search:");
    lblSearch.setFont(FONT_BODY);
    JTextField txtSearch = new JTextField(20);
    txtSearch.setFont(FONT_BODY);
    searchPanel.add(lblSearch);
    searchPanel.add(txtSearch);

    topPanel.add(btnRefresh, BorderLayout.WEST);
    topPanel.add(searchPanel, BorderLayout.EAST);
    panel.add(topPanel, BorderLayout.NORTH);

    // --- Table ---
    DefaultTableModel model = new DefaultTableModel(columns, 0);
    JTable table = new JTable(model);
    table.setFont(FONT_BODY);
    table.setRowHeight(30);
    table.setSelectionBackground(SECONDARY_ACTION);
    table.setSelectionForeground(Color.WHITE);
    JScrollPane scrollPane = new JScrollPane(table);
    panel.add(scrollPane, BorderLayout.CENTER);

    // --- Form Section ---
    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createTitledBorder("Form"));
    formPanel.setBackground(SECONDARY_BG);
    formPanel.setVisible(false);

    JLabel[] fieldLabels = new JLabel[columns.length];
    JComponent[] fieldInputs = new JComponent[columns.length];

    for (int i = 0; i < columns.length; i++) {
    fieldLabels[i] = new JLabel(columns[i] + ":");
    fieldLabels[i].setFont(FONT_BODY);

    if ("Role".equalsIgnoreCase(columns[i])) {
        fieldInputs[i] = new JComboBox<>(new String[]{"STUDENT", "ADMIN"});
    } else if ("GroupID".equalsIgnoreCase(columns[i])) {
        try {
            List<String> groupList = CrudHelper.getAllGroupIds();
            Set<String> groups = new HashSet<>(groupList);
            fieldInputs[i] = new JComboBox<>(groups.toArray(new String[0]));
        } catch (SQLException ex) {
            ex.printStackTrace();
            fieldInputs[i] = new JTextField();
        }
    } else if ("StudentID".equalsIgnoreCase(columns[i])) {
        fieldInputs[i] = new JTextField(); // <-- Admin enters manually
    } else if ("UserID".equalsIgnoreCase(columns[i])) {
        fieldInputs[i] = new JTextField();
        fieldInputs[i].setEnabled(false); // <-- Auto-generated
        try {
            ((JTextField) fieldInputs[i]).setText(CrudHelper.getNextUserId());
        } catch (SQLException ex) {
            ((JTextField) fieldInputs[i]).setText("");
        }
    } else {
        fieldInputs[i] = new JTextField();
    }

    formPanel.add(fieldLabels[i]);
    formPanel.add(fieldInputs[i]);
}



    // --- Action Section ---
    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    actionPanel.setBackground(SECONDARY_BG);
    JLabel lblAction = new JLabel("Action:");
    lblAction.setFont(FONT_BODY);
    JComboBox<String> cmbAction = new JComboBox<>(new String[]{"Select Action", "Add", "Edit", "Delete"});
    cmbAction.setFont(FONT_BODY);
    JButton btnApply = createStyledButton("Apply", PRIMARY_ACTION, Color.WHITE);
    actionPanel.add(lblAction);
    actionPanel.add(cmbAction);
    actionPanel.add(btnApply);

    JPanel bottomPanel = new JPanel(new BorderLayout());
    bottomPanel.setBackground(SECONDARY_BG);
    bottomPanel.add(formPanel, BorderLayout.CENTER);
    bottomPanel.add(actionPanel, BorderLayout.SOUTH);
    panel.add(bottomPanel, BorderLayout.SOUTH);

    Set<String> skipSet = new HashSet<>(Arrays.asList(skipOnAdd));

    // === SHOW/HIDE FORM LOGIC ===
    cmbAction.addActionListener(e -> {
        String action = (String) cmbAction.getSelectedItem();
        int selectedRow = table.getSelectedRow();

        if ("Add".equals(action) || "Edit".equals(action)) {
            for (int i = 0; i < columns.length; i++) {
                boolean visible = !skipSet.contains(columns[i]);
                fieldLabels[i].setVisible(visible);
                fieldInputs[i].setVisible(visible);

                if (!visible) continue;

                if ("Add".equals(action)) {
    if (fieldInputs[i] instanceof JTextField) ((JTextField) fieldInputs[i]).setText("");
    else if (fieldInputs[i] instanceof JComboBox) ((JComboBox<?>) fieldInputs[i]).setSelectedIndex(0);

    // Auto-fill IDs
    try {
        if ("UserAccount".equalsIgnoreCase(tableName) && "UserID".equalsIgnoreCase(columns[i]))
            ((JTextField) fieldInputs[i]).setText(CrudHelper.getNextUserId());
        else if ("Student".equalsIgnoreCase(tableName) && "UserID".equalsIgnoreCase(columns[i]))
            ((JTextField) fieldInputs[i]).setText(CrudHelper.getNextUserId());
        else if ("Admin".equalsIgnoreCase(tableName) && "UserID".equalsIgnoreCase(columns[i]))
            ((JTextField) fieldInputs[i]).setText(CrudHelper.getNextUserId());
    } catch (SQLException ex) {
        System.err.println("Could not auto-generate ID: " + ex.getMessage());
    }
}
 else if ("Edit".equals(action) && selectedRow >= 0) {
                    Object val = table.getValueAt(selectedRow, i);
                    if (fieldInputs[i] instanceof JTextField) ((JTextField) fieldInputs[i]).setText(val != null ? val.toString() : "");
                    else if (fieldInputs[i] instanceof JComboBox) ((JComboBox<Object>) fieldInputs[i]).setSelectedItem(val);
                }
            }
            formPanel.setVisible(true);
        } else formPanel.setVisible(false);

        panel.revalidate();
        panel.repaint();
    });

    // === APPLY CRUD LOGIC ===
    btnApply.addActionListener(e -> {
        try {
            String action = (String) cmbAction.getSelectedItem();
            int row = table.getSelectedRow();

            switch (action) {
                case "Add" -> handleAdd(tableName, fieldInputs,columns, panel);
                case "Edit" -> {
                    if (row < 0) { JOptionPane.showMessageDialog(panel, "Select a record to edit.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
                    handleEdit(tableName, fieldInputs, table, skipSet, keyColumn, row, panel);
                }
                case "Delete" -> {
                    if (row < 0) { JOptionPane.showMessageDialog(panel, "Select a record to delete.", "Warning", JOptionPane.WARNING_MESSAGE); return; }
                    CrudHelper.deleteRecord(tableName, keyColumn, table.getValueAt(row, 0).toString());
                    JOptionPane.showMessageDialog(panel, "Record deleted successfully.");
                }
            }

            refreshTableSafe(panel, model, tableName, columns);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    // === SEARCH ===
    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    table.setRowSorter(sorter);
    txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
        private void search() {
            String text = txtSearch.getText().trim();
            sorter.setRowFilter(text.isEmpty() ? null : RowFilter.regexFilter("(?i)" + text));
        }
    });

    // === REFRESH ===
    btnRefresh.addActionListener(e -> refreshTableSafe(panel, model, tableName, columns));
    refreshTableSafe(panel, model, tableName, columns);

    return panel;
}



    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Roboto", Font.BOLD, 13));
        btn.setForeground(fg);
        btn.setBackground(bg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(8, 16, 8, 16)
        ));
        btn.setOpaque(true);
        return btn;
    }

    private void refreshTableSafe(JPanel panel, DefaultTableModel model, String tableName, String[] columns) {
        try {
            model.setRowCount(0);
            for (String[] row : CrudHelper.loadTable(tableName, columns))
                model.addRow(row);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- ADD HANDLER ---
// --- HELPER METHOD ---
private String getFieldValue(JComponent[] inputs, String[] columns, String columnName) {
    for (int i = 0; i < columns.length; i++) {
        if (columns[i].equalsIgnoreCase(columnName)) {
            if (inputs[i] instanceof JTextField) return ((JTextField) inputs[i]).getText();
            else if (inputs[i] instanceof JComboBox) return ((JComboBox<?>) inputs[i]).getSelectedItem().toString();
        }
    }
    return "";
}

// --- SAFE ADD HANDLER ---
private void handleAdd(String tableName, JComponent[] fieldInputs, String[] columns, JPanel panel) throws SQLException {
    if ("Student".equalsIgnoreCase(tableName)) {
    String studentId = ((JTextField) fieldInputs[0]).getText(); // Manual input
    String userId = ((JTextField) fieldInputs[1]).getText();    // Auto-generated
    String groupId = ((JComboBox<?>) fieldInputs[2]).getSelectedItem().toString();
    String firstName = ((JTextField) fieldInputs[3]).getText();
    String lastName = ((JTextField) fieldInputs[4]).getText();
    String phone = ((JTextField) fieldInputs[5]).getText();
    String email = ((JTextField) fieldInputs[6]).getText();
    String password = JOptionPane.showInputDialog(panel, "Enter password for new student:");

    // Insert into UserAccount
    CrudHelper.insertRecord("UserAccount",
            new String[]{"UserID", "Email", "PasswordHash", "Role"},
            new String[]{userId, email, password, "STUDENT"});

    // Insert into Student
    CrudHelper.insertRecord("Student",
            new String[]{ "StudentID", "UserID", "GroupID", "FirstName", "LastName", "PhoneNumber", "Email"},
            new String[]{studentId, userId, groupId, firstName, lastName, phone, email});

    JOptionPane.showMessageDialog(panel, "Student added successfully!\nStudentID: " + studentId + "\nUserID: " + userId);
}

 else if ("Admin".equalsIgnoreCase(tableName)) {
        String userId = CrudHelper.getNextUserId();
        String adminId = CrudHelper.getNextAdminId();
        String firstName = getFieldValue(fieldInputs, columns, "FirstName");
        String lastName = getFieldValue(fieldInputs, columns, "LastName");
        String phone = getFieldValue(fieldInputs, columns, "PhoneNumber");
        String password = JOptionPane.showInputDialog(panel, "Enter password for new admin:");

        // Insert into UserAccount
        CrudHelper.insertRecord("UserAccount",
                new String[]{"UserID", "Email", "PasswordHash", "Role"},
                new String[]{userId, "", password, "ADMIN"}); // Email optional for Admin

        // Insert into Admin
        CrudHelper.insertRecord("Admin",
                new String[]{"AdminID", "UserID", "FirstName", "LastName", "PhoneNumber"},
                new String[]{adminId, userId, firstName, lastName, phone});

        JOptionPane.showMessageDialog(panel, "Admin added successfully! UserID: " + userId + ", AdminID: " + adminId);

    } else if ("UserAccount".equalsIgnoreCase(tableName)) {
        String userId = CrudHelper.getNextUserId();
        String email = getFieldValue(fieldInputs, columns, "Email");
        String password = getFieldValue(fieldInputs, columns, "PasswordHash");
        String role = getFieldValue(fieldInputs, columns, "Role");

        // Insert into UserAccount
        CrudHelper.insertRecord("UserAccount",
                new String[]{"UserID", "Email", "PasswordHash", "Role"},
                new String[]{userId, email, password, role});

        if ("STUDENT".equalsIgnoreCase(role)) {
            String groupId = getFieldValue(fieldInputs, columns, "GroupID");
            String firstName = getFieldValue(fieldInputs, columns, "FirstName");
            String lastName = getFieldValue(fieldInputs, columns, "LastName");
            String phone = getFieldValue(fieldInputs, columns, "PhoneNumber");

            CrudHelper.insertRecord("Student",
                    new String[]{"UserID", "GroupID", "FirstName", "LastName", "PhoneNumber", "Email"},
                    new String[]{userId, groupId, firstName, lastName, phone, email});

        } else if ("ADMIN".equalsIgnoreCase(role)) {
            String adminId = CrudHelper.getNextAdminId();
            String firstName = getFieldValue(fieldInputs, columns, "FirstName");
            String lastName = getFieldValue(fieldInputs, columns, "LastName");
            String phone = getFieldValue(fieldInputs, columns, "PhoneNumber");

            CrudHelper.insertRecord("Admin",
                    new String[]{"AdminID", "UserID", "FirstName", "LastName", "PhoneNumber"},
                    new String[]{adminId, userId, firstName, lastName, phone});
        }

        JOptionPane.showMessageDialog(panel, "User added successfully! UserID: " + userId);
    }
}


// --- EDIT HANDLER ---
private void handleEdit(String tableName, JComponent[] fieldInputs, JTable table, Set<String> skipSet, String keyColumn, int row, JPanel panel) throws SQLException {
    String[] editableCols = Arrays.stream(table.getColumnModel().getColumns().asIterator().next().toString().split(","))
            .filter(c -> !skipSet.contains(c)).toArray(String[]::new);

    String[] newValues = new String[editableCols.length];
    int j = 0;
    for (int i = 0; i < fieldInputs.length; i++) {
        if (!skipSet.contains(table.getColumnName(i))) {
            if (fieldInputs[i] instanceof JTextField)
                newValues[j++] = ((JTextField) fieldInputs[i]).getText();
            else if (fieldInputs[i] instanceof JComboBox)
                newValues[j++] = ((JComboBox<?>) fieldInputs[i]).getSelectedItem().toString();
        }
    }

    String keyValue = table.getValueAt(row, 0).toString();
    CrudHelper.updateRecord(tableName, keyColumn, keyValue, editableCols, newValues);
    JOptionPane.showMessageDialog(panel, "Record updated successfully.");

    // Cascade updates for UserAccount table
    if ("UserAccount".equalsIgnoreCase(tableName)) {
        String newEmail = ((JTextField) fieldInputs[1]).getText();
        String newRole = ((JComboBox<?>) fieldInputs[3]).getSelectedItem().toString();

        if (!newEmail.isEmpty()) CrudHelper.syncUserEmail(keyValue, newEmail);
        if (!newRole.isEmpty()) CrudHelper.syncUserRole(keyValue, newRole);
    }
}
// --- HELPER METHOD ---



}
