package chatRelay;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUser {
	private static int count = 0; // probably need to use atomic for potential concurrency issue?
	private String id;
	private String firstName;
	private String lastName;
	private String username;
	private String password;
	private Boolean isDisabled;
	private Boolean isAdmin = false;
	private List<Chat> chats = new ArrayList<>();

	// dones't take in id (this is when Admin creates a new user)
	public AbstractUser(String username, String password, String firstname, String lastname, boolean isDisabled,
			boolean isAdmin) {
		this.id = String.valueOf(++count);
		this.username = username;
		this.password = password;
		this.firstName = firstname;
		this.lastName = lastname;
		this.isDisabled = isDisabled;
		this.isAdmin = isAdmin;
	}

	// when user is read in from db .txt
	public AbstractUser(String username, String password, String id, String firstname, String lastname,
			boolean isDisabled, boolean isAdmin) {
		++count;

		this.username = username;
		this.password = password;
		this.id = id;
		this.firstName = firstname;
		this.lastName = lastname;
		this.isDisabled = isDisabled;
		this.isAdmin = isAdmin;
	}

	// constructor for client to make users (No Password Passed)
	public AbstractUser(boolean frontEndUser, String id, String username, String firstName, String lastName, boolean isDisabled, boolean isAdmin) {
		this.id = id;
		this.username = username;
		this.firstName = firstName;
		this.lastName = lastName;
		this.isDisabled = isDisabled;
		this.isAdmin = isAdmin;
		this.password = "******";
	}



	public void addChat(Chat chat) {
		chats.add(chat);
		return;
	}

	public void removeChat(Chat chat) {
		if (chat == null) {
			return;
		}

		for (int i = 0; i < chats.size(); i++) {
			Chat c = chats.get(i);
			if (c.getId().equals(chat.getId())) {
				chats.remove(i);
				break;
			}
		}
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getUserName() {
		return username;
	}

	public String getId() {
		return id;
	}

	public String getPassword() {
		return password;
	}

	public List<Chat> getChats() {
		return chats;
	}

	// used to get string to write record to .txt DB
	public String toString() {
		return username + "/" + password + "/" + id + "/" + firstName + "/" + lastName + "/"
				+ String.valueOf(isDisabled) + "/" + String.valueOf(isAdmin);
	}

	public String toStringClient() {
		return id + "/" + username + "/" + firstName + "/" + lastName + "/" + String.valueOf(isDisabled) + "/"
				+ String.valueOf(isAdmin);
	}

	public ArrayList<String> getAllChatIds() {
		ArrayList<String> chatIds = new ArrayList<>();

		for (Chat chat : chats) {
			chatIds.add(chat.getId());
		}

		return chatIds;
	}

	public boolean isAdmin() {
		return this.isAdmin;
	}

	public boolean isDisabled() {
		return this.isDisabled;
	}

	public void updateIsDisabled(boolean isDisabled) {
		this.isDisabled = isDisabled;
	}
}
