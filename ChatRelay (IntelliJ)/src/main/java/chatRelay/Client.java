package chatRelay;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private Socket socket;
    private boolean isConnected;

    private String username;
    private Chat lastChatSent;
    
    private List<Chat> chats;
    private List<AbstractUser> users;
    private String userId;
    private Boolean isITAdmin;
    private AbstractUser thisUser;
    
    private ObjectOutputStream objectStream;
    private ObjectInputStream objectInStream;
    private GUI clientGUI;

    public Client(String targetIp, String targetPort) {
        isConnected = false;
        users = new ArrayList<>();
        chats = new ArrayList<>();
        try {
            socket = new Socket(targetIp, Integer.parseInt(targetPort));
            OutputStream outputStream = socket.getOutputStream();
            objectStream = new ObjectOutputStream(outputStream);

            InputStream inputStream = socket.getInputStream();
            objectInStream = new ObjectInputStream(inputStream);
        } catch (IOException | NumberFormatException e) {
        	System.out.println("Error connecting to server");
        }
    }

    public void startUp() { //Added
    	
    	clientGUI = new GUI(this);
    	
    	new Thread(clientGUI).start();

        ClientInput input = new ClientInput(objectInStream, this);
    	
    	new Thread(input).start();
    	
    }
    
    public void login(String username, String password) { 
    	this.username = username;
    	
    	ArrayList<String> args = new ArrayList<>();
    	args.add(username);
    	args.add(password);
    	
		Packet login = new Packet(Status.NONE, actionType.LOGIN, args, "Requesting");
    	try {
    		objectStream.writeObject(login);
    	} catch (IOException e) {
    		System.out.println("Error logging in");
    	}
    }
    
    public void sendMessage(String chatId, String content) {
    	ArrayList<String> args = new ArrayList<>();
    	args.add(content);
    	args.add(chatId);
    	
    	Packet sendMessage = new Packet(Status.NONE, actionType.SEND_MESSAGE, args, userId);
    	try {
    		objectStream.writeObject(sendMessage);
    	} catch (IOException e) {
    		System.out.println("Error sending message");
    	}
    	lastChatSent = getChatById(chatId);
    }
    
    public void createChat(String[] userIds, String chatName, boolean isPrivate) {
    	ArrayList<String> args = new ArrayList<>();
    	String joinedUsers = String.join("/", userIds);
    	args.add(joinedUsers);
    	args.add(chatName);
    	args.add(String.valueOf(isPrivate));
    	Packet createChat = new Packet(Status.NONE, actionType.CREATE_CHAT, args, userId); 
    	try {
    		objectStream.writeObject(createChat);
    	} catch (IOException e) {
    		System.out.println("Error creating chat");
    	}
    }
        
    public void createUser(String username, String password, String firstname, String lastname, boolean isAdmin) {
    	if (isITAdmin) {
        	ArrayList<String> args = new ArrayList<>();
        	args.add(username);
        	args.add(password);
        	args.add(firstname);
        	args.add(lastname);
        	args.add(String.valueOf(false));
        	args.add(String.valueOf(isAdmin));
    		Packet createUser = new Packet(Status.NONE, actionType.CREATE_USER, args, userId);
    		try {
        		objectStream.writeObject(createUser);
        	} catch (IOException e) {
        		System.out.println("Error creating user");
        	}
    	}
    }
    
    public void updateUser(String userId, boolean isDisabled) {
    	if (isITAdmin) {
    		ArrayList<String> args = new ArrayList<>();
        	args.add(userId);
        	args.add(String.valueOf(isDisabled));
    		Packet enableUser = new Packet(Status.NONE, actionType.UPDATE_USER, args, this.userId);
    		try {
        		objectStream.writeObject(enableUser);
        	} catch (IOException e) {
        		System.out.println("Error updating user");
        	}
    	}
    }

	public void editChatDetails(String userIds, String chatName, String chatId) {
		ArrayList<String> args = new ArrayList<>();
		args.add(userIds);
		args.add(chatName);
		args.add(chatId);
		Packet updateChat = new Packet(Status.NONE, actionType.EDIT_CHAT, args, this.userId);
		try {
			objectStream.writeObject(updateChat);
		} catch (IOException e) {
			System.out.println("Error updating chat");
		}
	}

	public void saveChatToTxt(Chat chat) {
    	if (isITAdmin) {
    		String fileName = "chat_logs_" + chat.getRoomName() +".txt";

            try {
                String downloadsPath = System.getProperty("user.home") + File.separator + "Downloads";
                File file = new File(downloadsPath, fileName);

                FileWriter fileWriter = new FileWriter(file);
                BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                bufferedWriter.write(chat.toString() + "\n");
                for (Message message : chat.getMessages()) {
                	bufferedWriter.write(message.toString() + "\n");
                }
                bufferedWriter.close();
            } catch (IOException e) {
            	System.out.println("Error saving chat to txt");
            }
    	}
    }
    
    public void logout() {
    	ArrayList<String> args = new ArrayList<>();
    	Packet logout = new Packet(Status.NONE, actionType.LOGOUT, args, userId);
    	try {
    		objectStream.writeObject(logout);
        	isConnected = false;
    		objectStream.close();
    		objectInStream.close();
    		socket.close();
    	} catch (IOException e) {
    		System.out.println("Error logging out");
    	}
    }
    
    public void updateState(actionType action) {
    	clientGUI.update(action);
    }

	public String getThisUserId() {
    	return userId;
    }
    
    public AbstractUser getThisUser() {
    	return thisUser;
    }
    
    public Boolean getAdminStatus() { // Added
    	return isITAdmin;
    }
    
    public List<Chat> getChats() {
    	return chats;
    }
    
    public List<AbstractUser> getUsers() {
    	return users;
    }
    
    public Chat getLastChatSent() {
    	return lastChatSent;
    }

	private AbstractUser getUserById(String userId) {
		for (AbstractUser user : users) {
			if (userId.equals(user.getId())) {
				return user;
			}
		}
		return null;
	}
	
	Chat getChatById(String chatId) {
		for (Chat chat : chats) {
			if (chatId.equals(chat.getId())) {
				return chat;
			}
		}
		return null;
	}
    
    private class ClientInput implements Runnable { // Added
    	private static final String ESCAPED_SLASH = "498928918204";
    	
    	private final ObjectInputStream inputStream;
    	private final Client client;
    	
    	public ClientInput(ObjectInputStream input, Client client) {
    		this.inputStream = input;
    		this.client = client;
    	}

		@Override
		public void run() {
			try {
				Packet incoming = (Packet) inputStream.readObject();
				do {
					switch(incoming.getActionType()) {
						// Parsing each Action Type
						case LOGIN -> {
							if (incoming.getStatus() == Status.ERROR) { 
								clientGUI.showMessageDialog(actionType.ERROR);
							}
							client.isConnected = true;
							List<String> args = incoming.getActionArguments();
							
							userId = args.get(0);
							String firstName = args.get(1);
							String lastName = args.get(2);
							isITAdmin = (Boolean) (args.get(3).equals("true"));
							boolean isDisabled = args.get(4).equals("true");
							
							if (isITAdmin) {
								thisUser = new ITAdmin(true, userId, username, firstName, lastName, isDisabled, true);
							} else {
								thisUser = new User(true, userId, username, firstName, lastName, isDisabled, false);
							}
							
							synchronized (clientGUI.getFrame()) {
								clientGUI.getFrame().notify();
							}
			                break;
						}
						case GET_ALL_CHATS -> {
							// Requesting chats from server
							for (String line : incoming.getActionArguments()) {
								String[] words = line.split("/");

								String chatId = words[0];
								String ownerId = words[1];
								String roomName = words[2];
								boolean isPrivate = words[3].equals("true");
								String[] userIds = words[4].split(",");

								AbstractUser owner = getUserById(ownerId);
								
								List<AbstractUser> chatters = new ArrayList<>();
								for (String userId : userIds) {
									chatters.add(getUserById(userId));
								}
								
								Chat newChat = new Chat(owner, roomName, chatId, chatters, isPrivate);
								chats.add(newChat);
							}
			                break;
						}
						case GET_ALL_USERS -> {
							for (String line : incoming.getActionArguments()) {
								String[] words = line.split("/");
								
								String userId = words[0];
								String username = words[1];
								String firstName = words[2];
								String lastName = words[3];
								boolean isDisabled = words[4].equals("true");
								boolean isAdmin = words[5].equals("true");

								AbstractUser newUser;

								if (isAdmin) {
									newUser = new ITAdmin(true, userId, username, firstName, lastName, isDisabled, isAdmin);
								} else {
									newUser = new User(true, userId, username, firstName, lastName, isDisabled, isAdmin);
								}
								users.add(newUser);
							}
			                break;
						}
						case GET_ALL_MESSAGES -> {
							for (String line : incoming.getActionArguments()) {
								String[] words = line.split("/");

								String messageId = words[0];
								long createdAt = Long.parseLong(words[1]);
								String content = Packet.unsanitize(words[2]);
								String authorId = words[3];
								String chatId = words[4];
								
								AbstractUser author = getUserById(authorId);
								Chat chat = getChatById(chatId);

								Message newMessage = new Message(messageId, createdAt, content, author, chat);
								chat.addMessage(newMessage);
							}
							synchronized (clientGUI.getFrame()) {
								clientGUI.getFrame().notify();
							}
						}
						case UPDATED_USER_BROADCAST -> {
							List<String> args = incoming.getActionArguments();
							AbstractUser updateUser = getUserById(args.get(0));
							updateUser.updateIsDisabled(Boolean.parseBoolean(args.get(1)));
							updateState(actionType.SUCCESS);
						}
						case NEW_USER_BROADCAST -> {
							List<String> args = incoming.getActionArguments();
							
							String userId = args.get(0);
							String username = args.get(1);
							String firstname = args.get(2);
							String lastname = args.get(3);
							boolean isDisabled = args.get(4).equals("true");
							boolean isAdmin = args.get(5).equals("true");
							
							AbstractUser newUser;

							if (isAdmin) {
								newUser = new ITAdmin(true, userId, username, firstname, lastname, isDisabled, isAdmin);
							} else {
								newUser = new User(true, userId, username, firstname, lastname, isDisabled, isAdmin);
							}
							users.add(newUser);
							clientGUI.showMessageDialog(actionType.SUCCESS);
							updateState(actionType.SUCCESS);
						}
						case NEW_CHAT_BROADCAST -> {
							List<String> args = incoming.getActionArguments();

							String chatId = args.get(0);
							String ownerId = args.get(1);
							String roomName = args.get(2);
							boolean isPrivate = args.get(3).equals("true");
							String[] userIds = args.get(4).split("/");

							AbstractUser owner = null;
							List<AbstractUser> chatters;
							for (AbstractUser user : users) {
								if (user.getId().equals(ownerId)) {
									owner = user;
								}
							}
							
							chatters = new ArrayList<>();
							for (String userId : userIds) {
								chatters.add(getUserById(userId));
							}
							
							Chat newChat = new Chat(owner, roomName, chatId, chatters, isPrivate);
							chats.add(newChat);
							clientGUI.showMessageDialog(actionType.SUCCESS);
							updateState(actionType.NEW_CHAT_BROADCAST);
						}
						case NEW_MESSAGE_BROADCAST -> {
							List<String> args = incoming.getActionArguments();

							String messageId = args.get(0);
							long createdAt = Long.parseLong(args.get(1));
							String content = args.get(2).replace(ESCAPED_SLASH, "/"); 
							String authorId = args.get(3);
							String chatId = args.get(4);
							
							AbstractUser author = getUserById(authorId);
							Chat chat = getChatById(chatId);
							
							Message newMessage = new Message(messageId, createdAt, content, author, chat);
							chat.addMessage(newMessage);
							lastChatSent = newMessage.getChat();
							updateState(actionType.NEW_MESSAGE_BROADCAST);
						}
						case EDIT_CHAT -> {
							List<String> args = incoming.getActionArguments();

							String chatId = args.get(0);
							String roomName = args.get(1);
							String[] userIds = args.get(2).split("/");

							Chat changeChat = getChatById(chatId);

							List<AbstractUser> chatters = new ArrayList<>();
							for (String userId : userIds) {
								chatters.add(getUserById(userId));
							}
							changeChat.editChat(roomName, chatters);
							updateState(actionType.NEW_CHAT_BROADCAST);
						}
						case SEND_MESSAGE -> {
							if (incoming.getStatus() == Status.SUCCESS) {
								updateState(actionType.NEW_MESSAGE_BROADCAST);
							}
						}
						case ERROR -> {
							isConnected = false;
							objectStream.close();
							objectInStream.close();
							socket.close();
							clientGUI.showMessageDialog(actionType.ERROR);
			                break;
						}
						default -> {
							if (incoming.getStatus() == Status.SUCCESS) {
								updateState(actionType.NEW_MESSAGE_BROADCAST);
							} else {
								clientGUI.showMessageDialog(actionType.ERROR);
								System.out.println("Invalid Action Type: " + String.valueOf(incoming.getActionType()));
							}
							break;
						}
					}
				} while(isConnected && (incoming = (Packet) inputStream.readObject()) != null);
			} catch (ClassNotFoundException | IOException ignored) {
				
			}
		}
   }

   public static void main(String[] args) {
	   Client client = new Client("localhost", "1337");
	   client.startUp();
   }
}
