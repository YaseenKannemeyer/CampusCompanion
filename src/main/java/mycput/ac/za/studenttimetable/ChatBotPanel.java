package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import mycput.ac.za.openaiclient.OpenAIClient;

public class ChatBotPanel extends JPanel {

    private JPanel chatAreaPanel;
    private JScrollPane scrollPane;
    private JTextField inputField;
    private JButton sendButton;
    private Point mouseDownCompCoords = null;

    // JSON QA List
    private List<QA> qaList = JsonLoader.loadQA();

    // ================= COLORS =================
    private static final Color PRIMARY_BG = new Color(0xD6EEFF);
    private static final Color CARD_BG = new Color(0xFFFFFF);
    private static final Color CTA_PRIMARY = new Color(0xE7404A);
    private static final Color CTA_SECONDARY = new Color(0x1996CC);
    private static final Color SHADOW_COLOR = new Color(0, 0, 0, 30);
    private static final Color BACKGROUND = new Color(0xF4F7FA);
private static final Color USER_BUBBLE = new Color(0x4A90E2);
private static final Color BOT_BUBBLE = new Color(0xE0E0E0);
private static final Color USER_TEXT = Color.WHITE;
private static final Color BOT_TEXT = Color.BLACK;

    public ChatBotPanel() {
        setLayout(new BorderLayout());
        setBackground(PRIMARY_BG);
        setOpaque(false);
        setBorder(BorderFactory.createLineBorder(SHADOW_COLOR, 1, true));
        setSize(450, 500);
        setVisible(false);

        initHeader();
        initChatArea();
        initInputPanel();
        initDragging();
        initOutsideClickListener();
    }

    // ================= HEADER =================
    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(CTA_SECONDARY);
        header.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel title = new JLabel("💬 ChatBot");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Roboto", Font.BOLD, 16));

        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);
    }

    // ================= CHAT AREA =================
    private void initChatArea() {
        chatAreaPanel = new JPanel();
        chatAreaPanel.setLayout(new BoxLayout(chatAreaPanel, BoxLayout.Y_AXIS));
        chatAreaPanel.setBackground(CARD_BG);
        chatAreaPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        scrollPane = new JScrollPane(chatAreaPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        JPanel chatCard = new JPanel(new BorderLayout());
        chatCard.setBackground(CARD_BG);
        chatCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(12, 12, 12, 12),
                BorderFactory.createLineBorder(SHADOW_COLOR, 1, true)
        ));
        chatCard.setPreferredSize(new Dimension(450, 350));
        chatCard.add(scrollPane, BorderLayout.CENTER);

        add(chatCard, BorderLayout.CENTER);
    }

    // ================= INPUT PANEL =================
    private void initInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 0));
        inputPanel.setBackground(CARD_BG);
        inputPanel.setBorder(new EmptyBorder(10, 12, 10, 12));

        inputField = new JTextField();
        inputField.setFont(new Font("Roboto", Font.PLAIN, 16));
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(180, 180, 180), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        inputField.setPreferredSize(new Dimension(330, 45));

        sendButton = new JButton("Send");
        sendButton.setFont(new Font("Roboto", Font.BOLD, 15));
        sendButton.setBackground(CTA_PRIMARY);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        sendButton.setBorder(BorderFactory.createEmptyBorder());
        sendButton.setOpaque(true);
        sendButton.setPreferredSize(new Dimension(100, 45));

        sendButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) { sendButton.setBackground(CTA_SECONDARY); }
            @Override
            public void mouseExited(MouseEvent e) { sendButton.setBackground(CTA_PRIMARY); }
        });

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);

        inputField.addActionListener(e -> processMessage());
        sendButton.addActionListener(e -> processMessage());
    }

    // ================= DRAGGING =================
    private void initDragging() {
        MouseAdapter dragger = new MouseAdapter() {
            public void mousePressed(MouseEvent e) { mouseDownCompCoords = e.getPoint(); }
            public void mouseDragged(MouseEvent e) {
                Point curr = e.getLocationOnScreen();
                setLocation(curr.x - mouseDownCompCoords.x, curr.y - mouseDownCompCoords.y);
            }
        };
        addMouseListener(dragger);
        addMouseMotionListener(dragger);
    }

    // ================= OUTSIDE CLICK =================
    private void initOutsideClickListener() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof MouseEvent me)) return;
            if (me.getID() != MouseEvent.MOUSE_PRESSED) return;
            Component clicked = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());
            if (clicked == null || !SwingUtilities.isDescendingFrom(clicked, ChatBotPanel.this)) {
                setVisible(false);
            }
        }, AWTEvent.MOUSE_EVENT_MASK);
    }

    // ================= PROCESS MESSAGE =================
    private void processMessage() {
    String text = inputField.getText().trim();
    if (text.isEmpty()) return;

    appendMessage("You", text);
    inputField.setText("");
    inputField.setEnabled(false);
    sendButton.setEnabled(false);

    new Thread(() -> {
        String response = getJsonAnswer(text);
        if (response == null || response.isEmpty()) response = PythonChatbotConnector.getBotReply(text);
        if (response == null || response.isEmpty()) {
            try { response = OpenAIClient.askGPT(text); }
            catch (Exception e) { response = "⚠️ Error fetching response."; e.printStackTrace(); }
        }

        String finalResponse = response;
        SwingUtilities.invokeLater(() -> {
            appendMessage("ChatBot", finalResponse);
            inputField.setEnabled(true);
            sendButton.setEnabled(true);
            inputField.requestFocus();
        });
    }).start();
}

    // ================= JSON MATCHING =================
    private String getJsonAnswer(String userInput) {
        if (userInput == null || userInput.isEmpty()) return null;
        String inputLower = userInput.toLowerCase().trim();
        QA bestMatch = null;
        int bestScore = 0;

        for (QA qa : qaList) {
            int qaScore = 0;
            if (qa.questionList != null) {
                for (String q : qa.questionList) {
                    String qLower = q.toLowerCase().trim();
                    if (inputLower.equals(qLower)) return qa.answer;
                    String[] words = qLower.split("\\s+");
                    int matches = 0;
                    for (String w : words) if (inputLower.contains(w)) matches++;
                    qaScore = Math.max(qaScore, matches);
                }
            } else if (qa.question != null) {
                String qLower = qa.question.toLowerCase().trim();
                if (inputLower.equals(qLower)) return qa.answer;
                String[] words = qLower.split("\\s+");
                int matches = 0;
                for (String w : words) if (inputLower.contains(w)) matches++;
                qaScore = matches;
            }

            int keywordScore = 0;
            if (qa.keywords != null) {
                for (String kw : qa.keywords) if (inputLower.contains(kw.toLowerCase())) keywordScore++;
            }

            int totalScore = qaScore + keywordScore;
            if (totalScore > bestScore) {
                bestScore = totalScore;
                bestMatch = qa;
            }
        }

        if (bestMatch != null && bestScore > 0) return bestMatch.answer;
        return null;
    }

  // ================= APPEND MESSAGE =================
private void appendMessage(String sender, String msg) {
    JPanel messagePanel = new JPanel();
    messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
    messagePanel.setOpaque(false);

    RoundedPanel messageCard = new RoundedPanel(16);
    messageCard.setLayout(new BorderLayout());
    messageCard.setMaximumSize(new Dimension(280, Integer.MAX_VALUE)); // smaller max width
    messageCard.setBorder(new EmptyBorder(8, 12, 8, 12));

    // ===== Tiny label on top =====
    JLabel senderLabel = new JLabel(sender);
    senderLabel.setFont(new Font("Roboto", Font.PLAIN, 10));
    senderLabel.setForeground(new Color(100, 100, 100));
    senderLabel.setBorder(new EmptyBorder(0, 0, 4, 0));

    // ===== Message text =====
JTextArea messageText = new JTextArea(msg);
messageText.setLineWrap(true);
messageText.setWrapStyleWord(true);
messageText.setEditable(false);
messageText.setFont(new Font("Roboto", Font.PLAIN, 15));
messageText.setOpaque(false);

// ===== Calculate dynamic height =====
int maxWidth = 280; // max bubble width
int minHeight = 40; // minimum bubble height

// let the text area compute the height needed for text
messageText.setSize(maxWidth, Short.MAX_VALUE);
Dimension preferred = messageText.getPreferredSize();
messageText.setPreferredSize(new Dimension(maxWidth, Math.max(minHeight, preferred.height)));


    // ===== Combine label and text =====
    JPanel textPanel = new JPanel();
    textPanel.setLayout(new BorderLayout());
    textPanel.setOpaque(false);
    textPanel.add(senderLabel, BorderLayout.NORTH);
    textPanel.add(messageText, BorderLayout.CENTER);

    // ===== Colors and alignment =====
    if (sender.equals("You")) {
        messageCard.setBackground(new Color(0x2A6FDF));
        messageText.setForeground(Color.WHITE);
        messagePanel.add(Box.createHorizontalGlue());
        messagePanel.add(messageCard);
    } else {
        messageCard.setBackground(new Color(0xE0E0E0));
        messageText.setForeground(Color.BLACK);
        messagePanel.add(messageCard);
        messagePanel.add(Box.createHorizontalGlue());
    }

    messageCard.add(textPanel, BorderLayout.CENTER);

    chatAreaPanel.add(messagePanel);
    chatAreaPanel.add(Box.createVerticalStrut(6));
    chatAreaPanel.revalidate();
    chatAreaPanel.repaint();

    scrollToBottom();
}



// ================= CUSTOM ROUNDED PANEL =================
class RoundedPanel extends JPanel {
    private int cornerRadius;

    public RoundedPanel(int radius) {
        super();
        this.cornerRadius = radius;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }
}


    

    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
    }
}
