/**
 * User class demonstrating Inheritance by extending TrackerEvent.
 * Represents a user account in the Habit Tracker system.
 */
public class User extends TrackerEvent {
    // Private fields for Encapsulation
    private String username;
    private String email;

    /**
     * Constructor for User
     * @param id The unique identifier for the user
     * @param name The display name of the user
     * @param username The login username
     * @param email The user's email address
     */
    public User(int id, String name, String username, String email) {
        super(id, name);
        this.username = username;
        this.email = email;
    }

    // Getters and Setters for Encapsulation
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Implementation of the abstract method from TrackerEvent.
     * Demonstrates Abstraction and Polymorphism.
     * @return The details of the user
     */
    @Override
    public String getDetails() {
        return "User: " + getName() + " (@" + username + ")";
    }

    /**
     * Override toString for display purposes
     * @return The name and username of the user
     */
    @Override
    public String toString() {
        return getName() + " (@" + username + ")";
    }
}
