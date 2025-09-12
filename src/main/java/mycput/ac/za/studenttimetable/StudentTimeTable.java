package mycput.ac.za.studenttimetable;


import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import mycput.ac.za.studenttimetable.connection.DBConnection;

public class StudentTimeTable extends JFrame {

    private JPanel contentPanel;
    private JTable timetableTable;

    private StudentSignupForm signupForm;
    private LoginForm loginForm;
    private Subjects.ConnectionProvider connectionProvider;
    private SidebarPanel sidebar;



    private final int SLIDE_STEP = 20; // pixels per animation tick
    private final int TIMER_DELAY = 5; // ms per tick

   public StudentTimeTable() {
    super("📅 Weekly Timetable");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1100, 700);
    setLocationRelativeTo(null);
    setLayout(null); // for sliding login/signup panels

    // Initialize main content panel and timetable
    contentPanel = new JPanel(new BorderLayout());
    timetableTable = createTimetableTable();

    // Initialize connection provider BEFORE using it in SidebarPanel
    this.connectionProvider = new Subjects.ConnectionProvider() {
    @Override
    public Connection get() throws java.sql.SQLException {
        return DBConnection.derbyConnection();
    }
};


    // Initialize forms
    signupForm = new StudentSignupForm(this);
    loginForm = new LoginForm(this, connectionProvider);

    // Set bounds for sliding effect
    signupForm.setBounds(0, 0, getWidth(), getHeight());
    loginForm.setBounds(getWidth(), 0, getWidth(), getHeight());

    add(signupForm);
    add(loginForm);

    setVisible(true);
}


    // ------------------- Sliding Animations -------------------

    public void slideToLogin() {
        Timer timer = new Timer(TIMER_DELAY, null);
        timer.addActionListener(e -> {
            int loginX = loginForm.getX();
            int signupX = signupForm.getX();

            if (loginX <= 0) {
                loginForm.setLocation(0, 0);
                signupForm.setLocation(-getWidth(), 0);
                ((Timer) e.getSource()).stop();
            } else {
                loginForm.setLocation(loginX - SLIDE_STEP, 0);
                signupForm.setLocation(signupX - SLIDE_STEP, 0);
            }
            repaint();
        });
        timer.start();
    }

    public void slideToSignup() {
        Timer timer = new Timer(TIMER_DELAY, null);
        timer.addActionListener(e -> {
            int signupX = signupForm.getX();
            int loginX = loginForm.getX();

            if (signupX <= 0) {
                signupForm.setLocation(0, 0);
                loginForm.setLocation(-getWidth(), 0);
                ((Timer) e.getSource()).stop();
            } else {
                signupForm.setLocation(signupX - SLIDE_STEP, 0);
                loginForm.setLocation(loginX - SLIDE_STEP, 0);
            }
            repaint();
        });
        timer.start();
    }

    // ------------------- Main Dashboard -------------------
    public void showMainDashboard(JPanel panel) {
    getContentPane().removeAll();
    getContentPane().setLayout(new BorderLayout());

    // Recreate content panel with BorderLayout
    contentPanel = new JPanel(new BorderLayout());

    // Initialize and store sidebar in field
    sidebar = new SidebarPanel(contentPanel, timetableTable, connectionProvider);
    add(sidebar, BorderLayout.WEST);

    // Add the main panel (DashboardPanel, Subjects, or any other panel)
    contentPanel.add(panel, BorderLayout.CENTER);
    add(contentPanel, BorderLayout.CENTER);

    getContentPane().revalidate();
    getContentPane().repaint();
}
    
public SidebarPanel getSidebar() {
    return sidebar;
}



    public void showTimetableView() {
    contentPanel.removeAll();
    contentPanel.setLayout(new BorderLayout()); // IMPORTANT

    JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
    header.setBackground(Color.WHITE);
    JLabel title = new JLabel("📘 Weekly Timetable");
    title.setFont(new Font("SansSerif", Font.BOLD, 20));
    header.add(title);

    JScrollPane scrollPane = new JScrollPane(timetableTable);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());

    contentPanel.add(header, BorderLayout.NORTH);
    contentPanel.add(scrollPane, BorderLayout.CENTER);

    contentPanel.revalidate();
    contentPanel.repaint();
}

    // ------------------- Timetable Table -------------------
    private JTable createTimetableTable() {
        String[] columns = {"PER", "TIME FROM - TO", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[][] data = {
                {"1", "8:30 - 9:10", "ISA260S", "", "", "", ""},
                {"2", "9:15 - 9:55", "", "", "", "", ""},
                {"3", "10:00 - 10:40", "", "", "", "", ""},
                {"4", "10:45 - 11:25", "", "", "", "", ""},
                {"5", "11:30 - 12:10", "", "", "", "", ""},
                {"6", "12:15 - 12:55", "", "", "", "", ""},
                {"", "13:00 - 13:45", "L", "U", "N", "C", ""},
                {"7", "13:45 - 14:25", "", "", "", "", ""},
                {"8", "14:30 - 15:10", "", "", "", "", ""},
                {"9", "15:15 - 15:55", "", "", "", "", "ISA260S - PRA"},
                {"10", "16:00 - 16:40", "", "", "", "", ""},
                {"11", "16:45 - 17:25", "", "", "", "", ""},
                {"12", "17:30 - 18:55", "", "", "", "", ""}
        };

        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(40);
        table.setFont(new Font("Inter", Font.PLAIN, 14));
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(220, 230, 255));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));

        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
        return table;
    }
}
