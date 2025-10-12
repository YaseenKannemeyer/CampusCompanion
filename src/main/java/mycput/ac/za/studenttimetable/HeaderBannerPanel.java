package mycput.ac.za.studenttimetable;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.SwingWorker;
import mycput.ac.za.studenttimetable.dao.StudentDAO;
import mycput.ac.za.studenttimetable.domain.StudentDomain;

public class HeaderBannerPanel extends JPanel {

    private JLabel nameLabel, studentNumberLabel, groupLabel, courseLabel;
    private final Subjects.ConnectionProvider connectionProvider;

    // ================= COLORS =================
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);  // matches Sidebar
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color LABEL_PRIMARY = new Color(0x1996CC); // accent color
    private static final Color LABEL_SECONDARY = new Color(80, 80, 80);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);

    private static final Font ROBOTO_BOLD = new Font("Roboto", Font.BOLD, 22);
    private static final Font ROBOTO_MEDIUM = new Font("Roboto", Font.PLAIN, 16);

    public HeaderBannerPanel(Subjects.ConnectionProvider connectionProvider, String studentId) {
        this.connectionProvider = connectionProvider;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 200));
        setOpaque(false);
        setBorder(new EmptyBorder(15, 15, 15, 15));

        // Single card with logo on left and info on right
        JPanel infoCard = createInfoCard();
        add(infoCard, BorderLayout.CENTER);

        if (studentId != null) loadStudentInfo(studentId);
        else setStudent(null);
    }

    public HeaderBannerPanel(String studentId) {
        this(null, studentId);
    }

    // ================= PAINT BACKGROUND =================
   @Override
protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    // Draw main rounded background
    g2.setColor(PRIMARY_BG);
    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

    // Draw soft bottom shadow
    int shadowHeight = 15; // thinner shadow
    GradientPaint shadow = new GradientPaint(
            0, getHeight() - shadowHeight, new Color(0, 0, 0, 10), // very light black
            0, getHeight(), new Color(0, 0, 0, 0)                  // fade to transparent
    );
    g2.setPaint(shadow);
    g2.fillRect(0, getHeight() - shadowHeight, getWidth(), shadowHeight); // use fillRect for subtlety

    g2.dispose();
}



    // ================= INFO CARD (LOGO + STUDENT INFO) =================
   private JPanel createInfoCard() {
    JPanel card = new JPanel(new BorderLayout());
    card.setOpaque(true);
    card.setBackground(CARD_BG);
    card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(15, 20, 15, 20),
            BorderFactory.createLineBorder(SHADOW_COLOR, 1, true)
    ));

    // ================= STUDENT INFO (LEFT) =================
    JPanel infoPanel = new JPanel();
    infoPanel.setOpaque(false);
    infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
    infoPanel.setAlignmentY(Component.CENTER_ALIGNMENT);

    nameLabel = createLabel("Name: Loading...", ROBOTO_BOLD, LABEL_PRIMARY);
    studentNumberLabel = createLabel("Student Number: Loading...", ROBOTO_MEDIUM, LABEL_SECONDARY);
    groupLabel = createLabel("Group: Loading...", ROBOTO_MEDIUM, LABEL_SECONDARY);
    courseLabel = createLabel("Course: Loading...", ROBOTO_MEDIUM, LABEL_SECONDARY);

    infoPanel.add(nameLabel);
    infoPanel.add(Box.createVerticalStrut(8));
    infoPanel.add(studentNumberLabel);
    infoPanel.add(Box.createVerticalStrut(6));
    infoPanel.add(groupLabel);
    infoPanel.add(Box.createVerticalStrut(6));
    infoPanel.add(courseLabel);
infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
    card.add(infoPanel, BorderLayout.WEST);

    // ================= LOGO (RIGHT) =================
JLabel logoLabel = new JLabel();
try {
    ImageIcon icon = new ImageIcon(getClass().getResource("/icons/LogoW.png"));
    
    // Increase logo size
    int logoHeight = 140; // bigger than before
    int logoWidth = (icon.getIconWidth() * logoHeight) / icon.getIconHeight();
    Image scaled = icon.getImage().getScaledInstance(logoWidth, logoHeight, Image.SCALE_SMOOTH);
    logoLabel.setIcon(new ImageIcon(scaled));
} catch (Exception e) {
    logoLabel.setText("Logo");
    logoLabel.setFont(new Font("Poppins", Font.BOLD, 24)); // bigger fallback
    logoLabel.setForeground(LABEL_PRIMARY);
}

// Right align and add padding
logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
logoLabel.setVerticalAlignment(SwingConstants.CENTER);
logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20)); // right padding to separate from info

card.add(logoLabel, BorderLayout.EAST);

    return card;
}


    private JLabel createLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    // ================= LOAD STUDENT DATA =================
    private void loadStudentInfo(String studentId) {
        new SwingWorker<StudentDomain, Void>() {
            @Override
            protected StudentDomain doInBackground() throws Exception {
                StudentDAO dao = new StudentDAO();
                return dao.getStudentProfile(studentId);
            }

            @Override
            protected void done() {
                try {
                    StudentDomain student = get();
                    setStudent(student);
                } catch (Exception e) {
                    e.printStackTrace();
                    setStudent(null);
                }
            }
        }.execute();
    }

    public void setStudent(StudentDomain student) {
        if (student != null) {
            nameLabel.setText("Name: " + student.getFirstName() + " " + student.getLastName());
            studentNumberLabel.setText("Student Number: " + student.getStudentID());
            groupLabel.setText("Group: " + (student.getGroupID() != null ? student.getGroupID() : "-"));
            courseLabel.setText("Course: " + (student.getCourseName() != null ? student.getCourseName() : "-"));
        } else {
            nameLabel.setText("Name: -");
            studentNumberLabel.setText("Student Number: -");
            groupLabel.setText("Group: -");
            courseLabel.setText("Course: -");
        }
        revalidate();
        repaint();
    }

    public void setStudentId(String studentId) {
        if (studentId != null) loadStudentInfo(studentId);
        else setStudent(null);
    }
}
