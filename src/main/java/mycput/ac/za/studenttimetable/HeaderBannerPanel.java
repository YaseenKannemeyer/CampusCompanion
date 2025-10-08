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
    private static final Color GRADIENT_START = new Color(70, 130, 180); // same as LoginForm
    private static final Color GRADIENT_END = new Color(100, 180, 220);  // same as LoginForm
    private static final Color LABEL_PRIMARY = Color.WHITE;
    private static final Color LABEL_SECONDARY = new Color(200, 220, 255);
    private static final Color AVATAR_BG = new Color(240, 248, 255);
    private static final Color AVATAR_BORDER = new Color(80, 140, 255);

    public HeaderBannerPanel(Subjects.ConnectionProvider connectionProvider, String studentId) {
        this.connectionProvider = connectionProvider;

        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 200));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setOpaque(false);

        add(createStudentInfoPanel(), BorderLayout.WEST);
        add(createAvatarPanel(), BorderLayout.EAST);

        if (studentId != null) loadStudentInfo(studentId);
        else setStudent(null);
    }

    public HeaderBannerPanel(String studentId) {
        this(null, studentId);
    }

    // Custom paint for professional blue gradient background (matching LoginForm)
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        GradientPaint gp = new GradientPaint(0, 0, GRADIENT_START, 0, getHeight(), GRADIENT_END);
        g2.setPaint(gp);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
    }

    private JPanel createStudentInfoPanel() {
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        nameLabel = createLabel("Name: Loading...", 22, LABEL_PRIMARY);
        studentNumberLabel = createLabel("Student Number: Loading...", 16, LABEL_SECONDARY);
        groupLabel = createLabel("Group: Loading...", 16, LABEL_SECONDARY);
        courseLabel = createLabel("Course: Loading...", 16, LABEL_SECONDARY);

        infoPanel.add(nameLabel);
        infoPanel.add(Box.createVerticalStrut(8));
        infoPanel.add(studentNumberLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(groupLabel);
        infoPanel.add(Box.createVerticalStrut(6));
        infoPanel.add(courseLabel);

        return infoPanel;
    }

    private JLabel createLabel(String text, int fontSize, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Poppins", Font.BOLD, fontSize));
        lbl.setForeground(color);
        return lbl;
    }

    private JPanel createAvatarPanel() {
        JLabel avatar = new JLabel();
        avatar.setPreferredSize(new Dimension(120, 120));
        avatar.setOpaque(true);
        avatar.setBackground(AVATAR_BG);
        avatar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(AVATAR_BORDER, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        ImageIcon icon = new ImageIcon("resources/avatar.png");
        Image scaled = icon.getImage().getScaledInstance(110, 110, Image.SCALE_SMOOTH);
        avatar.setIcon(new ImageIcon(scaled));

        JPanel avatarPanel = new JPanel(new BorderLayout());
        avatarPanel.setOpaque(false);
        avatarPanel.add(avatar, BorderLayout.CENTER);

        return avatarPanel;
    }

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
