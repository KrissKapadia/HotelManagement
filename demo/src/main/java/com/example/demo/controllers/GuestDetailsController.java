package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.Guest;
import com.example.demo.models.ReservationDetails;
import com.example.demo.util.Systemlogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

public class GuestDetailsController {

    // Get the custom logger instance
    private static final Logger logger = Systemlogger.getLogger();

    // Guest Information Labels (now strictly matching the Guest model)
    @FXML private Label fullNameLabel; // Displays the full name directly
    @FXML private Label phoneLabel;
    @FXML private Label emailLabel;
    @FXML private Label streetNameLabel; // Maps to address in Guest model
    @FXML private Label cityLabel;
    @FXML private Label provinceStateLabel;
    @FXML private Label postalCodeLabel;
    @FXML private Label idProofTypeLabel;
    @FXML private Label idProofNumberLabel;

    // Reservation Information Labels
    @FXML private Label reservationIdLabel;
    @FXML private Label checkInDateLabel;
    @FXML private Label checkOutDateLabel;
    @FXML private Label numGuestsLabel;
    @FXML private Label statusLabel;

    // Assigned Room Information Labels
    @FXML private Label roomTypeLabel;

    @FXML private Button backButton;

    private Guest guest;
    private ReservationDetails reservationDetails;

    @FXML
    public void initialize() {
        logger.info("GuestDetailsController initialized.");
    }

    /**
     * Sets the Guest and ReservationDetails objects for this controller
     * and populates the UI labels with the respective data for display.
     * @param guest The Guest object containing guest information.
     * @param reservationDetails The ReservationDetails object containing reservation info.
     */
    public void setGuestAndReservationDetails(Guest guest, ReservationDetails reservationDetails) {
        this.guest = guest;
        this.reservationDetails = reservationDetails;
        populateLabels();
        if (guest != null && reservationDetails != null) {
            logger.info("Populating details for reservation ID: " + reservationDetails.getReservationId() + " for guest: " + guest.getFullName());
        }
    }

    /**
     * Populates all the FXML labels with data from the Guest and ReservationDetails objects.
     */
    private void populateLabels() {
        // Date formatter for consistent date display
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");

        if (guest != null) {
            fullNameLabel.setText(guest.getFullName() != null ? guest.getFullName() : "N/A");
            phoneLabel.setText(guest.getPhoneNumber() != null ? guest.getPhoneNumber() : "N/A");
            emailLabel.setText(guest.getEmail() != null ? guest.getEmail() : "N/A");
            streetNameLabel.setText(guest.getAddress() != null ? guest.getAddress() : "N/A");
            cityLabel.setText(guest.getCity() != null ? guest.getCity() : "N/A");
            provinceStateLabel.setText(guest.getProvince() != null ? guest.getProvince() : "N/A");
            postalCodeLabel.setText(guest.getPostalCode() != null ? guest.getPostalCode() : "N/A");
            idProofTypeLabel.setText(guest.getIdProofType() != null ? guest.getIdProofType() : "N/A");
            idProofNumberLabel.setText(guest.getIdProofNumber() != null ? guest.getIdProofNumber() : "N/A");

        } else {
            // Set all guest labels to N/A if guest object is null
            fullNameLabel.setText("N/A");
            phoneLabel.setText("N/A"); emailLabel.setText("N/A");
            streetNameLabel.setText("N/A"); cityLabel.setText("N/A"); provinceStateLabel.setText("N/A");
            postalCodeLabel.setText("N/A"); idProofTypeLabel.setText("N/A"); idProofNumberLabel.setText("N/A");
        }

        if (reservationDetails != null) {
            reservationIdLabel.setText(reservationDetails.getReservationId() != null ? reservationDetails.getReservationId() : "N/A");
            checkInDateLabel.setText(reservationDetails.getCheckInDate() != null ? reservationDetails.getCheckInDate().format(formatter) : "N/A");
            checkOutDateLabel.setText(reservationDetails.getCheckOutDate() != null ? reservationDetails.getCheckOutDate().format(formatter) : "N/A");
            numGuestsLabel.setText(String.valueOf(reservationDetails.getNumberOfAdults() + reservationDetails.getNumberOfChildren()));
            statusLabel.setText(reservationDetails.getStatus() != null ? reservationDetails.getStatus() : "N/A");

            roomTypeLabel.setText(reservationDetails.getRoomType() != null ? reservationDetails.getRoomType() : "N/A");
        } else {
            // Set all reservation labels to N/A if reservationDetails object is null
            reservationIdLabel.setText("N/A"); checkInDateLabel.setText("N/A"); checkOutDateLabel.setText("N/A");
            numGuestsLabel.setText("N/A"); statusLabel.setText("N/A");
            roomTypeLabel.setText("N/A");
        }
    }

    /**
     * Handles the action when the "Back to Search" button is clicked.
     * Navigates back to the Guest Search & Management page.
     * @param event The action event from the button click.
     * @throws IOException If the FXML for the guest search page cannot be loaded.
     */
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        logger.info("Navigating back to Guest Search & Management scene.");
        MainApplication.loadGuestSearchManagementScene();
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
