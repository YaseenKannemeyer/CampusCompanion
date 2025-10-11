package mycput.ac.za.studenttimetable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ModernCard extends JPanel {
    private final Color backgroundColor;
    private int pulseOffset = 0;

    // Constructor with single color (no gradient)
    public ModernCard(Color backgroundColor) {
        this.backgroundColor = backgroundColor;

        setOpaque(false);
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(920, 600));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
    }

    /**
     * Pulsing effect for subtle animation (like elevation change)
     */
    public void pulse() {
        Timer t = new Timer(18, null);
        t.addActionListener(new AbstractAction() {
            int step = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                step++;
                pulseOffset = (int)(6 * Math.sin(step * Math.PI / 40.0));
                repaint();
                if (step > 80) {
                    ((Timer)e.getSource()).stop();
                    pulseOffset = 0;
                    repaint();
                }
            }
        });
        t.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Shadow layers (elevation effect)
        for (int i = 0; i < 8; i++) {
            int alpha = 20 - i * 2;
            g2.setColor(new Color(0, 0, 0, Math.max(0, alpha)));
            int x = 8 - i + pulseOffset / 2;
            int y = 8 - i + Math.abs(pulseOffset / 3);
            g2.fillRoundRect(x, y, w - (8 - i) * 2, h - (8 - i) * 2, 18, 18);
        }

        // Solid color background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(0, 0, w - 12, h - 12, 18, 18);

        g2.dispose();
        super.paintComponent(g);
    }

    // Material Design button utility
    public static JButton createMDButton(String text, Color bgColor) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Roboto", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover animation
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { btn.setBackground(bgColor.brighter()); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { btn.setBackground(bgColor); }
        });
        return btn;
    }

    // Card header utility
    public static JPanel createCardHeader(String title, String subtitle) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Roboto", Font.BOLD, 22));
        lblTitle.setForeground(new Color(33, 33, 33)); // dark text for readability

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(new Font("Roboto", Font.PLAIN, 14));
        lblSub.setForeground(new Color(100, 100, 100)); // subtle secondary text

        panel.add(lblTitle);
        panel.add(Box.createVerticalStrut(6));
        panel.add(lblSub);

        return panel;
    }
}
