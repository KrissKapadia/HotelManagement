package com.example.demo.controllers;

import com.example.demo.MainApplication;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import java.io.IOException;

public class BookingSuccessfulController {

    @FXML
    private Label reservationIdValueLabel;

    /**
     * Sets the reservation ID to be displayed on the successful booking page.
     * @param reservationId The ID of the confirmed reservation.
     */
    public void setReservationId(String reservationId) {
        if (reservationIdValueLabel != null) {
            reservationIdValueLabel.setText(reservationId);
        }
    }

    /**
     * Handles the action when the "Finish" button is clicked.
     * Navigates back to the welcome page.
     * @param event The action event.
     * @throws IOException If the FXML for the welcome page cannot be loaded.
     */
    @FXML
    private void handleFinish(ActionEvent event) throws IOException {
        MainApplication.loadNewScene("KioskWelcomePage-01.fxml");
    }
}
