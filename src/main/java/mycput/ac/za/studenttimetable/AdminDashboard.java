package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.nimbus.NimbusLookAndFeel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
import mycput.ac.za.studenttimetable.dao.CrudHelper;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public AdminDashboard() {
        try { UIManager.setLookAndFeel(new NimbusLookAndFeel()); } 
        catch (UnsupportedLookAndFeelException ignored) {}

        setTitle("Admin Dashboard");
        setSize(1300, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new EmptyBorder(10, 10, 10, 10));
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Add tabs
        addTab("Users / Roles", "UserAccount", new String[]{"UserID", "Email", "PasswordHash", "Role", "CreatedAt"}, new String[]{"CreatedAt"}, "UserID");
        addTab("Courses", "Course", new String[]{"CourseID", "CourseName"}, new String[]{}, "CourseID");
        addTab("Subjects", "Subject", new String[]{"SubjectCode", "SubjectName", "YearLevel"}, new String[]{}, "SubjectCode");
        addTab("Students", "Student", new String[]{"StudentID", "UserID", "GroupID", "FirstName", "LastName", "PhoneNumber", "Email"}, new String[]{}, "StudentID");
        addTab("Admins", "Admin", new String[]{"AdminID", "UserID", "FirstName", "LastName", "PhoneNumber"}, new String[]{}, "AdminID");
        addTab("Lecturers", "Lecturer", new String[]{"LecturerID", "FirstName", "LastName", "Email", "PhoneNumber"}, new String[]{}, "LecturerID");
        addTab("Lecture Rooms", "LectureRoom", new String[]{"RoomID", "RoomType"}, new String[]{}, "RoomID");
        addTab("Timetable", "TimeTable", new String[]{"TimeTableID", "SubjectCode", "GroupID", "LecturerID", "RoomID", "ClassType", "ClassDate", "StartTime", "EndTime"}, new String[]{"TimeTableID"}, "TimeTableID");

        add(tabbedPane);
    }

    private void addTab(String title, String tableName, String[] columns, String[] skipOnAdd, String keyColumn) {
        tabbedPane.addTab(title, createDbCrudPanel(tableName, columns, skipOnAdd, keyColumn));
    }

   private JPanel createDbCrudPanel(String tableName, String[] columns, String[] skipOnAdd, String keyColumn) {
    JPanel panel = new JPanel(new BorderLayout(10, 10));
    panel.setBorder(new EmptyBorder(10, 10, 10, 10));
    panel.setBackground(Color.WHITE);

    // --- Top Toolbar ---
    JPanel topPanel = new JPanel(new BorderLayout(10, 10));
    topPanel.setBackground(Color.WHITE);

    JButton btnRefresh = new JButton("Refresh");
    styleButton(btnRefresh, new Color(34, 139, 230), Color.WHITE);

    JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    searchPanel.setBackground(Color.WHITE);
    JLabel lblSearch = new JLabel("Search: ");
    lblSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    JTextField txtSearch = new JTextField(20);
    txtSearch.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    searchPanel.add(lblSearch);
    searchPanel.add(txtSearch);

    topPanel.add(btnRefresh, BorderLayout.WEST);
    topPanel.add(searchPanel, BorderLayout.EAST);
    panel.add(topPanel, BorderLayout.NORTH);

    // --- Table ---
    DefaultTableModel tableModel = new DefaultTableModel(columns, 0);
    JTable table = new JTable(tableModel);
    table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    table.setRowHeight(28);
    table.setFillsViewportHeight(true);
    table.setGridColor(new Color(220, 220, 220));
    table.setSelectionBackground(new Color(30, 144, 255));
    table.setSelectionForeground(Color.WHITE);

    DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
    leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
    for (int i = 0; i < table.getColumnCount(); i++)
        table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);

    TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
    table.setRowSorter(sorter);

    txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
        private void search() {
            String text = txtSearch.getText().trim();
            if (text.isEmpty()) sorter.setRowFilter(null);
            else sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    });

    JScrollPane scrollPane = new JScrollPane(table);
    panel.add(scrollPane, BorderLayout.CENTER);

    // --- Bottom Panel: Form + Actions ---
    JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
    bottomPanel.setBackground(Color.WHITE);

    JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
    formPanel.setBorder(BorderFactory.createTitledBorder("Form"));
    formPanel.setBackground(Color.WHITE);
    formPanel.setVisible(false);

    JLabel[] fieldLabels = new JLabel[columns.length];
    JComponent[] fieldInputs = new JComponent[columns.length];

    for (int i = 0; i < columns.length; i++) {
        fieldLabels[i] = new JLabel(columns[i] + ":");
        fieldLabels[i].setFont(new Font("Segoe UI", Font.PLAIN, 13));

        if (columns[i].equalsIgnoreCase("Role")) {
            JComboBox<String> cmbRole = new JComboBox<>(new String[]{"STUDENT","ADMIN"});
            cmbRole.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fieldInputs[i] = cmbRole;
        } else {
            JTextField txt = new JTextField();
            txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            fieldInputs[i] = txt;

            if (columns[i].equalsIgnoreCase("CreatedAt") && tableName.equalsIgnoreCase("UserAccount")) {
                txt.setEnabled(false); // read-only for Edit
            }
        }

        formPanel.add(fieldLabels[i]);
        formPanel.add(fieldInputs[i]);
    }

    JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    actionPanel.setBackground(Color.WHITE);
    JLabel lblAction = new JLabel("Action:");
    lblAction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    String[] actions = {"Select Action", "Add", "Edit", "Delete"};
    JComboBox<String> cmbAction = new JComboBox<>(actions);
    cmbAction.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    JButton btnApply = new JButton("Apply");
    styleButton(btnApply, new Color(34, 139, 230), Color.WHITE);

    actionPanel.add(lblAction);
    actionPanel.add(cmbAction);
    actionPanel.add(btnApply);

    Set<String> skipSet = new HashSet<>(Set.of(skipOnAdd));

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
                    if (columns[i].equalsIgnoreCase("UserID") && tableName.equalsIgnoreCase("UserAccount")) {
                        try {
                            String lastID = CrudHelper.getLastUserID();
                            int num = Integer.parseInt(lastID.substring(1)) + 1;
                            ((JTextField) fieldInputs[i]).setText(String.format("U%03d", num));
                        } catch (Exception ex) { ((JTextField) fieldInputs[i]).setText("U001"); }
                    } else if (columns[i].equalsIgnoreCase("CreatedAt")) {
                        ((JTextField) fieldInputs[i]).setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    } else if (fieldInputs[i] instanceof JTextField) ((JTextField) fieldInputs[i]).setText("");
                    else if (fieldInputs[i] instanceof JComboBox) ((JComboBox<?>) fieldInputs[i]).setSelectedIndex(0);
                } else if ("Edit".equals(action) && selectedRow >= 0) {
                    Object val = table.getValueAt(selectedRow, i);
                    if (fieldInputs[i] instanceof JTextField) ((JTextField) fieldInputs[i]).setText(val.toString());
                    else if (fieldInputs[i] instanceof JComboBox) ((JComboBox<?>) fieldInputs[i]).setSelectedItem(val.toString());
                }
            }
            formPanel.setVisible(true);
        } else formPanel.setVisible(false);
        panel.revalidate();
        panel.repaint();
    });

    btnApply.addActionListener(e -> {
        try {
            handleCrudAction(panel, table, tableModel, tableName, columns, keyColumn, skipSet, cmbAction, fieldInputs);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(panel, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    });

    btnRefresh.addActionListener(e -> refreshTableSafe(panel, tableModel, tableName, columns));

    bottomPanel.add(formPanel, BorderLayout.CENTER);
    bottomPanel.add(actionPanel, BorderLayout.SOUTH);
    panel.add(bottomPanel, BorderLayout.SOUTH);

    refreshTableSafe(panel, tableModel, tableName, columns);
    return panel;
}

    private void styleButton(JButton btn, Color bg, Color fg) {
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }

private void handleCrudAction(JPanel panel, JTable table, DefaultTableModel tableModel,
                             String tableName, String[] columns, String keyColumn,
                             Set<String> skipSet, JComboBox<String> cmbAction, JComponent[] fieldInputs) throws SQLException {
    String action = (String) cmbAction.getSelectedItem();

    if ("Add".equals(action)) {
        String[] values = new String[columns.length - skipSet.size()];
        int idx = 0;
        for (int i = 0; i < columns.length; i++) {
            if (!skipSet.contains(columns[i])) {
                if (fieldInputs[i] instanceof JTextField)
                    values[idx++] = ((JTextField) fieldInputs[i]).getText();
                else if (fieldInputs[i] instanceof JComboBox)
                    values[idx++] = ((JComboBox<?>) fieldInputs[i]).getSelectedItem().toString();
            }
        }
        CrudHelper.insertRecord(tableName, getVisibleColumns(columns, skipSet), values);

        // Auto-insert into Admin or Student
        if (tableName.equalsIgnoreCase("UserAccount")) {
            String role = null, userId = null;
            for (int i = 0; i < columns.length; i++) {
                if (columns[i].equalsIgnoreCase("Role")) role = ((JComboBox<?>) fieldInputs[i]).getSelectedItem().toString();
                if (columns[i].equalsIgnoreCase("UserID")) userId = ((JTextField) fieldInputs[i]).getText();
            }
            if (role != null && userId != null) {
                if (role.equalsIgnoreCase("ADMIN")) {
                    String adminID = "ADM" + System.currentTimeMillis();
                    CrudHelper.insertRecord("Admin",
                            new String[]{"AdminID", "UserID", "FirstName", "LastName", "PhoneNumber"},
                            new String[]{adminID, userId, "New", "Admin", "0000000000"});
                } else if (role.equalsIgnoreCase("STUDENT")) {
                    String studentID = "STU" + System.currentTimeMillis();
                    CrudHelper.insertRecord("Student",
                            new String[]{"StudentID", "UserID", "GroupID", "FirstName", "LastName", "PhoneNumber", "Email"},
                            new String[]{studentID, userId, "DEFAULT", "New", "Student", "0000000000", ""});
                }
            }
        }
        JOptionPane.showMessageDialog(panel, "Record added successfully.");
        refreshTableSafe(panel, tableModel, tableName, columns);

    } else if ("Edit".equals(action)) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(panel, "Please select a record to edit.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int confirm = JOptionPane.showConfirmDialog(panel, "Apply changes?", "Confirm Edit", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            // Only update editable fields
            String[] columnsToUpdate = java.util.Arrays.stream(columns)
                    .filter(c -> !skipSet.contains(c) && !c.equalsIgnoreCase("CreatedAt")).toArray(String[]::new);
            String[] newValues = new String[columnsToUpdate.length];
            for (int i = 0, j = 0; i < columns.length; i++) {
                if (!skipSet.contains(columns[i]) && !columns[i].equalsIgnoreCase("CreatedAt")) {
                    if (fieldInputs[i] instanceof JTextField)
                        newValues[j++] = ((JTextField) fieldInputs[i]).getText();
                    else if (fieldInputs[i] instanceof JComboBox)
                        newValues[j++] = ((JComboBox<?>) fieldInputs[i]).getSelectedItem().toString();
                }
            }
            CrudHelper.updateRecord(tableName, keyColumn, table.getValueAt(row, 0).toString(), columnsToUpdate, newValues);
            JOptionPane.showMessageDialog(panel, "Record updated successfully.");
            refreshTableSafe(panel, tableModel, tableName, columns);
        }

    } else if ("Delete".equals(action)) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(panel, "Please select a record to delete.", "No Selection", JOptionPane.WARNING_MESSAGE); return; }
        int confirm = JOptionPane.showConfirmDialog(panel, "Are you sure you want to delete record " + table.getValueAt(row, 0) + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            CrudHelper.deleteRecord(tableName, keyColumn, table.getValueAt(row, 0).toString());
            JOptionPane.showMessageDialog(panel, "Record deleted successfully.");
            refreshTableSafe(panel, tableModel, tableName, columns);
        }
    }
}


    private void refreshTableSafe(JPanel panel, DefaultTableModel model, String tableName, String[] columns) {
        try { refreshTable(tableName, columns, model); } catch (SQLException ex) { JOptionPane.showMessageDialog(panel, "DB Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE); }
    }

    private String[] getVisibleColumns(String[] columns, Set<String> skipSet) {
        return java.util.Arrays.stream(columns).filter(c -> !skipSet.contains(c)).toArray(String[]::new);
    }

    private void refreshTable(String tableName, String[] columns, DefaultTableModel model) throws SQLException {
        model.setRowCount(0);
        for (String[] row : CrudHelper.loadTable(tableName, columns)) model.addRow(row);
    }
}
