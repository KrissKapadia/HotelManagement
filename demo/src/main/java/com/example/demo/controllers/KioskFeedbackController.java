package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.DatabaseManager;
import com.example.demo.util.Systemlogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the KioskFeedbackPage-17.fxml scene.
 * This class handles guest feedback submission, including a rating slider and
 * a comments section. It also manages navigation back to the main welcome page.
 */
public class KioskFeedbackController {

    // Declare and initialize a logger instance for this controller.
    private static final Logger logger = Systemlogger.getLogger();

    @FXML
    private Label messageLabel; // Label to display confirmation or error messages
    @FXML
    private Slider ratingSlider; // Slider for the star rating (1-5)
    @FXML
    private TextArea commentsTextArea; // TextArea for guest comments
    @FXML
    private TextField phoneNumberTextField; // TextField for guest's phone number

    /**
     * Handles the "Submit Feedback" button action.
     * This method collects the user's rating, comments, and phone number,
     * saves the feedback to a database, and then navigates back to the welcome page.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleSubmitFeedback(ActionEvent event) {
        try {
            // Get values from the UI components
            int rating = (int) ratingSlider.getValue();
            String comments = commentsTextArea.getText();
            String phoneNumber = phoneNumberTextField.getText();

            // Use the centralized method from the DatabaseManager to save feedback.
            // This is the correct way to use your database, which is already configured for SQLite.
            DatabaseManager.insertFeedback(phoneNumber, rating, comments);

            // Display a success message and log the event
            messageLabel.setText("Thank you for your feedback! It has been submitted successfully.");
            logger.log(Level.INFO, "Feedback submitted successfully: Phone Number: {0}, Rating: {1}, Comments: {2}",
                    new Object[]{phoneNumber, rating, comments});

            // The navigation is now delayed to allow the user to see the success message.
            // Consider adding a timer or a confirmation dialog to proceed.
            // For now, it will not navigate immediately.
            // MainApplication.loadNewScene("KioskWelcomePage-01.fxml");

        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred during feedback submission.", e);
            messageLabel.setText("An unexpected error occurred.");
        }
    }

    /**
     * Handles the "Skip" button action.
     * This method simply logs the action and navigates the user back to the
     * main welcome page without submitting any feedback.
     *
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleSkip(ActionEvent event) {
        logger.log(Level.INFO, "User skipped the feedback form. Navigating to welcome page.");
        try {
            MainApplication.loadNewScene("KioskWelcomePage-01.fxml");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Failed to load the welcome page after skipping feedback.", e);
            messageLabel.setText("An error occurred while loading the welcome page.");
        }
    }
}
