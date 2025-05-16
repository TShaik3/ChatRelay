package unitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import chatRelay.AbstractUser;
import chatRelay.Chat;
import chatRelay.User;
import chatRelay.Message;

import java.util.ArrayList;
import java.util.List;

public class UserTest {
    
    private User user;
    private User userWithId;
    private static final String USERNAME = "testuser";
    private static final String PASSWORD = "password123";
    private static final String USER_ID = "12345";
    private static final String FIRST_NAME = "John";
    private static final String LAST_NAME = "Doe";
    
    @BeforeEach
    public void setUp() {
        // create instances of user for testings
        // one with an auto generated id...
        user = new User(USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, false, false);
        
        // one with an explicit id...
        userWithId = new User(USERNAME, PASSWORD, USER_ID, FIRST_NAME, LAST_NAME, false, false);
    }
    
    @Test
   
    public void testAutoIdConstructor() {
        assertNotNull(user, "User should be created");
        assertNotNull(user.getId(), "ID should be auto-generated");
        assertEquals(USERNAME, user.getUserName(), "Username should match");
        assertEquals(PASSWORD, user.getPassword(), "Password should match");
        assertEquals(FIRST_NAME, user.getFirstName(), "First name should match");
        assertEquals(LAST_NAME, user.getLastName(), "Last name should match");
        assertFalse(user.isDisabled(), "User should not be disabled by default");
        assertFalse(user.isAdmin(), "User should not be admin by default");
        assertEquals(0, user.getChats().size(), "User should have no chats initially");
    }
    
    @Test
   
    public void testExplicitIdConstructor() {
        assertNotNull(userWithId, "User should be created");
        assertEquals(USER_ID, userWithId.getId(), "ID should match provided value");
        assertEquals(USERNAME, userWithId.getUserName(), "Username should match");
        assertEquals(PASSWORD, userWithId.getPassword(), "Password should match");
        assertEquals(FIRST_NAME, userWithId.getFirstName(), "First name should match");
        assertEquals(LAST_NAME, userWithId.getLastName(), "Last name should match");
        assertFalse(userWithId.isDisabled(), "User should not be disabled by default");
        assertFalse(userWithId.isAdmin(), "User should not be admin by default");
    }
    
    @Test
   
    public void testDisabledUser() {
        User disabledUser = new User(USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, true, false);
        assertTrue(disabledUser.isDisabled(), "User should be disabled");
    }
    
    @Test

    public void testAdminUser() {
        User adminUser = new User(USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, false, true);
        assertTrue(adminUser.isAdmin(), "User should be admin");
        assertFalse(adminUser.isDisabled(), "Admin should not be disabled by default");
    }
    
    @Test

    public void testAddChat() {
        // make mock chat
        Chat mockChat = createMockChat("Test Chat", "1");
        
        // add chat to user
        user.addChat(mockChat);
        
        // verify chat 
        List<Chat> userChats = user.getChats();
        assertEquals(1, userChats.size(), "User should have one chat");
        assertEquals(mockChat, userChats.get(0), "Added chat should match mock chat");
        
        // add another chat
        Chat mockChat2 = createMockChat("Second Chat", "2");
        user.addChat(mockChat2);
        
        // verify second chat was added
        userChats = user.getChats();
        assertEquals(2, userChats.size(), "User should have two chats");
        assertTrue(userChats.contains(mockChat), "User chats should contain first mock chat");
        assertTrue(userChats.contains(mockChat2), "User chats should contain second mock chat");
    }
    
    @Test
   
    public void testGetAllChatIds() {
        // add multiple chats to user
        Chat chat1 = createMockChat("Chat 1", "101");
        Chat chat2 = createMockChat("Chat 2", "102");
        Chat chat3 = createMockChat("Chat 3", "103");
        
        user.addChat(chat1);
        user.addChat(chat2);
        user.addChat(chat3);
        
        // get all chat IDs
        ArrayList<String> chatIds = user.getAllChatIds();
        
        // verify correct IDs were returned
        assertEquals(3, chatIds.size(), "Should return 3 chat IDs");
        assertTrue(chatIds.contains("101"), "Should contain first chat ID");
        assertTrue(chatIds.contains("102"), "Should contain second chat ID");
        assertTrue(chatIds.contains("103"), "Should contain third chat ID");
    }
    
    @Test

    public void testUpdateIsDisabled() {
        // starts off as not disabled
        assertFalse(user.isDisabled(), "User should start as not disabled");
        
        // update to disabled
        user.updateIsDisabled(true);
        assertTrue(user.isDisabled(), "User should be disabled after update");
        
        // update back to not disabled
        user.updateIsDisabled(false);
        assertFalse(user.isDisabled(), "User should be enabled after update");
    }
    
    @Test

    public void testToString() {
        String expected = USERNAME + "/" + PASSWORD + "/" + user.getId() + "/" + 
                          FIRST_NAME + "/" + LAST_NAME + "/false/false";
        
        assertEquals(expected, user.toString(), "toString should return correctly formatted string");
        
        expected = USERNAME + "/" + PASSWORD + "/" + USER_ID + "/" + 
                   FIRST_NAME + "/" + LAST_NAME + "/false/false";
        
        assertEquals(expected, userWithId.toString(), "toString with explicit ID should match");
    }
    
    @Test
    public void testToStringClient() {
        String expected = user.getId() + "/" + USERNAME + "/" + 
                          FIRST_NAME + "/" + LAST_NAME + "/false/false";
        
        assertEquals(expected, user.toStringClient(), "toStringClient should return correctly formatted string");
    }
    
    @Test
    public void testIdUniqueness() {
        User user1 = new User("user1", "pass1", "First1", "Last1", false, false);
        User user2 = new User("user2", "pass2", "First2", "Last2", false, false);
        
        assertNotEquals(user1.getId(), user2.getId(), "Auto-generated IDs should be unique");
    }
    
    @Test
    public void testSpecialCharacters() {
        
    	User specialUser = new User("user@name", "pass/word!", "First#Name", "Last&Name", false, false);
        
        assertEquals("user@name", specialUser.getUserName(), "Username with special chars should be stored correctly");
        assertEquals("pass/word!", specialUser.getPassword(), "Password with special chars should be stored correctly");
        assertEquals("First#Name", specialUser.getFirstName(), "First name with special chars should be stored correctly");
        assertEquals("Last&Name", specialUser.getLastName(), "Last name with special chars should be stored correctly");
        
        String toString = specialUser.toString();
        assertTrue(toString.contains("user@name"), "toString should include special username");
        assertTrue(toString.contains("pass/word!"), "toString should include special password");
    }
    
    // helper to make mock chats
    private Chat createMockChat(String name, String id) {
        // just as a disclaimer, these mock chats are hard to create, so consider this an abstraction
    	// assumes we have access to chat constructor
    	List<AbstractUser> chatters = new ArrayList<>();
        chatters.add(user);
        
        // we are trying to get a chat that already exists, we are testing user, not chat
        try {
            // use reflect
        	java.lang.reflect.Constructor<Chat> constructor = 
                Chat.class.getDeclaredConstructor(AbstractUser.class, String.class, String.class, 
                                                List.class, boolean.class);
            constructor.setAccessible(true);
            return constructor.newInstance(user, name, id, chatters, false);
        } catch (Exception e) {
        	//if reflection fails, use other constructor for basic testing
        	// this is a simple approach that will pass tests
        	return new Chat(user, name, id, chatters, false);
        }
    }
}
