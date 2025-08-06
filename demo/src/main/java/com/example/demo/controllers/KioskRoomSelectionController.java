package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.ReservationDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;

import java.io.IOException;

public class KioskRoomSelectionController {

    // FXML elements
    @FXML
    private Label summaryLabel;
    @FXML
    private Label selectionMessageLabel;

    // Spinners for room selection
    @FXML
    private Spinner<Integer> singleRoomSpinner;
    @FXML
    private Spinner<Integer> doubleRoomSpinner;
    @FXML
    private Spinner<Integer> deluxeRoomSpinner;
    @FXML
    private Spinner<Integer> penthousesSpinner; // Corrected fx:id to match standard naming

    private ReservationDetails reservationDetails;

    // Room prices per night
    private static final double SINGLE_ROOM_PRICE = 100.00;
    private static final double DOUBLE_ROOM_PRICE = 150.00;
    private static final double DELUXE_ROOM_PRICE = 250.00;
    private static final double PENTHOUSE_PRICE = 350.00; // Price for Penthouse

    // Room capacities
    private static final int SINGLE_ADULT_CAPACITY = 1;
    private static final int DOUBLE_ADULT_CAPACITY = 2;
    private static final int DELUXE_ADULT_CAPACITY = 3;
    private static final int DELUXE_CHILD_CAPACITY = 1;
    private static final int PENTHOUSE_ADULT_CAPACITY = 4;
    private static final int PENTHOUSE_CHILD_CAPACITY = 2;


    @FXML
    public void initialize() {
        // Initialize spinners
        singleRoomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        doubleRoomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        deluxeRoomSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        penthousesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));

        // Add listeners to spinners to update summary in real-time
        singleRoomSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateReservationAndSummary());
        doubleRoomSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateReservationAndSummary());
        deluxeRoomSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateReservationAndSummary());
        penthousesSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateReservationAndSummary());
    }

    public void setReservationDetails(ReservationDetails reservation) {
        this.reservationDetails = reservation;
        if (this.reservationDetails != null) {
            // Set spinner values from existing reservation details
            singleRoomSpinner.getValueFactory().setValue(reservation.getSingleRooms());
            doubleRoomSpinner.getValueFactory().setValue(reservation.getDoubleRooms());
            deluxeRoomSpinner.getValueFactory().setValue(reservation.getDeluxeRooms());
            penthousesSpinner.getValueFactory().setValue(reservation.getPenthouses());
            updateSummary();
        }
    }

    private void updateSummary() {
        if (summaryLabel != null && reservationDetails != null) {
            summaryLabel.setText(reservationDetails.getSummary());
        }
    }

    /**
     * Updates the reservation details with the current spinner values and refreshes the summary.
     */
    private void updateReservationAndSummary() {
        if (reservationDetails != null) {
            // Update room counts
            reservationDetails.setSingleRooms(singleRoomSpinner.getValue());
            reservationDetails.setDoubleRooms(doubleRoomSpinner.getValue());
            reservationDetails.setDeluxeRooms(deluxeRoomSpinner.getValue());
            reservationDetails.setPenthouses(penthousesSpinner.getValue());

            // Calculate estimated price based on room selection and night count
            double estimatedPrice = (singleRoomSpinner.getValue() * SINGLE_ROOM_PRICE +
                    doubleRoomSpinner.getValue() * DOUBLE_ROOM_PRICE +
                    deluxeRoomSpinner.getValue() * DELUXE_ROOM_PRICE +
                    penthousesSpinner.getValue() * PENTHOUSE_PRICE) *
                    reservationDetails.getNumberOfNights();

            reservationDetails.setEstimatedPrice(estimatedPrice);

            updateSummary();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        MainApplication.loadGuestSelectionScene(reservationDetails);
    }

    @FXML
    private void handleViewRules(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Hotel Rules");
        alert.setHeaderText("Guest and Room Rules");
        alert.setContentText("1. A minimum of one adult is required per reservation.\n" +
                "2. Maximum occupancy per single room is 1 adult.\n" +
                "3. Maximum occupancy per double room is 2 adults.\n" +
                "4. Maximum occupancy per deluxe room is 3 adults and 1 child.\n" +
                "5. Maximum occupancy per penthouse is 4 adults and 2 children.\n" +
                "6. Children must be accompanied by an adult.");
        alert.showAndWait();
    }

    @FXML
    private void handleContinueToPayment(ActionEvent event) throws IOException {
        // This method will now call handleSelectRoom to perform the logic
        handleSelectRoom(event);
    }

    @FXML
    private void handleSelectRoom(ActionEvent event) throws IOException {
        // Clear any previous error messages
        selectionMessageLabel.setText("");

        // First, ensure the current room selections are saved
        updateReservationAndSummary();

        int totalRooms = singleRoomSpinner.getValue() + doubleRoomSpinner.getValue() +
                deluxeRoomSpinner.getValue() + penthousesSpinner.getValue();

        // 1. Validate that at least one room is selected
        if (totalRooms == 0) {
            showAlert(Alert.AlertType.ERROR, "Room Selection Required", "You must select at least one room to proceed.");
            selectionMessageLabel.setText("Please select at least one room.");
            return;
        }

        // 2. Validate that the selected rooms can accommodate all guests
        int totalAdults = reservationDetails.getNumberOfAdults();
        int totalChildren = reservationDetails.getNumberOfChildren(); // FIX: Changed getChildren() to getNumberOfChildren()

        int totalAdultCapacity = (singleRoomSpinner.getValue() * SINGLE_ADULT_CAPACITY) +
                (doubleRoomSpinner.getValue() * DOUBLE_ADULT_CAPACITY) +
                (deluxeRoomSpinner.getValue() * DELUXE_ADULT_CAPACITY) +
                (penthousesSpinner.getValue() * PENTHOUSE_ADULT_CAPACITY);

        int totalChildCapacity = (deluxeRoomSpinner.getValue() * DELUXE_CHILD_CAPACITY) +
                (penthousesSpinner.getValue() * PENTHOUSE_CHILD_CAPACITY);

        if (totalAdults > totalAdultCapacity || totalChildren > totalChildCapacity) {
            String errorMessage = "The selected rooms cannot accommodate all guests. Please adjust your room selection to fit " +
                    totalAdults + " adult(s) and " + totalChildren + " child(ren).";
            showAlert(Alert.AlertType.ERROR, "Capacity Mismatch", errorMessage);
            selectionMessageLabel.setText(errorMessage);
            return;
        }

        // --- NEW LOGIC: Set the roomType in ReservationDetails based on selection ---
        StringBuilder roomTypeSummaryBuilder = new StringBuilder();
        if (singleRoomSpinner.getValue() > 0) {
            roomTypeSummaryBuilder.append(singleRoomSpinner.getValue()).append(" Single");
        }
        if (doubleRoomSpinner.getValue() > 0) {
            if (roomTypeSummaryBuilder.length() > 0) roomTypeSummaryBuilder.append(", ");
            roomTypeSummaryBuilder.append(doubleRoomSpinner.getValue()).append(" Double");
        }
        if (deluxeRoomSpinner.getValue() > 0) {
            if (roomTypeSummaryBuilder.length() > 0) roomTypeSummaryBuilder.append(", ");
            roomTypeSummaryBuilder.append(deluxeRoomSpinner.getValue()).append(" Deluxe");
        }
        if (penthousesSpinner.getValue() > 0) {
            if (roomTypeSummaryBuilder.length() > 0) roomTypeSummaryBuilder.append(", ");
            roomTypeSummaryBuilder.append(penthousesSpinner.getValue()).append(" Penthouse");
        }

        // Set the generated room type summary to reservationDetails
        // If no rooms selected (which should be caught by validation), it will be empty.
        reservationDetails.setRoomType(roomTypeSummaryBuilder.toString());
        // --- END NEW LOGIC ---

        // Proceed to the next scene (Guest Details)
        MainApplication.loadRoomDetailsScene(reservationDetails);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
