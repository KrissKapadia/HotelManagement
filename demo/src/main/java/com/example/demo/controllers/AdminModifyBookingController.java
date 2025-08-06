package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.DatabaseManager;
import com.example.demo.models.Guest;
import com.example.demo.models.ReservationDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import javafx.scene.control.DatePicker;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AdminModifyBookingController {

    // Guest Information Fields
    @FXML private TextField fullNameField;
    @FXML private TextField phoneNumberField;
    @FXML private TextField emailField;
    @FXML private TextArea addressField;
    @FXML private TextField cityField;
    @FXML private ComboBox<String> provinceComboBox;
    @FXML private TextField postalCodeField;
    @FXML private ComboBox<String> idProofTypeComboBox;
    @FXML private TextField idProofNumberField;

    // Reservation Information Fields
    @FXML private Label reservationIdLabel;
    @FXML private DatePicker checkInDateField;
    @FXML private DatePicker checkOutDateField;
    @FXML private TextField numberOfAdultsField;
    @FXML private TextField numberOfChildrenField;
    @FXML private ComboBox<String> roomTypeComboBox;
    @FXML private TextField roomNumberField;
    @FXML private ComboBox<String> statusComboBox;

    // Room Counts
    @FXML private TextField singleRoomsField;
    @FXML private TextField doubleRoomsField;
    @FXML private TextField deluxeRoomsField;
    @FXML private TextField penthousesField;

    // Billing Field (Discount Percentage field removed as per FXML)
    @FXML private Label estimatedTotalLabel; // This will show the final price after tax

    // Buttons
    @FXML private Button saveChangesButton;
    @FXML private Button backButton;

    private Guest guest;
    private ReservationDetails reservationDetails;

    // Room prices per night (must match your actual pricing logic)
    private static final double SINGLE_ROOM_PRICE = 100.00;
    private static final double DOUBLE_ROOM_PRICE = 150.00;
    private static final double DELUXE_ROOM_PRICE = 250.00;
    private static final double PENTHOUSE_PRICE = 350.00;
    private static final double TAX_RATE = 0.13; // 13% tax

    private DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");


    @FXML
    public void initialize() {
        // Initialize ComboBoxes
        provinceComboBox.getItems().addAll("Ontario", "Quebec", "British Columbia", "Alberta", "Manitoba", "New Brunswick", "Newfoundland and Labrador", "Nova Scotia", "Prince Edward Island", "Saskatchewan");
        statusComboBox.getItems().addAll("Confirmed", "Checked-in", "Checked-out");
        roomTypeComboBox.getItems().addAll("Single", "Double", "Deluxe", "Penthouse");
        idProofTypeComboBox.getItems().addAll("Passport", "Driver's License", "Health Card", "Other");

        // Add listeners for numeric fields to ensure only numbers are entered
        // Only for fields present in the FXML
        addNumericValidation(numberOfAdultsField);
        addNumericValidation(numberOfChildrenField);
        addNumericValidation(singleRoomsField);
        addNumericValidation(doubleRoomsField);
        addNumericValidation(deluxeRoomsField);
        addNumericValidation(penthousesField);
        // Removed: addNumericValidation(discountPercentageField);

        // Add listeners to recalculate total when relevant fields change
        checkInDateField.valueProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
        checkOutDateField.valueProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
        numberOfAdultsField.textProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
        numberOfChildrenField.textProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
        singleRoomsField.textProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
        doubleRoomsField.textProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
        deluxeRoomsField.textProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
        penthousesField.textProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
        // Removed: discountPercentageField.textProperty().addListener((obs, oldVal, newVal) -> updateEstimatedPrice());
    }

    /**
     * Sets the Guest and ReservationDetails objects for this controller
     * and populates the UI fields with the respective data for modification.
     * @param guest The Guest object containing guest information.
     * @param reservationDetails The ReservationDetails object containing reservation info.
     */
    public void setGuestAndReservationDetails(Guest guest, ReservationDetails reservationDetails) {
        this.guest = guest;
        this.reservationDetails = reservationDetails;
        populateFields();
        updateEstimatedPrice(); // Initial price calculation
    }

    /**
     * Populates all the FXML input fields with data from the Guest and ReservationDetails objects.
     */
    private void populateFields() {
        if (guest != null) {
            fullNameField.setText(guest.getFullName() != null ? guest.getFullName() : "");
            phoneNumberField.setText(guest.getPhoneNumber() != null ? guest.getPhoneNumber() : "");
            emailField.setText(guest.getEmail() != null ? guest.getEmail() : "");
            addressField.setText(guest.getAddress() != null ? guest.getAddress() : "");
            cityField.setText(guest.getCity() != null ? guest.getCity() : "");
            provinceComboBox.setValue(guest.getProvince());
            postalCodeField.setText(guest.getPostalCode() != null ? guest.getPostalCode() : "");
            idProofTypeComboBox.setValue(guest.getIdProofType());
            idProofNumberField.setText(guest.getIdProofNumber() != null ? guest.getIdProofNumber() : "");
        } else {
            // Clear all guest fields if guest object is null
            fullNameField.setText(""); phoneNumberField.setText(""); emailField.setText("");
            addressField.setText(""); cityField.setText(""); provinceComboBox.setValue(null);
            postalCodeField.setText(""); idProofTypeComboBox.setValue(null); idProofNumberField.setText("");
        }

        if (reservationDetails != null) {
            reservationIdLabel.setText(reservationDetails.getReservationId() != null ? reservationDetails.getReservationId() : "N/A");
            checkInDateField.setValue(reservationDetails.getCheckInDate());
            checkOutDateField.setValue(reservationDetails.getCheckOutDate());
            numberOfAdultsField.setText(String.valueOf(reservationDetails.getNumberOfAdults()));
            numberOfChildrenField.setText(String.valueOf(reservationDetails.getNumberOfChildren()));
            statusComboBox.setValue(reservationDetails.getStatus());
            roomNumberField.setText(reservationDetails.getRoomNumber());
            roomTypeComboBox.setValue(reservationDetails.getRoomType());

            singleRoomsField.setText(String.valueOf(reservationDetails.getSingleRooms()));
            doubleRoomsField.setText(String.valueOf(reservationDetails.getDoubleRooms()));
            deluxeRoomsField.setText(String.valueOf(reservationDetails.getDeluxeRooms()));
            penthousesField.setText(String.valueOf(reservationDetails.getPenthouses()));

            // Removed: discountPercentageField.setText("0.0");
        } else {
            // Clear all reservation fields if reservationDetails object is null
            reservationIdLabel.setText("N/A"); checkInDateField.setValue(null); checkOutDateField.setValue(null);
            numberOfAdultsField.setText(""); numberOfChildrenField.setText(""); statusComboBox.setValue(null);
            roomNumberField.setText(""); roomTypeComboBox.setValue(null);
            singleRoomsField.setText(""); doubleRoomsField.setText(""); deluxeRoomsField.setText(""); penthousesField.setText("");
            // Removed: discountPercentageField.setText("0.0");
        }
    }

    /**
     * Recalculates and updates the estimated price based on current room counts and dates.
     * Discount functionality is removed as per FXML.
     */
    private void updateEstimatedPrice() {
        if (reservationDetails == null || checkInDateField.getValue() == null || checkOutDateField.getValue() == null) {
            estimatedTotalLabel.setText(currencyFormat.format(0.0));
            return;
        }

        long numberOfNights = ChronoUnit.DAYS.between(checkInDateField.getValue(), checkOutDateField.getValue());
        if (numberOfNights < 0) numberOfNights = 0; // Prevent negative nights

        int singleRooms = parseTextFieldInt(singleRoomsField);
        int doubleRooms = parseTextFieldInt(doubleRoomsField);
        int deluxeRooms = parseTextFieldInt(deluxeRoomsField);
        int penthouses = parseTextFieldInt(penthousesField);

        double subTotal = (singleRooms * SINGLE_ROOM_PRICE +
                doubleRooms * DOUBLE_ROOM_PRICE +
                deluxeRooms * DELUXE_ROOM_PRICE +
                penthouses * PENTHOUSE_PRICE) * numberOfNights;

        // Removed discount calculation
        // double discountPercentage = parseTextFieldDouble(discountPercentageField);
        // if (discountPercentage < 0) discountPercentage = 0;
        // if (discountPercentage > 100) discountPercentage = 100;
        // double discountAmount = subTotal * (discountPercentage / 100.0);
        // double priceAfterDiscount = subTotal - discountAmount;

        double priceAfterDiscount = subTotal; // No discount applied
        double taxAmount = priceAfterDiscount * TAX_RATE;
        double finalEstimatedTotal = priceAfterDiscount + taxAmount;

        estimatedTotalLabel.setText(currencyFormat.format(finalEstimatedTotal));

        // Update the estimatedPrice in the reservationDetails object for saving
        reservationDetails.setEstimatedPrice(finalEstimatedTotal);
    }

    /**
     * Helper to safely parse int from TextField.
     */
    private int parseTextFieldInt(TextField field) {
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException e) {
            return 0; // Default to 0 if invalid number
        }
    }

    /**
     * Helper to safely parse double from TextField.
     */
    private double parseTextFieldDouble(TextField field) {
        try {
            return Double.parseDouble(field.getText());
        } catch (NumberFormatException e) {
            return 0.0; // Default to 0.0 if invalid number
        }
    }


    /**
     * Handles the action when the "Save Changes" button is clicked.
     * Validates input, updates the Guest and ReservationDetails objects,
     * and saves the changes to the database.
     * @param event The action event.
     */
    @FXML
    private void handleSaveChanges(ActionEvent event) {
        if (guest == null || reservationDetails == null) {
            showAlert(Alert.AlertType.ERROR, "Error", "No reservation data loaded to save.");
            return;
        }

        // 1. Validate input fields
        if (!validateInput()) {
            return; // Validation failed, error message already shown
        }

        // 2. Update Guest object from fields
        guest.setFullName(fullNameField.getText());
        guest.setPhoneNumber(phoneNumberField.getText());
        guest.setEmail(emailField.getText());
        guest.setAddress(addressField.getText());
        guest.setCity(cityField.getText());
        guest.setProvince(provinceComboBox.getValue());
        guest.setPostalCode(postalCodeField.getText());
        guest.setIdProofType(idProofTypeComboBox.getValue());
        guest.setIdProofNumber(idProofNumberField.getText());


        // 3. Update ReservationDetails object from fields
        reservationDetails.setCheckInDate(checkInDateField.getValue());
        reservationDetails.setCheckOutDate(checkOutDateField.getValue());
        try {
            reservationDetails.setNumberOfAdults(Integer.parseInt(numberOfAdultsField.getText()));
            reservationDetails.setNumberOfChildren(Integer.parseInt(numberOfChildrenField.getText()));
            reservationDetails.setSingleRooms(Integer.parseInt(singleRoomsField.getText()));
            reservationDetails.setDoubleRooms(Integer.parseInt(doubleRoomsField.getText()));
            reservationDetails.setDeluxeRooms(Integer.parseInt(deluxeRoomsField.getText()));
            reservationDetails.setPenthouses(Integer.parseInt(penthousesField.getText()));
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numbers for guests and rooms.");
            return;
        }
        reservationDetails.setStatus(statusComboBox.getValue());
        reservationDetails.setRoomNumber(roomNumberField.getText());
        reservationDetails.setRoomType(roomTypeComboBox.getValue());

        // The estimatedPrice in reservationDetails is already updated by updateEstimatedPrice()
        // which is called by listeners and populateFields().

        // 4. Save changes to database
        DatabaseManager.initialize(); // Ensure DB is ready
        boolean success = DatabaseManager.updateReservation(guest, reservationDetails);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", "Booking details updated successfully!");
        } else {
            showAlert(Alert.AlertType.ERROR, "Save Failed", "Failed to update booking details in the database.");
        }
    }

    /**
     * Basic input validation for critical fields.
     * @return true if all required fields are valid, false otherwise.
     */
    private boolean validateInput() {
        if (fullNameField.getText().trim().isEmpty() ||
                phoneNumberField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                addressField.getText().trim().isEmpty() ||
                cityField.getText().trim().isEmpty() ||
                provinceComboBox.getValue() == null ||
                postalCodeField.getText().trim().isEmpty() ||
                idProofTypeComboBox.getValue() == null || idProofNumberField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Information", "Please fill in all guest information fields.");
            return false;
        }

        if (checkInDateField.getValue() == null || checkOutDateField.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Dates", "Please select both check-in and check-out dates.");
            return false;
        }

        if (checkOutDateField.getValue().isBefore(checkInDateField.getValue())) {
            showAlert(Alert.AlertType.ERROR, "Invalid Dates", "Check-out date cannot be before check-in date.");
            return false;
        }

        try {
            if (Integer.parseInt(numberOfAdultsField.getText()) < 0 || Integer.parseInt(numberOfChildrenField.getText()) < 0 ||
                    Integer.parseInt(singleRoomsField.getText()) < 0 || Integer.parseInt(doubleRoomsField.getText()) < 0 ||
                    Integer.parseInt(deluxeRoomsField.getText()) < 0 || Integer.parseInt(penthousesField.getText()) < 0) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", "Guest and room counts cannot be negative.");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter valid numbers for guests and rooms.");
            return false;
        }

        if (statusComboBox.getValue() == null || roomNumberField.getText().trim().isEmpty() || roomTypeComboBox.getValue() == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Reservation Details", "Please fill in all reservation details (Status, Room Number, Room Type).");
            return false;
        }

        return true;
    }

    /**
     * Helper method to add numeric input validation to a TextField.
     * @param field The TextField to validate.
     */
    private void addNumericValidation(TextField field) {
        if (field != null) { // Defensive null check
            field.textProperty().addListener((observable, oldValue, newValue) -> {
                // Only allow digits
                if (!newValue.matches("\\d*")) {
                    field.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
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