package unitTesting;

//import org.junit.jupiter.api.Test;

import chatRelay.Server;

public class ServerMainTesting {
    public static void testServerConnect() {
    	Server server = new Server(1234, "nothing");
    	server.connect();
    }
    
    public static void main(String[] args) {
		testServerConnect();
	}
}