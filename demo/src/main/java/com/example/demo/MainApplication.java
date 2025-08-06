package com.example.demo;

import com.example.demo.controllers.*;
import com.example.demo.models.Guest;
import com.example.demo.models.ReservationDetails;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static Stage mainStage;
    // Corrected path prefix assuming FXML files are in src/main/resources/view/
    private static final String FXML_PATH_PREFIX = "/view/";


    @Override
    public void start(Stage stage) throws IOException {
        mainStage = stage;
        mainStage.setTitle("Hotel Kiosk Application");
        loadNewScene("KioskWelcomePage-01.fxml");
    }

    public static void loadNewScene(String fxmlFile) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadDateSelectionScene(ReservationDetails reservationDetails) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "KioskDateSelection-02.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        KioskDateSelectionController controller = fxmlLoader.getController();
        controller.setReservationDetails(reservationDetails);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadGuestSelectionScene(ReservationDetails reservationDetails) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "KioskGuestSelection-03.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        KioskGuestSelectionController controller = fxmlLoader.getController();
        controller.setReservationDetails(reservationDetails);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadRoomSelectionScene(ReservationDetails reservationDetails) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "KioskRoomSelectionPage-04.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        KioskRoomSelectionController controller = fxmlLoader.getController();
        controller.setReservationDetails(reservationDetails);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadRoomDetailsScene(ReservationDetails reservationDetails) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "KioskGuestDetailsPage-05.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        KioskGuestDetailsController controller = fxmlLoader.getController();
        controller.setReservationDetails(reservationDetails);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadRoomConfirmationScene(Guest guest, ReservationDetails reservationDetails) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "KioskRoomConfirmationPage-06.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        KioskRoomConfirmationController controller = fxmlLoader.getController();
        controller.setGuestAndReservationDetails(guest, reservationDetails);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadBookingSuccessfulScene(String reservationId) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "KioskBookingConfirmed-07.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        BookingSuccessfulController controller = fxmlLoader.getController();
        controller.setReservationId(reservationId); // Pass the reservation ID to the controller
        mainStage.setScene(scene);
        mainStage.show();
    }
    public static void loadAdminLoginScene() throws IOException {
        // Updated FXML filename to "AdminLoginPage-08.fxml"
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "AdminLoginPage-08.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadAdminDashboardScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "AdminDashboardPage-09.fxml")); // Assuming AdminDashboard.fxml is the name
        Scene scene = new Scene(fxmlLoader.load());
        // No controller specific setup needed here as AdminDashboardController handles its own initialization
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadAdminCheckOutScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "AdminProcessCheckOutPage-10.fxml")); // Assuming FXML is named AdminCheckOut.fxml
        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadGuestSearchManagementScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "AdminGuestSearch-11.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadAdminReportsScene() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "BillingReport-12.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadGuestDetailsScene(Guest guest, ReservationDetails reservationDetails) throws IOException {
        // This will load the editable GuestDetails.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "AdminGuestDetails-13.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        GuestDetailsController controller = fxmlLoader.getController();
        controller.setGuestAndReservationDetails(guest, reservationDetails);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void loadAdminGuestDetailsModifyScene(Guest guest, ReservationDetails reservationDetails) throws IOException {
        // This will load the AdminModifyBooking.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(FXML_PATH_PREFIX + "AdminModifyBooking-16.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        AdminModifyBookingController controller = fxmlLoader.getController();
        controller.setGuestAndReservationDetails(guest, reservationDetails);
        mainStage.setScene(scene);
        mainStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
