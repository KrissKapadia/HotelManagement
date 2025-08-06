package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.Guest;
import com.example.demo.models.ReservationDetails;
import com.example.demo.models.DatabaseManager; // Correct import
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.io.IOException;
import java.sql.SQLException; // Import SQLException
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;
import java.util.logging.Level;

public class KioskRoomConfirmationController {

    private static final Logger logger = Logger.getLogger(KioskRoomConfirmationController.class.getName());

    @FXML
    private Label reservationIdLabel;
    @FXML
    private Label guestNameLabel;
    @FXML
    private Label roomDetailsLabel; // Corrected label to match FXML: roomDetailsLabel
    @FXML
    private Label checkInDateLabel;
    @FXML
    private Label checkOutDateLabel;
    @FXML
    private Label subTotalLabel; // New label for Sub-Total
    @FXML
    private Label taxLabel; // New label for Tax
    @FXML
    private Label estimatedTotalLabel; // Changed from totalPaidLabel to estimatedTotalLabel
    @FXML
    private Label messageLabel;
    @FXML
    private Button confirmBookingButton;
    @FXML
    private Button backButton; // FXML button for "Back"
    @FXML
    private Button rulesButton;

    private Guest guest;
    private ReservationDetails reservationDetails;

    // Room prices per night (should match ReservationDetails for consistency)
    private static final double SINGLE_ROOM_PRICE = 100.00;
    private static final double DOUBLE_ROOM_PRICE = 150.00;
    private static final double DELUXE_ROOM_PRICE = 250.00;
    private static final double PENTHOUSE_PRICE = 350.00; // Assuming this is the price for Penthouse

    private static final double TAX_RATE = 0.13; // 13% tax

    public void setGuestAndReservationDetails(Guest guest, ReservationDetails reservationDetails) {
        this.guest = guest;
        this.reservationDetails = reservationDetails;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        DecimalFormat df = new DecimalFormat("$#,##0.00");

        // Display Guest Name
        guestNameLabel.setText(guest.getFullName());
        reservationIdLabel.setText("N/A - To be assigned on save"); // Reservation ID is assigned after saving

        // Display only booked rooms
        StringBuilder roomSummaryBuilder = new StringBuilder();
        if (reservationDetails.getSingleRooms() > 0) {
            roomSummaryBuilder.append(reservationDetails.getSingleRooms()).append(" Single");
        }
        if (reservationDetails.getDoubleRooms() > 0) {
            if (roomSummaryBuilder.length() > 0) roomSummaryBuilder.append(", ");
            roomSummaryBuilder.append(reservationDetails.getDoubleRooms()).append(" Double");
        }
        if (reservationDetails.getDeluxeRooms() > 0) {
            if (roomSummaryBuilder.length() > 0) roomSummaryBuilder.append(", ");
            roomSummaryBuilder.append(reservationDetails.getDeluxeRooms()).append(" Deluxe");
        }
        if (reservationDetails.getPenthouses() > 0) {
            if (roomSummaryBuilder.length() > 0) roomSummaryBuilder.append(", ");
            roomSummaryBuilder.append(reservationDetails.getPenthouses()).append(" Penthouse");
        }
        String roomSummary = roomSummaryBuilder.length() > 0 ? roomSummaryBuilder.toString() : "No rooms selected";
        roomDetailsLabel.setText(roomSummary);


        if (reservationDetails.getCheckInDate() != null) {
            checkInDateLabel.setText(reservationDetails.getCheckInDate().format(formatter));
        }
        if (reservationDetails.getCheckOutDate() != null) {
            checkOutDateLabel.setText(reservationDetails.getCheckOutDate().format(formatter));
        }

        // Calculate prices
        long numberOfNights = reservationDetails.getNumberOfNights();
        double subTotal = (reservationDetails.getSingleRooms() * SINGLE_ROOM_PRICE +
                reservationDetails.getDoubleRooms() * DOUBLE_ROOM_PRICE +
                reservationDetails.getDeluxeRooms() * DELUXE_ROOM_PRICE +
                reservationDetails.getPenthouses() * PENTHOUSE_PRICE) * numberOfNights;

        double taxAmount = subTotal * TAX_RATE;
        double estimatedTotal = subTotal + taxAmount;

        subTotalLabel.setText(df.format(subTotal));
        taxLabel.setText(df.format(taxAmount));
        estimatedTotalLabel.setText(df.format(estimatedTotal));

        messageLabel.setText("Please review your booking details before confirming.");
    }

    /**
     * This method is triggered when the "Confirm Booking" button is clicked.
     * It saves the reservation data to the database and then displays a confirmation message.
     * @param event The action event from the button click.
     */
    @FXML
    private void handleConfirmBooking(ActionEvent event) { // This now proceeds to BookingSuccessfulPage
        System.out.println("handleConfirmBooking method entered."); // Debugging print statement

        try {
            // Recalculate estimated total to ensure it's up-to-date before saving
            long numberOfNights = reservationDetails.getNumberOfNights();
            double subTotal = (reservationDetails.getSingleRooms() * SINGLE_ROOM_PRICE +
                    reservationDetails.getDoubleRooms() * DOUBLE_ROOM_PRICE +
                    reservationDetails.getDeluxeRooms() * DELUXE_ROOM_PRICE +
                    reservationDetails.getPenthouses() * PENTHOUSE_PRICE) * numberOfNights;
            double taxAmount = subTotal * TAX_RATE;
            double finalEstimatedTotal = subTotal + taxAmount; // No discount applied for saving to DB

            // First, initialize the database connection and table if they don't exist
            DatabaseManager.initialize();

            // Then, insert the reservation data into the database
            // Pass the final calculated total to the database manager
            DatabaseManager.insertReservation(guest, reservationDetails, finalEstimatedTotal);

            // In a real application, you would get the actual reservation ID from the DB here
            String dummyReservationId = "RES-" + System.currentTimeMillis(); // Placeholder ID

            // Navigate to the Booking Successful page
            MainApplication.loadBookingSuccessfulScene(dummyReservationId);

        } catch (Exception e) { // Catch generic Exception to ensure all errors are caught
            logger.log(Level.SEVERE, "Error saving reservation: " + e.getMessage(), e); // Log the full stack trace

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Database Error");
            alert.setHeaderText("Failed to Save Reservation");
            alert.setContentText("An error occurred while saving your reservation: " + e.getMessage() +
                    "\nPlease check the console for more details or contact support.");
            alert.showAndWait();
        }
    }

    /**
     * This method is for the "Back" button.
     * It navigates back to the KioskGuestDetailsPage-05.fxml.
     * @param event The action event from the button click.
     * @throws IOException If the FXML file for the previous page cannot be loaded.
     */
    @FXML // Added FXML annotation here
    private void handleBack(ActionEvent event) throws IOException {
        MainApplication.loadRoomDetailsScene(reservationDetails); // Go back to Guest Details page
    }

    @FXML
    private void handleViewRules(ActionEvent event) throws IOException {
        MainApplication.loadNewScene("KioskRulesPage-15.fxml");
    }
}