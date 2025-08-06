package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.ReservationDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.Alert;

import java.io.IOException;

public class KioskGuestSelectionController {

    @FXML
    private Spinner<Integer> adultsSpinner;

    @FXML
    private Spinner<Integer> childrenSpinner;

    @FXML
    private Label totalGuestsLabel;

    @FXML
    private Label validationMessageLabel;

    private ReservationDetails reservationDetails;

    @FXML
    public void initialize() {
        // Initialize spinners
        adultsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
        childrenSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));

        // Add listeners to update the total guests label
        adultsSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateGuests());
        childrenSpinner.valueProperty().addListener((obs, oldValue, newValue) -> updateGuests());

        // Initial update
        updateGuests();
    }

    public void setReservationDetails(ReservationDetails reservation) {
        this.reservationDetails = reservation;
        if (this.reservationDetails != null) {
            adultsSpinner.getValueFactory().setValue(reservation.getNumberOfAdults());
            childrenSpinner.getValueFactory().setValue(reservation.getNumberOfChildren());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        MainApplication.loadDateSelectionScene(reservationDetails);
    }

    @FXML
    private void handleSearchRooms(ActionEvent event) throws IOException {
        validationMessageLabel.setText("");
        int adults = adultsSpinner.getValue();
        int children = childrenSpinner.getValue();

        if (adults + children == 0) {
            validationMessageLabel.setText("You must have at least one guest to continue.");
            return;
        }

        if (reservationDetails == null) {
            reservationDetails = new ReservationDetails(null, null, adults, children);
        } else {
            reservationDetails.setNumberOfAdults(adults);
            reservationDetails.setNumberOfChildren(children);
        }

        // In a real application, this would load the next scene, which is KioskRoomSelection.
        MainApplication.loadRoomSelectionScene(reservationDetails);
    }

    @FXML
    private void handleClear(ActionEvent event) {
        adultsSpinner.getValueFactory().setValue(0);
        childrenSpinner.getValueFactory().setValue(0);
        validationMessageLabel.setText("");
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
                "5. Maximum occupancy per family suite is 4 adults and 2 children.\n" +
                "6. Children must be accompanied by an adult.");
        alert.showAndWait();
    }

    @FXML
    private void updateGuests() {
        if (adultsSpinner != null && childrenSpinner != null && totalGuestsLabel != null) {
            int totalGuests = adultsSpinner.getValue() + childrenSpinner.getValue();
            totalGuestsLabel.setText("Total Guests: " + totalGuests);
        } else {
            // This is a safety check and should not be reached with the correct FXML.
            System.err.println("One of the FXML elements is null. Check your FXML file.");
        }
    }
}
