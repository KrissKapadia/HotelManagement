package com.example.demo.controllers;

import com.example.demo.MainApplication; // Assuming MainApplication is in com.example.demo
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert; // For showing alerts
import java.io.IOException;
import java.text.DecimalFormat; // For currency formatting

public class AdminCheckOutController {

    @FXML
    private Label reservationInfoLabel;
    @FXML
    private Label roomChargesLabel;
    @FXML
    private Label taxesLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private TextField discountField;
    @FXML
    private Label discountAmountLabel;
    @FXML
    private Label totalAmountLabel;
    @FXML
    private Button printBillButton;
    @FXML
    private Button backButton;
    // Note: The "Proceed Checkout" button in FXML does not have an fx:id,
    // so it won't be directly accessible here unless one is added.

    // Dummy data for demonstration. In a real app, this would come from a model.
    private double initialRoomCharges = 300.00;
    private double initialTaxes = 39.00;
    private double initialSubtotal = initialRoomCharges + initialTaxes; // Should be calculated
    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");

    @FXML
    public void initialize() {
        // Set initial values for labels
        reservationInfoLabel.setText("Reservation ID: #1045   Guest: John Doe"); // Dummy data
        roomChargesLabel.setText(currencyFormat.format(initialRoomCharges));
        taxesLabel.setText(currencyFormat.format(initialTaxes));
        subtotalLabel.setText(currencyFormat.format(initialSubtotal));
        discountAmountLabel.setText(currencyFormat.format(0.00)); // Start with no discount
        totalAmountLabel.setText("Total Amount Due: " + currencyFormat.format(initialSubtotal));

        // Add a listener to the discount field to update calculations dynamically
        discountField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateBillingDetails();
        });

        // Ensure discount field starts with 0
        discountField.setText("0");
    }

    /**
     * This method is responsible for recalculating the discount and total amount
     * whenever the discount percentage is changed.
     */
    private void updateBillingDetails() {
        double discountPercentage = 0.0;
        try {
            // Remove '%' and parse as double
            String discountText = discountField.getText().replace("%", "").trim();
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

        double discountAmount = initialSubtotal * (discountPercentage / 100.0);
        double finalTotal = initialSubtotal - discountAmount;

        discountAmountLabel.setText(currencyFormat.format(-discountAmount)); // Display as negative
        totalAmountLabel.setText("Total Amount Due: " + currencyFormat.format(finalTotal));
    }

    /**
     * Handles the action for the "Print Bill" button.
     * @param event The action event.
     */
    @FXML
    private void handlePrintBill(ActionEvent event) {
        showAlert(Alert.AlertType.INFORMATION, "Print Bill", "Printing functionality will be implemented here.");
    }

    /**
     * Handles the action for the "Back" button.
     * @param event The action event.
     * @throws IOException If the FXML for the previous page cannot be loaded.
     */
    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        // Assuming you want to go back to the Admin Dashboard
        MainApplication.loadAdminDashboardScene();
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
