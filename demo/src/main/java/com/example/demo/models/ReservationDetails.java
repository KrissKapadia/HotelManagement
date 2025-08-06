package com.example.demo.models;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.StringJoiner;

public class ReservationDetails {
    private String reservationId; // New field for database ID
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfAdults;
    private int numberOfChildren;
    private int singleRooms;
    private int doubleRooms;
    private int deluxeRooms;
    private int penthouses;
    private double estimatedPrice;
    private String status;    // New field for reservation status (e.g., "Confirmed", "Pending")
    private String roomNumber; // New field for assigned room number
    private String roomType;   // New field for assigned room type
    private double discount; // New field for the discount

    /**
     * Constructor for creating a new reservation with initial dates and guest counts.
     * Sets default room counts to 0 and status to "booked".
     *
     * @param checkInDate The date the guest checks in.
     * @param checkOutDate The date the guest checks out.
     * @param numberOfAdults The number of adult guests.
     * @param numberOfChildren The number of child guests.
     */
    public ReservationDetails(LocalDate checkInDate, LocalDate checkOutDate, int numberOfAdults, int numberOfChildren) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfAdults = numberOfAdults;
        this.numberOfChildren = numberOfChildren;
        this.singleRooms = 0;
        this.doubleRooms = 0;
        this.deluxeRooms = 0;
        this.penthouses = 0;
        this.estimatedPrice = 0.0;
        this.reservationId = null; // Initialize as null, will be set by DB
        this.status = "booked";    // Default status when a booking is initially made
        this.roomNumber = "N/A";   // Default room number
        this.roomType = "N/A";     // Default room type
        this.discount = 0.0;
    }

    /**
     * Default constructor for flexibility, initializes all counts to 0 and status to "Pending".
     */
    public ReservationDetails() {
        this.numberOfAdults = 0;
        this.numberOfChildren = 0;
        this.singleRooms = 0;
        this.doubleRooms = 0;
        this.deluxeRooms = 0;
        this.penthouses = 0;
        this.estimatedPrice = 0.0;
        this.reservationId = null; // Initialize as null, will be set by DB
        this.status = "Pending";   // Default status
        this.roomNumber = "N/A";   // Default room number
        this.roomType = "N/A";     // Default room type
        this.discount = 0.0;
    }

    // Getters
    public String getReservationId() {
        return reservationId;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    public int getAdults() {
        return numberOfAdults;
    }

    public int getChildren() {
        return numberOfChildren;
    }

    public int getNumberOfAdults() {
        return numberOfAdults;
    }

    public int getNumberOfChildren() {
        return numberOfChildren;
    }

    public int getSingleRooms() {
        return singleRooms;
    }

    public int getDoubleRooms() {
        return doubleRooms;
    }

    public int getDeluxeRooms() {
        return deluxeRooms;
    }

    public int getPenthouses() {
        return penthouses;
    }

    public double getEstimatedPrice() {
        // Return the stored estimated price, which is set by the controller.
        return estimatedPrice;
    }

    public String getStatus() {
        return status;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRoomType() {
        return roomType;
    }

    public double getDiscount() {
        return discount;
    }

    // Setters
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public void setNumberOfAdults(int numberOfAdults) {
        this.numberOfAdults = numberOfAdults;
    }

    public void setNumberOfChildren(int numberOfChildren) {
        this.numberOfChildren = numberOfChildren;
    }

    public void setSingleRooms(int singleRooms) {
        this.singleRooms = singleRooms;
    }

    public void setDoubleRooms(int doubleRooms) {
        this.doubleRooms = doubleRooms;
    }

    public void setDeluxeRooms(int deluxeRooms) {
        this.deluxeRooms = deluxeRooms;
    }

    public void setPenthouses(int penthouses) {
        this.penthouses = penthouses;
    }

    public void setEstimatedPrice(double estimatedPrice) {
        this.estimatedPrice = estimatedPrice;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // Helper methods
    public long getNumberOfNights() {
        if (checkInDate != null && checkOutDate != null) {
            return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        }
        return 0;
    }

    public int getTotalRooms() {
        return singleRooms + doubleRooms + deluxeRooms + penthouses;
    }

    /**
     * Dynamically determines the current status of the reservation based on dates.
     * This method does NOT change the internal 'status' field, but provides a calculated view.
     * The 'status' field should be explicitly set for 'booked' or 'cancelled' and then
     * updated to 'checked-in'/'checked-out' by external logic (e.g., admin action or daily job).
     *
     * @param currentDate The current date to compare against check-in/check-out dates.
     * @return The determined status: "checked-in", "checked-out", "booked", or "cancelled".
     */
    public String determineCurrentStatus(LocalDate currentDate) {
        // If the status was explicitly set to "cancelled", it overrides date-based logic.
        if ("cancelled".equalsIgnoreCase(this.status)) {
            return "cancelled";
        }

        if (checkInDate == null || checkOutDate == null) {
            return "unknown"; // Or "Pending" if dates are not set yet
        }

        // If current date is on or after check-out date
        if (currentDate.isEqual(checkOutDate) || currentDate.isAfter(checkOutDate)) {
            return "checked-out";
        }
        // If current date is on or after check-in date but before check-out date
        else if (currentDate.isEqual(checkInDate) || currentDate.isAfter(checkInDate)) {
            return "checked-in";
        }
        // If current date is before check-in date
        else {
            return "booked"; // Booking is confirmed for a future date
        }
    }

    public String getSummary() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
        StringBuilder summary = new StringBuilder();

        summary.append("Check-in Date: ").append(checkInDate != null ? checkInDate.format(formatter) : "N/A").append("\n");
        summary.append("Check-out Date: ").append(checkOutDate != null ? checkOutDate.format(formatter) : "N/A").append("\n");
        summary.append("Total Guests: ").append(numberOfAdults + numberOfChildren).append(" (").append(numberOfAdults).append(" Adults, ").append(numberOfChildren).append(" Children)\n");
        summary.append("Rooms: ").append(singleRooms).append(" Single, ").append(doubleRooms).append(" Double, ").append(deluxeRooms).append(" Deluxe, ").append(penthouses).append(" Penthouse\n");
        summary.append("Estimated Total Price: $").append(String.format("%.2f", estimatedPrice));
        summary.append("\nStatus: ").append(status); // Display the stored status
        summary.append("\nAssigned Room: ").append(roomNumber).append(" (").append(roomType).append(")");
        return summary.toString();
    }
}
