package unitTesting;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;

import chatRelay.ClientHandler;
import chatRelay.Server;


public class ServerTesting {
    
    private Server server;
    private static final int TEST_PORT = 1338;
    private static final String TEST_IP = "127.0.0.1";
    
    @BeforeEach
    public void setUp() {
        //create a server with stated ip and port
    	server = new Server(TEST_PORT, TEST_IP);
    }
    
    @Test
    public void testServerConstructor() {
        // test initialization of server
        assertNotNull(server);
        assertNotNull(server.getDBManager());
    }
    
    @Test
    public void testAddClient() throws Exception {
        //simple tests that clients can be added to the server
        String testUserId = "test-user-1";
        Socket socket = new Socket();
        ClientHandler clientHandler = new ClientHandler(socket, server);
        
        // get the clients hashmap using reflection
        Field clientsField = Server.class.getDeclaredField("clients");
        clientsField.setAccessible(true);
        @SuppressWarnings("unchecked")
		ConcurrentHashMap<String, ClientHandler> clients = 
            (ConcurrentHashMap<String, ClientHandler>) clientsField.get(server);
        
        // starts off with no clients
        assertEquals(0, clients.size());
        
        // add client
        server.addClient(testUserId, clientHandler);
        
        // check client was added
        assertEquals(1, clients.size());
        assertTrue(clients.containsKey(testUserId));
    }
    
    @Test
    public void testHandleLogout() throws Exception {
        // test that logging out removes client from clienthandler
        String testUserId = "test-user-1";
        Socket socket = new Socket();
        ClientHandler clientHandler = new ClientHandler(socket, server);
        
        // add client
        server.addClient(testUserId, clientHandler);
        
        //get clients hashmap using reflection
        Field clientsField = Server.class.getDeclaredField("clients");
        clientsField.setAccessible(true);
        ConcurrentHashMap<String, ClientHandler> clients = 
            (ConcurrentHashMap<String, ClientHandler>) clientsField.get(server);
        
        // verify client was added
        assertEquals(1, clients.size());
        
        // handle logout
        server.handleLogout(testUserId);
        
        // verify client was removed
        assertEquals(0, clients.size());
    }
    
    @Test
    @Disabled
    public void testConnect() {
        // this would test the server connection, 
    	// it is left blank due to the robust nature of the setup.
    }
}