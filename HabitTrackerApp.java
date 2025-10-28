import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * HabitTrackerApp is the main application class with GUI.
 * Demonstrates Java Swing, all OOP principles, and integration of all components.
 */
public class HabitTrackerApp extends JFrame {
    // Private fields for Encapsulation
    private DatabaseManager dbManager;
    private User currentUser;
    private ArrayList<Habit> habits;
    private JComboBox<Habit> habitComboBox;
    private JButton[] calendarButtons;
    private JLabel streakLabel;
    private JLabel userLabel;
    private HashMap<LocalDate, Boolean> currentHabitLogs;
    private YearMonth currentMonth;
    private JLabel monthLabel;

    /**
     * Constructor initializes the GUI with logged-in user
     * @param user The logged-in user
     * @param dbManager The database manager instance
     */
    public HabitTrackerApp(User user, DatabaseManager dbManager) {
        // Initialize with user
        this.currentUser = user;
        this.dbManager = dbManager;
        habits = new ArrayList<>();
        currentHabitLogs = new HashMap<>();
        currentMonth = YearMonth.now();

        // Set up the main frame
        setTitle("Habit Tracker - " + user.getName());
        setSize(900, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Create and add panels
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);

        // Load habits from database for this user
        loadHabits();

        // Make the frame visible
        setVisible(true);
    }

    /**
     * Creates the top panel with habit selector and new habit button
     * Demonstrates Java Swing components
     * @return JPanel containing the top controls
     */
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // User label
        userLabel = new JLabel("User: " + currentUser.getName() + " (@" + currentUser.getUsername() + ")");
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        userLabel.setForeground(new Color(0, 100, 0));
        topPanel.add(userLabel);

        topPanel.add(Box.createHorizontalStrut(20));

        // Label
        JLabel selectLabel = new JLabel("Select Habit:");
        topPanel.add(selectLabel);

        // Habit ComboBox (demonstrates Java Collections - ArrayList)
        habitComboBox = new JComboBox<>();
        habitComboBox.setPreferredSize(new Dimension(200, 30));
        habitComboBox.addActionListener(e -> onHabitSelected());
        topPanel.add(habitComboBox);

        // New Habit Button
        JButton newHabitButton = new JButton("New Habit");
        newHabitButton.addActionListener(e -> createNewHabit());
        topPanel.add(newHabitButton);

        // Month navigation
        JButton prevMonthButton = new JButton("< Prev Month");
        prevMonthButton.addActionListener(e -> changeMonth(-1));
        topPanel.add(prevMonthButton);

        JButton nextMonthButton = new JButton("Next Month >");
        nextMonthButton.addActionListener(e -> changeMonth(1));
        topPanel.add(nextMonthButton);

        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);

        return topPanel;
    }

    /**
     * Creates the center panel with calendar grid
     * Demonstrates Java Swing components and GridLayout
     * @return JPanel containing the calendar
     */
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Month label
        monthLabel = new JLabel(currentMonth.toString(), SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 18));
        centerPanel.add(monthLabel, BorderLayout.NORTH);

        // Calendar panel with GridLayout (6 rows x 7 columns = 42 buttons)
        JPanel calendarPanel = new JPanel(new GridLayout(7, 7, 5, 5));
        calendarButtons = new JButton[42];

        // Day headers
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 12));
            calendarPanel.add(dayLabel);
        }

        // Calendar day buttons
        for (int i = 0; i < 42; i++) {
            final int index = i;
            JButton dayButton = new JButton("");
            dayButton.setPreferredSize(new Dimension(80, 60));
            dayButton.setBackground(Color.LIGHT_GRAY);
            dayButton.addActionListener(e -> onDayClicked(index));
            calendarButtons[i] = dayButton;
            calendarPanel.add(dayButton);
        }

        centerPanel.add(calendarPanel, BorderLayout.CENTER);
        return centerPanel;
    }

    /**
     * Creates the bottom panel with streak label and export button
     * Demonstrates Java Swing components
     * @return JPanel containing bottom controls
     */
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Streak label
        streakLabel = new JLabel("Current Streak: 0 days");
        streakLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottomPanel.add(streakLabel);

        // Add some spacing
        bottomPanel.add(Box.createHorizontalStrut(50));

        // Export Report Button (demonstrates Multithreading)
        JButton exportButton = new JButton("Export Report");
        exportButton.addActionListener(e -> exportReport());
        bottomPanel.add(exportButton);

        return bottomPanel;
    }

    /**
     * Loads all habits from the database for the current user
     * Demonstrates Database Connectivity and Java Collections
     */
    private void loadHabits() {
        habits = dbManager.getHabitsForUser(currentUser.getId());
        habitComboBox.removeAllItems();
        
        for (Habit habit : habits) {
            habitComboBox.addItem(habit);
        }

        if (!habits.isEmpty()) {
            habitComboBox.setSelectedIndex(0);
            onHabitSelected();
        }
    }

    /**
     * Handler for when a habit is selected from the combo box
     */
    private void onHabitSelected() {
        Habit selectedHabit = (Habit) habitComboBox.getSelectedItem();
        if (selectedHabit != null) {
            // Load logs for the selected habit (demonstrates HashMap)
            currentHabitLogs = dbManager.getLogsForHabit(selectedHabit.getId());
            updateCalendar();
            updateStreak();
        }
    }

    /**
     * Updates the calendar display based on current month and habit logs
     */
    private void updateCalendar() {
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int daysInMonth = currentMonth.lengthOfMonth();
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7; // Sunday = 0
        LocalDate today = LocalDate.now();

        // Update month label
        monthLabel.setText(currentMonth.toString());

        // Clear all buttons
        for (int i = 0; i < 42; i++) {
            calendarButtons[i].setText("");
            calendarButtons[i].setBackground(Color.LIGHT_GRAY);
            calendarButtons[i].setEnabled(false);
        }

        // Fill in the days
        for (int day = 1; day <= daysInMonth; day++) {
            int buttonIndex = firstDayOfWeek + day - 1;
            if (buttonIndex < 42) {
                calendarButtons[buttonIndex].setText(String.valueOf(day));
                
                LocalDate date = currentMonth.atDay(day);
                
                // Disable past dates - users cannot edit previous days
                if (date.isBefore(today)) {
                    calendarButtons[buttonIndex].setEnabled(false);
                    calendarButtons[buttonIndex].setToolTipText("Past dates cannot be edited");
                } else {
                    calendarButtons[buttonIndex].setEnabled(true);
                    calendarButtons[buttonIndex].setToolTipText("Click to toggle completion");
                }

                // Check if this day is logged (demonstrates HashMap usage)
                if (currentHabitLogs.containsKey(date)) {
                    boolean completed = currentHabitLogs.get(date);
                    if (completed) {
                        calendarButtons[buttonIndex].setBackground(Color.GREEN);
                    } else {
                        calendarButtons[buttonIndex].setBackground(Color.RED);
                    }
                } else {
                    if (date.isBefore(today)) {
                        // Past days with no log show as gray
                        calendarButtons[buttonIndex].setBackground(new Color(200, 200, 200));
                    } else {
                        calendarButtons[buttonIndex].setBackground(Color.WHITE);
                    }
                }
            }
        }
    }

    /**
     * Handler for when a calendar day button is clicked
     * @param buttonIndex The index of the button clicked
     */
    private void onDayClicked(int buttonIndex) {
        Habit selectedHabit = (Habit) habitComboBox.getSelectedItem();
        if (selectedHabit == null) {
            JOptionPane.showMessageDialog(this, "Please select a habit first!");
            return;
        }

        String dayText = calendarButtons[buttonIndex].getText();
        if (dayText.isEmpty()) {
            return;
        }

        int day = Integer.parseInt(dayText);
        LocalDate date = currentMonth.atDay(day);

        // Toggle completion status
        boolean currentStatus = currentHabitLogs.getOrDefault(date, false);
        boolean newStatus = !currentStatus;

        // Save to database
        dbManager.logHabit(selectedHabit.getId(), date, newStatus);

        // Update local map
        currentHabitLogs.put(date, newStatus);

        // Update UI
        if (newStatus) {
            calendarButtons[buttonIndex].setBackground(Color.GREEN);
        } else {
            calendarButtons[buttonIndex].setBackground(Color.RED);
        }

        updateStreak();
    }

    /**
     * Creates a new habit with user input
     */
    private void createNewHabit() {
        String habitName = JOptionPane.showInputDialog(this, "Enter habit name:");
        if (habitName != null && !habitName.trim().isEmpty()) {
            int habitId = dbManager.addHabitForUser(habitName.trim(), currentUser.getId());
            if (habitId > 0) {
                JOptionPane.showMessageDialog(this, "Habit created successfully!");
                loadHabits();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating habit!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Handles user logout
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame());
        }
    }

    /**
     * Changes the displayed month
     * @param offset Number of months to move (positive or negative)
     */
    private void changeMonth(int offset) {
        currentMonth = currentMonth.plusMonths(offset);
        updateCalendar();
    }

    /**
     * Calculates and updates the current streak display
     */
    private void updateStreak() {
        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;

        // Count consecutive completed days backwards from today
        while (currentHabitLogs.getOrDefault(checkDate, false)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        streakLabel.setText("Current Streak: " + streak + " days");
    }

    /**
     * Exports the habit report to a text file
     * Demonstrates Multithreading, Interface usage (Polymorphism), and File Handling
     */
    private void exportReport() {
        Habit selectedHabit = (Habit) habitComboBox.getSelectedItem();
        if (selectedHabit == null) {
            JOptionPane.showMessageDialog(this, "Please select a habit first!");
            return;
        }

        // Demonstrates Multithreading - run export on a separate thread
        new Thread(() -> {
            try {
                // Demonstrates Polymorphism - IFileExporter interface
                IFileExporter exporter = new TxtFileExporter("habit_report.txt");
                exporter.export(selectedHabit.getName(), currentHabitLogs);

                // Show success message on the GUI thread
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Report exported successfully to habit_report.txt!", 
                        "Export Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                // Exception handling
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Error exporting report: " + e.getMessage(), 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start(); // Start the thread
    }

    /**
     * Main method - entry point redirects to LoginFrame
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Run the Login GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
