import java.time.LocalDate;
import java.util.Map;

/**
 * Interface demonstrating Interface and Polymorphism.
 * Defines a contract for exporting habit logs to a file.
 */
public interface IFileExporter {
    /**
     * Exports habit logs to a file
     * @param habitName The name of the habit
     * @param logs A map of dates to completion status
     */
    void export(String habitName, Map<LocalDate, Boolean> logs);
}
