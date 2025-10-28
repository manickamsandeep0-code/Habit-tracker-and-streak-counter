import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.Map;
import java.util.TreeMap;

/**
 * TxtFileExporter demonstrates Interface implementation and Polymorphism.
 * Exports habit logs to a text file.
 */
public class TxtFileExporter implements IFileExporter {
    // Private field for Encapsulation
    private String outputFileName;

    /**
     * Constructor for TxtFileExporter
     * @param outputFileName The name of the output file
     */
    public TxtFileExporter(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    // Getter and Setter for Encapsulation
    public String getOutputFileName() {
        return outputFileName;
    }

    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * Implementation of the export method from IFileExporter.
     * Demonstrates Exception Handling with try-catch-finally.
     * @param habitName The name of the habit
     * @param logs A map of dates to completion status
     */
    @Override
    public void export(String habitName, Map<LocalDate, Boolean> logs) {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new FileWriter(outputFileName));
            writer.println("===========================================");
            writer.println("       HABIT TRACKER REPORT");
            writer.println("===========================================");
            writer.println("Habit: " + habitName);
            writer.println("Generated: " + LocalDate.now());
            writer.println("===========================================");
            writer.println();

            // Sort logs by date using TreeMap
            TreeMap<LocalDate, Boolean> sortedLogs = new TreeMap<>(logs);
            
            int totalDays = sortedLogs.size();
            int completedDays = 0;

            writer.println("Daily Log:");
            writer.println("-------------------------------------------");
            for (Map.Entry<LocalDate, Boolean> entry : sortedLogs.entrySet()) {
                String status = entry.getValue() ? "[X] Completed" : "[ ] Not Completed";
                writer.println(entry.getKey() + " - " + status);
                if (entry.getValue()) {
                    completedDays++;
                }
            }

            writer.println("-------------------------------------------");
            writer.println();
            writer.println("Summary:");
            writer.println("Total Days Tracked: " + totalDays);
            writer.println("Days Completed: " + completedDays);
            writer.println("Days Missed: " + (totalDays - completedDays));
            if (totalDays > 0) {
                double percentage = (completedDays * 100.0) / totalDays;
                writer.println("Completion Rate: " + String.format("%.1f", percentage) + "%");
            }
            writer.println("===========================================");

            System.out.println("Report exported successfully to " + outputFileName);

        } catch (IOException e) {
            // Exception Handling for File I/O
            System.err.println("Error exporting report: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure resources are closed
            if (writer != null) {
                writer.close();
            }
        }
    }
}
