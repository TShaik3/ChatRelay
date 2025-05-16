package chatRelay;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
	private static final ConcurrentHashMap<String, ClientHandler> clients = new ConcurrentHashMap<>();

	private DBManager dbManager;
	private int port;
	private String IP;

	public Server(int port, String IP) {
		this.port = port;
		this.IP = IP; 
		
		
		// DEVELOPMENT DB - "/savedStates/dev state 1"
		this.dbManager = new DBManager("./chatRelay/dbFiles/development/", "Users.txt", "Chats.txt", "Messages.txt");
		
		// PRODUCTION DB - "/savedStates/prod state 1"
		// USE THIS FOR PRESENTATION:
//		this.dbManager = new DBManager("./chatRelay/dbFiles/production/", "Users.txt", "Chats.txt", "Messages.txt");
	
	}

	public void connect() {
		System.out.println("Server.connect() fired");
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			serverSocket.setReuseAddress(true);
			InetAddress ip = InetAddress.getLocalHost();
            String currentIp = ip.getHostAddress();
            System.out.println("Current IP address: " + currentIp);
			while (true) {
				Socket socket = serverSocket.accept();
				ClientHandler clientHandler = new ClientHandler(socket, this);
				new Thread(clientHandler).start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void receivePacket(String clientId, Packet packet) {

		try {

			System.out.println("Server.receivePacket() fired");
			switch (packet.getActionType()) {
			case SEND_MESSAGE:
				System.out.println("Server.receievePacket() SEND_MESSAGE switch fired");
				handleSendMessage(clientId, packet);
				break;
			case CREATE_CHAT:
				System.out.println("Server.receievePacket() CREATE_CHAT switch fired");
				handleCreateChat(clientId, packet);
				break;
			case CREATE_USER:
				System.out.println("Server.receievePacket() CREATE_USER switch fired");
				handleCreateUser(clientId, packet);
				break;
			case UPDATE_USER:
				System.out.println("Server.receievePacket() CREATE_USER switch fired");
				handleUpdateUser(clientId, packet);
				break;

			case ADD_USER_TO_CHAT:
				System.out.println("Server.receievePacket() ADD_USER_TO_CHAT switch fired");
				handleAddUserToChat(clientId, packet);
				break;
			case REMOVE_USER_FROM_CHAT:
				System.out.println("Server.receievePacket REMOVE_USER_FROM_CHAT switch fired");
				handleRemoveUserFromChat(clientId, packet);
				break;

			case RENAME_CHAT:
				System.out.println("Server.receievePacket RENAME_CHAT switch fired");
				handleRenameChat(clientId, packet);
				break;
			case LOGOUT:
				System.out.println("Server.receievePacket LOGOUT switch fired");
				handleLogout(clientId);
				break;
			default:
				sendErrorMessage(clientId, "Unknown action type: " + packet.getActionType());
			}
		} catch (Exception e) {
			sendErrorMessage(clientId, "Unable to handle the packet");
		}
	}

	private void handleRenameChat(String clientId, Packet packet) {
		ArrayList<String> args = packet.getActionArguments();
		String chatroomId = args.get(0);
		String newName = args.get(1);

		Chat chat = dbManager.renameChat(clientId, chatroomId, newName);
		ArrayList<String> broadcastingArgs = new ArrayList<>();

		if (chat == null) {
			broadcastingArgs.add("Cannot rename chatroom");
			Packet errorPacket = new Packet(Status.ERROR, actionType.RENAME_CHAT, broadcastingArgs, "Server");
			broadcastToClientById(clientId, errorPacket);
		} else {
			broadcastingArgs.add(chat.getId());
			broadcastingArgs.add(chat.getRoomName());

			Packet successPacket = new Packet(Status.SUCCESS, actionType.RENAME_CHAT_BROADCAST, broadcastingArgs,
					"Server");

			broadcastToUsers(chat.getChatters(), successPacket);
		}
	}

	private void handleRemoveUserFromChat(String clientId, Packet packet) {
		ArrayList<String> args = packet.getActionArguments();
		String userIdToRemove = args.get(0);
		String chatId = args.get(1);

		Chat chat = dbManager.removeUserFromChat(userIdToRemove, chatId, clientId);
		ArrayList<String> broadcastingArgs = new ArrayList<>();

		if (chat == null) {
			broadcastingArgs.add("Cannot to remove User from the Chat");

			Packet errorPacket = new Packet(Status.ERROR, actionType.REMOVE_USER_FROM_CHAT, broadcastingArgs, "Server");

			broadcastToClientById(clientId, errorPacket);

		} else {
			broadcastingArgs.add(userIdToRemove);
			broadcastingArgs.add(chatId);

			Packet successPacket = new Packet(Status.SUCCESS, actionType.REMOVE_USER_FROM_CHAT_BROADCAST,
					broadcastingArgs, "Server");

			broadcastToUsers(chat.getChatters(), successPacket);
		}
	}

	private void handleAddUserToChat(String clientId, Packet packet) {
		ArrayList<String> args = packet.getActionArguments();
		String userIdToAdd = args.get(0);
		String chatId = args.get(1);

//		boolean operationSucceeded = dbManager.addUserToChat(userIdToAdd, chatId, clientId);
		Chat chat = dbManager.addUserToChat(userIdToAdd, chatId, clientId);
		ArrayList<String> broadcastingArgs = new ArrayList<>();

		if (chat == null) {
			broadcastingArgs.add("Unable to add User to the Chat");

			Packet chatroomInfoPacket = new Packet(Status.ERROR, actionType.ADD_USER_TO_CHAT, broadcastingArgs,
					"Server");
			broadcastToClientById(clientId, chatroomInfoPacket);

		} else {
			// ["userAddedId", "chatId5", "ownerId1", "my chatroom name 1",
			// "userId1/userId2/userId3",
			// "messageId1/1745655698/mymessagecontent/authorId1/chatId1", ..., ..., ]

			broadcastingArgs.add(userIdToAdd);
			broadcastingArgs.add(chat.getId());
			broadcastingArgs.add(chat.getOwner().getId());
			broadcastingArgs.add(chat.getRoomName());
			broadcastingArgs.add(String.join("/", chat.getChattersIds()));

			ArrayList<Message> messagesInChat = (ArrayList<Message>) chat.getMessages();

			for (Message message : messagesInChat) {
				broadcastingArgs.add(message.toString());
			}

			Packet chatroomInfoPacket = new Packet(Status.SUCCESS, actionType.ADD_USER_TO_CHAT_BROADCAST,
					broadcastingArgs, "Server");
			broadcastToUsers(chat.getChatters(), chatroomInfoPacket);

		}
	}

	private void handleUpdateUser(String clientId, Packet packet) {
//		TODO: 1) (low priority) Admins cant enable/disable admins
//		TODO: 2) (low priority) can't disable twice (useful to signal something weird on frontend) 
//		TODO: 3) (low priority) make sure user is found 

		ArrayList<String> broadcastingArgs = new ArrayList<>();
		ArrayList<String> args = packet.getActionArguments();
		String userIdToUpdate = args.get(0);
		boolean isDisabled = args.get(1).equals("true");

		AbstractUser updatedUser = dbManager.updateUserIsDisabled(userIdToUpdate, isDisabled);

		if (updatedUser == null) {
			broadcastingArgs.add("Unable to add User to the Chat");
			Packet errorPacket = new Packet(Status.ERROR, actionType.UPDATE_USER, broadcastingArgs, "Server");
			broadcastToClientById(clientId, errorPacket);

		} else {
			broadcastingArgs.add(updatedUser.getId());
			broadcastingArgs.add(String.valueOf(updatedUser.isDisabled()));

			Packet updatedUserPacket = new Packet(Status.SUCCESS, actionType.UPDATED_USER_BROADCAST, broadcastingArgs,
					"Server");

			broadcastToAllUsersConnected(updatedUserPacket);
		}

	}

	private void handleCreateUser(String clientId, Packet packet) {
		ArrayList<String> broadcastingArgs = new ArrayList<>();

		// Requestor must be an admin
		if (!dbManager.getUserById(clientId).isAdmin()) {
			broadcastingArgs.add("You're not an admin, get out of here!");
			Packet errorPacket = new Packet(Status.ERROR, actionType.CREATE_USER, broadcastingArgs, "Server");
			broadcastToClientById(clientId, errorPacket);
			return;
		}

		ArrayList<String> args = packet.getActionArguments();
		String username = args.get(0);
		String password = args.get(1);
		String firstname = args.get(2);
		String lastname = args.get(3);
		boolean isDisabled = args.get(4).equals("true");
		boolean isAdmin = args.get(5).equals("true");

		// No duplicate user names
		if (dbManager.getUserByUsername(username) != null) {
			broadcastingArgs.add("That username already exists");
			Packet errorPacket = new Packet(Status.ERROR, actionType.CREATE_USER, broadcastingArgs, "Server");
			broadcastToClientById(clientId, errorPacket);
			return;
		}

		// Firstname, Lastname and Username must be letters or numbers only (alpha numeric)
		if (!Packet.isAlphanumeric(firstname) || !Packet.isAlphanumeric(lastname) || !Packet.isAlphanumeric(username) ) {
			broadcastingArgs.add("Firstname, lastname & username must be letters and numbers only");
			Packet errorPacket = new Packet(Status.ERROR, actionType.CREATE_USER, broadcastingArgs, "Server");
			broadcastToClientById(clientId, errorPacket);
			return;
		}
		AbstractUser newUser = dbManager.writeNewUser(username, password, firstname, lastname, isDisabled, isAdmin);

		if (newUser == null) {
			broadcastingArgs.add("Unable to create new User");
			Packet errorPacket = new Packet(Status.ERROR, actionType.CREATE_USER, broadcastingArgs, "Server");
			broadcastToClientById(clientId, errorPacket);
			return;
		}

		broadcastingArgs.add(newUser.getId());
		broadcastingArgs.add(newUser.getUserName());
		broadcastingArgs.add(newUser.getFirstName());
		broadcastingArgs.add(newUser.getLastName());
		broadcastingArgs.add(String.valueOf(newUser.isAdmin()));
		broadcastingArgs.add(String.valueOf(newUser.isDisabled()));

		Packet newUserPacket = new Packet(Status.SUCCESS, actionType.NEW_USER_BROADCAST, broadcastingArgs, "Server");
		broadcastToAllUsersConnected(newUserPacket);

	}

	private void handleCreateChat(String clientId, Packet packet) {
		ArrayList<String> args = packet.getActionArguments();
		ArrayList<String> userIds = new ArrayList<>();

		for (String userId : args.get(0).split("/")) {
			userIds.add(userId);
		}

		String roomName = args.get(1);
		boolean isPrivate = args.get(2).equals("true");

		Chat newChat = dbManager.writeNewChat(clientId, roomName, userIds, isPrivate);

		ArrayList<String> broadcastingArgs = new ArrayList<>();

		if (newChat == null) {
			broadcastingArgs.add("Unable to add User to the Chat");
			Packet errorPacket = new Packet(Status.ERROR, actionType.CREATE_CHAT, broadcastingArgs, "Server");
			broadcastToClientById(clientId, errorPacket);
			return;
		}

		broadcastingArgs.add(newChat.getId());
		broadcastingArgs.add(newChat.getOwner().getId());
		broadcastingArgs.add(roomName);
		broadcastingArgs.add(String.valueOf(isPrivate));
		broadcastingArgs.add(String.join("/", newChat.getChattersIds()));

		Packet chatPacket = new Packet(Status.SUCCESS, actionType.NEW_CHAT_BROADCAST, broadcastingArgs, "Server");


		if (newChat.isPrivate()) {
			broadcastToUsers(newChat.getChatters(), chatPacket);
		} else {
			broadcastToAllUsersConnected(chatPacket);
		}

	}

	private void handleSendMessage(String clientId, Packet packet) {
		ArrayList<String> args = packet.getActionArguments();
		String content = args.get(0);
		String chatId = args.get(1);

		Message newMessage = dbManager.writeNewMessage(content, clientId, chatId);

		Chat chat = dbManager.getChatById(chatId);

		ArrayList<String> broadcastingArgs = new ArrayList<>();

		if (chat == null) {

			broadcastingArgs.add("Unable to add User to the Chat");
			Packet errorPacket = new Packet(Status.ERROR, actionType.SEND_MESSAGE, broadcastingArgs, "Server");
			broadcastToClientById(clientId, errorPacket);
		}

		broadcastingArgs.add(newMessage.getId());
		broadcastingArgs.add(String.valueOf(newMessage.getCreatedAt()));
		broadcastingArgs.add(newMessage.getContent());
		broadcastingArgs.add(newMessage.getSender().getId());
		broadcastingArgs.add(newMessage.getChat().getId());

		Packet messagePacket = new Packet(Status.SUCCESS, actionType.NEW_MESSAGE_BROADCAST, broadcastingArgs, "Server");
		broadcastToUsers(chat.getChatters(), messagePacket);
	}

	private void broadcastToUsers(List<AbstractUser> usersToSendTo, Packet packet) {
		for (AbstractUser user : usersToSendTo) {
			ClientHandler client = clients.get(user.getId());

			if (client != null) {
				client.sendPacket(packet);
			}
		}
	}

	private void broadcastToAllUsersConnected(Packet chatPacket) {
		for (ClientHandler client : clients.values()) {
			client.sendPacket(chatPacket);
		}
	}

	private void broadcastToClientById(String requestorId, Packet packet) {
		ClientHandler client = clients.get(requestorId);
		client.sendPacket(packet);
	}

	public void handleLogout(String clientId) {
		clients.remove(clientId);
		System.out.println(clientId + " logged out and removed from clients.");
	}

	public void sendErrorMessage(String userId, String errorMessage) {
		ArrayList<String> broadcastingArgs = new ArrayList<>();
		broadcastingArgs.add(errorMessage);
		Packet errorPacket = new Packet(Status.ERROR, actionType.ERROR, broadcastingArgs, "Server");
		broadcastToClientById(userId, errorPacket);
	}

	public void sendSuccessMessage(String userId, String successMessage) {
	}

//	TODO : Probabl delete as we already have a 'Broadcast' function that does this?
	// instead get active userids living on clienthandler??
	public void sendPacketToUsers(Packet packet, String[] userIds) {
		for (String userId : userIds) {
			ClientHandler client = clients.get(userId);
			if (client != null) {
				client.sendPacket(packet);
			}
		}
	}
	
	public DBManager getDBManager() {
		return this.dbManager;
	}

	public void addClient(String userId, ClientHandler ch) {
		clients.put(userId, ch);
	}

	public boolean containsClient(String userId) {
		return clients.containsKey(userId);
	}

	public static void main(String[] args) {

// commands to compile + run
//Src % javac chatRelay/*.java
//Src % java chatRelay.Server
//Src % java chatRelay.BasicClient
		int port = 1337;
		String IP = "127.0.0.1";

		System.out.println("Server.java's main() fired\n");
		System.out.println(
				"NOTE: Database is currently sensitive. Each .txt file needs 1 blank line under the last record\n");

		Server server = new Server(port, IP);

		server.connect();
	}
}
