package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.ReservationDetails;
import com.example.demo.util.Systemlogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller for the KioskWelcomePage-01.fxml scene.
 * This class handles the initial user interactions, such as starting a booking
 * or navigating to other parts of the application like the admin login or
 * rules page.
 */
public class KioskWelcomeController {

    // Declare and initialize a logger instance using the custom utility class.
    // This allows all methods in this controller to use the 'logger' object.
    private final Logger logger = Systemlogger.getLogger();

    /**
     * Handles the "Self-Checkout/Kiosk" button action.
     * This method initializes a new ReservationDetails object and navigates
     * the user to the date selection scene to begin the booking process.
     * @param event The action event triggered by the button click.
     * @throws IOException If the FXML file for the next scene cannot be loaded.
     */
    @FXML
    private void handleStart(ActionEvent event) throws IOException {
        logger.log(Level.INFO, "User initiated a new self-checkout booking.");
        ReservationDetails reservationDetails = new ReservationDetails(null, null, 0, 0);
        MainApplication.loadDateSelectionScene(reservationDetails);
    }

    /**
     * Handles the "Admin Login" button action.
     * This method now navigates to the admin login scene using the specific FXML file.
     * @param event The action event triggered by the button click.
     * @throws IOException If the FXML file for the admin scene cannot be loaded.
     */
    @FXML
    private void handleAdminLogin(ActionEvent event) throws IOException {
        logger.log(Level.INFO, "Navigating to Admin Login page.");
        System.out.println("Admin login button clicked.");
        MainApplication.loadAdminLoginScene(); // This will load "AdminLoginPage-08.fxml" as defined in MainApplication
    }

    /**
     * Handles the "Rules & Regulations" button action.
     * This method now displays an alert message with the hotel rules instead of loading a new scene.
     * @param event The action event triggered by the button click.
     */
    @FXML
    private void handleViewRules(ActionEvent event) {
        logger.log(Level.INFO, "Displaying Rules & Regulations...");
        String rulesText = "• Single room: Max two people.\n\n" +
                "• Double room: Max 4 people.\n\n" +
                "• Deluxe and Pent rooms: Max two people but the prices are higher.\n\n" +
                "• More than 2 adults less than 5 can have Double room or two single rooms will be offered.\n\n" +
                "• More than 4 adults will have multiple Double or combination of Double and single rooms.";

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules and Regulations");
        alert.setHeaderText("Hotel Room Booking Rules");

        TextArea textArea = new TextArea(rulesText);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefHeight(200);
        textArea.setPrefWidth(400);

        alert.getDialogPane().setContent(textArea);
        alert.getDialogPane().setPrefWidth(500);
        alert.setResizable(true);

        alert.showAndWait();
    }

    /**
     * Handles the "Leave a Feedback" button action.
     * This method navigates the user to a new scene where they can provide feedback.
     * @param event The action event triggered by the button click.
     * @throws IOException If the FXML file for the feedback scene cannot be loaded.
     */
    @FXML
    private void handleLeaveFeedback(ActionEvent event) throws IOException {
        logger.log(Level.INFO, "Navigating to Feedback page.");
        MainApplication.loadFeedbackScene();
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
