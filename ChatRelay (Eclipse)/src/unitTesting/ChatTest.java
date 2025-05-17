package unitTesting;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chatRelay.AbstractUser;
import chatRelay.Chat;
import chatRelay.Message;
import chatRelay.User;

public class ChatTest {
    
    private User owner;
    private User user1;
    private User user2;
    private Chat chat;
    
    @BeforeEach
    public void setUp() {
        // make test users
        owner = new User("Zoheb", "password123", "id1", "Zoheb", "Sharif", false, false);
        user1 = new User("Talhah", "password456", "id2", "Talhah", "Shaik", false, false);
        user2 = new User("Kenny", "password789", "id3", "Kenny", "Kottenstette", false, false);
        
        // make test chat
        List<AbstractUser> initialChatters = new ArrayList<>();
        initialChatters.add(owner);
        chat = new Chat(owner, "Test Chat Room", initialChatters, false);
    }
    
    @Test
    public void testChatConstructor() {
        // now, test that chat was created successfully
        assertEquals("Test Chat Room", chat.getRoomName());
        assertEquals(owner, chat.getOwner());
        assertFalse(chat.isPrivate());
        assertNotNull(chat.getId());
        // ID is now a simple number rather than "CHAT_X"
        assertTrue(chat.getId().matches("\\d+"));
        
        // test owner is a chatter
        List<AbstractUser> chatters = chat.getChatters();
        assertEquals(1, chatters.size());
        assertEquals(owner, chatters.get(0));
        
        // test that messages start as empty
        assertEquals(0, chat.getMessages().size());
    }
    
    @Test
    public void testSecondConstructor() {
        // test creation of chat using second constructor
        // Now requires List<AbstractUser> instead of User[]
        List<AbstractUser> users = new ArrayList<>();
        users.add(owner);
        users.add(user1);
        Chat chat2 = new Chat(owner, "Another Chat", "TEST_ID", users, false);
        
        // check that chat is initialized correctly
        assertEquals("Another Chat", chat2.getRoomName());
        assertEquals(owner, chat2.getOwner());
        assertEquals("TEST_ID", chat2.getId());
        
        // test users were added correctly
        List<AbstractUser> chatters = chat2.getChatters();
        assertEquals(2, chatters.size());
        assertTrue(chatters.contains(owner));
        assertTrue(chatters.contains(user1));
    }
    
    @Test
    public void testAddChatter() {
        //  add new user to chat
        chat.addChatter(user1);
        
        // check is user was actually added
        List<AbstractUser> chatters = chat.getChatters();
        assertEquals(2, chatters.size());
        assertEquals(owner, chatters.get(0));
        assertEquals(user1, chatters.get(1));
        
        // add another user
        chat.addChatter(user2);
        
        // check if this other user was added successfully
        chatters = chat.getChatters();
        assertEquals(3, chatters.size());
        assertEquals(owner, chatters.get(0));
        assertEquals(user1, chatters.get(1));
        assertEquals(user2, chatters.get(2));
        
        // try adding the same user twice (duplication prevention)
        chat.addChatter(user1);
        assertEquals(3, chat.getChatters().size());
    }
    
    @Test
    public void testRemoveChatter() {
        // add users to chat
        chat.addChatter(user1);
        chat.addChatter(user2);
        
        // check initial state
        assertEquals(3, chat.getChatters().size());
        
        // remove user
        chat.removeChatter(user1);
        
        // check if user was removed correctly
        List<AbstractUser> chatters = chat.getChatters();
        assertEquals(2, chatters.size());
        assertEquals(owner, chatters.get(0));
        assertEquals(user2, chatters.get(1));
        
        // try to remove last user (shouldn't remove)
        chat.removeChatter(user2);
        chat.removeChatter(owner);
        
        // check that one user is still there (owner)
        chatters = chat.getChatters();
        assertEquals(1, chatters.size());
        assertEquals(owner, chatters.get(0));
    }
    
    @Test
    public void testAddMessage() {
        // create test message
        Message message1 = new Message("Hello, world!", owner, chat);
        
        // add message
        chat.addMessage(message1);
        
        //check is message was added
        List<Message> messages = chat.getMessages();
        assertEquals(1, messages.size());
        assertEquals(message1, messages.get(0));
        
        // add message
        Message message2 = new Message("How are you?", user1, chat);
        chat.addMessage(message2);
        
        // check if message was added
        messages = chat.getMessages();
        assertEquals(2, messages.size());
        assertEquals(message1, messages.get(0));
        assertEquals(message2, messages.get(1));
    }
    
    @Test
    public void testChangePrivacy() {
        // Test starting with public chat (false)
        assertFalse(chat.isPrivate());
        
        // Make it private
        chat.changePrivacy(true);
        assertTrue(chat.isPrivate());
        
        // Make it public again
        chat.changePrivacy(false);
        assertFalse(chat.isPrivate());
    }
    
    @Test
    public void testToString() {
        // test toString
        String chatString = chat.toString();
        
        // verify toString contains vital information
        assertTrue(chatString.contains(chat.getId()));
        assertTrue(chatString.contains("Test Chat Room"));
        assertTrue(chatString.contains(owner.getId()));
        assertTrue(chatString.contains("false"));
    }
    
    public static void main(String[] args) {
        // allow tests to run
        org.junit.platform.launcher.core.LauncherFactory
            .create()
            .execute(org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder
                    .request()
                    .selectors(org.junit.platform.engine.discovery.DiscoverySelectors.selectClass(ChatTest.class))
                    .build());
    }
}
