package unitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import chatRelay.AbstractUser;
import chatRelay.Chat;
import chatRelay.DBManager;
import chatRelay.ITAdmin;
import chatRelay.Message;

public class DBManagerTesting {
    
    private DBManager dbManager;
    private String testDirPath;
    private String usersFilePath;
    private String chatsFilePath;
    private String messagesFilePath;

    @BeforeEach
    public void setUp() throws IOException {
        // set up directories and files
        testDirPath = "./src/chatRelay/dbFiles/test/";
        usersFilePath = testDirPath + "Users.txt";
        chatsFilePath = testDirPath + "Chats.txt";
        messagesFilePath = testDirPath + "Messages.txt";
        
        // create directories if they do not already exist, this is completely for testing purposes
        new File(testDirPath).mkdirs();
        
        // create initial test data
        createTestUserData();
        createTestChatData();
        createTestMessageData();
        
     	// now, instantiate a DBManager with these files.
        dbManager = new DBManager(testDirPath, "Users.txt", "Chats.txt", "Messages.txt");
    }
    
    @AfterEach
    public void tearDown() {
       // clean up tests files, will be replaced with new ones
        new File(usersFilePath).delete();
        new File(chatsFilePath).delete();
        new File(messagesFilePath).delete();
    }
    
    private void createTestUserData() throws IOException {
        try (FileWriter writer = new FileWriter(usersFilePath)) {
   
            writer.write("testuser1/pass123/1/John/Doe/false/false\n");
            writer.write("testuser2/pass456/2/Jane/Smith/false/false\n");
            writer.write("testadmin/adminpass/3/Admin/User/false/true\n");
            writer.write("disableduser/pass789/4/Disabled/User/true/false\n");
        }
    }
    
    private void createTestChatData() throws IOException {
        try (FileWriter writer = new FileWriter(chatsFilePath)) {
        	
        	writer.write("1/1/General Chat/false/1,2,3\n");
            writer.write("2/2/Private Chat/true/2,3\n");
        }
    }
    
    private void createTestMessageData() throws IOException {
        try (FileWriter writer = new FileWriter(messagesFilePath)) {

            long currentTime = System.currentTimeMillis();
            writer.write("1/" + currentTime + "/Hello everyone!/1/1\n");
            writer.write("2/" + (currentTime + 1000) + "/Hi there/2/1\n");
            writer.write("3/" + (currentTime + 2000) + "/Private message/2/2\n");
        }
    }
    
    @Test

    public void testLoadUsers() {
    	
        // check if users were added correctly
        AbstractUser user1 = dbManager.getUserById("1");
        AbstractUser user2 = dbManager.getUserById("2");
        AbstractUser admin = dbManager.getUserById("3");
        AbstractUser disabled = dbManager.getUserById("4");
        
        assertNotNull(user1, "User 1 should be loaded");
        assertNotNull(user2, "User 2 should be loaded");
        assertNotNull(admin, "Admin user should be loaded");
        assertNotNull(disabled, "Disabled user should be loaded");
        
        assertEquals("testuser1", user1.getUserName(), "Username should match");
        assertEquals("John", user1.getFirstName(), "First name should match");
        assertEquals("Doe", user1.getLastName(), "Last name should match");
        assertFalse(user1.isAdmin(), "Regular user should not be admin");
        assertFalse(user1.isDisabled(), "Regular user should not be disabled");
        
        assertTrue(admin.isAdmin(), "Admin user should have admin privileges");
        assertTrue(disabled.isDisabled(), "Disabled user should be marked as disabled");
    }
    
    @Test

    public void testLoadChats() {
        // now, check if chats were loaded correctly
        Chat chat1 = dbManager.getChatById("1");
        Chat chat2 = dbManager.getChatById("2");
        
        assertNotNull(chat1, "Chat 1 should be loaded");
        assertNotNull(chat2, "Chat 2 should be loaded");
        
        assertEquals("General Chat", chat1.getRoomName(), "Chat name should match");
        assertEquals("1", chat1.getOwner().getId(), "Owner ID should match");
        assertFalse(chat1.isPrivate(), "General chat should be public");
        
        assertEquals("Private Chat", chat2.getRoomName(), "Chat name should match");
        assertEquals("2", chat2.getOwner().getId(), "Owner ID should match");
        assertTrue(chat2.isPrivate(), "Private chat should be private");
        
        // check chat to user relationships
        List<AbstractUser> chat1Chatters = chat1.getChatters();
        assertEquals(3, chat1Chatters.size(), "General chat should have 3 users");
        
        List<AbstractUser> chat2Chatters = chat2.getChatters();
        assertEquals(2, chat2Chatters.size(), "Private chat should have 2 users");
        
        // check user object possesses respective chats
        AbstractUser user1 = dbManager.getUserById("1");
        AbstractUser user2 = dbManager.getUserById("2");
        
        assertTrue(user1.getChats().contains(chat1), "User 1 should be in general chat");
        assertTrue(user2.getChats().contains(chat1), "User 2 should be in general chat");
        assertTrue(user2.getChats().contains(chat2), "User 2 should be in private chat");
    }
    
    @Test
 
    public void testLoadMessages() {
        // get chats, check their messages
        Chat chat1 = dbManager.getChatById("1");
        Chat chat2 = dbManager.getChatById("2");
        
        assertNotNull(chat1, "Chat 1 should be loaded");
        assertNotNull(chat2, "Chat 2 should be loaded");
        
        // cbeck if messages were properly loaded from their chats
        List<Message> chat1Messages = chat1.getMessages();
        List<Message> chat2Messages = chat2.getMessages();
        
        assertEquals(2, chat1Messages.size(), "General chat should have 2 messages");
        assertEquals(1, chat2Messages.size(), "Private chat should have 1 message");
        
        //check the content of the message
        assertEquals("Hello everyone!", chat1Messages.get(0).getContent(), "Message content should match");
        assertEquals("Hi there", chat1Messages.get(1).getContent(), "Message content should match");
        assertEquals("Private message", chat2Messages.get(0).getContent(), "Message content should match");
        
        // check the relationship between message user
        assertEquals("1", chat1Messages.get(0).getSender().getId(), "Message sender should match");
        assertEquals("2", chat1Messages.get(1).getSender().getId(), "Message sender should match");
        assertEquals("2", chat2Messages.get(0).getSender().getId(), "Message sender should match");
    }
    
    @Test

    public void testWriteNewUser() {
        // instantiate a new user
        AbstractUser newUser = dbManager.writeNewUser("newuser", "newpass", "New", "User", false, false);
        
        assertNotNull(newUser, "New user should be created");
        assertNotNull(newUser.getId(), "New user should have an ID");
        assertEquals("newuser", newUser.getUserName(), "Username should match");
        assertEquals("New", newUser.getFirstName(), "First name should match");
        
        // check if it is possible to get a user by ID
        AbstractUser retrievedUser = dbManager.getUserById(newUser.getId());
        assertNotNull(retrievedUser, "User should be retrievable by ID");
        assertEquals(newUser.getUserName(), retrievedUser.getUserName(), "Retrieved user should match created user");
        
        // check if it is possible to get a user by username
        AbstractUser retrievedByUsername = dbManager.getUserByUsername("newuser");
        assertNotNull(retrievedByUsername, "User should be retrievable by username");
        assertEquals(newUser.getId(), retrievedByUsername.getId(), "IDs should match");
    }
    
    @Test

    public void testWriteAdminUser() {
        // create a new admin user
        AbstractUser adminUser = dbManager.writeNewUser("newadmin", "adminpass", "New", "Admin", false, true);
        
        assertNotNull(adminUser, "Admin user should be created");
        assertTrue(adminUser.isAdmin(), "User should have admin privileges");
        assertFalse(adminUser.isDisabled(), "Admin should not be disabled by default");
        
       }
    
    @Test

    public void testWriteNewChat() {
        // get  users to add to the chat
        AbstractUser user1 = dbManager.getUserById("1");
        AbstractUser user2 = dbManager.getUserById("2");
        
        // createa  list of ids for the chat
        ArrayList<String> userIds = new ArrayList<>();
        userIds.add("1");
        userIds.add("2");
        
        // create a new chat
        Chat newChat = dbManager.writeNewChat("1", "Test Chat", userIds, true);
        
        assertNotNull(newChat, "New chat should be created");
        assertEquals("Test Chat", newChat.getRoomName(), "Chat name should match");
        assertEquals(user1, newChat.getOwner(), "Owner should match");
        assertTrue(newChat.isPrivate(), "Chat should be private");
        
        // check if chat can be grabbed by id
        Chat retrievedChat = dbManager.getChatById(newChat.getId());
        assertNotNull(retrievedChat, "Chat should be retrievable by ID");
        
        // check if users got added to the chat
        List<AbstractUser> chatters = newChat.getChatters();
        assertEquals(2, chatters.size(), "Chat should have 2 users");
        assertTrue(chatters.contains(user1), "Chat should contain user 1");
        assertTrue(chatters.contains(user2), "Chat should contain user 2");
        
        // check if the user received the chat
        assertTrue(user1.getChats().contains(newChat), "User 1 should have the new chat");
        assertTrue(user2.getChats().contains(newChat), "User 2 should have the new chat");
    }
    
    @Test

    public void testWriteNewMessage() {
        // make a new message
        Message newMessage = dbManager.writeNewMessage("Test message content", "1", "1");
        
        assertNotNull(newMessage, "New message should be created");
        assertEquals("Test message content", newMessage.getContent(), "Message content should match");
        assertEquals(dbManager.getUserById("1"), newMessage.getSender(), "Sender should match");
        assertEquals(dbManager.getChatById("1"), newMessage.getChat(), "Chat should match");
        
        // check if the message was added to the chat
        Chat chat = dbManager.getChatById("1");
        List<Message> messages = chat.getMessages();
        assertTrue(messages.contains(newMessage), "Chat should contain the new message");
    }
    
    
    @Test
  
    public void testLoginCredentials() {
        // test if login was valid
        AbstractUser validUser = dbManager.checkLoginCredentials("testuser1", "pass123");
        assertNotNull(validUser, "Valid user should be logged in");
        assertEquals("1", validUser.getId(), "Logged in user ID should match");
        
        // test the case where the username is invalid
        AbstractUser invalidUsername = dbManager.checkLoginCredentials("nonexistent", "pass123");
        assertNull(invalidUsername, "Invalid username should not log in");
        
        // test the case where the password was wrong
        AbstractUser invalidPassword = dbManager.checkLoginCredentials("testuser1", "wrongpass");
        assertNull(invalidPassword, "Invalid password should not log in");
        
        // test the login of a user that has been disabled
        AbstractUser disabledUser = dbManager.checkLoginCredentials("disableduser", "pass789");
        assertNotNull(disabledUser, "Disabled user should be able to log in");
        assertTrue(disabledUser.isDisabled(), "Logged in user should be marked as disabled");
    }
    
    @Test

    public void testUpdateUserIsDisabled() {
        // test enabling an a disabled user
        AbstractUser disabledUser = dbManager.getUserById("4");
        assertTrue(disabledUser.isDisabled(), "User should start disabled");
        
        AbstractUser updatedUser = dbManager.updateUserIsDisabled("4", false);
        assertNotNull(updatedUser, "User should be updated");
        assertFalse(updatedUser.isDisabled(), "User should be enabled");
        
        // check if the update is persisted
        AbstractUser retrievedUser = dbManager.getUserById("4");
        assertFalse(retrievedUser.isDisabled(), "User should remain enabled when retrieved");
        
        // test disabling a user
        AbstractUser regularUser = dbManager.getUserById("1");
        assertFalse(regularUser.isDisabled(), "User should start enabled");
        
        AbstractUser disabledRegularUser = dbManager.updateUserIsDisabled("1", true);
        assertNotNull(disabledRegularUser, "User should be updated");
        assertTrue(disabledRegularUser.isDisabled(), "User should be disabled");
        
        // check if the update is persisted
        AbstractUser retrievedRegularUser = dbManager.getUserById("1");
        assertTrue(retrievedRegularUser.isDisabled(), "User should remain disabled when retrieved");
    }
    
    @Test
    
    public void testFetchAllUsers() {
        ArrayList<String> allUsers = dbManager.fetchAllUsers();
        
        assertNotNull(allUsers, "User list should not be null");
        assertEquals(4, allUsers.size(), "Should have 4 users");
        
        // create a new user, check if it shows up in the list
        dbManager.writeNewUser("extrauser", "extrapass", "Extra", "User", false, false);
        
        ArrayList<String> updatedUsers = dbManager.fetchAllUsers();
        assertEquals(5, updatedUsers.size(), "Should have 5 users after adding one");
    }
    
    @Test
    
    public void testFetchChatsForRegularUser() {
        AbstractUser user = dbManager.getUserById("2");
        ArrayList<String> userChats = dbManager.fetchAllChats(user);
        
        assertNotNull(userChats, "Chat list should not be null");
        assertEquals(2, userChats.size(), "User 2 should have access to 2 chats");
    }
    
    @Test

    public void testFetchChatsForAdminUser() {
        AbstractUser admin = dbManager.getUserById("3");
        ArrayList<String> adminChats = dbManager.fetchAllChats(admin);
        
        assertNotNull(adminChats, "Chat list should not be null");
        assertEquals(2, adminChats.size(), "Admin should have access to all chats (2)");
        
        // createa  new chat that doesnt have an admin
        ArrayList<String> userIds = new ArrayList<>();
        userIds.add("1");
        userIds.add("2");
        dbManager.writeNewChat("1", "Admin-less Chat", userIds, true);
        
        // Admin should still see all chats
        ArrayList<String> updatedAdminChats = dbManager.fetchAllChats(admin);
        assertEquals(3, updatedAdminChats.size(), "Admin should see all 3 chats");
    }
    
    @Test

    public void testFetchMessagesForRegularUser() {
        AbstractUser user = dbManager.getUserById("1");
        ArrayList<String> userMessages = dbManager.fetchAllMessages(user);
        
        assertNotNull(userMessages, "Message list should not be null");
        assertEquals(2, userMessages.size(), "User should see messages from chats they're in");
    }
    
    @Test

    public void testFetchMessagesForAdminUser() {
        AbstractUser admin = dbManager.getUserById("3");
        ArrayList<String> adminMessages = dbManager.fetchAllMessages(admin);
        
        assertNotNull(adminMessages, "Message list should not be null");
        assertEquals(3, adminMessages.size(), "Admin should see all messages (3)");
    }
}