package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.DatabaseManager;
import com.example.demo.models.Guest;
import com.example.demo.models.ReservationDetails;
import com.example.demo.util.Systemlogger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AdminReportsController {

    private static final Logger logger = Systemlogger.getLogger();

    @FXML
    private TextField searchField;
    @FXML
    private Button generateReportButton;

    @FXML
    private VBox billView;

    @FXML
    private Label guestNameLabel;
    @FXML
    private Label reservationIdLabel;
    @FXML
    private Label billIdLabel;
    @FXML
    private Label checkInLabel;
    @FXML
    private Label checkOutLabel;
    @FXML
    private Label billDateLabel;

    @FXML
    private Label roomChargesLabel;
    @FXML
    private Label taxesLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button printBillButton;
    @FXML
    private Button backButton;

    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private static final double TAX_RATE = 0.13; // 13% tax

    // Room prices per night (should match ReservationDetails for consistency)
    private static final double SINGLE_ROOM_PRICE = 100.00;
    private static final double DOUBLE_ROOM_PRICE = 150.00;
    private static final double DELUXE_ROOM_PRICE = 250.00;
    private static final double PENTHOUSE_PRICE = 350.00;

    @FXML
    public void initialize() {
        billView.setVisible(false);
        billView.setManaged(false);

        DatabaseManager.initialize();

        logger.info("AdminReportsController initialized. Discount functionality removed.");
    }

    /**
     * Handles the action when the "Generate Report" button is clicked.
     * Fetches reservation data from the database based on the entered mobile number
     * and displays it as a report.
     * @param event The action event.
     */
    @FXML
    private void handleGenerateReport(ActionEvent event) {
        String mobileNumber = searchField.getText().trim();
        logger.info("Admin attempting to generate report for mobile number: " + mobileNumber);
        if (mobileNumber.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a mobile number to generate a report.");
            billView.setVisible(false);
            billView.setManaged(false);
            logger.warning("Attempted to generate report with an empty mobile number field.");
            return;
        }

        List<DatabaseManager.ReservationDisplay> searchResults = DatabaseManager.searchReservationsByPhoneNumber(mobileNumber);

        if (searchResults != null && !searchResults.isEmpty()) {
            DatabaseManager.ReservationDisplay displayData = searchResults.get(0);
            String reservationId = displayData.getReservationIdValue();

            Map<String, Object> fullReservationData = DatabaseManager.getReservationById(reservationId);

            if (fullReservationData != null) {
                Guest guest = (Guest) fullReservationData.get("guest");
                ReservationDetails details = (ReservationDetails) fullReservationData.get("reservationDetails");

                // Populate general information
                guestNameLabel.setText(guest.getFullName());
                reservationIdLabel.setText(details.getReservationId());
                billIdLabel.setText("BILL-" + details.getReservationId());
                checkInLabel.setText(details.getCheckInDate().format(dateFormat));
                checkOutLabel.setText(details.getCheckOutDate().format(dateFormat));
                billDateLabel.setText(LocalDate.now().format(dateFormat));

                // Calculate charges based on reservation details
                long numberOfNights = details.getNumberOfNights();
                double roomCharges = (details.getSingleRooms() * SINGLE_ROOM_PRICE +
                        details.getDoubleRooms() * DOUBLE_ROOM_PRICE +
                        details.getDeluxeRooms() * DELUXE_ROOM_PRICE +
                        details.getPenthouses() * PENTHOUSE_PRICE) * numberOfNights;
                double taxes = roomCharges * TAX_RATE;
                double subtotal = roomCharges + taxes;
                double totalAmountDue = subtotal; // Total is now simply the subtotal

                // Update labels
                roomChargesLabel.setText(currencyFormat.format(roomCharges));
                taxesLabel.setText(currencyFormat.format(taxes));
                subtotalLabel.setText(currencyFormat.format(subtotal));
                totalAmountLabel.setText(currencyFormat.format(totalAmountDue));

                billView.setVisible(true);
                billView.setManaged(true);
                logger.info("Report generated successfully for reservation ID: " + reservationId + " and guest: " + guest.getFullName());
            } else {
                showAlert(Alert.AlertType.ERROR, "Data Error", "Could not retrieve full reservation details for ID: " + reservationId);
                billView.setVisible(false);
                billView.setManaged(false);
                logger.severe("Could not retrieve full reservation details for ID: " + reservationId);
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Report Found", "No reservations found for the mobile number: " + mobileNumber);
            billView.setVisible(false);
            billView.setManaged(false);
            logger.warning("No reservations found for mobile number: " + mobileNumber);
        }
    }

    /**
     * Handles the action for the "Print Bill" button.
     * @param event The action event.
     */
    @FXML
    private void handlePrintBill(ActionEvent event) {
        logger.info("Print Bill button clicked.");
        showAlert(Alert.AlertType.INFORMATION, "Print Bill", "Printing functionality for the bill will be implemented here.");
    }

    /**
     * Handles the action for the "Back to Dashboard" button.
     * @param event The action event.
     * @throws IOException If the FXML for the dashboard cannot be loaded.
     */
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        logger.info("Navigating back to Admin Dashboard from reports page.");
        MainApplication.loadAdminDashboardScene();
    }

    /**
     * Helper method to display an alert.
     * @param type The type of alert (e.g., WARNING, INFORMATION).
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
