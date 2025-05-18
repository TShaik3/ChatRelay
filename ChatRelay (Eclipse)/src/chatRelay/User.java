package chatRelay;



public class User extends AbstractUser {
    
    //constructor for loading from file (has ID)
    public User(String username, String password, String id, String firstName, String lastName, boolean isDisabled, boolean isAdmin) {
        super(username, password, id, firstName, lastName, isDisabled, isAdmin);
    }
    
    //constructor for creating new user (no ID passed)
    public User(String username, String password, String firstName, String lastName, boolean isDisabled, boolean isAdmin) {
        super(username, password, firstName, lastName, isDisabled, isAdmin);
    }
    
 // constructor for client to make users (No Password Passed)
    public User(boolean frontEndUser, String id, String username, String firstName, String lastName, boolean isDisabled, boolean isAdmin) {
        super(frontEndUser, id, username, firstName, lastName, isDisabled, isAdmin);
    }
}
