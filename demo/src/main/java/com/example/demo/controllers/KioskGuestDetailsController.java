package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.Guest;
import com.example.demo.models.ReservationDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.io.IOException;

public class KioskGuestDetailsController {

    @FXML
    private TextField fullNameField;
    @FXML
    private TextField phoneNumberField;
    @FXML
    private TextField emailField;
    @FXML
    private TextArea addressTextArea;
    @FXML
    private ComboBox<String> provinceComboBox;
    @FXML
    private TextField cityField;
    @FXML
    private TextField postalCodeField;
    @FXML
    private ComboBox<String> idProofTypeComboBox;
    @FXML
    private TextField idProofNumberField;

    @FXML
    private Label totalRoomsLabel;
    @FXML
    private Label totalGuestsLabel;
    @FXML
    private Button confirmDetailsButton;
    @FXML
    private Button backButton;

    private ReservationDetails reservationDetails;

    @FXML
    public void initialize() {
        provinceComboBox.getItems().addAll("Ontario", "Quebec", "British Columbia", "Alberta");
        idProofTypeComboBox.getItems().addAll("Passport", "Driver's License", "Health Card");
    }

    public void setReservationDetails(ReservationDetails reservation) {
        this.reservationDetails = reservation;
        updateLabels();
    }

    private void updateLabels() {
        if (reservationDetails != null) {
            // Calculate total rooms and total guests
            int totalRooms = reservationDetails.getSingleRooms() + reservationDetails.getDoubleRooms() +
                    reservationDetails.getDeluxeRooms() + reservationDetails.getPenthouses(); // Assuming getPenthouses() is correct
            int totalGuests = reservationDetails.getAdults() + reservationDetails.getChildren();

            // Set the text for the labels
            totalRoomsLabel.setText("Total Rooms: " + totalRooms);
            totalGuestsLabel.setText("Total Guests: " + totalGuests);
        }
    }

    /**
     * This method is triggered when the user clicks the "Confirm Details" button.
     * It validates all guest details and, if valid, proceeds to the room confirmation scene.
     * @param event The action event from the button click.
     * @throws IOException If the FXML file for the next scene cannot be loaded.
     */
    @FXML
    private void handleContinue(ActionEvent event) throws IOException {
        // Explicitly declaring String type for clarity, though getText() already returns String
        String fullName = fullNameField.getText();
        String phoneNumber = phoneNumberField.getText(); // Stored as String for flexibility (e.g., "+1 (555) 123-4567")
        String email = emailField.getText();
        String address = addressTextArea.getText();
        String province = provinceComboBox.getValue();
        String city = cityField.getText();
        String postalCode = postalCodeField.getText(); // Stored as String (e.g., "M5V 2H1" or "12345-6789")
        String idProofType = idProofTypeComboBox.getValue();
        String idProofNumber = idProofNumberField.getText(); // Stored as String (can contain letters/special chars)

        if (fullName.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() || address.isEmpty() ||
                province == null || city.isEmpty() || postalCode.isEmpty() || idProofType == null ||
                idProofNumber.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all required guest details.");
            return;
        }

        Guest guest = new Guest(fullName, phoneNumber, email, address, province, city, postalCode, idProofType, idProofNumber);

        // Load the next scene, passing the guest and reservation details
        MainApplication.loadRoomConfirmationScene(guest, reservationDetails);
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        MainApplication.loadRoomSelectionScene(reservationDetails);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
