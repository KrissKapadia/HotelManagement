package com.example.demo.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ReservationDisplay {
    private final StringProperty guestName;
    private final StringProperty phoneNumber;
    private final StringProperty reservationId;
    private final StringProperty checkInDate;
    private final StringProperty status; // Added status property

    public ReservationDisplay(Guest guest, ReservationDetails reservationDetails) {
        this.guestName = new SimpleStringProperty(guest.getFullName());
        this.phoneNumber = new SimpleStringProperty(guest.getPhoneNumber());
        this.reservationId = new SimpleStringProperty(reservationDetails.getReservationId());
        this.checkInDate = new SimpleStringProperty(
                reservationDetails.getCheckInDate() != null ?
                        reservationDetails.getCheckInDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")) : "N/A"
        );
        this.status = new SimpleStringProperty(reservationDetails.getStatus()); // Initialize status
    }

    // Getters for properties (required for TableView PropertyValueFactory)
    public String getGuestName() { return guestName.get(); }
    public StringProperty guestNameProperty() { return guestName; }

    public String getPhoneNumber() { return phoneNumber.get(); }
    public StringProperty phoneNumberProperty() { return phoneNumber; }

    public String getReservationId() { return reservationId.get(); }
    public StringProperty reservationIdProperty() { return reservationId; }

    public String getCheckInDate() { return checkInDate.get(); }
    public StringProperty checkInDateProperty() { return checkInDate; }

    public String getStatus() { return status.get(); }
    public StringProperty statusProperty() { return status; }
}
