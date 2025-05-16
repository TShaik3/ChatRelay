package chatRelay;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {

	private Socket clientSocket;
	private String userId;
	private ObjectInputStream inputStream;
	private ObjectOutputStream outputStream;
	private Server server;

	public ClientHandler(Socket socket, Server server) {
		this.clientSocket = socket;
		this.server = server;

		try {
			OutputStream outStream = clientSocket.getOutputStream();
			outputStream = new ObjectOutputStream(outStream);
			
			InputStream inStream = clientSocket.getInputStream();
			inputStream = new ObjectInputStream(inStream);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	public void start() {
//		try {
//			Packet checkLogin = (Packet) inputStream.readObject();
//
//			if (checkLogin.getActionType().equals(actionType.LOGIN)) {
//				String[] args = { "Success" };
//				Packet accept = new Packet(actionType.SUCCESS, args, "Client");
//				System.out.println("Got: " + accept.getActionType().toString());
//				outputStream.writeObject(accept);
//			}
//		} catch (IOException | ClassNotFoundException e) {
//
//		}
//	}
//
//	public void stop() {
//		try {
//			Packet checkLogout = (Packet) inputStream.readObject();
//
//			if (checkLogout.getActionType().equals(actionType.LOGOUT)) {
//				String[] args = { "Success" };
//				Packet accept = new Packet(actionType.SUCCESS, args, "Client");
//				System.out.println("Got: " + accept.getActionType().toString());
//				outputStream.writeObject(accept);
//			}
//		} catch (IOException | ClassNotFoundException e) {
//
//		}
//	}

	public void setUserId(String userId) { // What is the use?
		this.userId = userId;
	}

	public String getUserId() {
		return userId;
	}

	public ObjectInputStream getInputStream() {
		return inputStream;
	}

	public ObjectOutputStream getOutputStream() {
		return outputStream;
	}

	public void sendPacket(Packet packet) {
		try {
			outputStream.writeObject(packet);
			outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {

		System.out.println("Client.run() fired");
		try {
//			Step 1 - Handle login
			Packet packet = (Packet) inputStream.readObject();

			if (packet.getActionType() == actionType.LOGIN) {
				ArrayList<String> args = packet.getActionArguments();
				String username = args.get(0);
				String password = args.get(1);

				System.out.println("username: " + username + " password: " + password);

				AbstractUser user = server.getDBManager().checkLoginCredentials(username, password);
				
				if (user == null) { //Early return for null user
					ArrayList<String> errorArgs = new ArrayList<>();
					errorArgs.add("Invalid username or password");

					Packet errorPacket = new Packet(Status.ERROR, actionType.ERROR, errorArgs, "Server");
					sendPacket(errorPacket);

					clientSocket.close();
					return;
				}

				if (user != null) {

					// Can't log in if User's account has been disabled
					if (user.isDisabled()) {
						ArrayList<String> errorArgs = new ArrayList<>();
						errorArgs.add("User's account has been disabled");

						Packet errorPacket = new Packet(Status.ERROR, actionType.LOGIN, errorArgs, "Server");
						sendPacket(errorPacket);

						clientSocket.close();
						return;
					}

					// Can't be logged into 2 computers as same user
					if (server.containsClient(user.getId())) {
						ArrayList<String> errorArgs = new ArrayList<>();
						errorArgs.add("Already logged in from another client");

						Packet errorPacket = new Packet(Status.ERROR, actionType.LOGIN, errorArgs, "Server");
						sendPacket(errorPacket);

						clientSocket.close();
						return;
          }
        }
				// check if user isn't disabled too
				if (!user.isDisabled()) {
					this.userId = user.getId();

					server.addClient(userId, this); // add this ClientHanlder to Server's HashMap

					System.out.println("User ID being sent to client: " + userId);

					ArrayList<String> userInfoStringed = new ArrayList<>();
					System.out.println(
							"Login Was Successful - sending user info: " + userId + ", isAdmin() = " + user.isAdmin());

					// basic client version
					userInfoStringed.add(userId);
					userInfoStringed.add(user.getFirstName());
					userInfoStringed.add(user.getLastName());
					userInfoStringed.add(String.valueOf(user.isAdmin()));
					userInfoStringed.add(String.valueOf(user.isDisabled()));
					Packet userInfoPacket = new Packet(Status.SUCCESS, actionType.LOGIN, userInfoStringed, "Server");

					// userInfoStringed.add(server.getDBManager().getUserById(userId).toStringClient());
					// Packet userInfoPacket = new Packet(Status.SUCCESS, actionType.LOGIN, args,
					// "SERVER");

					sendPacket(userInfoPacket);

					System.out.println("allUsersStringed Packet created/sent");
					ArrayList<String> allUsersStringed = server.getDBManager().fetchAllUsers();
					System.out.println("allUsersStringed: " + allUsersStringed);
					Packet usersPacket = new Packet(Status.SUCCESS, actionType.GET_ALL_USERS, allUsersStringed,
							"SERVER");
					sendPacket(usersPacket);

//					TODO: sort chats/messages by timestamp? 
//					TODO: Add timestamps on Chats (filter by name is good too though)? 

					System.out.println(user.getAllChatIds());

					ArrayList<String> allChatsStringed = server.getDBManager().fetchAllChats(user);
					Packet chatsPacket = new Packet(Status.SUCCESS, actionType.GET_ALL_CHATS, allChatsStringed,
							"SERVER");
					System.out.println("\n\nallChatsStringed: " + allChatsStringed);
					sendPacket(chatsPacket);

					ArrayList<String> allMessagesStringed = server.getDBManager().fetchAllMessages(user);
					Packet messagesPacket = new Packet(Status.SUCCESS, actionType.GET_ALL_MESSAGES, allMessagesStringed,
							"SERVER");
					System.out.println("\n\nallMessagesStringed: " + allMessagesStringed);
					sendPacket(messagesPacket);

				}
			} else {
				return;
			}

// Step 2 - Now that user is logged in, process their subsequent steps		
			Packet nextPacket = (Packet) inputStream.readObject();
			do {
				System.out.println("Inside 'step 2' loop for after login");
				server.receivePacket(userId, nextPacket);
			} while ((nextPacket = (Packet) inputStream.readObject()) != null);

		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (userId != null) {
				server.handleLogout(userId);
				System.out.println("Client with id of  " + userId + "  has disconnected");
			}
		}
	}

}
