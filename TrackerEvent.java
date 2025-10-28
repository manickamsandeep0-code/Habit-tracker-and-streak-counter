/**
 * Abstract base class demonstrating Abstraction and Inheritance.
 * All tracker events have an id and name, and must implement getDetails().
 */
public abstract class TrackerEvent {
    // Private fields for Encapsulation
    private int id;
    private String name;

    /**
     * Constructor for TrackerEvent
     * @param id The unique identifier
     * @param name The name of the event
     */
    public TrackerEvent(int id, String name) {
        this.id = id;
        this.name = name;
    }

    // Getters and Setters for Encapsulation
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Abstract method that must be implemented by subclasses.
     * Demonstrates Abstraction.
     * @return Details about the tracker event
     */
    public abstract String getDetails();
}
