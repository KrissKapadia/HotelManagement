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
import javafx.scene.control.ButtonType;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

public class AdminCheckOutController {

    private static final Logger logger = Systemlogger.getLogger();

    @FXML
    private TextField phoneSearchField;
    @FXML
    private Button searchButton;

    @FXML
    private VBox checkoutDetailsView;

    @FXML private Label guestNameLabel;
    @FXML private Label phoneNumberLabel;
    @FXML private Label reservationIdLabel;
    @FXML private Label checkInDateLabel;
    @FXML private Label checkOutDateLabel;
    @FXML private Label roomTypeLabel;

    @FXML private Label roomChargesLabel;
    @FXML private Label taxesLabel;
    @FXML private TextField discountField;
    @FXML private Label subtotalLabel;
    @FXML private Label totalAmountLabel;

    @FXML private Button printBillButton;
    @FXML private Button checkoutButton;
    @FXML private Button backButton;

    private Guest currentGuest;
    private ReservationDetails currentReservationDetails;

    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    private DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("MMM dd, yyyy");

    private static final double TAX_RATE = 0.13; // 13% tax

    @FXML
    public void initialize() {
        checkoutDetailsView.setVisible(false);
        checkoutDetailsView.setManaged(false);

        DatabaseManager.initialize();

        // The listener is attached here to update the billing details whenever the discount field changes.
        discountField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBillingDetails();
        });

        // The discountField is now initialized as an empty string.
        // To show "if applicable only", you should set the promptText property in the FXML file.
        // For example: <TextField fx:id="discountField" promptText="if applicable only" />

        logger.info("AdminCheckOutController initialized.");
    }

    /**
     * Handles the action when the "Search" button is clicked.
     * Fetches reservation data from the database based on the entered mobile number
     * and displays it.
     * @param event The action event.
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String phoneNumber = phoneSearchField.getText().trim();
        logger.info("Admin attempting to search for reservation by phone number: " + phoneNumber);
        if (phoneNumber.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Required", "Please enter a phone number to search.");
            checkoutDetailsView.setVisible(false);
            checkoutDetailsView.setManaged(false);
            logger.warning("Search failed: Phone number field was empty.");
            return;
        }

        List<DatabaseManager.ReservationDisplay> searchResults = DatabaseManager.searchReservationsByPhoneNumber(phoneNumber);

        if (searchResults != null && !searchResults.isEmpty()) {
            DatabaseManager.ReservationDisplay displayData = searchResults.get(0);
            String reservationId = displayData.getReservationIdValue();

            Map<String, Object> fullReservationData = DatabaseManager.getReservationById(reservationId);

            if (fullReservationData != null) {
                currentGuest = (Guest) fullReservationData.get("guest");
                currentReservationDetails = (ReservationDetails) fullReservationData.get("reservationDetails");

                populateCheckoutDetails();
                checkoutDetailsView.setVisible(true);
                checkoutDetailsView.setManaged(true);
                logger.info("Reservation found and details populated for reservation ID: " + reservationId);
            } else {
                showAlert(Alert.AlertType.ERROR, "Data Error", "Could not retrieve full reservation details for ID: " + reservationId);
                checkoutDetailsView.setVisible(false);
                checkoutDetailsView.setManaged(false);
                logger.severe("Could not retrieve full reservation details for ID: " + reservationId);
            }
        } else {
            showAlert(Alert.AlertType.INFORMATION, "No Reservation Found", "No reservations found for phone number: " + phoneNumber);
            checkoutDetailsView.setVisible(false);
            checkoutDetailsView.setManaged(false);
            logger.info("No reservations found for phone number: " + phoneNumber);
        }
    }

    /**
     * Populates the UI labels and fields with the current guest and reservation details.
     */
    private void populateCheckoutDetails() {
        if (currentGuest != null && currentReservationDetails != null) {
            guestNameLabel.setText(currentGuest.getFullName());
            phoneNumberLabel.setText(currentGuest.getPhoneNumber());
            reservationIdLabel.setText(currentReservationDetails.getReservationId());
            checkInDateLabel.setText(currentReservationDetails.getCheckInDate().format(dateFormat));
            checkOutDateLabel.setText(currentReservationDetails.getCheckOutDate().format(dateFormat));
            roomTypeLabel.setText(currentReservationDetails.getRoomType());

            long numberOfNights = currentReservationDetails.getNumberOfNights();
            double roomCharges = (currentReservationDetails.getSingleRooms() * 100.00 +
                    currentReservationDetails.getDoubleRooms() * 150.00 +
                    currentReservationDetails.getDeluxeRooms() * 250.00 +
                    currentReservationDetails.getPenthouses() * 350.00) * numberOfNights;

            double taxes = roomCharges * TAX_RATE;
            double preDiscountSubtotal = roomCharges + taxes;

            roomChargesLabel.setText("$" + currencyFormat.format(roomCharges));
            taxesLabel.setText("$" + currencyFormat.format(taxes));

            // Set the estimated price on the reservation details object
            currentReservationDetails.setEstimatedPrice(preDiscountSubtotal);

            // Now call updateBillingDetails to set the subtotal and total due labels
            updateBillingDetails();
            logger.info("Populated checkout details for guest: " + currentGuest.getFullName() + ", Reservation ID: " + currentReservationDetails.getReservationId());
        }
    }

    /**
     * This method is responsible for recalculating the discount and total amount
     * whenever the discount percentage is changed.
     * This has been modified to update both the subtotal and total amount labels
     * with the final discounted price.
     */
    private void updateBillingDetails() {
        if (currentReservationDetails == null) {
            return;
        }

        // Get the initial subtotal (room charges + taxes) from the reservation details object
        double preDiscountSubtotal = currentReservationDetails.getEstimatedPrice();
        double discountAmount = 0.0;
        int discountPercentage = 0;

        try {
            String discountText = discountField.getText().trim();
            if (!discountText.isEmpty()) {
                discountPercentage = Integer.parseInt(discountText);
            }
        } catch (NumberFormatException e) {
            discountPercentage = 0;
            logger.warning("Invalid number format for discount percentage. Defaulting to 0.");
        }

        if (discountPercentage < 0 || discountPercentage > 100) {
            discountPercentage = Math.max(0, Math.min(100, discountPercentage));
            discountField.setText(String.valueOf(discountPercentage));
        }

        discountAmount = preDiscountSubtotal * (discountPercentage / 100.0);
        double finalTotal = preDiscountSubtotal - discountAmount;

        subtotalLabel.setText("$" + currencyFormat.format(preDiscountSubtotal)); // Displays the subtotal before discount
        totalAmountLabel.setText("Total Amount Due: $" + currencyFormat.format(finalTotal)); // Displays the final total after discount
        logger.info("Discount of " + discountPercentage + "% applied. New total due: " + finalTotal);
    }

    /**
     * Handles the action for the "Print Bill" button.
     * @param event The action event.
     */
    @FXML
    private void handlePrintBill(ActionEvent event) {
        if (currentReservationDetails == null) {
            showAlert(Alert.AlertType.WARNING, "No Reservation", "Please search for a reservation first to print a bill.");
            logger.warning("Attempted to print bill with no reservation loaded.");
            return;
        }
        logger.info("Print Bill button clicked for reservation ID: " + currentReservationDetails.getReservationId());
        showAlert(Alert.AlertType.INFORMATION, "Print Bill", "Printing functionality for the bill will be implemented here.");
    }

    /**
     * Handles the action for the "Proceed Checkout" button.
     * Displays a confirmation dialog and, if confirmed, updates the reservation status
     * to "checked-out" in the database and redirects to the Admin Dashboard.
     * @param event The action event.
     */
    @FXML
    private void handleCheckout(ActionEvent event) {
        if (currentReservationDetails == null) {
            showAlert(Alert.AlertType.WARNING, "No Reservation", "Please search for a reservation first to proceed with checkout.");
            logger.warning("Attempted to checkout with no reservation loaded.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirm Checkout");
        confirmationAlert.setHeaderText("Proceed with Checkout?");
        confirmationAlert.setContentText("Are you sure you want to mark reservation ID " + currentReservationDetails.getReservationId() + " as checked-out?");

        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            logger.info("User confirmed checkout for reservation ID: " + currentReservationDetails.getReservationId());

            // Calculate the final price to be saved.
            double finalPrice = 0.0;
            try {
                // The `subtotalLabel` now contains the pre-discount subtotal, so we need to
                // recalculate the final price based on the current discount field value.
                double preDiscountSubtotal = currentReservationDetails.getEstimatedPrice();
                int discountPercentage = 0;
                String discountText = discountField.getText().trim();
                if (!discountText.isEmpty()) {
                    discountPercentage = Integer.parseInt(discountText);
                }

                double discountAmount = preDiscountSubtotal * (discountPercentage / 100.0);
                finalPrice = preDiscountSubtotal - discountAmount;
            } catch (NumberFormatException e) {
                logger.severe("Could not parse discount amount for checkout: " + e.getMessage());
                showAlert(Alert.AlertType.ERROR, "Calculation Error", "Failed to parse discount value. Please enter a valid number.");
                return;
            }

            // Call the correct method to save the final price and update the status.
            boolean success = DatabaseManager.saveCheckoutDetails(currentReservationDetails.getReservationId(), finalPrice);

            if (success) {
                showAlert(Alert.AlertType.INFORMATION, "Checkout Successful", "Reservation " + currentReservationDetails.getReservationId() + " has been successfully checked out.");
                logger.info("Successfully checked out reservation ID: " + currentReservationDetails.getReservationId() + " with a final price of " + finalPrice);
                try {
                    MainApplication.loadAdminDashboardScene();
                } catch (IOException e) {
                    logger.severe("Failed to load Admin Dashboard after successful checkout: " + e.getMessage());
                    showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load Admin Dashboard: " + e.getMessage());
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Checkout Failed", "Failed to update reservation status. Please try again.");
                logger.severe("Failed to update reservation status for ID: " + currentReservationDetails.getReservationId());
            }
        } else {
            logger.info("Checkout process cancelled by user for reservation ID: " + currentReservationDetails.getReservationId());
            showAlert(Alert.AlertType.INFORMATION, "Checkout Cancelled", "Checkout process cancelled by user.");
        }
    }

    /**
     * Clears all the fields and labels in the checkout form.
     */
    private void clearForm() {
        phoneSearchField.clear();
        guestNameLabel.setText("");
        phoneNumberLabel.setText("");
        reservationIdLabel.setText("");
        checkInDateLabel.setText("");
        checkOutDateLabel.setText("");
        roomTypeLabel.setText("");
        roomChargesLabel.setText("");
        taxesLabel.setText("");
        discountField.clear(); // Use clear() to make it empty
        subtotalLabel.setText("");
        totalAmountLabel.setText("");
        currentGuest = null;
        currentReservationDetails = null;
    }

    /**
     * Handles the action for the "Back to Dashboard" button.
     * @param event The action event.
     * @throws IOException If the FXML for the dashboard cannot be loaded.
     */
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        logger.info("Navigating back to Admin Dashboard from checkout page.");
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
