/**
 * Habit class demonstrating Inheritance by extending TrackerEvent.
 * Represents a habit that can be tracked.
 */
public class Habit extends TrackerEvent {
    
    /**
     * Constructor for Habit
     * @param id The unique identifier for the habit
     * @param name The name of the habit
     */
    public Habit(int id, String name) {
        super(id, name);
    }

    /**
     * Implementation of the abstract method from TrackerEvent.
     * Demonstrates Abstraction and Polymorphism.
     * @return The details of the habit (its name)
     */
    @Override
    public String getDetails() {
        return "Habit: " + getName();
    }

    /**
     * Override toString for JComboBox display
     * @return The name of the habit
     */
    @Override
    public String toString() {
        return getName();
    }
}
