package unitTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chatRelay.AbstractUser;
import chatRelay.Chat;
import chatRelay.Client;
import chatRelay.Packet;
import chatRelay.Status;
import chatRelay.User;
import chatRelay.actionType;

public class ClientTesting {
    
    private Client client;
    private TestSocketStub socketStub;
    private ObjectOutputStream testOutputStream;
    private ByteArrayOutputStream outputCapture;
    
    @BeforeEach
    public void setUp() throws Exception {
    	// create a socket stub and output stream to mock a server instantiation 

        outputCapture = new ByteArrayOutputStream();
        testOutputStream = new ObjectOutputStream(outputCapture);
        socketStub = new TestSocketStub(testOutputStream);
    }
    
    @Test
    public void testClientConstructor() {
    	// test client has a valid ip and port
        // this is a mock server that will act like a real server, but isn't actually require a network connection

    	client = new Client("localhost", "1234");
        assertNotNull(client);
    }
    
    @Test
    public void testCreateChat() throws Exception {
    	//test that createchat forms packets
        client = new Client("localhost", "1234");
        
        //using reflection to get id
        setPrivateField(client, "userId", "test-user-id");
        
        // initialize objectstream
        setPrivateField(client, "objectStream", testOutputStream);
        
        // call the method
        String[] userIds = {"user1", "user2"};
        client.createChat(userIds, "Test Chat", true);
        
        // reset outputstrema to position to read from the start
        outputCapture.flush();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputCapture.toByteArray());
        ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        
        // read and verify the packet
        Packet sentPacket = (Packet) objectInput.readObject();
        
        assertEquals(actionType.CREATE_CHAT, sentPacket.getActionType());
        assertEquals(Status.NONE, sentPacket.getStatus());
        assertEquals("test-user-id", sentPacket.getSenderId());
        
        List<String> args = sentPacket.getActionArguments();
        assertEquals(3, args.size());
        assertEquals("user1/user2", args.get(0));
        assertEquals("Test Chat", args.get(1));
        assertEquals("true", args.get(2));
    }
    
    @Test
    public void testSendMessage() throws Exception {
    	
    	// test that sendMessage makes  packets correctly
          client = new Client("localhost", "1234");
        
        // use reflection to set id
        setPrivateField(client, "userId", "test-user-id");
        
        // initialze objectstream with test stream
        setPrivateField(client, "objectStream", testOutputStream);
        
        client.sendMessage("chat-123", "Hello, world!");
        
        // flush output stream, reset
        outputCapture.flush();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputCapture.toByteArray());
        ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        
        // read, verify packet
        Packet sentPacket = (Packet) objectInput.readObject();
        
        assertEquals(actionType.SEND_MESSAGE, sentPacket.getActionType());
        assertEquals(Status.NONE, sentPacket.getStatus());
        assertEquals("test-user-id", sentPacket.getSenderId());
        
        List<String> args = sentPacket.getActionArguments();
        assertEquals(2, args.size());
        assertEquals("Hello, world!", args.get(0));
        assertEquals("chat-123", args.get(1));
    }
    
 
    
    @Test
    public void testGetters() throws Exception {
        client = new Client("localhost", "1234");
        
        // set up test data
        setPrivateField(client, "isConnected", true);
        
        User testUser = new User("testuser", "password", "user1", "Test", "User", false, false);
        setPrivateField(client, "thisUser", testUser);
        
        List<Chat> testChats = new ArrayList<>();
        Chat testChat = new Chat(testUser, "Test Chat", new ArrayList<>(), false);
        testChats.add(testChat);
        setPrivateField(client, "chats", testChats);
        
        List<AbstractUser> testUsers = new ArrayList<>();
        testUsers.add(testUser);
        setPrivateField(client, "users", testUsers);
        
        setPrivateField(client, "isITAdmin", false);
        
        // test getters
        assertTrue(client.getIsConnected());
        assertEquals(testUser, client.getThisUser());
        assertFalse(client.getAdminStatus());
        assertEquals(testChats, client.getChats());
        assertEquals(testUsers, client.getUsers());
    }
    
    @Test
    public void testUpdateUser() throws Exception {
    	// test that updateUser makes packets correctly
        client = new Client("localhost", "1234");
        
        // using reflectino to get the user id
        setPrivateField(client, "userId", "test-admin-id");
        setPrivateField(client, "isITAdmin", true);
        
        // initialize objectstream with our test stream
        setPrivateField(client, "objectStream", testOutputStream);
        
        // disable user
        client.updateUser("user-to-update", true);
        
        //flush output stream, reset stream
        outputCapture.flush();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputCapture.toByteArray());
        ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        
        // read and verify the packet
        Packet sentPacket = (Packet) objectInput.readObject();
        
        assertEquals(actionType.UPDATE_USER, sentPacket.getActionType());
        assertEquals(Status.NONE, sentPacket.getStatus());
        assertEquals("test-admin-id", sentPacket.getSenderId());
        
        List<String> args = sentPacket.getActionArguments();
        assertEquals(2, args.size());
        assertEquals("user-to-update", args.get(0));
        assertEquals("true", args.get(1));
    }
        
    @Test
    public void testGetAllUsers() throws Exception {
        // ensure packet is being made correctly
    	client = new Client("localhost", "1234");
        
        // use reflection to get user id
        setPrivateField(client, "userId", "test-user-id");
        
        // initialize objectStream with test stream
        setPrivateField(client, "objectStream", testOutputStream);
        
        // call the method
        client.getAllUsers();
        
        // flush output stream, reset stream 
        outputCapture.flush();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputCapture.toByteArray());
        ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        
        // read and perform tests on packet
        Packet sentPacket = (Packet) objectInput.readObject();
        
        assertEquals(actionType.GET_ALL_USERS, sentPacket.getActionType());
        assertEquals(Status.NONE, sentPacket.getStatus());
        assertEquals("test-user-id", sentPacket.getSenderId());
        
        
        // arguments should be empty
        List<String> args = sentPacket.getActionArguments();
        assertTrue(args.isEmpty());
    }
    
    @Test
    public void testGetAllChats() throws Exception {
        // set up test 
        client = new Client("localhost", "1234");
        
        // use reflection for id
        setPrivateField(client, "userId", "test-user-id");
        
        // initialize objectstream with test stream
        setPrivateField(client, "objectStream", testOutputStream);
        
        // call the method
        client.getAllChats();
        
        // reset ouput stream
        outputCapture.flush();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputCapture.toByteArray());
        ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        
        // read and do tests on packet
        Packet sentPacket = (Packet) objectInput.readObject();
        
        assertEquals(actionType.GET_ALL_CHATS, sentPacket.getActionType());
        assertEquals(Status.NONE, sentPacket.getStatus());
        assertEquals("test-user-id", sentPacket.getSenderId());
        
        //arguments should be empty
        List<String> args = sentPacket.getActionArguments();
        assertTrue(args.isEmpty());
    }
    
    @Test
    public void testLogout() throws Exception {
    	// this test is partial, logout closes stream so we cannot perform
    	// extensive tests on this

        client = new Client("localhost", "1234");
        setPrivateField(client, "userId", "test-user-id");
        setPrivateField(client, "isConnected", true);
        
        // initialize objectStream with our test stream
        setPrivateField(client, "objectStream", testOutputStream);
        
        try {
        	// logout closes stream, that throws an exception
        	// for testing, we will just be making the packet

            client.logout();
        } catch (NullPointerException e) {
            
        }
        
        // reset outputstream
        outputCapture.flush();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputCapture.toByteArray());
        ObjectInputStream objectInput = new ObjectInputStream(inputStream);
        
        // read the packet and perform tests
        Packet sentPacket = (Packet) objectInput.readObject();
        
        assertEquals(actionType.LOGOUT, sentPacket.getActionType());
        assertEquals(Status.NONE, sentPacket.getStatus());
        assertEquals("test-user-id", sentPacket.getSenderId());
        
        // arguments should be empty
        List<String> args = sentPacket.getActionArguments();
        assertTrue(args.isEmpty());
    }
    
    // helper method to set private fields for reflection
    private void setPrivateField(Object object, String fieldName, Object value) throws Exception {
        Field field = object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
    }
    
    //  mock Socket class to test
    private static class TestSocketStub extends Socket {
        private ObjectOutputStream outputStream;
        
        public TestSocketStub(ObjectOutputStream outputStream) {
            this.outputStream = outputStream;
        }
        
        @Override
        public ObjectOutputStream getOutputStream() throws IOException {
            return outputStream;
        }
    }
}
