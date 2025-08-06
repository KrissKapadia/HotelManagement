package com.example.demo.controllers; // Corrected package name as per your specification

import com.example.demo.MainApplication; // Assuming MainApplication is in com.example.demo
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert; // For showing alerts
import javafx.scene.control.Button; // Explicit import for Button
import java.io.IOException;

public class AdminLoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Button loginButton; // Changed to Button

    @FXML
    private Button clearButton; // New FXML for the Clear button

    @FXML
    private Button backToWelcomeButton; // Changed to Button

    // Hardcoded credentials for demonstration purposes
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "password";

    @FXML
    public void initialize() {
        // Clear any previous error messages on initialization
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
            errorMessageLabel.setText(""); // Clear any previous error
            showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome, Administrator!");
            try {
                // Navigate to the Admin Dashboard
                // This line calls the static method in MainApplication to load the dashboard scene.
                MainApplication.loadAdminDashboardScene();
            } catch (IOException e) {
                errorMessageLabel.setText("Error loading dashboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
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
        errorMessageLabel.setText(""); // Clear any error messages
    }

    /**
     * Handles the action when the "Back to Welcome" button is clicked.
     * Navigates back to the main Kiosk Welcome page.
     * @param event The action event from the button click.
     * @throws IOException If the FXML file for the welcome page cannot be loaded.
     */
    @FXML
    private void handleBackToWelcome(ActionEvent event) throws IOException {
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
