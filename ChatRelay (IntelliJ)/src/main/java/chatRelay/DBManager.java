package chatRelay;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

public class DBManager {
	private ConcurrentHashMap<String, AbstractUser> users = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Chat> chats = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, Message> messages = new ConcurrentHashMap<>();

    private final String userTxtFilename;
	private final String chatTxtFilename;
	private final String messageTxtFilename;

//    filepath = "./src/chatRelay/dbFiles/development/"    (navigate from root path!)
	public DBManager(String filepath, String userTxtFilename, String chatTxtFilename, String messageTxtFilename) {
        this.userTxtFilename = filepath + userTxtFilename; // username/password/id/firstName/lastName/isDisabled/isAdmin
		this.chatTxtFilename = filepath + chatTxtFilename; // id/owner/roomName/[userId1, userId2, userId3]/isPrivate
		this.messageTxtFilename = filepath + messageTxtFilename; // id/createdAt/content/authorId/chatId

		loadUsers(); // convert TXT strings into real User objects, put those into the hashmap
		loadChats();
		loadMessages();

	}

	// Retrieve all users because they need to see the user list to add people to a
	// Chat
	public ArrayList<String> fetchAllUsers() {
		ArrayList<String> stringedUsers = new ArrayList<>();
		for (AbstractUser user : users.values()) {
			stringedUsers.add(user.toStringClient());
		}
		return stringedUsers;
	}

	// Retrieve ONLY Chats that User has access too. Admin gets all though
	public ArrayList<String> fetchAllChats(AbstractUser user) {
		ArrayList<String> stringedChats = new ArrayList<>();

//		give admin everything
		if (user.isAdmin()) {
			for (Chat chat : chats.values()) {
				stringedChats.add(chat.toString());
			}
		} else {
			for (Chat chat : user.getChats()) {
				stringedChats.add(chat.toString());
			}
		}

		return stringedChats;
	}

	// Retrieve Messages that User has access too. Admin gets all though
	public ArrayList<String> fetchAllMessages(AbstractUser user) {
		ArrayList<String> stringedMessages = new ArrayList<>();

//		give admin everything
		if (user.isAdmin()) {
			for (Message message : messages.values()) {
				stringedMessages.add(message.toString());
			}
		} else {
			for (Chat chat : user.getChats()) {

				for (Message message : chat.getMessages()) {
					stringedMessages.add(message.toString());
				}
			}
		}

		return stringedMessages;
	}

	private void loadUsers() {
		try (Scanner scanner = new Scanner(new File(this.userTxtFilename))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				System.out.println(line);

				String[] words = line.split("/");

				String username = words[0];
				String password = words[1];
				String userId = words[2];
				String firstName = words[3];
				String lastName = words[4];
				boolean isDisabled = words[5].equals("true");
				boolean isAdmin = words[6].equals("true");

				AbstractUser newUser;

				if (isAdmin) {
					newUser = new ITAdmin(username, password, userId, firstName, lastName, isDisabled, true);
				} else {
					newUser = new User(username, password, userId, firstName, lastName, isDisabled, false);
				}

				users.put(userId, newUser);

			}
			System.out.println("end of loadUsers()\n");
		} catch (IOException e) {
			System.out.println("Error loading users: " + e.getMessage());
		}
	}

	private void loadChats() {
		try (Scanner scanner = new Scanner(new File(this.chatTxtFilename))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				System.out.println(line);

				String[] words = line.split("/");

				String chatId = words[0];
				String ownerId = words[1];
				String roomName = words[2];
				boolean isPrivate = words[3].equals("true");
				String[] userIds = words[4].split(",");

				// TODO: Consider that this is adding the owner to chatters
				AbstractUser owner = getUserById(ownerId);
				List<AbstractUser> chatters = new ArrayList<>();
				for (String userId : userIds) {
					AbstractUser u = getUserById(userId);
					chatters.add(u);
				}

				// Add to hashmap
				Chat newChat = new Chat(owner, roomName, chatId, chatters, isPrivate);
				chats.put(chatId, newChat);

				// Add relationship on each User, to connect to this chat
				// TODO: POSSIBLE ALTERNATIVE - MAKE ADDCHATTERS() AS A SETTER
				// TO PREVENT ANOTHER LOOP HERE?
				for (String userId : userIds) {
					AbstractUser u = getUserById(userId);
					u.addChat(newChat);
				}

			}
			System.out.println("end of loadChats()\n");
		} catch (IOException e) {
			System.out.println("Error loading chats: " + e.getMessage());
		}
	}

	private void loadMessages() {
		try (Scanner scanner = new Scanner(new File(this.messageTxtFilename))) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				System.out.println(line);

				String[] words = line.split("/");

				String messageId = words[0];
				long createdAt = Long.parseLong(words[1]);
//				String content = words[2].replace(ESCAPED_SLASH, "/"); // have client replace escaped char instead
				String content = words[2];
				String authorId = words[3];
				String chatId = words[4];

//				.txt format: id/createdAt/content/authorId/chatId				

				AbstractUser author = getUserById(authorId);
				Chat chat = getChatById(chatId);

				Message newMessage = new Message(messageId, createdAt, content, author, chat);

				chat.addMessage(newMessage);
//				author.addChat(chat);

				messages.put(messageId, newMessage);

			}
			System.out.println("end of loadMessages()\n");
		} catch (IOException e) {
			System.out.println("Error loading messages: " + e.getMessage());
		}
	}

	// public User checkLoginCredentials(String username, String password) {}
	public void sendUserAllFreshData() {
	}

	public AbstractUser getUserById(String userId) {
		return users.get(userId);
	}

	public Chat getChatById(String chatId) {
		return chats.get(chatId);
	}

	// public Chat getChatById(String chatId) {}
	// public Message getMessageById(String messageId) {}
	// public List<User> fetchAllUsers() {}
	// public List<Chat> fetchAllChats() {}
	// public List<Message> fetchAllMessages() {}
	// public List<Chat> getChatsForUser(String userId) {}

//	private void writeNewUser(String username, String password, String firstname, String lastname, boolean isDisabled,
	public synchronized AbstractUser writeNewUser(String username, String password, String firstname, String lastname,
			boolean isDisabled, boolean isAdmin) {
		// TODO: consider if "/" char is ever used. Have server reject the packet if
		// anything except password has a "/".

		// TODO: ENSURE USERNAMES ARE UNIQUE

//		String sanitizedPassword = password.replace("/", ESCAPED_SLASH);
		String sanitizedPassword = Packet.sanitize(password);

		AbstractUser newUser;

		if (isAdmin) {
			newUser = new ITAdmin(username, sanitizedPassword, firstname, lastname, isDisabled, true);
		} else {
			newUser = new User(username, sanitizedPassword, firstname, lastname, isDisabled, false);
		}

		users.put(newUser.getId(), newUser);

		try {
			File file = new File(this.userTxtFilename);
			FileWriter writer = new FileWriter(file, true); // append mode
			writer.write(newUser.toString() + "\n");
			writer.close();
		} catch (IOException e) {
			System.out.println("Error writing new user: " + e.getMessage());
		}
		return newUser;
	}

//	private void writeNewChat(String ownerId, String roomName, String[] chatterIds, boolean isPrivate) {
	public synchronized Chat writeNewChat(String ownerId, String roomName, ArrayList<String> chatterIds, boolean isPrivate) {
		AbstractUser owner = getUserById(ownerId);
//		String sanitizedRoomName = roomName.replace("/", ESCAPED_SLASH); // a "/" inside content will break the DB
		String sanitizedRoomName = Packet.sanitize(roomName);

//		consider using a setter to avoid 2nd loop?

		List<AbstractUser> chatters = new ArrayList<>();
		for (String chatterId : chatterIds) {
			AbstractUser chatter = getUserById(chatterId);
			chatters.add(chatter);
		}

		Chat newChat = new Chat(owner, sanitizedRoomName, chatters, isPrivate);
		chats.put(newChat.getId(), newChat);

		// add relationship
		for (AbstractUser user : chatters) {
			user.addChat(newChat);
		}

		// write new chat to file
		try {
			File file = new File(this.chatTxtFilename);

			FileWriter writer = new FileWriter(file, true); // true is append mode!
			writer.write(newChat.toString() + "\n");
			writer.close();
		} catch (IOException e) {
			System.out.println("Error writing new chat: " + e.getMessage());
		}

		return newChat;

	}

	// by returning new message, it lets my Server have access to that message which
	// is needed!
//	public void writeNewMessage(String content, String authorId, String chatId) {
	public synchronized Message writeNewMessage(String content, String authorId, String chatId) {
		AbstractUser author = getUserById(authorId);
		Chat chat = getChatById(chatId);

//		String sanitizedContent = content.replace("/", ESCAPED_SLASH); // a "/" inside content will break the DB
		String sanitizedContent = Packet.sanitize(content);
		Message newMessage = new Message(sanitizedContent, author, chat);

		chat.addMessage(newMessage);
		messages.put(newMessage.getId(), newMessage);

		// blocking an issue?
		try {
			File file = new File(this.messageTxtFilename);

			FileWriter writer = new FileWriter(file, true); // true puts it in 'append' mode
			writer.write(newMessage.toString() + "\n"); // Add newline for each message
			writer.close();

		} catch (IOException e) {
			System.out.println("Error writing new message: " + e.getMessage());
		}

		return newMessage;

	}
    // private User stringToUser(String userString) {}
    // private Chat stringToChat(String chatString) {}
    // private Message StringToMessage(String messageString) {}
    // Don't use this function? just use .replace()
    // ex for incoming messages from user into DB: str1.replace("/", [sLaSH])
    // ex for loading messages from DB into memory: str1.replace("[sLaSH]", /)

//	public void addUserToChat(String userId) {
//	}

	public AbstractUser getUserByUsername(String username) {
		for (AbstractUser user : users.values()) {
			if (user.getUserName().equalsIgnoreCase(username)) {
				return user;
			}
		}
		return null;
	}

	public AbstractUser checkLoginCredentials(String username, String password) {
		System.out.println("checkLoginCreds(), username: " + username + ", pass:" + password);
		AbstractUser user = getUserByUsername(username);
		if (user == null)
			return null;

//		if (user.getPassword().replace(ESCAPED_SLASH, "/").equals(password)) {
//		if (Packet.unsanitize(user.getPassword()).equals(password)) {

//		remember packet sanitizes
		if (user.getPassword().equals(password)) {
			return user;
		}
		return null;
	}

	public synchronized AbstractUser updateUserIsDisabled(String userId, boolean isDisabled) {
		AbstractUser user = getUserById(userId);
		if (user == null)
			return null;

		user.updateIsDisabled(isDisabled);

		try {
			File file = new File(this.userTxtFilename);
			FileWriter writer = new FileWriter(file, false); // false is for over-writing

			for (AbstractUser u : users.values()) {
				writer.write(u.toString() + "\n");
			}

			writer.close();
		} catch (IOException e) {
			System.out.println("Error updating user file: " + e.getMessage());
		}

		return user;

	}

	public synchronized Chat addUserToChat(String userId, String chatId, String packetSenderUserId) {
		AbstractUser userToAdd = getUserById(userId);
		Chat chat = getChatById(chatId);

		// requestor must be the owner of the chat
		// chat owner can't remove themselves
		// userToAdd must not already be on the chat
		if (userToAdd == null || chat == null || !packetSenderUserId.equals(chat.getOwner().getId())
				|| userToAdd.getAllChatIds().contains(chatId) || userId.equals(packetSenderUserId))
			return null;

		chat.addChatter(userToAdd);
		userToAdd.addChat(chat);

		try {
			File file = new File(this.chatTxtFilename);
			FileWriter writer = new FileWriter(file, false);

			for (Chat c : chats.values()) {
				writer.write(c.toString() + "\n");
			}

			writer.close();
		} catch (IOException e) {
			System.out.println("Error writing chat updates: " + e.getMessage());
		}

		return chat;
	}

	public synchronized Chat removeUserFromChat(String userId, String chatId, String packetSenderUserId) {
		AbstractUser userToRemove = getUserById(userId);
		Chat chat = getChatById(chatId);

		// requestor must be the owner of the chat
		// user must be part of the chat already
		// prevent owner from removing themselves
		if (userToRemove == null || chat == null || !packetSenderUserId.equals(chat.getOwner().getId())
				|| !userToRemove.getAllChatIds().contains(chatId) || userId.equals(packetSenderUserId))
			return null;

		chat.removeChatter(userToRemove);
		userToRemove.removeChat(chat);

		try {
			File file = new File(this.chatTxtFilename);
			FileWriter writer = new FileWriter(file, false);

			for (Chat c : chats.values()) {
				writer.write(c.toString() + "\n");
			}

			writer.close();
		} catch (IOException e) {
			System.out.println("Error writing chat updates: " + e.getMessage());
		}

		return chat;
	}

	public synchronized Chat renameChat(String senderId, String chatId, String newChatRoomName) {
		Chat chat = getChatById(chatId);

		// requestor must be the owner of the chat
		if (!chat.getOwner().getId().equals(senderId)) {
			return null;
		}

		chat.setRoomName(newChatRoomName);

		try {
			File file = new File(this.chatTxtFilename);
			FileWriter writer = new FileWriter(file, false);

			for (Chat c : chats.values()) {
				writer.write(c.toString() + "\n");
			}

			writer.close();
		} catch (IOException e) {
			System.out.println("Error writing chat updates: " + e.getMessage());
		}

		return chat;
	}

//	 public Boolean usernameExists(String name) {
//		 
//	 }
	// private Boolean validUsername(String name) {}
}
