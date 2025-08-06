package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.util.Systemlogger; // Import your custom logger utility
import java.io.IOException;
import java.util.logging.Logger; // Import the Java logging class
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AdminLoginController {

    // Get the custom logger instance
    private static final Logger logger = Systemlogger.getLogger();

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Button clearButton;

    @FXML
    private Button backToWelcomeButton;

    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";

    @FXML
    public void initialize() {
        errorMessageLabel.setText("");
    }

    /**
     * Handles the login attempt when the "Login" button is clicked.
     * Validates credentials and navigates to the Admin Dashboard on success.
     * @param event The action event from the button click.
     */
    @FXML
    private void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            // Log a successful login
            logger.info("Admin has successfully logged in.");

            errorMessageLabel.setText("");
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Administrator!");
            try {
                MainApplication.loadAdminDashboardScene();
            } catch (IOException e) {
                errorMessageLabel.setText("Error loading dashboard: " + e.getMessage());
                logger.severe("Failed to load admin dashboard: " + e.getMessage()); // Log the exception
                e.printStackTrace();
            }
        } else {
            // Log a failed login attempt
            logger.warning("Admin login failed. Invalid username or password.");

            errorMessageLabel.setText("Invalid username or password. Please try again.");
        }
    }

    /**
     * Handles the action when the "Clear" button is clicked.
     * Clears the username and password fields.
     * @param event The action event from the button click.
     */
    @FXML
    private void handleClear(ActionEvent event) {
        usernameField.clear();
        passwordField.clear();
        errorMessageLabel.setText("");
        logger.info("Admin login fields cleared."); // Log the clear action
    }

    /**
     * Handles the action when the "Back to Welcome" button is clicked.
     * Navigates back to the main Kiosk Welcome page.
     * @param event The action event from the button click.
     * @throws IOException If the FXML file for the welcome page cannot be loaded.
     */
    @FXML
    private void handleBackToWelcome(ActionEvent event) throws IOException {
        logger.info("Admin login page: navigating back to welcome page."); // Log the back action
        MainApplication.loadNewScene("KioskWelcomePage-01.fxml");
    }

    /**
     * Helper method to display an alert.
     * @param type The type of alert (e.g., INFORMATION, ERROR).
     * @param title The title of the alert window.
     * @param content The message content of the alert.
     */
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
