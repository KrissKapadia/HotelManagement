package com.example.demo.controllers;

import com.example.demo.util.Systemlogger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import com.example.demo.MainApplication;
import com.example.demo.models.DatabaseManager;
import com.example.demo.models.DatabaseManager.ReservationDisplay;
import com.example.demo.models.Guest;
import com.example.demo.models.ReservationDetails;
import com.example.demo.util.Systemlogger; // Import the custom logger
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger; // Import the Java logging class

public class GuestSearchManagementController {

    // Get the custom logger instance
    private static final Logger logger = Systemlogger.getLogger();

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<ReservationDisplay> guestReservationsTable;
    @FXML
    private TableColumn<ReservationDisplay, String> guestNameColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> phoneNumberColumn; // New TableColumn for phone number
    @FXML
    private TableColumn<ReservationDisplay, String> reservationIdColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> checkInDateColumn;
    @FXML
    private TableColumn<ReservationDisplay, String> statusColumn;

    @FXML
    private Button backButton;
    @FXML
    private Button viewDetailsButton;
    @FXML
    private Button cancelBookingButton;
    @FXML
    private Button modifyBookingButton;

    @FXML
    public void initialize() {
        // Set up cell value factories for TableColumns
        guestNameColumn.setCellValueFactory(new PropertyValueFactory<>("guestFullName"));
        phoneNumberColumn.setCellValueFactory(new PropertyValueFactory<>("phoneNumber")); // Set value factory for new column
        reservationIdColumn.setCellValueFactory(new PropertyValueFactory<>("reservationIdValue"));
        checkInDateColumn.setCellValueFactory(new PropertyValueFactory<>("checkInDateFormatted"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        DatabaseManager.initialize();

        guestReservationsTable.setPlaceholder(new Label("Enter a phone number to search for reservations."));

        viewDetailsButton.setDisable(true);
        cancelBookingButton.setDisable(true);
        modifyBookingButton.setDisable(true);

        guestReservationsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean isSelected = newSelection != null;
            viewDetailsButton.setDisable(!isSelected);
            cancelBookingButton.setDisable(!isSelected);
            modifyBookingButton.setDisable(!isSelected);
        });

        logger.info("GuestSearchManagementController initialized.");
    }

    /**
     * Handles the search action when the search button is clicked.
     * Searches for reservations by phone number.
     * @param event The action event.
     */
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchText = searchField.getText().trim();
        logger.info("Admin searched for guest reservations with phone number: " + searchText);
        List<ReservationDisplay> results = DatabaseManager.searchReservationsByPhoneNumber(searchText);

        if (results.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Search Results", "No reservations found for the given phone number.");
            logger.warning("No reservations found for phone number: " + searchText);
        }
        guestReservationsTable.setItems(FXCollections.observableArrayList(results));
    }

    /**
     * Handles the action for the "View Details" button.
     * Retrieves the selected reservation's full details and loads the read-only Guest Details scene.
     * @param event The action event.
     * @throws IOException If the FXML for the guest details page cannot be loaded.
     */
    @FXML
    private void handleViewDetails(ActionEvent event) throws IOException {
        ReservationDisplay selectedDisplay = guestReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedDisplay != null) {
            String reservationId = selectedDisplay.getReservationIdValue();
            logger.info("Admin viewing details for reservation ID: " + reservationId);
            Map<String, Object> reservationData = DatabaseManager.getReservationById(reservationId);

            if (reservationData != null) {
                Guest guest = (Guest) reservationData.get("guest");
                ReservationDetails details = (ReservationDetails) reservationData.get("reservationDetails");
                MainApplication.loadGuestDetailsScene(guest, details);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve full details for reservation ID: " + reservationId);
                logger.severe("Could not retrieve details for reservation ID: " + reservationId);
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to view its details.");
            logger.warning("Attempted to view details without selecting a reservation.");
        }
    }

    /**
     * Handles the action for the "Modify Booking" button.
     * Retrieves the selected reservation's full details and loads the editable Modify Booking scene.
     * @param event The action event.
     * @throws IOException If the FXML for the editable guest details page cannot be loaded.
     */
    @FXML
    private void handleModifyBooking(ActionEvent event) throws IOException {
        ReservationDisplay selectedDisplay = guestReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedDisplay != null) {
            String reservationId = selectedDisplay.getReservationIdValue();
            logger.info("Admin modifying booking for reservation ID: " + reservationId);
            Map<String, Object> reservationData = DatabaseManager.getReservationById(reservationId);

            if (reservationData != null) {
                Guest guest = (Guest) reservationData.get("guest");
                ReservationDetails details = (ReservationDetails) reservationData.get("reservationDetails");
                MainApplication.loadAdminGuestDetailsModifyScene(guest, details);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", "Could not retrieve full details for reservation ID: " + reservationId);
                logger.severe("Could not retrieve details for modification of reservation ID: " + reservationId);
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to modify.");
            logger.warning("Attempted to modify booking without selecting a reservation.");
        }
    }

    /**
     * Handles the action for the "Cancel Booking" button.
     * Deletes the selected reservation from the database upon user confirmation.
     * @param event The action event.
     */
    @FXML
    private void handleCancelBooking(ActionEvent event) {
        ReservationDisplay selectedDisplay = guestReservationsTable.getSelectionModel().getSelectedItem();
        if (selectedDisplay != null) {
            String reservationId = selectedDisplay.getReservationIdValue();
            logger.info("Admin initiated cancellation for reservation ID: " + reservationId);

            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirm Cancellation");
            confirmationAlert.setHeaderText("Delete Reservation?");
            confirmationAlert.setContentText("Are you sure you want to cancel and delete reservation ID " + reservationId + "? This action cannot be undone.");

            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                logger.info("Admin confirmed cancellation of reservation ID: " + reservationId);
                DatabaseManager.initialize();
                boolean deleted = DatabaseManager.deleteReservation(reservationId);
                if (deleted) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Reservation ID " + reservationId + " has been successfully cancelled and deleted.");
                    logger.info("Successfully deleted reservation ID: " + reservationId);
                    handleSearch(null);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete reservation ID " + reservationId + ".");
                    logger.severe("Failed to delete reservation ID: " + reservationId);
                }
            } else {
                logger.info("Admin cancelled the cancellation process for reservation ID: " + reservationId);
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a reservation to cancel.");
            logger.warning("Attempted to cancel booking without selecting a reservation.");
        }
    }

    /**
     * Handles the action for the "Back to Dashboard" button.
     * Navigates back to the Admin Dashboard.
     * @param event The action event.
     * @throws IOException If the FXML for the dashboard cannot be loaded.
     */
    @FXML
    private void handleBackToDashboard(ActionEvent event) throws IOException {
        logger.info("Admin navigated back to the dashboard from guest search management.");
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
