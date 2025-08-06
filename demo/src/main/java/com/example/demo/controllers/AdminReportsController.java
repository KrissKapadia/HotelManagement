package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.DatabaseManager;
import com.example.demo.models.Guest;
import com.example.demo.models.ReservationDetails;
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

public class AdminReportsController {

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
    private Label billIdLabel; // This will be a generated ID for the report
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
    private TextField discountField; // For user input of discount percentage
    @FXML
    private Label discountLabel; // For displaying the calculated discount amount
    @FXML
    private Label totalAmountLabel;

    @FXML
    private Button printBillButton;
    @FXML
    private Button backButton;

    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    // Variables to hold the current reservation's billing details for recalculation
    private double currentRoomCharges = 0.0;
    private double currentTaxes = 0.0;
    private double currentSubtotal = 0.0;
    private double currentDiscountAmount = 0.0;
    private double currentTotalDue = 0.0;

    private static final double TAX_RATE = 0.13; // 13% tax

    // Room prices per night (should match ReservationDetails for consistency)
    private static final double SINGLE_ROOM_PRICE = 100.00;
    private static final double DOUBLE_ROOM_PRICE = 150.00;
    private static final double DELUXE_ROOM_PRICE = 250.00;
    private static final double PENTHOUSE_PRICE = 350.00;

    @FXML
    public void initialize() {
        // Hide the bill view initially
        billView.setVisible(false);
        billView.setManaged(false); // Do not take up space in layout

        // Initialize database (important before any DB operations)
        DatabaseManager.initialize();

        // Add a listener to the discount field to update calculations dynamically
        discountField.textProperty().addListener((observable, oldValue, newValue) -> {
            applyDiscount();
        });

        // Ensure discount field starts with "0"
        discountField.setText("0");
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
        if (mobileNumber.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a mobile number to generate a report.");
            billView.setVisible(false);
            billView.setManaged(false);
            return;
        }

        // Search for reservations by phone number
        List<DatabaseManager.ReservationDisplay> searchResults = DatabaseManager.searchReservationsByPhoneNumber(mobileNumber);

        if (searchResults != null && !searchResults.isEmpty()) {
            // For simplicity, we'll take the first matching reservation.
            // In a real scenario, you might want to let the admin choose if multiple exist.
            DatabaseManager.ReservationDisplay displayData = searchResults.get(0);
            String reservationId = displayData.getReservationIdValue();

            // Retrieve the full Guest and ReservationDetails objects
            Map<String, Object> fullReservationData = DatabaseManager.getReservationById(reservationId);

            if (fullReservationData != null) {
                Guest guest = (Guest) fullReservationData.get("guest");
                ReservationDetails details = (ReservationDetails) fullReservationData.get("reservationDetails");

                // Populate general information
                guestNameLabel.setText(guest.getFullName());
                reservationIdLabel.setText(details.getReservationId());
                billIdLabel.setText("BILL-" + details.getReservationId()); // Generate a simple bill ID
                checkInLabel.setText(details.getCheckInDate().format(dateFormat));
                checkOutLabel.setText(details.getCheckOutDate().format(dateFormat));
                billDateLabel.setText(LocalDate.now().format(dateFormat)); // Current date for bill date

                // Calculate initial charges based on reservation details
                long numberOfNights = details.getNumberOfNights();
                currentRoomCharges = (details.getSingleRooms() * SINGLE_ROOM_PRICE +
                        details.getDoubleRooms() * DOUBLE_ROOM_PRICE +
                        details.getDeluxeRooms() * DELUXE_ROOM_PRICE +
                        details.getPenthouses() * PENTHOUSE_PRICE) * numberOfNights;

                currentTaxes = currentRoomCharges * TAX_RATE;
                currentSubtotal = currentRoomCharges + currentTaxes;
                currentDiscountAmount = 0.0; // Reset discount when a new report is generated
                currentTotalDue = currentSubtotal;

                // Update labels
                roomChargesLabel.setText(currencyFormat.format(currentRoomCharges));
                taxesLabel.setText(currencyFormat.format(currentTaxes));
                subtotalLabel.setText(currencyFormat.format(currentSubtotal));
                discountField.setText("0"); // Reset discount field
                discountLabel.setText(currencyFormat.format(currentDiscountAmount));
                totalAmountLabel.setText(currencyFormat.format(currentTotalDue));

                billView.setVisible(true);
                billView.setManaged(true);
            } else {
                showAlert(Alert.AlertType.ERROR, "Data Error", "Could not retrieve full reservation details for ID: " + reservationId);
                billView.setVisible(false);
                billView.setManaged(false);
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Report Found", "No reservations found for the mobile number: " + mobileNumber);
            billView.setVisible(false);
            billView.setManaged(false);
        }
    }

    /**
     * Applies the discount entered in the discountField and updates the total amount due.
     */
    private void applyDiscount() {
        double discountPercentage = 0.0;
        try {
            String discountText = discountField.getText().trim();
            if (!discountText.isEmpty()) {
                discountPercentage = Double.parseDouble(discountText);
            }
        } catch (NumberFormatException e) {
            // Handle invalid input, e.g., show an error or default to 0
            discountPercentage = 0.0;
            // Optionally, show an alert or set a specific error message on the UI
            // showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid number for discount.");
        }

        // Ensure discount percentage is within a reasonable range (e.g., 0-100)
        if (discountPercentage < 0) discountPercentage = 0;
        if (discountPercentage > 100) discountPercentage = 100;

        currentDiscountAmount = currentSubtotal * (discountPercentage / 100.0);
        currentTotalDue = currentSubtotal - currentDiscountAmount;

        discountLabel.setText(currencyFormat.format(-currentDiscountAmount)); // Display as negative
        totalAmountLabel.setText(currencyFormat.format(currentTotalDue));
    }

    /**
     * Handles the action for the "Print Bill" button.
     * @param event The action event.
     */
    @FXML
    private void handlePrintBill(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Print Bill", "Printing functionality for the bill will be implemented here.");
    }

    /**
     * Handles the action for the "Back to Dashboard" button.
     * @param event The action event.
     * @throws IOException If the FXML for the dashboard cannot be loaded.
     */
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
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
