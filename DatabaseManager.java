import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * DatabaseManager handles all database operations using JDBC.
 * Demonstrates Database Connectivity, Exception Handling, and Encapsulation.
 */
public class DatabaseManager {
    // Private fields for Encapsulation
    private String url;
    private String user;
    private String password;

    /**
     * Constructor that loads database configuration from config.properties
     * Demonstrates File Handling and Exception Handling
     */
    public DatabaseManager() {
        loadConfiguration();
    }

    /**
     * Loads database configuration from config.properties file
     * Demonstrates Exception Handling with try-catch-finally
     */
    private void loadConfiguration() {
        Properties props = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("config.properties");
            props.load(fis);
            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password");
            System.out.println("Database configuration loaded successfully.");
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
            e.printStackTrace();
            // Set default values if config file not found
            this.url = "jdbc:mysql://localhost:3306/habit_tracker";
            this.user = "root";
            this.password = "";
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Getters and Setters for Encapsulation
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Gets a database connection
     * Demonstrates Exception Handling
     * @return A Connection object or null if connection fails
     */
    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Retrieves all habits from the database
     * Demonstrates JDBC, Exception Handling, and Java Collections (ArrayList)
     * @return ArrayList of Habit objects
     */
    public ArrayList<Habit> getAllHabits() {
        ArrayList<Habit> habits = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            if (conn != null) {
                stmt = conn.createStatement();
                String sql = "SELECT id, name FROM habits ORDER BY name";
                rs = stmt.executeQuery(sql);

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    habits.add(new Habit(id, name));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving habits: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources in finally block
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return habits;
    }

    /**
     * Adds a new habit to the database
     * Demonstrates JDBC, Exception Handling
     * @param habitName The name of the new habit
     * @return The ID of the newly created habit, or -1 if failed
     */
    public int addHabit(String habitName) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int habitId = -1;

        try {
            conn = getConnection();
            if (conn != null) {
                String sql = "INSERT INTO habits (name) VALUES (?)";
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, habitName);
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        habitId = rs.getInt(1);
                        System.out.println("Habit added successfully with ID: " + habitId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding habit: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return habitId;
    }

    /**
     * Logs a habit completion for a specific date
     * Demonstrates JDBC, Exception Handling
     * @param habitId The ID of the habit
     * @param date The date to log
     * @param completed Whether the habit was completed
     */
    public void logHabit(int habitId, LocalDate date, boolean completed) {
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = getConnection();
            if (conn != null) {
                // First, check if a log already exists for this habit and date
                String checkSql = "SELECT id FROM habit_logs WHERE habit_id = ? AND date = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setInt(1, habitId);
                pstmt.setDate(2, Date.valueOf(date));
                ResultSet rs = pstmt.executeQuery();

                if (rs.next()) {
                    // Update existing log
                    int logId = rs.getInt("id");
                    rs.close();
                    pstmt.close();

                    String updateSql = "UPDATE habit_logs SET completed = ? WHERE id = ?";
                    pstmt = conn.prepareStatement(updateSql);
                    pstmt.setBoolean(1, completed);
                    pstmt.setInt(2, logId);
                    pstmt.executeUpdate();
                } else {
                    // Insert new log
                    rs.close();
                    pstmt.close();

                    String insertSql = "INSERT INTO habit_logs (habit_id, date, completed) VALUES (?, ?, ?)";
                    pstmt = conn.prepareStatement(insertSql);
                    pstmt.setInt(1, habitId);
                    pstmt.setDate(2, Date.valueOf(date));
                    pstmt.setBoolean(3, completed);
                    pstmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error logging habit: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Retrieves all logs for a specific habit
     * Demonstrates JDBC, Exception Handling, Java Collections (HashMap)
     * @param habitId The ID of the habit
     * @return A HashMap mapping dates to completion status
     */
    public HashMap<LocalDate, Boolean> getLogsForHabit(int habitId) {
        HashMap<LocalDate, Boolean> logs = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            if (conn != null) {
                String sql = "SELECT date, completed FROM habit_logs WHERE habit_id = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, habitId);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    LocalDate date = rs.getDate("date").toLocalDate();
                    boolean completed = rs.getBoolean("completed");
                    logs.put(date, completed);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving logs: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return logs;
    }

    /**
     * Hashes a password using SHA-256
     * @param password The plain text password
     * @return The hashed password as a hex string
     */
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Registers a new user account
     * Demonstrates JDBC, Exception Handling
     * @param name The user's display name
     * @param username The login username
     * @param email The user's email
     * @param password The user's password (will be hashed)
     * @return The ID of the newly created user, or -1 if failed
     */
    public int registerUser(String name, String username, String email, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int userId = -1;

        try {
            conn = getConnection();
            if (conn != null) {
                // Check if username already exists
                String checkSql = "SELECT id FROM users WHERE username = ?";
                pstmt = conn.prepareStatement(checkSql);
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    System.err.println("Username already exists!");
                    return -1;
                }

                rs.close();
                pstmt.close();

                // Insert new user
                String insertSql = "INSERT INTO users (name, username, email, password_hash) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, name);
                pstmt.setString(2, username);
                pstmt.setString(3, email);
                pstmt.setString(4, hashPassword(password));

                int affectedRows = pstmt.executeUpdate();

                if (affectedRows > 0) {
                    rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        userId = rs.getInt(1);
                        System.out.println("User registered successfully with ID: " + userId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return userId;
    }

    /**
     * Authenticates a user login
     * Demonstrates JDBC, Exception Handling
     * @param username The login username
     * @param password The user's password
     * @return A User object if login successful, null otherwise
     */
    public User loginUser(String username, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = getConnection();
            if (conn != null) {
                String sql = "SELECT id, name, username, email, password_hash FROM users WHERE username = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, username);
                rs = pstmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("password_hash");
                    String inputHash = hashPassword(password);

                    if (storedHash.equals(inputHash)) {
                        int id = rs.getInt("id");
                        String name = rs.getString("name");
                        String email = rs.getString("email");
                        user = new User(id, name, username, email);
                        System.out.println("Login successful for user: " + username);
                    } else {
                        System.err.println("Invalid password!");
                    }
                } else {
                    System.err.println("User not found!");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error during login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    /**
     * Adds a habit for a specific user
     * @param habitName The name of the new habit
     * @param userId The ID of the user creating the habit
     * @return The ID of the newly created habit, or -1 if failed
     */
    public int addHabitForUser(String habitName, int userId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        int habitId = -1;

        try {
            conn = getConnection();
            if (conn != null) {
                String sql = "INSERT INTO habits (name, user_id) VALUES (?, ?)";
                pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, habitName);
                pstmt.setInt(2, userId);
                
                int affectedRows = pstmt.executeUpdate();
                
                if (affectedRows > 0) {
                    rs = pstmt.getGeneratedKeys();
                    if (rs.next()) {
                        habitId = rs.getInt(1);
                        System.out.println("Habit added successfully with ID: " + habitId);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding habit: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return habitId;
    }

    /**
     * Retrieves all habits for a specific user
     * @param userId The ID of the user
     * @return ArrayList of Habit objects belonging to the user
     */
    public ArrayList<Habit> getHabitsForUser(int userId) {
        ArrayList<Habit> habits = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            if (conn != null) {
                String sql = "SELECT id, name FROM habits WHERE user_id = ? ORDER BY name";
                pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, userId);
                rs = pstmt.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    habits.add(new Habit(id, name));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving habits: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return habits;
    }

}
