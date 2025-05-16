import java.awt.*;
import javax.swing.*;

public class GuiPlayground extends JFrame {
    private final JSplitPane splitPane;
    private final JPanel leftPanel;
    private final JPanel rightPanel;
    private final JLabel nameLabel;
    private final JScrollPane privateChats;
    private final JScrollPane teamChats;
    private final JLabel chatInfo;
    private final JPanel messagesPanel;  // Container for chat messages
    private final JScrollPane listOfMessages;
    private final JPanel inputPanel;
    private final JTextField textField;
    private final JButton sendButton;
  
    public GuiPlayground() {
        setTitle("Chat Relay");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        splitPane = new JSplitPane();
        leftPanel = new JPanel();
        rightPanel = new JPanel();

        nameLabel = new JLabel("First Last");
        privateChats = new JScrollPane();
        teamChats = new JScrollPane();

        chatInfo = new JLabel("Team Name - Members List");
        messagesPanel = new JPanel();
        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        listOfMessages = new JScrollPane(messagesPanel);

        inputPanel = new JPanel();
        textField = new JTextField();
        sendButton = new JButton("Send");

        setPreferredSize(new Dimension(1000, 1000));
        getContentPane().setLayout(new GridLayout());
        getContentPane().add(splitPane);

        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(200);
        splitPane.setRightComponent(rightPanel);
        splitPane.setLeftComponent(leftPanel);

        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.add(nameLabel);
        nameLabel.setPreferredSize(new Dimension(200, 50));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

        String[] names = {"Zoheb", "Talhah", "Shawn", "Kenny"};
        for (String name : names) {
            JButton b = new JButton();
            b.setLayout(new GridLayout(2,2));
            JLabel label1 = new JLabel("Your");
            JLabel label2 = new JLabel("Name");
            b.add(label1);
            b.add(label2);
            buttonPanel.add(b);
            // JButton nameButton = new JButton(name);
            // nameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            // buttonPanel.add(nameButton);
        }

        privateChats.setViewportView(buttonPanel);
        leftPanel.add(privateChats);
        leftPanel.add(teamChats);

        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(chatInfo);
        chatInfo.setPreferredSize(new Dimension(600, 50));
        rightPanel.add(listOfMessages);
        rightPanel.add(inputPanel);

        inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
        inputPanel.add(textField);
        inputPanel.add(sendButton);

        // addChatMessage("Zoheb", "10:15 AM", "Hello!");
        // addChatMessage("You", "10:16 AM", "Hi!");
        // addChatMessage("Zoheb", "10:17 AM", "Is this a test?");
        // addChatMessage("You", "10:18 AM", "This is a test.");

        pack();
    }

    private void addChatMessage(String sender, String time, String messageText) {
        JPanel messagePane = new JPanel();
        messagePane.setLayout(new BoxLayout(messagePane, BoxLayout.Y_AXIS));
        messagePane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        messagePane.setAlignmentX(Component.LEFT_ALIGNMENT);
        messagePane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JLabel headerLabel = new JLabel(sender + " - " + time);
        headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerLabel.setHorizontalAlignment(SwingConstants.LEFT);
        headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
        messagePane.add(headerLabel);

        JTextArea messageArea = new JTextArea(messageText);
        messageArea.setEditable(false);
        messageArea.setLineWrap(true);
        messageArea.setWrapStyleWord(true);
        messageArea.setBackground(null);
        messagePane.add(messageArea);

        // Add some spacing
        messagePane.add(Box.createRigidArea(new Dimension(0, 10)));

        messagesPanel.add(messagePane);
    }

    public static void main(String args[]) {
        EventQueue.invokeLater(() -> new GuiPlayground().setVisible(true));
    }
}
