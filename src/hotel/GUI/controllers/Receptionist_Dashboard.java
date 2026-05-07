package hotel.GUI.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.util.Duration;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Controller class for the Grand Heritage Hotel & Spa Receptionist Dashboard.
 * Handles check-ins, check-outs, navigation, live clock, and stat cards.
 */
public class Receptionist_Dashboard implements Initializable {

    // ─── TOP BAR ────────────────────────────────────────────────────────────
    @FXML private TextField   searchField;
    @FXML private Button      btnNotifications;
    @FXML private Button      btnSettings;
    @FXML private Button      btnHelp;
    @FXML private Circle      userAvatar;

    // ─── GREETING & TIME ────────────────────────────────────────────────────
    @FXML private Label lblGreeting;
    @FXML private Label lblDateSubtitle;
    @FXML private Label lblCurrentTime;

    // ─── STAT CARDS ─────────────────────────────────────────────────────────
    @FXML private VBox  cardCheckIns;
    @FXML private Label lblCheckInCount;

    @FXML private VBox  cardCheckOuts;
    @FXML private Label lblCheckOutCount;

    @FXML private VBox  cardPendingRequests;
    @FXML private Label lblPendingCount;

    @FXML private VBox  cardOccupancy;
    @FXML private Label lblOccupancyPercent;

    // ─── CHECK-IN ROWS ──────────────────────────────────────────────────────
    @FXML private HBox   rowEleanor;
    @FXML private Button btnCheckInEleanor;

    @FXML private HBox   rowMarcus;
    @FXML private Button btnCheckInMarcus;

    @FXML private HBox   rowSarah;
    @FXML private Button btnCheckInSarah;

    // ─── CHECK-OUT ROWS ─────────────────────────────────────────────────────
    @FXML private HBox   rowDavidCheckout;
    @FXML private Button btnCheckOutDavid;

    @FXML private HBox   rowAmandaCheckout;
    @FXML private Label  lblAmandaStatus;
    @FXML private Button btnCheckOutAmanda;

    @FXML private HBox   rowRobertCheckout;
    @FXML private Button btnCheckOutRobert;

    // ─── NAVIGATION ─────────────────────────────────────────────────────────
    @FXML private HBox navConcierge;
    @FXML private HBox navReservations;
    @FXML private HBox navRoomMap;
    @FXML private HBox navGuestProfiles;
    @FXML private HBox navAnalytics;

    // ─── LINKS & OTHER ──────────────────────────────────────────────────────
    @FXML private Hyperlink lnkViewAll;
    @FXML private Button    btnNewBooking;

    // ─── Internal state ─────────────────────────────────────────────────────
    private int checkInCount      = 4;
    private int checkOutCount     = 3;
    private int pendingCount      = 7;
    private int occupancyPercent  = 68;

    private static final String ACTIVE_NAV_STYLE =
            "-fx-background-color: #2e5240; -fx-padding: 0 20 0 20; -fx-cursor: hand;";
    private static final String INACTIVE_NAV_STYLE =
            "-fx-padding: 0 20 0 20; -fx-cursor: hand;";

    private static final String CHECKED_IN_STYLE =
            "-fx-background-color: #2e8b57; -fx-text-fill: white; -fx-font-size: 12px; " +
                    "-fx-background-radius: 6; -fx-padding: 7 16 7 16;";
    private static final String CHECKED_OUT_STYLE =
            "-fx-background-color: #2e8b57; -fx-text-fill: white; -fx-font-size: 12px; " +
                    "-fx-background-radius: 6; -fx-padding: 7 14 7 14; -fx-border-radius: 6; -fx-border-width: 0;";

    // =========================================================================
    //  INITIALIZE
    // =========================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateGreetingAndDate();
        startClock();
        updateStatLabels();
    }

    // ─── Greeting & date ────────────────────────────────────────────────────
    private void updateGreetingAndDate() {
        int hour = LocalTime.now().getHour();
        String timeOfDay;
        if (hour < 12)        timeOfDay = "morning";
        else if (hour < 17)   timeOfDay = "afternoon";
        else                   timeOfDay = "evening";

        lblGreeting.setText("Good " + timeOfDay + ", Mark.");

        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("MMMM d");
        String dateStr = LocalDate.now().format(dateFmt);
        // Add ordinal suffix
        int day = LocalDate.now().getDayOfMonth();
        String suffix = getOrdinal(day);
        lblDateSubtitle.setText("Here is the overview for today, " + dateStr + suffix + ".");
    }

    private String getOrdinal(int day) {
        if (day >= 11 && day <= 13) return "th";
        return switch (day % 10) {
            case 1  -> "st";
            case 2  -> "nd";
            case 3  -> "rd";
            default -> "th";
        };
    }

    // ─── Live clock ─────────────────────────────────────────────────────────
    private void startClock() {
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("hh:mm a");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e ->
                lblCurrentTime.setText(LocalTime.now().format(timeFmt).toUpperCase())));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
        // Set initial value immediately
        lblCurrentTime.setText(LocalTime.now().format(timeFmt).toUpperCase());
    }

    // ─── Stat card labels ────────────────────────────────────────────────────
    private void updateStatLabels() {
        lblCheckInCount.setText(String.valueOf(checkInCount));
        lblCheckOutCount.setText(String.valueOf(checkOutCount));
        lblPendingCount.setText(String.valueOf(pendingCount));
        lblOccupancyPercent.setText(occupancyPercent + "%");
    }

    // =========================================================================
    //  CHECK-IN ACTIONS
    // =========================================================================

    @FXML
    private void onCheckInEleanor(ActionEvent event) {
        processCheckIn("Eleanor James", "Room 402", btnCheckInEleanor, rowEleanor);
    }

    @FXML
    private void onCheckInMarcus(ActionEvent event) {
        processCheckIn("Marcus Reed", "Room 215", btnCheckInMarcus, rowMarcus);
    }

    @FXML
    private void onCheckInSarah(ActionEvent event) {
        processCheckIn("Sarah Williams", "Room 510", btnCheckInSarah, rowSarah);
    }

    /**
     * Marks a guest as checked in: disables the button, updates its label,
     * decrements the check-in counter, and increments occupancy.
     */
    private void processCheckIn(String guestName, String room, Button btn, HBox row) {
        if (btn.isDisabled()) return; // already checked in

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirm check-in for " + guestName + " (" + room + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Check-In Confirmation");
        confirm.setHeaderText("Guest Check-In");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                btn.setText("Checked In ✓");
                btn.setStyle(CHECKED_IN_STYLE);
                btn.setDisable(true);

                // Decrement pending check-ins
                checkInCount = Math.max(0, checkInCount - 1);

                // Bump occupancy
                occupancyPercent = Math.min(100, occupancyPercent + 1);

                updateStatLabels();

                showInfoAlert("Check-In Successful",
                        guestName + " has been checked into " + room + ".");
            }
        });
    }

    // =========================================================================
    //  CHECK-OUT ACTIONS
    // =========================================================================

    @FXML
    private void onCheckOutDavid(ActionEvent event) {
        processCheckOut("David Chen", "Room 102", btnCheckOutDavid, rowDavidCheckout, null);
    }

    @FXML
    private void onCheckOutAmanda(ActionEvent event) {
        // Amanda is overdue – button is disabled in FXML; this handler is kept
        // for completeness. A supervisor override could re-enable it at runtime.
        showInfoAlert("Overdue Balance",
                "Room 305 (Amanda Smith) has an outstanding balance. " +
                        "Please resolve payment before checkout.");
    }

    @FXML
    private void onCheckOutRobert(ActionEvent event) {
        processCheckOut("Robert Fox", "Room 612", btnCheckOutRobert, rowRobertCheckout, null);
    }

    /**
     * Marks a room as checked out: disables the button, updates its label,
     * decrements the check-out counter, and adjusts occupancy.
     */
    private void processCheckOut(String guestName, String room, Button btn,
                                 HBox row, Label statusLabel) {
        if (btn.isDisabled()) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Confirm checkout for " + guestName + " (" + room + ")?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Check-Out Confirmation");
        confirm.setHeaderText("Guest Check-Out");
        confirm.showAndWait().ifPresent(result -> {
            if (result == ButtonType.YES) {
                btn.setText("Checked Out ✓");
                btn.setStyle(CHECKED_OUT_STYLE);
                btn.setDisable(true);

                // Decrement pending check-outs
                checkOutCount = Math.max(0, checkOutCount - 1);

                // Lower occupancy
                occupancyPercent = Math.max(0, occupancyPercent - 1);

                updateStatLabels();

                showInfoAlert("Check-Out Successful",
                        guestName + " has been checked out of " + room + ".");
            }
        });
    }

    // =========================================================================
    //  NAVIGATION ACTIONS
    // =========================================================================

    @FXML
    private void onNavConcierge() {
        setActiveNav(navConcierge);
        // Already on the concierge view – no scene switch needed
    }

    @FXML
    private void onNavReservations() {
        setActiveNav(navReservations);
        showInfoAlert("Navigation", "Opening Reservations view…");
        // TODO: load Reservations.fxml into the main content pane
    }

    @FXML
    private void onNavRoomMap() {
        setActiveNav(navRoomMap);
        showInfoAlert("Navigation", "Opening Room Map view…");
        // TODO: load RoomMap.fxml
    }

    @FXML
    private void onNavGuestProfiles() {
        setActiveNav(navGuestProfiles);
        showInfoAlert("Navigation", "Opening Guest Profiles view…");
        // TODO: load GuestProfiles.fxml
    }

    @FXML
    private void onNavAnalytics() {
        setActiveNav(navAnalytics);
        showInfoAlert("Navigation", "Opening Analytics view…");
        // TODO: load Analytics.fxml
    }

    /** Highlights the clicked nav item and resets all others. */
    private void setActiveNav(HBox active) {
        List<HBox> all = Arrays.asList(
                navConcierge, navReservations, navRoomMap,
                navGuestProfiles, navAnalytics);
        for (HBox nav : all) {
            nav.setStyle(nav == active ? ACTIVE_NAV_STYLE : INACTIVE_NAV_STYLE);
        }
    }

    // =========================================================================
    //  TOOLBAR ACTIONS
    // =========================================================================

    @FXML
    private void onNotifications(ActionEvent event) {
        showInfoAlert("Notifications",
                "You have " + pendingCount + " pending requests that need attention.");
    }

    @FXML
    private void onSettings(ActionEvent event) {
        showInfoAlert("Settings", "Settings panel coming soon.");
    }

    @FXML
    private void onHelp(ActionEvent event) {
        showInfoAlert("Help",
                "Grand Heritage Hotel & Spa – Receptionist Dashboard\n\n" +
                        "• Use the Check In buttons to check in arriving guests.\n" +
                        "• Use the Check Out buttons to check out departing guests.\n" +
                        "• The Pending Requests card shows items requiring attention.\n" +
                        "• Use + New Booking to create a reservation.");
    }

    // =========================================================================
    //  BOOKING & VIEW-ALL
    // =========================================================================

    @FXML
    private void onNewBooking(ActionEvent event) {
        showInfoAlert("New Booking", "Opening New Booking form…");
        // TODO: open NewBooking.fxml dialog or scene
    }

    @FXML
    private void onViewAllCheckIns(ActionEvent event) {
        showInfoAlert("All Check-Ins", "Loading full check-in list for today…");
        // TODO: navigate to full check-in list view
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}