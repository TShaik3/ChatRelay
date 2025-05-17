package unitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import chatRelay.Message;
import chatRelay.User;
import chatRelay.Chat;

public class MessageTesting {
    
    private Message message;
    private Message newMessage;
    private User user;
    private Chat chat;
    private final String MESSAGE_ID = "123";
    private final long TIMESTAMP = System.currentTimeMillis();
    private final String CONTENT = "Hello, this is a test message!";
    
    @BeforeEach
    public void setUp() {
        // create a test user
        user = new User("testuser", "password", "USER_123", "Zoheb", "Sharif", false, false);
        
        // create a test chat
        chat = new Chat(user, "Test Chat", "CHAT_123", null, false);
        
        // create a a test message with full constructor
        message = new Message(MESSAGE_ID, TIMESTAMP, CONTENT, user, chat);
        
        // create a test message with normal constructor
        newMessage = new Message(CONTENT, user, chat);
    }
    
    @Test
    @DisplayName("Test full constructor initialization")
    public void testFullConstructor() {
    	//verify message was created with attributes using full constructor
        assertNotNull(message, "Message should not be null");
        assertEquals(MESSAGE_ID, message.getId(), "Message ID should match");
        assertEquals(TIMESTAMP, message.getCreatedAt(), "Timestamp should match");
        assertEquals(CONTENT, message.getContent(), "Content should match");
        assertEquals(user, message.getSender(), "Author should match");
        assertEquals(chat, message.getChat(), "Chat should match");
    }
    
    @Test
    @DisplayName("Test normal constructor initialization")
    public void testNormalConstructor() {
    	// verify the message was created with all attributes using normal constructor
        assertNotNull(newMessage, "Message should not be null");
        assertNotNull(newMessage.getId(), "ID should be generated");
        assertTrue(newMessage.getCreatedAt() > 0, "Timestamp should be set");
        assertEquals(CONTENT, newMessage.getContent(), "Content should match");
        assertEquals(user, newMessage.getSender(), "Author should match");
        assertEquals(chat, newMessage.getChat(), "Chat should match");
    }
    
    @Test
    @DisplayName("Test ID generation is sequential")
    public void testIdGeneration() {
    	// make two messages in a row to test ID generation
        Message firstMsg = new Message("First message", user, chat);
        Message secondMsg = new Message("Second message", user, chat);
        
        // IDs should be unique and sequential
        assertNotEquals(firstMsg.getId(), secondMsg.getId(), "Message IDs should be different");
        
        // converts to integers since IDs are strings
        int firstId = Integer.parseInt(firstMsg.getId());
        int secondId = Integer.parseInt(secondMsg.getId());
        assertEquals(firstId + 1, secondId, "Second ID should be one more than first ID");
    }
    
    @Test
    @DisplayName("Test getId method")
    public void testGetId() {
        assertEquals(MESSAGE_ID, message.getId(), "getId() should return the correct message ID");
    }
    
    @Test
    @DisplayName("Test getCreatedAt method")
    public void testGetCreatedAt() {
        assertEquals(TIMESTAMP, message.getCreatedAt(), "getCreatedAt() should return the correct timestamp");
    }
    
    @Test
    @DisplayName("Test getContent method")
    public void testGetContent() {
        assertEquals(CONTENT, message.getContent(), "getContent() should return the correct message content");
    }
    
    @Test
    @DisplayName("Test getSender method")
    public void testGetSender() {
        assertEquals(user, message.getSender(), "getSender() should return the correct user");
    }
    
    @Test
    @DisplayName("Test getChat method")
    public void testGetChat() {
        assertEquals(chat, message.getChat(), "getChat() should return the correct chat");
    }
    
    @Test
    @DisplayName("Test toString method formatting")
    public void testToString() {
        String expected = MESSAGE_ID + "/" + TIMESTAMP + "/" + CONTENT + "/" + user.getId() + "/" + chat.getId();
        assertEquals(expected, message.toString(), "toString() should return correctly formatted string");
    }
    
    @Test
    @DisplayName("Test adding message to chat")
    public void testMessageInChat() {
    	// add message to chat
        chat.addMessage(message);
        
        // verify message was put into chat
        assertTrue(chat.getMessages().contains(message), "Message should be in the chat's message list");
    }
    
    @Test
    @DisplayName("Test message with empty content")
    public void testEmptyContent() {
        Message emptyMessage = new Message("", user, chat);
        assertEquals("", emptyMessage.getContent(), "Empty content should be allowed");
    }
    
    @Test
    @DisplayName("Test multiple messages in chat")
    public void testMultipleMessagesInChat() {
        // Create multiple messages
        Message message1 = new Message("First message", user, chat);
        Message message2 = new Message("Second message", user, chat);
        Message message3 = new Message("Third message", user, chat);
        
        // Add all messages to chat
        chat.addMessage(message1);
        chat.addMessage(message2);
        chat.addMessage(message3);
        
        // Verify all messages are in the chat
        assertTrue(chat.getMessages().contains(message1), "First message should be in chat");
        assertTrue(chat.getMessages().contains(message2), "Second message should be in chat");
        assertTrue(chat.getMessages().contains(message3), "Third message should be in chat");
        
        // Verify the number of messages
        assertEquals(3, chat.getMessages().size(), "Chat should contain 3 messages");
    }
}