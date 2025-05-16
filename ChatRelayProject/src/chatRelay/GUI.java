package chatRelay;

import java.awt.*;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import javax.swing.*;

public class GUI extends JFrame implements Runnable {
    private Client client;
    private JFrame frame;
    private JPanel privateChatsList;
    private JPanel groupChatsList;
    private JPanel chatMessagesPanel;
    private JScrollPane chatScroll;
    private HashMap<JButton, Chat> chatMap;

    public GUI(Client client) {
    	this.client = client;
    	chatMap = new HashMap<>();
    }
    
    public JFrame getFrame() {
    	return frame;
    }

	@Override
	public void run() {
		loginPane();
		try { 
			synchronized (frame) {
				frame.wait();
			}
		} catch (InterruptedException e) { e.printStackTrace(); }
		if (client.getAdminStatus()) {
			buildITGUI();
		} else {
			buildUserGUI();
		}
	}
	
	private void loginPane() {
		frame = new JFrame("Login Pane");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(null);
        frame.setResizable(false);

        JPanel outerPanel = new JPanel();
        outerPanel.setLayout(null);
        outerPanel.setBounds(20, 20, 440, 220);
        outerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        frame.add(outerPanel);

        JLabel loginLabel = new JLabel("Sign into your Account");
        loginLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        loginLabel.setBounds(110, 20, 200, 30);
        outerPanel.add(loginLabel);

        JTextField usernameField = new JTextField();
        usernameField.setBounds(110, 60, 240, 30);
        outerPanel.add(usernameField);

        JTextField passwordField = new JPasswordField("Password");
        passwordField.setBounds(110, 100, 240, 30);
        outerPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(110, 150, 100, 30);
        outerPanel.add(loginButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBounds(250, 150, 100, 30);
        outerPanel.add(cancelButton);
        
        usernameField.addFocusListener(new FocusAdapter() {
        	@Override
        	public void focusGained(FocusEvent e) {
        		if(usernameField.getText().equals("Username")) {
        			usernameField.setText("");
        		} else {
        			usernameField.setText(usernameField.getText());
        		}
        	}
        	
        	@Override
        	public void focusLost(FocusEvent e) {
        		if (usernameField.getText().equals("Username") || usernameField.getText().length() == 0) {
        			usernameField.setText("Username");
        			usernameField.setForeground(Color.GRAY);
        		} else {
        			usernameField.setText(usernameField.getText());
        			usernameField.setForeground(Color.BLACK);
        		}
        	}
        });
        
        passwordField.addFocusListener(new FocusAdapter() {
        	@Override
        	public void focusGained(FocusEvent e) {
        		if(passwordField.getText().equals("Password")) {
        			passwordField.setText("");
        		} else {
        			passwordField.setText(passwordField.getText());
        		}
        	}
        	
        	@Override
        	public void focusLost(FocusEvent e) {
        		if (passwordField.getText().equals("Password") || passwordField.getText().length() == 0) {
        			passwordField.setText("Password");
        			passwordField.setForeground(Color.GRAY);
        		} else {
        			passwordField.setText(passwordField.getText());
        			passwordField.setForeground(Color.BLACK);
        		}
        	}
        });
        
        loginButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		client.login(usernameField.getText(), passwordField.getText());
        		frame.dispose();
        	}
        });
        
        cancelButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		System.exit(0);
        	}
        });
        
        passwordField.addKeyListener(new KeyAdapter() {
        	@Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                	client.login(usernameField.getText(), passwordField.getText());
            		frame.dispose();
                }
            }
        });

        frame.setVisible(true);
	}
	
	private void buildUserGUI() {
        frame = new JFrame("Chat Application");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLayout(new BorderLayout());

        // Top Panel (User name and Chat title)
        JPanel topPanel = new JPanel(new BorderLayout());
        
        JLabel userLabel = new JLabel(client.getThisUser().getFirstName() + " " + client.getThisUser().getLastName());
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addChatButton = new JButton("Create Chat"); // Opens create chat dialog
        JButton editChatButton = new JButton("Edit Chat");
        JButton logoutButton = new JButton("Logout");
        userInfoPanel.add(userLabel);
        userInfoPanel.add(addChatButton);
        userInfoPanel.add(editChatButton);
        userInfoPanel.add(logoutButton);

        JLabel chatTitleLabel = new JLabel("Team/Chat Name • Member List", SwingConstants.CENTER); // Updated with information
        topPanel.add(userInfoPanel, BorderLayout.WEST);
        topPanel.add(chatTitleLabel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);

        // Left Panel (Private Chats and Group Chats)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Private Chats title
        JLabel privateChatsTitle = new JLabel("Private Chats");
        leftPanel.add(privateChatsTitle);
        
        if (client.getChats() == null) {
        	try { 
    			synchronized (frame) {
    				frame.wait();
    			}
    		} catch (InterruptedException e) { e.printStackTrace(); }
        }

        // Private Chats list (in a scroll pane)
        JPanel privateChatsList = new JPanel();
        privateChatsList.setLayout(new BoxLayout(privateChatsList, BoxLayout.Y_AXIS));
        loadChatPanel(privateChatsList, true);
        JScrollPane privateScroll = new JScrollPane(privateChatsList);
        privateScroll.setPreferredSize(new Dimension(200, 150));
        leftPanel.add(privateScroll);

        // Group Chats title
        JLabel groupChatsTitle = new JLabel("Group Chats");
        leftPanel.add(groupChatsTitle);
        
        // Group Chats list (in a scroll pane)
        JPanel groupChatsList = new JPanel();
        groupChatsList.setLayout(new BoxLayout(groupChatsList, BoxLayout.Y_AXIS));
        loadChatPanel(groupChatsList, true);
        JScrollPane groupScroll = new JScrollPane(groupChatsList);
        groupScroll.setPreferredSize(new Dimension(200, 150));
        leftPanel.add(groupScroll);

        // Right Panel (Chat messages)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        // Chat messages list
        chatMessagesPanel = new JPanel();
        chatMessagesPanel.setName("panel");
        chatMessagesPanel.setLayout(new BoxLayout(chatMessagesPanel, BoxLayout.Y_AXIS));
        chatScroll = new JScrollPane(chatMessagesPanel);
        chatScroll.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        // Message input area
        JPanel messageInputPanel = new JPanel(new BorderLayout());
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        messageInputPanel.add(messageField, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);
        rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // Split Pane (Left - Right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(250);
        frame.add(splitPane, BorderLayout.CENTER);
        
        for (HashMap.Entry<JButton, Chat> entry : chatMap.entrySet()) {
        	JButton button = entry.getKey();
        	Chat chat = entry.getValue();
        	
        	button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String membersList = "";
					for (int i = 0; i < chat.getChatters().size(); i++) {
						AbstractUser user = chat.getChatters().get(i);
						membersList += user.getFirstName();
						if (i + 1 != chat.getChatters().size()) {
							membersList += ", ";
						}
					}
					chatTitleLabel.setText(chat.getRoomName() + "•" + membersList);
					loadMessagePanel(chatMessagesPanel, chat);
					chatMessagesPanel.setName(chat.getId());
					update(actionType.SUCCESS);
				}
        	});
        }
        
        addChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCreateChatDialog();
			}
        });
        
        sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!messageField.getText().equals("") && !chatMessagesPanel.getName().equals("panel")) {
					client.sendMessage(chatMessagesPanel.getName(), messageField.getText());
					messageField.setText("");
				}
			}
        });
        
        editChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!chatMessagesPanel.getName().equals("panel")) {
					editChatDialog(client.getChatById(chatMessagesPanel.getName()));
				}
			}
        });
        
        messageField.addKeyListener(new KeyAdapter() {
        	@Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !chatMessagesPanel.getName().equals("panel")) {
                	client.sendMessage(chatMessagesPanel.getName(), messageField.getText());
					messageField.setText("");
                }
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.logout();
				frame.dispose();
			}
        });

        frame.setVisible(true);
	}
	
	private void buildITGUI() {
		frame = new JFrame("Chat Application - IT Admin View");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 1000);
        frame.setLayout(new BorderLayout());

        // Top Panel (Role, User name and Chat title)
        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel fullNameLabel = new JLabel("<html>" + client.getThisUser().getFirstName() + " " + client.getThisUser().getLastName() + "<br></html>");
        JButton addChatButton = new JButton("Create Chat");
        JButton addUserButton = new JButton("Create User");
        JButton editUserButton = new JButton("Edit User");
        JButton editChatButton = new JButton("Edit Chat");
        JButton logoutButton = new JButton("Logout");
        fullNameLabel.setForeground(Color.RED);
        userInfoPanel.add(fullNameLabel);
        userInfoPanel.add(addChatButton);
        userInfoPanel.add(addUserButton);
        userInfoPanel.add(editUserButton);
        userInfoPanel.add(editChatButton);
        userInfoPanel.add(logoutButton); 

        JLabel chatTitleLabel = new JLabel("Team/Chat Name • Member List", SwingConstants.CENTER);

        topPanel.add(userInfoPanel, BorderLayout.WEST);
        topPanel.add(chatTitleLabel, BorderLayout.SOUTH);

        frame.add(topPanel, BorderLayout.NORTH);

        // Left Panel (IT Badge + Private + Group Chats)
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));

        // Private Chats Title
        JLabel privateChatsTitle = new JLabel("Private Chats");
        privateChatsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(privateChatsTitle);

        if (client.getChats() == null) {
        	try { 
    			synchronized (frame) {
    				frame.wait();
    			}
    		} catch (InterruptedException e) { e.printStackTrace(); }
        }
        
        // Private Chats list
        privateChatsList = new JPanel();
        privateChatsList.setLayout(new BoxLayout(privateChatsList, BoxLayout.Y_AXIS));
        loadChatPanel(privateChatsList, true);
        JScrollPane privateScroll = new JScrollPane(privateChatsList);
        privateScroll.setPreferredSize(new Dimension(200, 200));
        leftPanel.add(privateScroll);

        // Group Chats Title
        JLabel groupChatsTitle = new JLabel("Group Chats");
        groupChatsTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        leftPanel.add(groupChatsTitle);

        // Group Chats list
        groupChatsList = new JPanel();
        groupChatsList.setLayout(new BoxLayout(groupChatsList, BoxLayout.Y_AXIS));
        loadChatPanel(groupChatsList, false);
        JScrollPane groupScroll = new JScrollPane(groupChatsList);
        groupScroll.setPreferredSize(new Dimension(200, 150));
        leftPanel.add(groupScroll);

        // Right Panel (Chat messages)
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());

        // Chat messages list
        chatMessagesPanel = new JPanel();
        chatMessagesPanel.setName("panel");
        chatMessagesPanel.setLayout(new BoxLayout(chatMessagesPanel, BoxLayout.Y_AXIS));
        chatScroll = new JScrollPane(chatMessagesPanel);
        chatScroll.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        // Message input area
        JPanel messageInputPanel = new JPanel(new BorderLayout());
        JTextField messageField = new JTextField();
        JButton sendButton = new JButton("Send");
        JButton saveButton = new JButton("Save");
        messageInputPanel.add(messageField, BorderLayout.CENTER);
        messageInputPanel.add(sendButton, BorderLayout.EAST);
        messageInputPanel.add(saveButton, BorderLayout.WEST);
        rightPanel.add(messageInputPanel, BorderLayout.SOUTH);

        // Split Pane (Left - Right)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(250);
        frame.add(splitPane, BorderLayout.CENTER);

        for (HashMap.Entry<JButton, Chat> entry : chatMap.entrySet()) {
        	JButton button = entry.getKey();
        	Chat chat = entry.getValue();
        	
        	button.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					chatMessagesPanel.removeAll();
					String membersList = "";
					for (int i = 0; i < chat.getChatters().size(); i++) {
						AbstractUser user = chat.getChatters().get(i);
						membersList += user.getFirstName();
						if (i + 1 != chat.getChatters().size()) {
							membersList += ", ";
						}
					}
					chatTitleLabel.setText(chat.getRoomName() + " • " + membersList);
					loadMessagePanel(chatMessagesPanel, chat);
					chatMessagesPanel.setName(chat.getId());
					update(actionType.SUCCESS);
				}
        	});
        }
        
        addChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showCreateChatDialog();
			}
        });
        
        addUserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createUserPane();
			}
        });
        
        editUserButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				editUserDialog();
			}
        });
        
        editChatButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!chatMessagesPanel.getName().equals("panel")) {
					editChatDialog(client.getChatById(chatMessagesPanel.getName()));
				}
			}
        });
        
        sendButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!messageField.getText().equals("") && !chatMessagesPanel.getName().equals("panel")) {
					client.sendMessage(chatMessagesPanel.getName(), messageField.getText());
					messageField.setText("");
				}
			}
        });
        
        saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!chatMessagesPanel.getName().equals("panel")) {
					client.saveChatToTxt(client.getChatById(chatMessagesPanel.getName()));
				}
			}
        });
        
        messageField.addKeyListener(new KeyAdapter() {
        	@Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !chatMessagesPanel.getName().equals("panel")) {
                	client.sendMessage(chatMessagesPanel.getName(), messageField.getText());
					messageField.setText("");
                }
            }
        });
        
        logoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				client.logout();
				frame.dispose();
			}
        });
        
        frame.setVisible(true);
	}
	
	private void scrollToBottom(JScrollPane scrollPane) {
	    JScrollBar verticalBar = scrollPane.getVerticalScrollBar();
	    AdjustmentListener downScroller = new AdjustmentListener() {
	        @Override
	        public void adjustmentValueChanged(AdjustmentEvent e) {
	            Adjustable adjustable = e.getAdjustable();
	            adjustable.setValue(adjustable.getMaximum());
	            verticalBar.removeAdjustmentListener(this);
	        }
	    };
	    verticalBar.addAdjustmentListener(downScroller);
	}
	
	private void editChatDialog(Chat chat) {
		JDialog dialog = new JDialog(frame, "Edit Chat Pane", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        // Header Label
        JLabel headerLabel = new JLabel("Edit Chat", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        nameField.setText(chat.getRoomName());
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));

        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(10));

        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        DefaultListModel<String> listModel = new DefaultListModel<>();

        String[] allUsers = new String[client.getUsers().size()];
        for (int i = 0; i < client.getUsers().size(); i++) {
			AbstractUser user = client.getUsers().get(i);
			allUsers[i] = user.getFirstName() + " " + user.getLastName();
		}
        for (String user : allUsers) {
        	listModel.addElement(user);
        }

        JList<String> userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);

        // Filter logic
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterList(); }
            public void removeUpdate(DocumentEvent e) { filterList(); }
            public void changedUpdate(DocumentEvent e) { filterList(); }

            private void filterList() {
                String filter = searchField.getText().toLowerCase();
                listModel.clear();
                for (String user : allUsers) {
                    if (user.toLowerCase().contains(filter)) {
                        listModel.addElement(user);
                    }
                }
            }
        });
        
        for (AbstractUser user : chat.getChatters()) {
        	String name = user.getFirstName() + " " + user.getLastName();
        	userList.setSelectedValue(name, true);
        }
        
        formPanel.add(new JLabel("Search Users:"));
        formPanel.add(searchField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Select Users to Add:"));
        formPanel.add(userScrollPane);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {
            String chatName = nameField.getText().trim();
            if (chatName.isBlank()) { chatName = "Chat Room"; }
            if (!chatName.equals(chat.getRoomName())) {
                client.renameChat(chat.getId(), chatName);
            }
            List<String> namedUsers = userList.getSelectedValuesList();
            
            List<AbstractUser> selectedUsers = new ArrayList<>();
            selectedUsers.add(client.getThisUser());
            for (int i = 1; i < namedUsers.size(); i++) {
            	selectedUsers.add(searchForUser(namedUsers.get(i)));
            }
            
            for(AbstractUser user : selectedUsers) {
            	if (!chat.getChatters().contains(user)) {
            		client.addUserToChat(user.getId(), chat.getId());
            	}
            }
            
            for (AbstractUser user : chat.getChatters()) {
            	if (!selectedUsers.contains(user)) {
            		client.removeUserFromChat(user.getId(), chat.getId());
            	}
            }
            dialog.dispose();
        });

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        dialog.setVisible(true);
	}
	
	private void editUserDialog() {
		JDialog dialog = new JDialog(frame, "Edit A User", true);
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setLayout(new BorderLayout(10, 10));

	    // Title
	    JLabel titleLabel = new JLabel("Edit A User", SwingConstants.CENTER);
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
	    dialog.add(titleLabel, BorderLayout.NORTH);

	    // Table Data
	    String[] columnNames = {"IsDisabled", "Names"};
	    Object[][] data = new Object[client.getUsers().size()][2];
	    
	    for (int i = 0; i < client.getUsers().size(); i++) {
	    	data[i][0] = client.getUsers().get(i).isDisabled();
	    	data[i][1] = client.getUsers().get(i).getFirstName() + " " + client.getUsers().get(i).getLastName();
	    }

	    DefaultTableModel model = new DefaultTableModel(data, columnNames) {
	        @Override
	        public Class<?> getColumnClass(int columnIndex) {
	            return columnIndex == 0 ? Boolean.class : String.class;
	        }

	        @Override
	        public boolean isCellEditable(int row, int column) {
	            return column == 0; // Only allow editing the checkbox
	        }
	    };

	    JTable table = new JTable(model);
	    table.setRowHeight(25);
	    JScrollPane scrollPane = new JScrollPane(table);
	    dialog.add(scrollPane, BorderLayout.CENTER);

	    // Buttons
	    JPanel buttonPanel = new JPanel();
	    JButton saveButton = new JButton("Save");
	    JButton cancelButton = new JButton("Cancel");

	    saveButton.addActionListener(e -> {
	        for (int i = 0; i < model.getRowCount(); i++) {
	        	String name = (String) model.getValueAt(i, 1);
	            boolean isDisabled = (Boolean) model.getValueAt(i, 0);
	            AbstractUser user = searchForUser(name);
	            if (user.isDisabled() != isDisabled) {
	            	client.updateUser(user.getId(), isDisabled);
	            }
	        }
	        dialog.dispose();
	    });

	    cancelButton.addActionListener(e -> dialog.dispose());

	    buttonPanel.add(saveButton);
	    buttonPanel.add(cancelButton);
	    dialog.add(buttonPanel, BorderLayout.SOUTH);

	    dialog.pack();
	    dialog.setLocationRelativeTo(frame);
	    dialog.setVisible(true);
	}
	
	private void showCreateChatDialog() { // Needs work
		JDialog dialog = new JDialog(frame, "Create Chat Pane", true);
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        // Header Label
        JLabel headerLabel = new JLabel("Create Chat", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dialog.add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();
        nameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        nameField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));

        formPanel.add(nameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        formPanel.add(nameField);
        formPanel.add(Box.createVerticalStrut(10));

        JTextField searchField = new JTextField();
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        searchField.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        DefaultListModel<String> listModel = new DefaultListModel<>();

        String[] allUsers = new String[client.getUsers().size()];
        for (int i = 0; i < client.getUsers().size(); i++) {
			AbstractUser user = client.getUsers().get(i);
			allUsers[i] = user.getFirstName() + " " + user.getLastName();
		}
        for (String user : allUsers) listModel.addElement(user);

        JList<String> userList = new JList<>(listModel);
        userList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        JScrollPane userScrollPane = new JScrollPane(userList);

        // Filter logic
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterList(); }
            public void removeUpdate(DocumentEvent e) { filterList(); }
            public void changedUpdate(DocumentEvent e) { filterList(); }

            private void filterList() {
                String filter = searchField.getText().toLowerCase();
                listModel.clear();
                for (String user : allUsers) {
                    if (user.toLowerCase().contains(filter)) {
                        listModel.addElement(user);
                    }
                }
            }
        });
        formPanel.add(new JLabel("Search Users:"));
        formPanel.add(searchField);
        formPanel.add(Box.createVerticalStrut(10));
        formPanel.add(new JLabel("Select Users to Add:"));
        formPanel.add(userScrollPane);

        dialog.add(formPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("Create");
        JButton cancelButton = new JButton("Cancel");

        cancelButton.addActionListener(e -> dialog.dispose());

        createButton.addActionListener(e -> {
            String chatName = nameField.getText().trim();
            if (chatName.isBlank()) { chatName = "Chat Room"; }
            
            boolean added = false;
            int index = 0;
            List<String> namedUsers = userList.getSelectedValuesList();
            for (String name : namedUsers) {
            	if(searchForUser(name).getId().equals(client.getThisUserId())) {
            		added = true;
            	}
            	namedUsers.set(index, searchForUser(name).getId());
            	index++;
            }
            if (!added && !client.getAdminStatus()) {
            	namedUsers.add(client.getThisUserId());
            }
            
            String[] selectedUsers = new String[namedUsers.size()];
            for (int i = 0; i < namedUsers.size(); i++) {
            	selectedUsers[i] = namedUsers.get(i);
            }
            
            client.createChat(selectedUsers, chatName, true);
            dialog.dispose();
        });

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK, 2, true));
        dialog.setVisible(true);
    }
	
	private void createUserPane() {
		JDialog dialog = new JDialog(frame, "Create User", true);
		dialog.setSize(300, 500);
	    dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	    dialog.setLayout(new BoxLayout(dialog.getContentPane(), BoxLayout.Y_AXIS));
	    dialog.setResizable(false);

	    // Title
	    JLabel titleLabel = new JLabel("Create User");
	    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
	    titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
	    dialog.add(Box.createVerticalStrut(10));
	    dialog.add(titleLabel);
	    dialog.add(Box.createVerticalStrut(20));

	    // Input Fields
	    JTextField firstNameField = new JTextField(15);
	    JTextField lastNameField = new JTextField(15);
	    JTextField usernameField = new JTextField(15);
	    JPasswordField passwordField = new JPasswordField(15);
	    JCheckBox isAdminBox = new JCheckBox("Is Admin?");

	    dialog.add(createLabeledField("First Name:", firstNameField));
	    dialog.add(createLabeledField("Last Name:", lastNameField));
	    dialog.add(createLabeledField("Username:", usernameField));
	    dialog.add(createLabeledField("Password:", passwordField));
	    dialog.add(Box.createVerticalStrut(10));
	    dialog.add(isAdminBox);
	    dialog.add(Box.createVerticalStrut(20));

	    // Buttons
	    JPanel buttonPanel = new JPanel();
	    JButton createButton = new JButton("Create");
	    JButton cancelButton = new JButton("Cancel");

	    createButton.addActionListener(e -> {
	        String first = firstNameField.getText();
	        String last = lastNameField.getText();
	        String user = usernameField.getText();
	        String pass = new String(passwordField.getPassword());
	        boolean isAdmin = isAdminBox.isSelected();
	        
	        client.createUser(user, pass, first, last, isAdmin);
	        dialog.dispose();
	    });

	    cancelButton.addActionListener(e -> dialog.dispose());

	    buttonPanel.add(createButton);
	    buttonPanel.add(cancelButton);
	    dialog.add(buttonPanel);
	    dialog.add(Box.createVerticalStrut(10));

	    dialog.pack();
	    dialog.setLocationRelativeTo(frame);
	    dialog.setVisible(true);
	}

	private JPanel createLabeledField(String label, JComponent field) {
	    JPanel panel = new JPanel(new BorderLayout());
	    panel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	    panel.add(new JLabel(label), BorderLayout.NORTH);
	    panel.add(field, BorderLayout.CENTER);
	    return panel;
	}
	
	public void showMessageDialog(actionType action) {
		switch (action) {
			case SUCCESS -> {
				JOptionPane.showMessageDialog(frame, "Success", "Information Dialog", JOptionPane.INFORMATION_MESSAGE);
			}
			case ERROR -> {
				JOptionPane.showMessageDialog(frame, "Error", "Information Dialog", JOptionPane.ERROR_MESSAGE);
			}
			default -> {
				break;
			}
		}
	}
	
	private void loadChatPanel(JPanel chatButtons, boolean isPrivate) {
		chatButtons.removeAll();
		if (isPrivate) {
			for (Chat chat : client.getChats()) {
	        	if (chat.getChatters().size() < 3) {
	        		String owner = chat.getOwner().getFirstName();
	        		String membersList = "";
					for (int i = 0; i < chat.getChatters().size(); i++) {
						AbstractUser user = chat.getChatters().get(i);
						membersList += user.getFirstName();
						if (i + 1 != chat.getChatters().size()) {
							membersList += ", ";
						}
					}
	            	String time = "No Messages";
	            	if (chat.getMessages().size() > 0) {
	            		long lastMessageTime = chat.getMessages().get(chat.getMessages().size() - 1).getCreatedAt();
	                	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	                	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-7"));
	                	Date date = new Date(lastMessageTime);
	                	time = dateFormat.format(date);
	            	}
	                JButton chatButton = new JButton("<html>" + owner + "<br>" + membersList + "<br>" + time + "</html>");
	                if (!checkForUser(chat)) {
	                	chatButton.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
	                }
	                chatMap.put(chatButton, chat);
	                chatButtons.add(chatButton);
	        	}
	        }
		} else {
	        for (Chat chat : client.getChats()) {
	        	if (chat.getChatters().size() >= 3) {
	        		String membersList = "";
					for (int i = 0; i < chat.getChatters().size(); i++) {
						AbstractUser user = chat.getChatters().get(i);
						membersList += user.getFirstName();
						if (i + 1 != chat.getChatters().size()) {
							membersList += ", ";
						}
					}
	            	String time = "No Messages";
	            	if (chat.getMessages().size() > 0) {
	            		long lastMessageTime = chat.getMessages().get(chat.getMessages().size() - 1).getCreatedAt();
	                	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
	                	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-7"));
	                	Date date = new Date(lastMessageTime);
	                	time = dateFormat.format(date);
	            	}
	                JButton groupButton = new JButton("<html>" + chat.getRoomName() + "<br>" + membersList + "<br>" + time + "</html>");
	                if (!checkForUser(chat)) {
	                	groupButton.setBorder(BorderFactory.createLineBorder(Color.RED, 1));
	                }
	                chatMap.put(groupButton, chat);
	                chatButtons.add(groupButton);
	        	}
	        }
		}
	}
	
	private boolean checkForUser(Chat chat) {
		for (AbstractUser user : chat.getChatters()) {
			if (user.getId().equals(client.getThisUser().getId())) {
				return true;
			}
		}
		return false;
	}
	
	private void loadMessagePanel(JPanel chatMessagePanel, Chat chat) {
		chatMessagePanel.removeAll();
    	for (Message message : chat.getMessages()) {
    		JPanel messagePanel = new JPanel(new BorderLayout());
    		messagePanel.setMinimumSize(new Dimension(chatMessagePanel.getWidth(), 50));
    		messagePanel.setMaximumSize(new Dimension(chatMessagePanel.getWidth(), 50));
    		String sender = message.getSender().getFirstName();
    		
    		long lastMessageTime = message.getCreatedAt();
        	DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        	dateFormat.setTimeZone(TimeZone.getTimeZone("GMT-7"));
        	Date date = new Date(lastMessageTime);
        	String time = dateFormat.format(date);
        	String content = message.getContent();
        	
            JLabel messageLabel = new JLabel("<html>" + sender + " • " + time + "<br><br>" + content + "<br></html>");
            messagePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            messagePanel.add(messageLabel, BorderLayout.CENTER);
            chatMessagePanel.add(messagePanel);
    	}
    	scrollToBottom(chatScroll);
	}
	
	private AbstractUser searchForUser(String name) {
		for (AbstractUser user : client.getUsers()) {
			String[] splitName = name.split(" ");
			if (splitName[0].equals(user.getFirstName()) && splitName[1].equals(user.getLastName())) {
				return user;
			}
		}
		return null;
	}
	
	public void update(actionType action) {
		switch (action) {
		case NEW_CHAT_BROADCAST:
			loadChatPanel(privateChatsList, true);
			loadChatPanel(groupChatsList, false);
			break;
		case NEW_MESSAGE_BROADCAST:
			loadMessagePanel(chatMessagesPanel, client.getLastChatSent());
			break;
		default:
			break;
	}
	frame.revalidate();
	frame.repaint();
	}
}
