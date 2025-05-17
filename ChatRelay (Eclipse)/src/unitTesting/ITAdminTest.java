package unitTesting;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import chatRelay.AbstractUser;
import chatRelay.Chat;
import chatRelay.ITAdmin;

import java.util.ArrayList;
import java.util.List;

public class ITAdminTest {
    
    private ITAdmin admin;
    private ITAdmin adminWithId;
    private ITAdmin clientAdmin;
    private static final String USERNAME = "adminuser";
    private static final String PASSWORD = "adminpass123";
    private static final String ADMIN_ID = "A12345";
    private static final String FIRST_NAME = "Admin";
    private static final String LAST_NAME = "User";
    
    @BeforeEach
    public void setUp() {
    	// create admin instances to perform tests on
    	// one has a auto generated ID
        admin = new ITAdmin(USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, false, true);
        
        // one has a loaded ID (like in the db)
        adminWithId = new ITAdmin(USERNAME, PASSWORD, ADMIN_ID, FIRST_NAME, LAST_NAME, false, true);
    }
    
    @Test
// test admin with auto generated ID
    public void testAutoIdConstructor() {
        assertNotNull(admin, "Admin should be created");
        assertNotNull(admin.getId(), "ID should be auto-generated");
        assertEquals(USERNAME, admin.getUserName(), "Username should match");
        assertEquals(PASSWORD, admin.getPassword(), "Password should match");
        assertEquals(FIRST_NAME, admin.getFirstName(), "First name should match");
        assertEquals(LAST_NAME, admin.getLastName(), "Last name should match");
        assertFalse(admin.isDisabled(), "Admin should not be disabled by default");
        assertTrue(admin.isAdmin(), "Admin should have admin privileges");
        assertEquals(0, admin.getChats().size(), "Admin should have no chats initially");
    }
    
    // test admin constructor with an explicit ID
    
    @Test
    public void testExplicitIdConstructor() {
        assertNotNull(adminWithId, "Admin should be created");
        assertEquals(ADMIN_ID, adminWithId.getId(), "ID should match provided value");
        assertEquals(USERNAME, adminWithId.getUserName(), "Username should match");
        assertEquals(PASSWORD, adminWithId.getPassword(), "Password should match");
        assertEquals(FIRST_NAME, adminWithId.getFirstName(), "First name should match");
        assertEquals(LAST_NAME, adminWithId.getLastName(), "Last name should match");
        assertFalse(adminWithId.isDisabled(), "Admin should not be disabled by default");
        assertTrue(adminWithId.isAdmin(), "Admin should have admin privileges");
    }
    
 
    // test creating a disabled admin
    @Test
    public void testDisabledAdmin() {
        ITAdmin disabledAdmin = new ITAdmin(USERNAME, PASSWORD, FIRST_NAME, LAST_NAME, true, true);
        assertTrue(disabledAdmin.isDisabled(), "Admin should be disabled");
        assertTrue(disabledAdmin.isAdmin(), "Should still have admin privileges even when disabled");
    }
    
    // test adding a chat to admin
    @Test
    public void testAddChat() {
        //mock chat
    	Chat mockChat = createMockChat("Admin Chat", "A1");
        
        // add the chat ot the admin
        admin.addChat(mockChat);
        
        // check if chat was actually added
        List<Chat> adminChats = admin.getChats();
        assertEquals(1, adminChats.size(), "Admin should have one chat");
        assertEquals(mockChat, adminChats.get(0), "Added chat should match mock chat");
        
        // add another chat
        Chat mockChat2 = createMockChat("Second Admin Chat", "A2");
        admin.addChat(mockChat2);
        
        // check if chat was actually added
        adminChats = admin.getChats();
        assertEquals(2, adminChats.size(), "Admin should have two chats");
        assertTrue(adminChats.contains(mockChat), "Admin chats should contain first mock chat");
        assertTrue(adminChats.contains(mockChat2), "Admin chats should contain second mock chat");
    }
    
    // get all IDs
    @Test
    public void testGetAllChatIds() {
        // add multiple chats to admin
        Chat chat1 = createMockChat("Admin Chat 1", "A101");
        Chat chat2 = createMockChat("Admin Chat 2", "A102");
        Chat chat3 = createMockChat("Admin Chat 3", "A103");
        
        admin.addChat(chat1);
        admin.addChat(chat2);
        admin.addChat(chat3);
        
        // grab all IDs of chat
        ArrayList<String> chatIds = admin.getAllChatIds();
        
        //verify chat ids were actually added
        assertEquals(3, chatIds.size(), "Should return 3 chat IDs");
        assertTrue(chatIds.contains("A101"), "Should contain first chat ID");
        assertTrue(chatIds.contains("A102"), "Should contain second chat ID");
        assertTrue(chatIds.contains("A103"), "Should contain third chat ID");
    }
    
    // test updating disabled status
    @Test
    public void testUpdateIsDisabled() {
        //not disabled at first
    	assertFalse(admin.isDisabled(), "Admin should start as not disabled");
        
        // now is disabled
    	admin.updateIsDisabled(true);
        assertTrue(admin.isDisabled(), "Admin should be disabled after update");
        
        // update back to not disabled
        admin.updateIsDisabled(false);
        assertFalse(admin.isDisabled(), "Admin should be enabled after update");
        
        // admin privileges should still persist 
        assertTrue(admin.isAdmin(), "Should maintain admin privileges when disabled status changes");
    }
    
    @Test
    // test toString...
    public void testToString() {
        String expected = USERNAME + "/" + PASSWORD + "/" + admin.getId() + "/" + 
                          FIRST_NAME + "/" + LAST_NAME + "/false/true";
        
        assertEquals(expected, admin.toString(), "toString should return correctly formatted string");
        
        expected = USERNAME + "/" + PASSWORD + "/" + ADMIN_ID + "/" + 
                   FIRST_NAME + "/" + LAST_NAME + "/false/true";
        
        assertEquals(expected, adminWithId.toString(), "toString with explicit ID should match");
    }
    
    // test that ITAdmin is an instance of AbstractUser
 
    @Test
    public void testInheritance() {
        assertTrue(admin instanceof AbstractUser, "ITAdmin should be an instance of AbstractUser");
    }
    
    // create mock chat, like we have done for other tests
    private Chat createMockChat(String name, String id) {
        List<AbstractUser> chatters = new ArrayList<>();
        chatters.add(admin);
        
        return new Chat(admin, name, id, chatters, false);
    }
}
