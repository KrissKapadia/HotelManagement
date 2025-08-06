package com.example.demo.controllers;

import com.example.demo.MainApplication;
import com.example.demo.models.ReservationDetails;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class KioskDateSelectionController {

    @FXML
    private DatePicker checkInDatePicker;

    @FXML
    private DatePicker checkOutDatePicker;

    @FXML
    private Label nightsLabel;

    @FXML
    private Label validationMessageLabel;

    private ReservationDetails reservationDetails;

    @FXML
    public void initialize() {
        // You can add logic here to set default dates or disable past dates.
        // Also ensure the nights label is updated initially
        updateNightsLabel();
    }

    public void setReservationDetails(ReservationDetails reservation) {
        this.reservationDetails = reservation;
        if (this.reservationDetails != null) {
            checkInDatePicker.setValue(reservation.getCheckInDate());
            checkOutDatePicker.setValue(reservation.getCheckOutDate());
        }
    }

    @FXML
    private void handleContinueToGuests(ActionEvent event) throws IOException {
        validationMessageLabel.setText("");
        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();

        if (checkInDate == null || checkOutDate == null) {
            validationMessageLabel.setText("Please select both check-in and check-out dates.");
            return;
        }

        if (checkOutDate.isBefore(checkInDate)) {
            validationMessageLabel.setText("Check-out date cannot be before check-in date.");
            return;
        }

        if (reservationDetails == null) {
            reservationDetails = new ReservationDetails(checkInDate, checkOutDate, 0, 0);
        } else {
            reservationDetails.setCheckInDate(checkInDate);
            reservationDetails.setCheckOutDate(checkOutDate);
        }

        MainApplication.loadGuestSelectionScene(reservationDetails);
    }

    @FXML
    private void handleBack(ActionEvent event) throws IOException {
        MainApplication.loadNewScene("KioskWelcomePage-01.fxml");
    }

    @FXML
    private void handleViewRules(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules & Regulations");
        alert.setHeaderText(null);
        alert.setContentText("This is where the hotel rules and regulations would be displayed.");
        alert.showAndWait();
    }

    @FXML
    private void updateNightsLabel() {
        LocalDate checkInDate = checkInDatePicker.getValue();
        LocalDate checkOutDate = checkOutDatePicker.getValue();

        if (checkInDate != null && checkOutDate != null && checkOutDate.isAfter(checkInDate)) {
            long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            nightsLabel.setText("Number of Nights: " + nights);
        } else {
            nightsLabel.setText("Number of Nights: -");
        }
    }
}
