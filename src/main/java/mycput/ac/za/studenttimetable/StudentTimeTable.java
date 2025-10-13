package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import mycput.ac.za.studenttimetable.connection.DBConnection;

public class StudentTimeTable extends JFrame {

    private JPanel contentPanel;
    private JTable timetableTable;

    private StudentSignupForm signupForm;
    private LoginForm loginForm;
    private Subjects.ConnectionProvider connectionProvider;
    private SidebarPanel sidebar;

    private final int SLIDE_STEP = 20; // pixels per tick
    private final int TIMER_DELAY = 5; // ms per tick

    public StudentTimeTable() {
        super("📅 Weekly Timetable");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setLayout(null); // required for sliding panels

        // DB connection provider
        this.connectionProvider = () -> DBConnection.derbyConnection();

        // Initialize panels
        loginForm = new LoginForm(this, connectionProvider);
        signupForm = new StudentSignupForm(this);

        // ✅ Correct setup: login visible, signup hidden off-screen
loginForm.setBounds(0, 0, getWidth(), getHeight());
signupForm.setBounds(getWidth(), 0, getWidth(), getHeight());
add(loginForm);
add(signupForm);
 // add last so it’s on top


        setVisible(true);
    }

    // ======================================================
    // =============== SLIDING ANIMATIONS ====================
    // ======================================================

   // ------------------- Sliding Animations -------------------
public void slideToLogin() {
    Timer timer = new Timer(TIMER_DELAY, null);
    timer.addActionListener(e -> {
        int loginX = loginForm.getX();
        int signupX = signupForm.getX();

        // ✅ When login reaches 0, stop the animation
        if (loginX >= 0) {
            loginForm.setLocation(0, 0);
            signupForm.setLocation(getWidth(), 0);
            ((Timer) e.getSource()).stop();
        } else {
            // ✅ Move both panels to the right
            loginForm.setLocation(loginX + SLIDE_STEP, 0);
            signupForm.setLocation(signupX + SLIDE_STEP, 0);
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

        // ✅ When signup reaches 0, stop the animation
        if (signupX <= 0) {
            signupForm.setLocation(0, 0);
            loginForm.setLocation(-getWidth(), 0);
            ((Timer) e.getSource()).stop();
        } else {
            // ✅ Move both panels to the left
            signupForm.setLocation(signupX - SLIDE_STEP, 0);
            loginForm.setLocation(loginX - SLIDE_STEP, 0);
        }
        repaint();
    });
    timer.start();
}


    // ======================================================
    // =============== DASHBOARD SCREEN ======================
    // ======================================================

    public void showMainDashboard() {
        // Remove login/signup panels
        getContentPane().removeAll();
        setLayout(new BorderLayout());

        // Maximize window
        setExtendedState(JFrame.MAXIMIZED_BOTH);

        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        timetableTable = createTimetableTable();

        // Create layered pane for future overlays
        JLayeredPane layeredPane = new JLayeredPane();
        layeredPane.setLayout(new BorderLayout());
        layeredPane.add(contentPanel, BorderLayout.CENTER);

        // Sidebar
        sidebar = new SidebarPanel(contentPanel, timetableTable, connectionProvider);
        sidebar.setLayeredPane(layeredPane);

        add(sidebar, BorderLayout.WEST);
        add(layeredPane, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    public SidebarPanel getSidebar() {
        return sidebar;
    }

    // ======================================================
    // =============== TIMETABLE TABLE =======================
    // ======================================================

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
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(SwingConstants.CENTER);
                if (!isSelected)
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                return c;
            }
        };
        table.setDefaultRenderer(Object.class, renderer);
        return table;
    }

    // ======================================================
    // =============== LOGIN PANEL ===========================
    // ======================================================

    public void showLoginPanel(LoginForm loginForm) {
        getContentPane().removeAll();
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(loginForm, BorderLayout.CENTER);

        // Reset references
        contentPanel = new JPanel(new BorderLayout());
        sidebar = null;

        getContentPane().revalidate();
        getContentPane().repaint();
    }
}
