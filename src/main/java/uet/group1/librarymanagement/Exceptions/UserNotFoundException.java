package uet.group1.librarymanagement.Exceptions;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String userId) {
        super("User not found: " + userId);
    }
}
