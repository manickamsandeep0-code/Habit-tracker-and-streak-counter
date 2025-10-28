import javax.swing.*;
import java.awt.*;

/**
 * LoginFrame provides user authentication GUI.
 * Demonstrates Java Swing and user authentication.
 */
public class LoginFrame extends JFrame {
    // Private fields for Encapsulation
    private DatabaseManager dbManager;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private User loggedInUser;

    /**
     * Constructor initializes the login GUI
     */
    public LoginFrame() {
        dbManager = new DatabaseManager();
        loggedInUser = null;

        // Set up the main frame
        setTitle("Habit Tracker - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Create tabbed pane for Login/Register
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Login", createLoginPanel());
        tabbedPane.addTab("Register", createRegisterPanel());

        add(tabbedPane, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * Creates the login panel
     * @return JPanel containing login form
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Login to Habit Tracker");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Username
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login Button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(loginButton, gbc);

        // Add enter key listener
        passwordField.addActionListener(e -> handleLogin());

        return panel;
    }

    /**
     * Creates the register panel
     * @return JPanel containing registration form
     */
    private JPanel createRegisterPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel titleLabel = new JLabel("Create New Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(titleLabel, gbc);

        // Name
        gbc.gridwidth = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(new JLabel("Full Name:"), gbc);

        nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Email
        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Email:"), gbc);

        emailField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(emailField, gbc);

        // Username
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Username:"), gbc);

        JTextField regUsernameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(regUsernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Password:"), gbc);

        JPasswordField regPasswordField = new JPasswordField(20);
        gbc.gridx = 1;
        panel.add(regPasswordField, gbc);

        // Register Button
        JButton registerButton = new JButton("Create Account");
        registerButton.addActionListener(e -> handleRegister(
            nameField.getText(),
            emailField.getText(),
            regUsernameField.getText(),
            new String(regPasswordField.getPassword())
        ));
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(registerButton, gbc);

        return panel;
    }

    /**
     * Handles login button click
     */
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password!", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Authenticate user
        loggedInUser = dbManager.loginUser(username, password);

        if (loggedInUser != null) {
            JOptionPane.showMessageDialog(this, 
                "Welcome back, " + loggedInUser.getName() + "!", 
                "Login Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Close login window and open main app
            openMainApplication();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password!", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
        }
    }

    /**
     * Handles registration
     */
    private void handleRegister(String name, String email, String username, String password) {
        // Validate inputs
        if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please fill in all fields!", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(this, 
                "Password must be at least 6 characters long!", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Register user
        int userId = dbManager.registerUser(name, username, email, password);

        if (userId > 0) {
            JOptionPane.showMessageDialog(this, 
                "Account created successfully! Please login.", 
                "Registration Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Clear fields
            nameField.setText("");
            emailField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, 
                "Registration failed! Username may already exist.", 
                "Registration Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Opens the main application window
     */
    private void openMainApplication() {
        SwingUtilities.invokeLater(() -> {
            new HabitTrackerApp(loggedInUser, dbManager);
            dispose(); // Close login window
        });
    }

    /**
     * Main method - entry point of the application
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Run the GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}
