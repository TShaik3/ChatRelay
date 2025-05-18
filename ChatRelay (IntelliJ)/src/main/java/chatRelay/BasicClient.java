package chatRelay;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class BasicClient {
//	private static boolean STRESS_TEST = true;
	private static boolean STRESS_TEST = false;

	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String userId;

	public BasicClient(String ip, int port) {
		System.out.println("BasicClient constructor fired");
		try {
			socket = new Socket(ip, port);
			OutputStream outputStream = socket.getOutputStream();
			out = new ObjectOutputStream(outputStream);

			InputStream inputStream = socket.getInputStream();
			in = new ObjectInputStream(inputStream);

			System.out.println("Connected to server at " + ip + ":" + port);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void login(String username, String password) {
		System.out.println("BasicClient.login() fired");
		try {
			ArrayList<String> loginArgs = new ArrayList<>();
			loginArgs.add(username);
			loginArgs.add(password);

			Packet loginPacket = new Packet(Status.NONE, actionType.LOGIN, loginArgs, "tempClient");
			out.writeObject(loginPacket);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void sendMessage(String authorId, String chatId, String content) {
		System.out.println("BasicClient.sendMessage() fired");
		try {
			ArrayList<String> messageArgs = new ArrayList<>();
//			messageArgs.add(authorId);
			messageArgs.add(content);
			messageArgs.add(chatId);

			Packet messagePacket = new Packet(Status.NONE, actionType.SEND_MESSAGE, messageArgs, authorId);
			out.writeObject(messagePacket);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createChat(String chatName, boolean isPrivate, String[] userIds) {
		System.out.println("BasicClient.createChat() fired");
		try {
			String joinedUsers = String.join("/", userIds); // format: "1/2/6"
			ArrayList<String> args = new ArrayList<>();
			args.add(joinedUsers);
			args.add(chatName);
			args.add(String.valueOf(isPrivate));

			Packet packet = new Packet(Status.NONE, actionType.CREATE_CHAT, args, userId);
			out.writeObject(packet);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void logout(String userId) {
		try {
			ArrayList<String> logoutArgs = new ArrayList<>();
			Packet logoutPacket = new Packet(Status.NONE, actionType.LOGOUT, logoutArgs, userId);
			out.writeObject(logoutPacket);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllUsers() {
		try {
			ArrayList<String> args = new ArrayList<>();
			Packet getUsersPacket = new Packet(Status.NONE, actionType.GET_ALL_USERS, args, userId);
			out.writeObject(getUsersPacket);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void getAllChats() {
		try {
			ArrayList<String> args = new ArrayList<>();
			Packet getChatsPacket = new Packet(Status.NONE, actionType.GET_ALL_CHATS, args, userId);
			out.writeObject(getChatsPacket);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void createUser(String username, String password, String firstName, String lastName, boolean isDisabled,
			boolean isAdmin) {
		System.out.println("BasicClient.createUser() fired");
		try {
			ArrayList<String> args = new ArrayList<>();
			args.add(username);
			args.add(password);
			args.add(firstName);
			args.add(lastName);
			args.add(String.valueOf(isDisabled));
			args.add(String.valueOf(isAdmin));

			Packet packet = new Packet(Status.NONE, actionType.CREATE_USER, args, userId);
			out.writeObject(packet);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateUserIsDisabled(String targetUserId, boolean shouldBeDisabled) {
		System.out.println("BasicClient.updateUserIsDisabled() fired");
		try {
			ArrayList<String> args = new ArrayList<>();
			args.add(targetUserId);
			args.add(String.valueOf(shouldBeDisabled));

			Packet packet = new Packet(Status.NONE, actionType.UPDATE_USER, args, userId);
			out.writeObject(packet);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addUserToChat(String userIdToAdd, String chatId) {
		System.out.println("BasicClient.addUserToChat() fired");
		try {
			ArrayList<String> args = new ArrayList<>();
			args.add(userIdToAdd);
			args.add(chatId);

			Packet addUserPacket = new Packet(Status.NONE, actionType.ADD_USER_TO_CHAT, args, userId);
			out.writeObject(addUserPacket);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeUserFromChat(String userIdToRemove, String chatId) {
		System.out.println("BasicClient.removeUserFromChat() fired");
		try {
			ArrayList<String> args = new ArrayList<>();
			args.add(userIdToRemove);
			args.add(chatId);

			Packet removeUserPacket = new Packet(Status.NONE, actionType.REMOVE_USER_FROM_CHAT, args, userId);
			out.writeObject(removeUserPacket);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void renameChat(String chatId, String newRoomName) {
		System.out.println("BasicClient.renameChat() fired");
		try {
			ArrayList<String> args = new ArrayList<>();
			args.add(chatId);
			args.add(newRoomName);

			Packet renamePacket = new Packet(Status.NONE, actionType.RENAME_CHAT, args, userId);
			out.writeObject(renamePacket);
			out.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void listen() {
		System.out.println("in listen() loop");
		try {
			while (true) {
				Packet incoming = (Packet) in.readObject();
				System.out.println("Received from server: " + incoming.getActionType());

				ArrayList<String> args = incoming.getActionArguments();
				System.out.println("Arguments received:");
				for (String arg : args) {
					System.out.println(" - " + arg);
				}

				if (incoming.getStatus() == Status.ERROR) {
					System.out.println("response is an error: " + incoming.getActionType() + ": "
							+ (incoming.getActionArguments().isEmpty() ? "(no message)"
									: incoming.getActionArguments().get(0)));
					continue;
				}

				switch (incoming.getActionType()) {
				case LOGIN: {
					if (incoming.getStatus() == Status.SUCCESS && args.size() >= 3) {
						userId = args.get(0);
						System.out.println("Logged in as userId: " + userId);
					}

					if (incoming.getStatus() == Status.ERROR && args.size() == 1) {
						String errorMessage = args.get(0);
						System.out.println("error logging in: " + errorMessage);
						close();
					}

					//
					// dirty testing
					// test sending a message after successfull login

					// TESTING TO SEND A MESSAGE
					String chatId = "2";
					sendMessage(userId, chatId, "TESTING \nsendMessage() / SEND_MESSAGE");

					System.out.println("\n");

					// TESTING TO CREATE A CHAT
//					!!!
					String[] userIds = { "1", "2", "6", "8" };
//					String[] userIds = null; 
					createChat("test chat created from BasicClient!", true, userIds);

					// TESTING TO CREATE A USER
//					createUser("sarcon", "asdf", "Sara", "Connor", false, false);
					createUser("bigbob", "asdf", "big", "bob", false, false);
					System.out.println("\n");

					// TESTING TO UPDATE USER'S isDisabled()
					updateUserIsDisabled("8", true); // this is Bill Samsung

					// TESTING TO ADD A USER TO A CHAT
					addUserToChat("3", "2"); // adds userId 3 into chatId 2. Must be done by Chatroom Owner!

					// version that should fail:
					// addUserToChat("3", "1"); // adds userId 3 into chatId 2. Must be done by
					// Chatroom Owner!

					// TESTING TO ADD A USER TO A CHAT
					addUserToChat("5", "2"); // "5" is the User, "2" is the chatroom
					System.out.println("\n");

					// addUserToChat("3", "1"); // Should fail since i'm not owner of chatroom 1
					System.out.println("\n");

					// ++++++++++++++++++++++++++++++++++++++++++++++
					// first arg is userId, second arg is chatId
					// TESTING TO REMOVE A USER FROM CHAT
					removeUserFromChat("7", "2"); // remove 'Sarah Connor' w/ user id for 7, from chat room 2
					System.out.println("\n");

// TESTING TO RENAME A CHAT
					System.out.println("\n");
					renameChat("4", "Renamed Chat From BasicClient!"); // chatroom id of 4 should get name changed
					System.out.println("\n");

//					TESTING LOGOUT
//					logout(userId);

					// ++++++++++++++++++++++++++++++++++++++++++++++
					// ++++++++++++++++++++++++++++++++++++++++++++++
					// ++++++++++++++++++++++++++++++++++++++++++++++
					// ++++++++++++++++++++++++++++++++++++++++++++++
					// ++++++++++++++++++++++++++++++++++++++++++++++
					// ++++++++++++++++++++++++++++++++++++++++++++++
					// THREAD / STRESS TEST

					if (!STRESS_TEST)
						continue;
					for (int i = 0; i < 100; ++i) {
						sendMessage(userId, "2", "message " + i);
					}

					continue;
				}
				case NEW_MESSAGE_BROADCAST: {
					System.out.println("─────────────────────────────────────");
					System.out.println("!!! RECEIVED A NEW BROADCAST !!!");
					String messageId = args.get(0);
					String timestamp = args.get(1);
					String content = args.get(2);
					String senderId = args.get(3);
					String chatId = args.get(4);

					System.out.println("id:" + chatId + ", " + senderId + " at " + timestamp + ": " + content);
					System.out.println("─────────────────────────────────────");
					break;
				}
				case NEW_CHAT_BROADCAST: {
					System.out.println("──────────── NEW CHAT BROADCAST ────────────");
					for (String arg : args) {
						System.out.println("chat info: " + arg);
					}
					System.out.println("─────────────────────────────────────");
					break;
				}
				case NEW_USER_BROADCAST: {
					System.out.println("──────────── NEW USER BROADCAST ────────────");
					for (String arg : args) {
						System.out.println("user info: " + arg);
					}
					System.out.println("─────────────────────────────────────");
					break;
				}
				case UPDATED_USER_BROADCAST: {
					System.out.println("──────────── USER STATUS UPDATED ────────────");
					System.out.println("UserID: " + args.get(0));
					System.out.println("IsDisabled: " + args.get(1));
					System.out.println("─────────────────────────────────────");
					break;
				}
				case ADD_USER_TO_CHAT_BROADCAST: {
					System.out.println("──────────── USER ADDED TO CHAT BROADCAST ────────────");

					if (incoming.getStatus() == Status.ERROR) {
						System.out.println("Error: " + args.get(0));
					} else {
						System.out.println("UserID added: " + args.get(0));
						System.out.println("ChatID: " + args.get(1));
					}

					System.out.println("─────────────────────────────────────");
					break;
				}
				case REMOVE_USER_FROM_CHAT_BROADCAST: {
					System.out.println("──────────── USER REMOVED FROM CHAT BROADCAST ────────────");

					if (incoming.getStatus() == Status.ERROR) {
						System.out.println("Error: " + args.get(0));
					} else {
						System.out.println("UserID removed: " + args.get(0));
						System.out.println("ChatID: " + args.get(1));
					}

					System.out.println("─────────────────────────────────────");
					break;
				}
				case RENAME_CHAT_BROADCAST: {
					System.out.println("──────────── CHAT RENAMED BROADCAST ────────────");

					if (incoming.getStatus() == Status.ERROR) {
						System.out.println("Error: " + args.get(0));
					} else {
						System.out.println("Chat ID: " + args.get(0));
						System.out.println("New Chatroom Name: " + args.get(1));
					}

					System.out.println("─────────────────────────────────────");
					break;
				}

				case GET_ALL_USERS:
					System.out.println("Handled GET_ALL_USERS\n");
					break;
				case GET_ALL_CHATS:
					System.out.println("Handled GET_ALL_CHATS\n");
					break;
				case GET_ALL_MESSAGES:
					System.out.println("Handled GET_ALL_MESSAGES\n");
					break;
				case SUCCESS:
					System.out.println("Action successful.");
					break;
				case ERROR: {
					if (!args.isEmpty()) {
						System.out.println("Error from server: " + args.get(0));
					}
					break;
				}
				default: {
					System.out.println("Unhandled packet type: " + incoming.getActionType());
					break;
				}
				}
			}
		} catch (Exception e) {
			System.out.println("Disconnected from server.");
		} finally {
			close();
		}
	}

	public void close() {
		try {
			socket.close();
			System.out.println("Disconnected.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
//		boolean STRESS_TEST = true;
//		BasicClient client = new BasicClient("127.0.0.1", 1337);

		if (STRESS_TEST) {
			String[] usernames = { "biljoe", "chrsmi", "kenkot", "stearm", "zohsha", "talsha" };

			for (String username : usernames) {
				final String user = username;

				new Thread(() -> {
					BasicClient client = new BasicClient("127.0.0.1", 1337);
//					BasicClient client = new BasicClient("non local ip"", 1337);
					String password = "asdf";
					client.login(user, password);
					client.listen();
				}).start();
			}
		}

		else {

		BasicClient client = new BasicClient("192.168.1.103", 1337); // connect to another computer on network
//			BasicClient client = new BasicClient("127.0.0.1", 1337); // local host

			System.out.println(
					"Some users you can log into, otherwise it'll log into \"bilsam\": chrsmi kenkot stearm zohsha talsha biljoe ");
			System.out.println("juse type in a username into CLI next time");

			// default user to log in if no CLI args given
			String username = "kenkot"; 
//			String username = "talsha";
			String password = "asdf";
//			String password = "a\nsdf/";

			if (args.length == 1) {
				username = args[0];
				password = "asdf";
			}

			client.login(username, password);
//		client.login("biljoe", "asdf/"); // non-admin
//		client.login("chrsmi", "asdf"); // admin

			client.listen();
		}
	}

}
