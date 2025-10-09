package mycput.ac.za.studenttimetable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

import mycput.ac.za.openaiclient.OpenAIClient;

public class ChatBotPanel extends JPanel {

    private JTextField inputField;
    private JButton sendButton;
    private JButton collapseButton;
    private JPanel chatPanel;
    private JScrollPane scrollPane;
    private boolean collapsed = false;
    private Point mouseDownCompCoords = null;
    private List<ChatMessage> messages = new ArrayList<>();

    // JSON QA List
    private List<QA> qaList = JsonLoader.loadQA(); // load from JSON file

    // ================= CHATBOT SETUP =================
    public ChatBotPanel() {
        setLayout(new BorderLayout());
        setSize(450, 400);
        setBorder(BorderFactory.createLineBorder(new Color(40, 120, 200), 2));
        setBackground(new Color(70, 130, 180, 230));
        setVisible(false);

        initHeader();
        initChatArea();
        initInputPanel();
        initDragging();
    }

    // ================= HEADER =================
    private void initHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(40, 120, 200));
        header.setBorder(new EmptyBorder(4, 8, 4, 8));

        JLabel title = new JLabel("💬 ChatBot");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Poppins", Font.BOLD, 16));

        collapseButton = new JButton("–");
        collapseButton.setFocusPainted(false);
        collapseButton.addActionListener(e -> toggleCollapse());

        header.add(title, BorderLayout.WEST);
        header.add(collapseButton, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);
    }

    // ================= CHAT AREA =================
    private void initChatArea() {
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(new Color(0, 0, 0, 0));

        scrollPane = new JScrollPane(chatPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    // ================= INPUT =================
    private void initInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        sendButton = new JButton("Send");

        inputField.addActionListener(this::processMessage);
        sendButton.addActionListener(this::processMessage);

        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        add(inputPanel, BorderLayout.SOUTH);
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

    // ================= COLLAPSE =================
    private void toggleCollapse() {
        collapsed = !collapsed;
        chatPanel.setVisible(!collapsed);
        inputField.setVisible(!collapsed);
        sendButton.setVisible(!collapsed);
        collapseButton.setText(collapsed ? "+" : "–");
        revalidate();
        repaint();
    }

    // ================= PROCESS MESSAGE =================
    private void processMessage(ActionEvent e) {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        inputField.setText("");

        appendMessage("You", text, Source.USER);
        appendMessage("ChatBot", "Typing...", Source.SYSTEM);

        inputField.setEnabled(false);
        sendButton.setEnabled(false);

        new Thread(() -> {
            String response = null;
            Source source = Source.SYSTEM;

            try {
                // 1️⃣ Check JSON keywords
                response = getJsonAnswer(text);
                if (response != null) source = Source.JSON;

                // 2️⃣ Fallback to Python ChatBot
                if (response == null || response.isEmpty() || response.equals("...")) {
                    response = PythonChatbotConnector.getBotReply(text);
                    if (response != null && !response.equals("...")) source = Source.CHATTERBOT;
                }

                // 3️⃣ Fallback to GPT
                if (response == null || response.isEmpty() || response.equals("...")) {
                    response = OpenAIClient.askGPT(text);
                    source = Source.GPT;
                }

            } catch (Exception ex) {
                response = "⚠️ Error: Could not get response.";
                source = Source.SYSTEM;
                ex.printStackTrace();
            }

            String finalResponse = response;
            Source finalSource = source;
            SwingUtilities.invokeLater(() -> {
                removeLastMessage(); // remove "Typing..."
                appendMessage("ChatBot", finalResponse, finalSource);
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
        int qaBestScore = 0;

        // 1️⃣ Handle question as list
        if (qa.questionList != null && !qa.questionList.isEmpty()) {
            for (String q : qa.questionList) {
                String questionLower = q.toLowerCase().trim();

                // Exact match
                if (inputLower.equals(questionLower)) {
                    return qa.answer;
                }

                // Partial match
                String[] questionWords = questionLower.split("\\s+");
                int matchedWords = 0;
                for (String qw : questionWords) {
                    if (inputLower.contains(qw)) matchedWords++;
                }

                qaBestScore = Math.max(qaBestScore, matchedWords);
            }
        } else {
            // fallback if question is still a string
            String questionLower = qa.question.toLowerCase().trim();

            if (inputLower.equals(questionLower)) return qa.answer;

            String[] questionWords = questionLower.split("\\s+");
            int matchedWords = 0;
            for (String qw : questionWords) {
                if (inputLower.contains(qw)) matchedWords++;
            }
            qaBestScore = matchedWords;
        }

        // Keyword match
        int keywordScore = 0;
        if (qa.keywords != null) {
            for (String kw : qa.keywords) {
                if (inputLower.contains(kw.toLowerCase())) keywordScore++;
            }
        }

        int totalScore = qaBestScore + keywordScore;

        if (totalScore > bestScore) {
            bestScore = totalScore;
            bestMatch = qa;
        }
    }

    if (bestMatch != null && bestScore > 0) {
        return bestMatch.answer;
    }

    return null;
}





    // ================= APPEND MESSAGE =================
    private void appendMessage(String sender, String msg, Source source) {
        ChatMessage chatMessage = new ChatMessage(sender, msg, source);
        messages.add(chatMessage);

        JPanel bubble = new JPanel();
        bubble.setLayout(new BoxLayout(bubble, BoxLayout.Y_AXIS));
        bubble.setOpaque(false);

        JTextArea textArea = new JTextArea(msg);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textArea.setFont(new Font("Inter", Font.PLAIN, 14));
        textArea.setOpaque(true);

        // Set colors based on source
        switch (source) {
            case USER -> {
                textArea.setBackground(new Color(70, 130, 180, 220));
                textArea.setForeground(Color.WHITE);
                bubble.setAlignmentX(Component.RIGHT_ALIGNMENT);
            }
            case JSON -> {
                textArea.setBackground(new Color(180, 255, 180, 220));
                textArea.setForeground(Color.BLACK);
                bubble.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            case CHATTERBOT -> {
                textArea.setBackground(new Color(180, 220, 255, 220));
                textArea.setForeground(Color.BLACK);
                bubble.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            case GPT -> {
                textArea.setBackground(new Color(255, 230, 120, 220));
                textArea.setForeground(Color.BLACK);
                bubble.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
            case SYSTEM -> {
                textArea.setBackground(new Color(220, 220, 220, 220));
                textArea.setForeground(Color.BLACK);
                bubble.setAlignmentX(Component.LEFT_ALIGNMENT);
            }
        }

        textArea.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        bubble.add(textArea);
        chatPanel.add(bubble);
        chatPanel.add(Box.createVerticalStrut(5));

        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
        chatPanel.revalidate();
        chatPanel.repaint();
    }

    // ================= REMOVE LAST MESSAGE =================
    private void removeLastMessage() {
        if (chatPanel.getComponentCount() > 0) {
            chatPanel.remove(chatPanel.getComponentCount() - 1);
            chatPanel.revalidate();
            chatPanel.repaint();
        }
    }

    // ================= CHAT MESSAGE MODEL =================
    public static class ChatMessage {
        String sender;
        String message;
        Source source;

        public ChatMessage(String sender, String message, Source source) {
            this.sender = sender;
            this.message = message;
            this.source = source;
        }
    }

    // ================= MESSAGE SOURCE =================
    public enum Source {
        USER, JSON, CHATTERBOT, GPT, SYSTEM
    }

   
}
