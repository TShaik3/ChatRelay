package chatRelay;

public class Message {
	private static int count = 0;
	private String id;

	private long createdAt;

	private String content;
	private AbstractUser author;
	private Chat chat;

	// used when a user submits a new message
	public Message(String content, AbstractUser author, Chat chat) {
		this.id = String.valueOf(++count);
		this.createdAt = System.currentTimeMillis(); // unix miliseconds
		this.content = content;
		this.author = author;

		this.chat = chat;
		
		System.out.println("Message constructor 1 fired");

	}
	
    // used when loading in all Messages from the DB
	public Message(String id, long createdAt, String content, AbstractUser author, Chat chat) {
		++count; // important so class count doesn't start at 0 after loading in all Messages
		
		
		this.id = id;
		this.createdAt = createdAt;
		this.content = content;
		this.author = author;
		this.chat = chat;
	}

	// update to be "LONG"
	public long getCreatedAt() {
		return this.createdAt;
	}

	public String getId() {
		return this.id;
	}

	public String getContent() {
		return this.content;
	}

	public Chat getChat() {
		return this.chat;
	}

	public AbstractUser getSender() {
		return this.author;
	}

// used to get String needed to save to .txt DB file
	public String toString() {

		return id + "/" + createdAt + "/" + content + "/" + author.getId() + "/" + chat.getId();
	}
}