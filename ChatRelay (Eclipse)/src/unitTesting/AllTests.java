package unitTesting;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)

@SuiteClasses({
	 DBManagerTesting.class, DBManagerTest.class, ChatTest.class, MessageTesting.class, 
	 UserTest.class, ITAdminTest.class, ClientTesting.class, 
	 ServerTesting.class, PacketTesting.class
})

public class AllTests {
}