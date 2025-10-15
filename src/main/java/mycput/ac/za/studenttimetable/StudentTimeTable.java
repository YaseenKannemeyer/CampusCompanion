package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import javax.swing.table.JTableHeader;
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
    // ===== Columns and Data =====
    String[] columns = {"PER", "TIME FROM - TO", "MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
    String[][] data = {
            {"1", "8:30 - 9:10", "ISA260S","ADP262S", "","", ""},
            {"2", "9:15 - 9:55", "ISA260S","ADP262S", "", "", ""},
            {"3", "10:00 - 10:40", "ADP262S",  "",  "", "", ""},
            {"4", "10:45 - 11:25", "ADP262S", "",  "PRC262S", "", ""},
            {"5", "11:30 - 12:10", "MAF262S", "", "ADP262S","", "ISA260S"},
            {"6", "12:15 - 12:55", "MAF262S", "PRC262S",  "ADP262S", "", "ISA260S"},
            {"", "13:00 - 13:45", "L", "U", "N", "C", "H"},
            {"7", "13:45 - 14:25", "CNF262S","PRT262S","INM262S","ICE262S", ""},
            {"8", "14:30 - 15:10", "CNF262S","PRT262S","INM262S","ICE262S", ""},
            {"9", "15:15 - 15:55", "INM262S", "", "", "", ""},
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

    JTable table = new JTable(model) {
        @Override
        public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            if (!isRowSelected(row)) {
                c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(0xF8FBFF)); // soft contrast
            } else {
                c.setBackground(new Color(0xD6EEFF)); // primary highlight
            }
            c.setForeground(Color.BLACK);
            return c;
        }
    };

    // ===== Table Appearance =====
    table.setRowHeight(45);
    table.setFont(new Font("Roboto", Font.PLAIN, 14));
    table.setForeground(Color.BLACK);
    table.setGridColor(new Color(0xE0E0E0));
    table.setShowHorizontalLines(false);
    table.setShowVerticalLines(false);
    table.setIntercellSpacing(new Dimension(0, 0));
    table.setFillsViewportHeight(true);
    table.setSelectionBackground(new Color(0xE7404A)); // red CTA for selection
    table.setSelectionForeground(Color.WHITE);
    table.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

    // ===== Table Header Styling =====
    JTableHeader header = table.getTableHeader();
    header.setBackground(new Color(0x1996CC)); // blue accent
    header.setForeground(Color.WHITE);
    header.setFont(new Font("Roboto", Font.BOLD, 15));
    header.setOpaque(true);
    header.setPreferredSize(new Dimension(header.getWidth(), 40));
    ((DefaultTableCellRenderer) header.getDefaultRenderer())
            .setHorizontalAlignment(SwingConstants.CENTER);

    // ===== Center Alignment for All Cells =====
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    table.setDefaultRenderer(Object.class, centerRenderer);

    // ===== Scroll Pane Styling (Material Card Look) =====
    JScrollPane scrollPane = new JScrollPane(table);
    scrollPane.setBorder(BorderFactory.createEmptyBorder());
    scrollPane.getViewport().setBackground(new Color(0xFFFFFF));

    // Rounded corners with subtle shadow (card effect)
    scrollPane.setViewportBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE0E0E0), 1, true),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    // Add soft drop shadow under the card
    scrollPane.setBackground(new Color(0xFFFFFF));
    scrollPane.setOpaque(true);

    // ===== Return the table wrapped in a scroll pane =====
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