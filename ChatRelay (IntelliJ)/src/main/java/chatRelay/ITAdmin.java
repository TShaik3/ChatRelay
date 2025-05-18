package chatRelay;

public class ITAdmin extends AbstractUser {

	// constructor for loading from file (has ID)
	public ITAdmin(String username, String password, String id, String firstName, String lastName, boolean isDisabled,
			boolean isAdmin) {
		super(username, password, id, firstName, lastName, isDisabled, isAdmin);
	}

	// constructor for creating new admin (no ID passed)
	public ITAdmin(String username, String password, String firstName, String lastName, boolean isDisabled,
			boolean isAdmin) {
		super(username, password, firstName, lastName, isDisabled, isAdmin);
	}
	
	// constructor for client to make users (No Password Passed)
	public ITAdmin(boolean frontEndUser, String id, String username, String firstName, String lastName, boolean isDisabled, boolean isAdmin) {
		super(frontEndUser, id, username, firstName, lastName, isDisabled, isAdmin);
	}
}
