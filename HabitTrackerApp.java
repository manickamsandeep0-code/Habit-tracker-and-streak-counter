import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
public class HabitTrackerApp extends JFrame {
    private static final long serialVersionUID = 1L;
    private final DatabaseManager dbManager;
    private final User currentUser;
    private ArrayList<Habit> habits;
    private JComboBox<Habit> habitComboBox;
    private JButton[] calendarButtons;
    private JLabel streakLabel;
    private JLabel userLabel;
    private HashMap<LocalDate, Boolean> currentHabitLogs;
    private YearMonth currentMonth;
    private JLabel monthLabel;
    public HabitTrackerApp(User user, DatabaseManager dbManager) {
        this.currentUser = user;
        this.dbManager = dbManager;
        habits = new ArrayList<>();
        currentHabitLogs = new HashMap<>();
        currentMonth = YearMonth.now();
        setTitle("Habit Tracker - " + user.getName());
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(15, 15));
        add(createTopPanel(), BorderLayout.NORTH);
        add(createCenterPanel(), BorderLayout.CENTER);
        add(createBottomPanel(), BorderLayout.SOUTH);
        loadHabits();
        setVisible(true);
    }
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        topPanel.setBackground(new Color(245, 245, 250));
        userLabel = new JLabel("User: " + currentUser.getName() + " (@" + currentUser.getUsername() + ")");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        userLabel.setForeground(new Color(0, 100, 0));
        topPanel.add(userLabel);
        topPanel.add(Box.createHorizontalStrut(30));
        JLabel selectLabel = new JLabel("Select Habit:");
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        topPanel.add(selectLabel);
        habitComboBox = new JComboBox<>();
        habitComboBox.setPreferredSize(new Dimension(220, 35));
        habitComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        habitComboBox.addActionListener(e -> onHabitSelected());
        topPanel.add(habitComboBox);
        JButton newHabitButton = new JButton("New Habit");
        newHabitButton.setFont(new Font("Arial", Font.PLAIN, 14));
        newHabitButton.setFocusPainted(false);
        newHabitButton.addActionListener(e -> createNewHabit());
        topPanel.add(newHabitButton);
        topPanel.add(Box.createHorizontalStrut(20));
        JButton prevMonthButton = new JButton("◄ Prev");
        prevMonthButton.setFont(new Font("Arial", Font.PLAIN, 14));
        prevMonthButton.setFocusPainted(false);
        prevMonthButton.addActionListener(e -> changeMonth(-1));
        topPanel.add(prevMonthButton);
        JButton nextMonthButton = new JButton("Next ►");
        nextMonthButton.setFont(new Font("Arial", Font.PLAIN, 14));
        nextMonthButton.setFocusPainted(false);
        nextMonthButton.addActionListener(e -> changeMonth(1));
        topPanel.add(nextMonthButton);
        topPanel.add(Box.createHorizontalStrut(20));
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setFocusPainted(false);
        logoutButton.setForeground(Color.RED);
        logoutButton.addActionListener(e -> logout());
        topPanel.add(logoutButton);
        return topPanel;
    }
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        centerPanel.setBackground(Color.WHITE);
        monthLabel = new JLabel(currentMonth.toString(), SwingConstants.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 22));
        monthLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        centerPanel.add(monthLabel, BorderLayout.NORTH);
        JPanel calendarPanel = new JPanel(new GridLayout(7, 7, 8, 8));
        calendarPanel.setBackground(Color.WHITE);
        calendarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        calendarButtons = new JButton[42];
        String[] dayNames = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
        for (String dayName : dayNames) {
            JLabel dayLabel = new JLabel(dayName, SwingConstants.CENTER);
            dayLabel.setFont(new Font("Arial", Font.BOLD, 14));
            dayLabel.setForeground(new Color(100, 100, 100));
            calendarPanel.add(dayLabel);
        }
        for (int i = 0; i < 42; i++) {
            final int index = i;
            JButton dayButton = new JButton("");
            dayButton.setPreferredSize(new Dimension(90, 70));
            dayButton.setFont(new Font("Arial", Font.BOLD, 16));
            dayButton.setFocusPainted(false);
            dayButton.setOpaque(true);
            dayButton.setBorderPainted(true);
            dayButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            dayButton.setBackground(Color.LIGHT_GRAY);
            dayButton.addActionListener(e -> onDayClicked(index));
            calendarButtons[i] = dayButton;
            calendarPanel.add(dayButton);
        }
        centerPanel.add(calendarPanel, BorderLayout.CENTER);
        return centerPanel;
    }
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        bottomPanel.setBackground(new Color(245, 245, 250));
        streakLabel = new JLabel("Current Streak: 0 days");
        streakLabel.setFont(new Font("Arial", Font.BOLD, 16));
        streakLabel.setForeground(new Color(0, 100, 200));
        bottomPanel.add(streakLabel);
        bottomPanel.add(Box.createHorizontalStrut(100));
        JButton exportButton = new JButton("Export Report");
        exportButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportReport());
        bottomPanel.add(exportButton);
        return bottomPanel;
    }
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
    private void onHabitSelected() {
        Habit selectedHabit = (Habit) habitComboBox.getSelectedItem();
        if (selectedHabit != null) {
            currentHabitLogs = dbManager.getLogsForHabit(selectedHabit.getId());
            updateCalendar();
            updateStreak();
        }
    }
    private void updateCalendar() {
        LocalDate firstOfMonth = currentMonth.atDay(1);
        int daysInMonth = currentMonth.lengthOfMonth();
        int firstDayOfWeek = firstOfMonth.getDayOfWeek().getValue() % 7;
        LocalDate today = LocalDate.now();
        monthLabel.setText(currentMonth.toString());
        for (int i = 0; i < 42; i++) {
            calendarButtons[i].setText("");
            calendarButtons[i].setBackground(Color.LIGHT_GRAY);
            calendarButtons[i].setForeground(Color.BLACK);
            calendarButtons[i].setEnabled(false);
        }
        for (int day = 1; day <= daysInMonth; day++) {
            int buttonIndex = firstDayOfWeek + day - 1;
            if (buttonIndex < 42) {
                calendarButtons[buttonIndex].setText(String.valueOf(day));
                LocalDate date = currentMonth.atDay(day);
                if (date.isBefore(today)) {
                    calendarButtons[buttonIndex].setEnabled(false);
                    calendarButtons[buttonIndex].setToolTipText("Past dates cannot be edited");
                } else {
                    calendarButtons[buttonIndex].setEnabled(true);
                    calendarButtons[buttonIndex].setToolTipText("Click to toggle completion");
                }
                if (currentHabitLogs.containsKey(date)) {
                    boolean completed = currentHabitLogs.get(date);
                    if (completed) {
                        calendarButtons[buttonIndex].setBackground(new Color(76, 175, 80));
                        calendarButtons[buttonIndex].setForeground(Color.WHITE);
                    } else {
                        calendarButtons[buttonIndex].setBackground(new Color(244, 67, 54));
                        calendarButtons[buttonIndex].setForeground(Color.WHITE);
                    }
                } else {
                    if (date.isBefore(today)) {
                        calendarButtons[buttonIndex].setBackground(new Color(235, 235, 235));
                        calendarButtons[buttonIndex].setForeground(new Color(150, 150, 150));
                    } else {
                        calendarButtons[buttonIndex].setBackground(Color.WHITE);
                        calendarButtons[buttonIndex].setForeground(Color.BLACK);
                    }
                }
            }
        }
    }
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
        Color currentColor = calendarButtons[buttonIndex].getBackground();
        if (currentColor.equals(Color.WHITE)) {
            dbManager.logHabit(selectedHabit.getId(), date, true);
            currentHabitLogs.put(date, true);
            calendarButtons[buttonIndex].setBackground(new Color(76, 175, 80));
            calendarButtons[buttonIndex].setForeground(Color.WHITE);
        } else if (currentColor.getRed() == 76 && currentColor.getGreen() == 175) {
            dbManager.logHabit(selectedHabit.getId(), date, false);
            currentHabitLogs.put(date, false);
            calendarButtons[buttonIndex].setBackground(new Color(244, 67, 54));
            calendarButtons[buttonIndex].setForeground(Color.WHITE);
        } else if (currentColor.getRed() == 244 && currentColor.getGreen() == 67) {
            dbManager.deleteHabitLog(selectedHabit.getId(), date);
            currentHabitLogs.remove(date);
            calendarButtons[buttonIndex].setBackground(Color.WHITE);
            calendarButtons[buttonIndex].setForeground(Color.BLACK);
        }
        updateStreak();
    }
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
    private void changeMonth(int offset) {
        currentMonth = currentMonth.plusMonths(offset);
        updateCalendar();
    }
    private void updateStreak() {
        int streak = 0;
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        while (currentHabitLogs.getOrDefault(checkDate, false)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }
        streakLabel.setText("Current Streak: " + streak + " days");
    }
    private void exportReport() {
        Habit selectedHabit = (Habit) habitComboBox.getSelectedItem();
        if (selectedHabit == null) {
            JOptionPane.showMessageDialog(this, "Please select a habit first!");
            return;
        }
        new Thread(() -> {
            try {
                IFileExporter exporter = new TxtFileExporter("habit_report.txt");
                exporter.export(selectedHabit.getName(), currentHabitLogs);
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Report exported successfully to habit_report.txt!", 
                        "Export Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Error exporting report: " + e.getMessage(), 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
