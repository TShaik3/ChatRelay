package unitTesting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import chatRelay.Packet;
import chatRelay.actionType;
import java.util.ArrayList;

public class PacketTesting {
    
    // create test objects
    private static ArrayList<String> LOGIN_ARGS = new ArrayList<String>();
    private static ArrayList<String> MESSAGE_ARGS = new ArrayList<String>();
    private static ArrayList<String> CREATE_CHAT_ARGS = new ArrayList<String>();
    private static final String SENDER_ID = "user123";
    
    // create packets
    private Packet loginPacket;
    private Packet messagePacket;
    private Packet createChatPacket;
    
    @BeforeEach
    // set up ArrayLists
    // set up test packets with action types
    public void setUp() {
        // Clear ArrayLists in case tests run multiple times
        LOGIN_ARGS.clear();
        MESSAGE_ARGS.clear();
        CREATE_CHAT_ARGS.clear();
        
        LOGIN_ARGS.add("username");
        LOGIN_ARGS.add("password");
    	
        MESSAGE_ARGS.add("Hello, this is a test message!");
        MESSAGE_ARGS.add("chat001");
        
        CREATE_CHAT_ARGS.add("[user1, user2, user3]");
        CREATE_CHAT_ARGS.add("Test Chat Room");
        
        loginPacket = new Packet(null, actionType.LOGIN, LOGIN_ARGS, SENDER_ID);
        messagePacket = new Packet(null, actionType.SEND_MESSAGE, MESSAGE_ARGS, SENDER_ID);
        createChatPacket = new Packet(null, actionType.CREATE_CHAT, CREATE_CHAT_ARGS, SENDER_ID);
    }
    
    // test the constructor
    @Test
    public void testConstructor() {
        assertNotNull(loginPacket, "Packet should not be null");
        assertEquals(actionType.LOGIN, loginPacket.getActionType(), "Action type should match");
        assertEquals(LOGIN_ARGS, loginPacket.getActionArguments(), "Action arguments should match");
        assertNotNull(loginPacket.getTimeCreated(), "Creation time should not be null");
    }
    
    // check the getactiontypes
    @Test
    public void testGetActionType() {
        assertEquals(actionType.LOGIN, loginPacket.getActionType(), "getActionType() should return LOGIN for login packet");
        assertEquals(actionType.SEND_MESSAGE, messagePacket.getActionType(), "getActionType() should return SEND_MESSAGE for message packet");
        assertEquals(actionType.CREATE_CHAT, createChatPacket.getActionType(), "getActionType() should return CREATE_CHAT for create chat packet");
    }
    
    // test getactionargument methods
    @Test
    public void testGetActionArguments() {
        assertEquals(LOGIN_ARGS, loginPacket.getActionArguments(), "getActionArguments() should return correct login args");
        assertEquals(MESSAGE_ARGS, messagePacket.getActionArguments(), "getActionArguments() should return correct message args");
        assertEquals(CREATE_CHAT_ARGS, createChatPacket.getActionArguments(), "getActionArguments() should return correct create chat args");
    }
    
    // test gettimecreated method
    @Test
    public void testGetTimeCreated() {
        LocalTime now = LocalTime.now();
        LocalTime packetTime = loginPacket.getTimeCreated();
        
        // time difference should be very small (within a few seconds)
        long secondsDiff = ChronoUnit.SECONDS.between(packetTime, now);
        assertTrue(secondsDiff < 5, "Packet creation time should be very close to current time");
    }
    
    // test the serialization 
    @Test
    public void testSerialization() throws IOException, ClassNotFoundException {
        Packet testPacket = new Packet(null, actionType.SEND_MESSAGE, MESSAGE_ARGS, "serialTest123");
        
        // serialize the packet
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(testPacket);
        oos.close();
        
        // deserialze the pack et
        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bais);
        Packet deserializedPacket = (Packet) ois.readObject();
        ois.close();
        
        // verify deserialized packet matches original
        assertEquals(testPacket.getActionType(), deserializedPacket.getActionType(), "Action type should match after serialization");
        assertEquals(testPacket.getActionArguments(), deserializedPacket.getActionArguments(), "Action arguments should match after serialization");
        
    
    }
        
    // test with empty action arguments
    @Test
    public void testEmptyActionArguments() {
        // create packet with empty ArrayList
        ArrayList<String> emptyArgs = new ArrayList<>();
        Packet emptyArgsPacket = new Packet(null, actionType.LOGOUT, emptyArgs, SENDER_ID);
        
        assertEquals(0, emptyArgsPacket.getActionArguments().size(), "Action arguments ArrayList should be empty");
        assertEquals(actionType.LOGOUT, emptyArgsPacket.getActionType(), "Action type should be LOGOUT");
    }
    
    // test with null action arguments
    @Test
    public void testNullActionArguments() {
        // create packet with null action args
        Packet nullArgsPacket = new Packet(null, actionType.LOGOUT, null, SENDER_ID);
        
        assertNull(nullArgsPacket.getActionArguments(), "Action arguments should be null");
    }
    
    // test varied action types
    @Test
    public void testAllActionTypes() {
        // create ArrayList for each test case
        ArrayList<String> emptyArgs = new ArrayList<>();
        ArrayList<String> successArgs = new ArrayList<>();
        successArgs.add("Operation completed successfully");
        ArrayList<String> errorArgs = new ArrayList<>();
        errorArgs.add("Error occurred");
        ArrayList<String> createUserArgs = new ArrayList<>();
        createUserArgs.add("username");
        createUserArgs.add("password");
        createUserArgs.add("firstName");
        createUserArgs.add("lastName");
        ArrayList<String> enableUserArgs = new ArrayList<>();
        enableUserArgs.add("user456");
        ArrayList<String> disableUserArgs = new ArrayList<>();
        disableUserArgs.add("user789");

        Packet loginPacket = new Packet(null, actionType.LOGIN, LOGIN_ARGS, SENDER_ID);
        Packet logoutPacket = new Packet(null, actionType.LOGOUT, emptyArgs, SENDER_ID);
        Packet sendMessagePacket = new Packet(null, actionType.SEND_MESSAGE, MESSAGE_ARGS, SENDER_ID);
        Packet getAllChatsPacket = new Packet(null, actionType.GET_ALL_CHATS, emptyArgs, SENDER_ID);
        Packet getAllUsersPacket = new Packet(null, actionType.GET_ALL_USERS, emptyArgs, SENDER_ID);
        Packet createChatPacket = new Packet(null, actionType.CREATE_CHAT, CREATE_CHAT_ARGS, SENDER_ID);
        Packet successPacket = new Packet(null, actionType.SUCCESS, successArgs, SENDER_ID);
        Packet errorPacket = new Packet(null, actionType.ERROR, errorArgs, SENDER_ID);
        Packet createUserPacket = new Packet(null, actionType.CREATE_USER, createUserArgs, SENDER_ID);
        Packet enableUserPacket = new Packet(null, actionType.ENABLE_USER, enableUserArgs, SENDER_ID);
        Packet disableUserPacket = new Packet(null, actionType.DISABLE_USER, disableUserArgs, SENDER_ID);
        
        // verify all of these packets have action types
        assertEquals(actionType.LOGIN, loginPacket.getActionType());
        assertEquals(actionType.LOGOUT, logoutPacket.getActionType());
        assertEquals(actionType.SEND_MESSAGE, sendMessagePacket.getActionType());
        assertEquals(actionType.GET_ALL_CHATS, getAllChatsPacket.getActionType());
        assertEquals(actionType.GET_ALL_USERS, getAllUsersPacket.getActionType());
        assertEquals(actionType.CREATE_CHAT, createChatPacket.getActionType());
        assertEquals(actionType.SUCCESS, successPacket.getActionType());
        assertEquals(actionType.ERROR, errorPacket.getActionType());
        assertEquals(actionType.CREATE_USER, createUserPacket.getActionType());
        assertEquals(actionType.ENABLE_USER, enableUserPacket.getActionType());
        assertEquals(actionType.DISABLE_USER, disableUserPacket.getActionType());
    }
}