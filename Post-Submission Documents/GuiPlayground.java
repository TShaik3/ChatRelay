import java.awt.*;
import javax.swing.*;

public class GuiPlayground extends JFrame {
    // private final JSplitPane splitPane;
    // private final JPanel leftPanel;
    // private final JPanel rightPanel;
    // private final JLabel nameLabel;
    // private final JScrollPane privateChats;
    // private final JScrollPane teamChats;
    // private final JLabel chatInfo;
    // private final JPanel messagesPanel;  // Container for chat messages
    // private final JScrollPane listOfMessages;
    // private final JPanel inputPanel;
    // private final JTextField textField;
    // private final JButton sendButton;
  
    // public GuiPlayground() {
    //     setTitle("Chat Relay");
    //     setDefaultCloseOperation(EXIT_ON_CLOSE);

    //     splitPane = new JSplitPane();
    //     leftPanel = new JPanel();
    //     rightPanel = new JPanel();

    //     nameLabel = new JLabel("First Last");
    //     privateChats = new JScrollPane();
    //     teamChats = new JScrollPane();

    //     chatInfo = new JLabel("Team Name - Members List");
    //     messagesPanel = new JPanel();
    //     messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
    //     listOfMessages = new JScrollPane(messagesPanel);

    //     inputPanel = new JPanel();
    //     textField = new JTextField();
    //     sendButton = new JButton("Send");

    //     setPreferredSize(new Dimension(1000, 1000));
    //     getContentPane().setLayout(new GridLayout());
    //     getContentPane().add(splitPane);

    //     splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
    //     splitPane.setDividerLocation(200);
    //     splitPane.setRightComponent(rightPanel);
    //     splitPane.setLeftComponent(leftPanel);

    //     leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
    //     leftPanel.add(nameLabel);
    //     nameLabel.setPreferredSize(new Dimension(200, 50));

    //     JPanel buttonPanel = new JPanel();
    //     buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

    //     String[] names = {"Zoheb", "Talhah", "Shawn", "Kenny"};
    //     for (String name : names) {
    //         JButton b = new JButton();
    //         b.setLayout(new GridLayout(2,2));
    //         JLabel label1 = new JLabel("Your");
    //         JLabel label2 = new JLabel("Name");
    //         b.add(label1);
    //         b.add(label2);
    //         buttonPanel.add(b);
    //         // JButton nameButton = new JButton(name);
    //         // nameButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    //         // buttonPanel.add(nameButton);
    //     }

    //     privateChats.setViewportView(buttonPanel);
    //     leftPanel.add(privateChats);
    //     leftPanel.add(teamChats);

    //     rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
    //     rightPanel.add(chatInfo);
    //     chatInfo.setPreferredSize(new Dimension(600, 50));
    //     rightPanel.add(listOfMessages);
    //     rightPanel.add(inputPanel);

    //     inputPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));
    //     inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));
    //     inputPanel.add(textField);
    //     inputPanel.add(sendButton);

    //     addChatMessage("Zoheb", "10:15 AM", "Hello!");
    //     addChatMessage("You", "10:16 AM", "Hi!");
    //     addChatMessage("Zoheb", "10:17 AM", "Is this a test?");
    //     addChatMessage("You", "10:18 AM", "This is a test.");

    //     pack();
    // }

    // private void addChatMessage(String sender, String time, String messageText) {
    //     JPanel messagePane = new JPanel();
    //     messagePane.setLayout(new BoxLayout(messagePane, BoxLayout.Y_AXIS));
    //     messagePane.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
    //     messagePane.setAlignmentX(Component.LEFT_ALIGNMENT);
    //     messagePane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

    //     JLabel headerLabel = new JLabel(sender + " - " + time);
    //     headerLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
    //     headerLabel.setHorizontalAlignment(SwingConstants.LEFT);
    //     headerLabel.setFont(headerLabel.getFont().deriveFont(Font.BOLD));
    //     messagePane.add(headerLabel);

    //     JTextArea messageArea = new JTextArea(messageText);
    //     messageArea.setEditable(false);
    //     messageArea.setLineWrap(true);
    //     messageArea.setWrapStyleWord(true);
    //     messageArea.setBackground(null);
    //     messagePane.add(messageArea);

    //     // Add some spacing
    //     messagePane.add(Box.createRigidArea(new Dimension(0, 10)));

    //     messagesPanel.add(messagePane);
    // }

    public void ITAdmin() {
        //EventQueue.invokeLater(() -> new GuiPlayground().setVisible(true));
        
        // JFrame frame = new JFrame("Login Pane");
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(500, 300);
        // frame.setLayout(null);
        // frame.setResizable(false);

        // // Outer Panel
        // JPanel outerPanel = new JPanel();
        // outerPanel.setLayout(null);
        // outerPanel.setBounds(20, 20, 440, 220);
        // outerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        // frame.add(outerPanel);

        // // App Icon panel (Chat Relay Icon placeholder)
        // JPanel iconPanel = new JPanel();
        // iconPanel.setBounds(20, 30, 130, 130);
        // iconPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        // JLabel iconLabel = new JLabel("<html><center>Chat<br>Relay<br>Icon</center></html>", SwingConstants.CENTER);
        // iconLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        // iconPanel.add(iconLabel);
        // outerPanel.add(iconPanel);

        // // Login Label
        // JLabel loginLabel = new JLabel("Sign into your Account");
        // loginLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        // loginLabel.setBounds(170, 20, 200, 30);
        // outerPanel.add(loginLabel);

        // // Username Field
        // JTextField usernameField = new JTextField("Username");
        // usernameField.setBounds(170, 60, 240, 30);
        // outerPanel.add(usernameField);

        // // Password Field
        // JTextField passwordField = new JPasswordField("Password");
        // passwordField.setBounds(170, 100, 240, 30);
        // outerPanel.add(passwordField);

        // // Login Button
        // JButton loginButton = new JButton("Login");
        // loginButton.setBounds(170, 150, 100, 30);
        // outerPanel.add(loginButton);

        // // Cancel Button
        // JButton cancelButton = new JButton("Cancel");
        // cancelButton.setBounds(310, 150, 100, 30);
        // outerPanel.add(cancelButton);

        // usernameField.addFocusListener(new FocusAdapter() {
        // 	@Override
        // 	public void focusGained(FocusEvent e) {
        // 		if(usernameField.getText().equals("Username")) {
        // 			usernameField.setText("");
        // 		} else {
        // 			usernameField.setText(usernameField.getText());
        // 		}
        // 	}
        	
        // 	@Override
        // 	public void focusLost(FocusEvent e) {
        // 		if (usernameField.getText().equals("Username") || usernameField.getText().length() == 0) {
        // 			usernameField.setText("Username");
        // 			usernameField.setForeground(Color.GRAY);
        // 		} else {
        // 			usernameField.setText(usernameField.getText());
        // 			usernameField.setForeground(Color.BLACK);
        // 		}
        // 	}
        // });
        
        // passwordField.addFocusListener(new FocusAdapter() {
        // 	@Override
        // 	public void focusGained(FocusEvent e) {
        // 		if(passwordField.getText().equals("Password")) {
        // 			passwordField.setText("");
        // 		} else {
        // 			passwordField.setText(passwordField.getText());
        // 		}
        // 	}
        	
        // 	@Override
        // 	public void focusLost(FocusEvent e) {
        // 		if (passwordField.getText().equals("Password") || passwordField.getText().length() == 0) {
        // 			passwordField.setText("Password");
        // 			passwordField.setForeground(Color.GRAY);
        // 		} else {
        // 			passwordField.setText(passwordField.getText());
        // 			passwordField.setForeground(Color.BLACK);
        // 		}
        // 	}
        // });

        // // Show frame
        // frame.setVisible(true);

        // JFrame frame = new JFrame("Chat Application");
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(900, 600);
        // frame.setLayout(new BorderLayout());

        // // Top Panel (User name and Chat title)
        // JPanel topPanel = new JPanel(new BorderLayout());
        // JLabel userLabel = new JLabel("Full Name");
        // JButton addButton = new JButton("+");
        // JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // userInfoPanel.add(userLabel);
        // userInfoPanel.add(addButton);

        // JLabel chatTitleLabel = new JLabel("Team/Chat Name • Member List", SwingConstants.CENTER);
        // topPanel.add(userInfoPanel, BorderLayout.WEST);
        // topPanel.add(chatTitleLabel, BorderLayout.CENTER);

        // frame.add(topPanel, BorderLayout.NORTH);

        // // Left Panel (Private Chats and Group Chats)
        // JPanel leftPanel = new JPanel();
        // leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // // Private Chats title
        // JLabel privateChatsTitle = new JLabel("Private Chats");
        // leftPanel.add(privateChatsTitle);

        // // Private Chats list (in a scroll pane)
        // JPanel privateChatsList = new JPanel();
        // privateChatsList.setLayout(new BoxLayout(privateChatsList, BoxLayout.Y_AXIS));
        // for (int i = 0; i < 4; i++) {
        //     JButton chatButton = new JButton("<html>Name<br>Members List<br>Last Message Time</html>");
        //     privateChatsList.add(chatButton);
        // }
        // JScrollPane privateScroll = new JScrollPane(privateChatsList);
        // privateScroll.setPreferredSize(new Dimension(200, 150));
        // leftPanel.add(privateScroll);

        // // Group Chats title
        // JLabel groupChatsTitle = new JLabel("Group Chats");
        // leftPanel.add(groupChatsTitle);

        // // Group Chats list (in a scroll pane)
        // JPanel groupChatsList = new JPanel();
        // groupChatsList.setLayout(new BoxLayout(groupChatsList, BoxLayout.Y_AXIS));
        // for (int i = 0; i < 3; i++) {
        //     JButton groupButton = new JButton("<html>Name<br>Members List<br>Last Message Time</html>");
        //     groupChatsList.add(groupButton);
        // }
        // JScrollPane groupScroll = new JScrollPane(groupChatsList);
        // groupScroll.setPreferredSize(new Dimension(200, 150));
        // leftPanel.add(groupScroll);

        // // Right Panel (Chat messages)
        // JPanel rightPanel = new JPanel();
        // rightPanel.setLayout(new BorderLayout());

        // // Chat messages list
        // JPanel chatMessagesPanel = new JPanel();
        // chatMessagesPanel.setLayout(new BoxLayout(chatMessagesPanel, BoxLayout.Y_AXIS));
        // for (int i = 0; i < 7; i++) {
        //     JPanel messagePanel = new JPanel(new BorderLayout());
        //     JLabel messageLabel = new JLabel("<html>Name • Time<br>Message</html>");
        //     messagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //     messagePanel.add(messageLabel, BorderLayout.CENTER);
        //     chatMessagesPanel.add(messagePanel);
        // }
        // JScrollPane chatScroll = new JScrollPane(chatMessagesPanel);
        // rightPanel.add(chatScroll, BorderLayout.CENTER);

        // // Message input area
        // JPanel messageInputPanel = new JPanel(new BorderLayout());
        // JTextField messageField = new JTextField();
        // JButton sendButton = new JButton("Send");
        // messageInputPanel.add(messageField, BorderLayout.CENTER);
        // messageInputPanel.add(sendButton, BorderLayout.EAST);
        // rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // // Split Pane (Left - Right)
        // JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        // splitPane.setDividerLocation(250);
        // frame.add(splitPane, BorderLayout.CENTER);

        // // Show frame
        // frame.setVisible(true);

        // JFrame frame = new JFrame("Chat Application - IT Admin View");
        // frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // frame.setSize(900, 600);
        // frame.setLayout(new BorderLayout());

        // // Top Panel (Role, User name and Chat title)
        // JPanel topPanel = new JPanel(new BorderLayout());

        // JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        // JLabel fullNameLabel = new JLabel("Full Name");
        // JButton addButton = new JButton("+");
        // fullNameLabel.setForeground(Color.RED);
        // userInfoPanel.add(fullNameLabel);
        // userInfoPanel.add(addButton);

        // JLabel chatTitleLabel = new JLabel("Team/Chat Name • Member List", SwingConstants.CENTER);

        // topPanel.add(userInfoPanel, BorderLayout.CENTER);
        // topPanel.add(chatTitleLabel, BorderLayout.SOUTH);

        // frame.add(topPanel, BorderLayout.NORTH);

        // // Left Panel (IT Badge + Private + Group Chats)
        // JPanel leftPanel = new JPanel();
        // leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // // IT Badge
        // JLabel badgeLabel = new JLabel("IT Badge");
        // badgeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        // leftPanel.add(badgeLabel);

        // // Private Chats Title
        // JLabel privateChatsTitle = new JLabel("Private Chats");
        // privateChatsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        // leftPanel.add(privateChatsTitle);

        // // Private Chats list
        // JPanel privateChatsList = new JPanel();
        // privateChatsList.setLayout(new BoxLayout(privateChatsList, BoxLayout.Y_AXIS));
        // for (int i = 0; i < 6; i++) {
        //     JButton chatButton = new JButton("<html>Name<br>Members List<br>Last Message Time</html>");
        //     if (i == 2 || i == 3) { // Access to all users - special
        //         chatButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        //     }
        //     privateChatsList.add(chatButton);
        // }
        // JScrollPane privateScroll = new JScrollPane(privateChatsList);
        // privateScroll.setPreferredSize(new Dimension(200, 200));
        // leftPanel.add(privateScroll);

        // // Group Chats Title
        // JLabel groupChatsTitle = new JLabel("Group Chats");
        // groupChatsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        // leftPanel.add(groupChatsTitle);

        // // Group Chats list
        // JPanel groupChatsList = new JPanel();
        // groupChatsList.setLayout(new BoxLayout(groupChatsList, BoxLayout.Y_AXIS));
        // for (int i = 0; i < 3; i++) {
        //     JButton groupButton = new JButton("<html>Name<br>Members List<br>Last Message Time</html>");
        //     if (i == 2) { // Access to all chats - special
        //         groupButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        //     }
        //     groupChatsList.add(groupButton);
        // }
        // JScrollPane groupScroll = new JScrollPane(groupChatsList);
        // groupScroll.setPreferredSize(new Dimension(200, 150));
        // leftPanel.add(groupScroll);

        // // Right Panel (Chat messages)
        // JPanel rightPanel = new JPanel();
        // rightPanel.setLayout(new BorderLayout());

        // // Chat messages list
        // JPanel chatMessagesPanel = new JPanel();
        // chatMessagesPanel.setLayout(new BoxLayout(chatMessagesPanel, BoxLayout.Y_AXIS));
        // for (int i = 0; i < 7; i++) {
        //     JPanel messagePanel = new JPanel(new BorderLayout());
        //     JLabel messageLabel = new JLabel("<html>Name • Time<br>Message</html>");
        //     messagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        //     messagePanel.add(messageLabel, BorderLayout.CENTER);
        //     chatMessagesPanel.add(messagePanel);
        // }
        // JScrollPane chatScroll = new JScrollPane(chatMessagesPanel);
        // rightPanel.add(chatScroll, BorderLayout.CENTER);

        // // Message input area
        // JPanel messageInputPanel = new JPanel(new BorderLayout());
        // JTextField messageField = new JTextField();
        // JButton sendButton = new JButton("Send");
        // messageInputPanel.add(messageField, BorderLayout.CENTER);
        // messageInputPanel.add(sendButton, BorderLayout.EAST);
        // rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // // Split Pane (Left - Right)
        // JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        // splitPane.setDividerLocation(250);
        // frame.add(splitPane, BorderLayout.CENTER);

        // // Show frame
        // frame.setVisible(true);
    }

    public static void main(String args[]) {
        

        
    }

    
}
