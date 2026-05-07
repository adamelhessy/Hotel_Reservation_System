package hotel.GUI.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for the Grand Heritage Hotel & Spa – Receptionist Dashboard.
 *
 * Responsibilities:
 *  - Live clock and contextual greeting
 *  - Stat-card counters (check-ins, check-outs, pending, occupancy, available rooms)
 *  - Per-guest check-in / check-out workflows with confirmation dialogs
 *  - Sidebar navigation (highlights active item, loads new scenes)
 *  - Top-bar search (filters visible guest rows in real time)
 *  - Activity log (appends timestamped entries for every action)
 *  - Notification, Settings, Help, Logout, and New Booking actions
 */
public class Receptionist_Dashboard implements Initializable {

    // ─── TOP BAR ─────────────────────────────────────────────────────────────
    @FXML private TextField  searchField;
    @FXML private Button     btnNotifications;
    @FXML private Button     btnSettings;
    @FXML private Button     btnHelp;
    @FXML private Circle     userAvatar;

    // ─── GREETING & TIME ─────────────────────────────────────────────────────
    @FXML private Label lblGreeting;
    @FXML private Label lblDateSubtitle;
    @FXML private Label lblCurrentTime;

    // ─── STAT CARDS ──────────────────────────────────────────────────────────
    @FXML private VBox  cardCheckIns;
    @FXML private Label lblCheckInCount;
    @FXML private Label lblCheckInDone;

    @FXML private VBox  cardCheckOuts;
    @FXML private Label lblCheckOutCount;
    @FXML private Label lblCheckOutDone;

    @FXML private VBox  cardPendingRequests;
    @FXML private Label lblPendingCount;

    @FXML private VBox  cardOccupancy;
    @FXML private Label lblOccupancyPercent;
    @FXML private Label lblOccupancyRooms;

    @FXML private VBox  cardAvailableRooms;
    @FXML private Label lblAvailableRooms;

    // ─── SIDEBAR ─────────────────────────────────────────────────────────────
    @FXML private Label  lblSidebarNotifBadge;
    @FXML private Button btnLogout;
    @FXML private Button btnNewBooking;

    // ─── SIDEBAR NAV ITEMS ───────────────────────────────────────────────────
    @FXML private HBox navConcierge;
    @FXML private HBox navReservations;
    @FXML private HBox navRoomMap;
    @FXML private HBox navGuestProfiles;
    @FXML private HBox navAnalytics;
    @FXML private HBox navBilling;
    @FXML private HBox navHousekeeping;

    // ─── CHECK-IN ROWS ───────────────────────────────────────────────────────
    @FXML private HBox   rowEleanor;
    @FXML private Label  lblEleanorStatus;
    @FXML private Button btnCheckInEleanor;

    @FXML private HBox   rowMarcus;
    @FXML private Label  lblMarcusStatus;
    @FXML private Button btnCheckInMarcus;

    @FXML private HBox   rowSarah;
    @FXML private Label  lblSarahStatus;
    @FXML private Button btnCheckInSarah;

    @FXML private HBox   rowLiam;
    @FXML private Label  lblLiamStatus;
    @FXML private Button btnCheckInLiam;

    // ─── CHECK-OUT ROWS ──────────────────────────────────────────────────────
    @FXML private HBox   rowDavidCheckout;
    @FXML private Button btnCheckOutDavid;

    @FXML private HBox   rowAmandaCheckout;
    @FXML private Label  lblAmandaStatus;
    @FXML private Button btnCheckOutAmanda;

    @FXML private HBox   rowRobertCheckout;
    @FXML private Button btnCheckOutRobert;

    // ─── LINKS / VIEW-ALL ────────────────────────────────────────────────────
    @FXML private Hyperlink lnkViewAllCheckIns;
    @FXML private Hyperlink lnkViewAllCheckOuts;

    // ─── SEARCH RESULTS PANEL ────────────────────────────────────────────────
    @FXML private VBox  searchResultsPanel;
    @FXML private Label lblSearchResults;

    // ─── ACTIVITY LOG ────────────────────────────────────────────────────────
    @FXML private VBox activityLog;

    // ─── Internal counters ───────────────────────────────────────────────────
    private int totalCheckIns     = 4;
    private int totalCheckOuts    = 3;
    private int doneCheckIns      = 0;
    private int doneCheckOuts     = 0;
    private int pendingCount      = 7;
    private int totalRooms        = 80;
    private int occupiedRooms     = 54;

    // ─── Style constants ─────────────────────────────────────────────────────
    private static final String ACTIVE_NAV =
            "-fx-background-color: #2e5240; -fx-padding: 0 20 0 20; -fx-cursor: hand;";
    private static final String INACTIVE_NAV =
            "-fx-padding: 0 20 0 20; -fx-cursor: hand;";

    private static final String BTN_DONE_STYLE =
            "-fx-background-color: #2e8b57; -fx-text-fill: white; -fx-font-size: 12px; " +
                    "-fx-background-radius: 6; -fx-padding: 7 16 7 16;";

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("hh:mm a");

    // =========================================================================
    //  INITIALIZE
    // =========================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updateGreetingAndDate();
        startLiveClock();
        refreshStatCards();
        setupSearchListener();
        logActivity("Dashboard loaded.");
    }

    // ─── Greeting ────────────────────────────────────────────────────────────
    private void updateGreetingAndDate() {
        int hour = LocalTime.now().getHour();
        String part = (hour < 12) ? "morning" : (hour < 17) ? "afternoon" : "evening";
        lblGreeting.setText("Good " + part + ", Mark.");

        LocalDate today = LocalDate.now();
        String formatted = today.format(DateTimeFormatter.ofPattern("MMMM d"));
        lblDateSubtitle.setText("Here is the overview for today, " +
                formatted + ordinal(today.getDayOfMonth()) + ".");
    }

    private static String ordinal(int d) {
        if (d >= 11 && d <= 13) return "th";
        return switch (d % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

    // ─── Live clock ──────────────────────────────────────────────────────────
    private void startLiveClock() {
        lblCurrentTime.setText(LocalTime.now().format(TIME_FMT).toUpperCase());
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), e ->
                lblCurrentTime.setText(LocalTime.now().format(TIME_FMT).toUpperCase())));
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }

    // ─── Stat card refresh ───────────────────────────────────────────────────
    private void refreshStatCards() {
        // Check-ins
        int remaining = Math.max(0, totalCheckIns - doneCheckIns);
        lblCheckInCount.setText(String.valueOf(remaining));
        lblCheckInDone.setText(doneCheckIns + " completed");

        // Check-outs
        int remainingOut = Math.max(0, totalCheckOuts - doneCheckOuts);
        lblCheckOutCount.setText(String.valueOf(remainingOut));
        lblCheckOutDone.setText(doneCheckOuts + " completed");

        // Pending
        lblPendingCount.setText(String.valueOf(pendingCount));
        lblSidebarNotifBadge.setText(pendingCount + " Pending");

        // Occupancy
        int pct = totalRooms > 0 ? (occupiedRooms * 100) / totalRooms : 0;
        lblOccupancyPercent.setText(pct + "%");
        lblOccupancyRooms.setText(occupiedRooms + " / " + totalRooms + " rooms");

        // Available
        int available = totalRooms - occupiedRooms;
        lblAvailableRooms.setText(String.valueOf(available));
    }

    // ─── Real-time search listener ───────────────────────────────────────────
    private void setupSearchListener() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> onSearch(null));
    }

    @FXML
    private void onSearch(javafx.scene.input.KeyEvent event) {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) {
            searchResultsPanel.setVisible(false);
            searchResultsPanel.setManaged(false);
            // Restore all rows
            setRowVisible(rowEleanor,       true);
            setRowVisible(rowMarcus,        true);
            setRowVisible(rowSarah,         true);
            setRowVisible(rowLiam,          true);
            setRowVisible(rowDavidCheckout, true);
            setRowVisible(rowAmandaCheckout,true);
            setRowVisible(rowRobertCheckout,true);
            return;
        }

        // Filter rows
        filterRow(rowEleanor,        query, "eleanor james",  "402");
        filterRow(rowMarcus,         query, "marcus reed",    "215");
        filterRow(rowSarah,          query, "sarah williams", "510");
        filterRow(rowLiam,           query, "liam turner",    "318");
        filterRow(rowDavidCheckout,  query, "david chen",     "102");
        filterRow(rowAmandaCheckout, query, "amanda smith",   "305");
        filterRow(rowRobertCheckout, query, "robert fox",     "612");

        // Summary
        long visible = countVisibleRows();
        lblSearchResults.setText(visible == 0
                ? "No results found for \"" + query + "\"."
                : visible + " result(s) matching \"" + query + "\".");
        searchResultsPanel.setVisible(true);
        searchResultsPanel.setManaged(true);
    }

    private void filterRow(HBox row, String query, String guestName, String room) {
        boolean matches = guestName.contains(query) || room.contains(query);
        setRowVisible(row, matches);
    }

    private void setRowVisible(HBox row, boolean visible) {
        row.setVisible(visible);
        row.setManaged(visible);
    }

    private long countVisibleRows() {
        return Arrays.asList(rowEleanor, rowMarcus, rowSarah, rowLiam,
                        rowDavidCheckout, rowAmandaCheckout, rowRobertCheckout)
                .stream().filter(Node::isVisible).count();
    }

    // =========================================================================
    //  CHECK-IN ACTIONS
    // =========================================================================

    @FXML private void onCheckInEleanor(ActionEvent e) {
        processCheckIn("Eleanor James", "Room 402", "#RES-8921",
                btnCheckInEleanor, lblEleanorStatus);
    }

    @FXML private void onCheckInMarcus(ActionEvent e) {
        processCheckIn("Marcus Reed", "Room 215", "#RES-8924",
                btnCheckInMarcus, lblMarcusStatus);
    }

    @FXML private void onCheckInSarah(ActionEvent e) {
        processCheckIn("Sarah Williams", "Room 510", "#RES-8930",
                btnCheckInSarah, lblSarahStatus);
    }

    @FXML private void onCheckInLiam(ActionEvent e) {
        processCheckIn("Liam Turner", "Room 318", "#RES-8935",
                btnCheckInLiam, lblLiamStatus);
    }

    /**
     * Shared check-in workflow:
     * 1. Guard against double-processing.
     * 2. Show a confirmation dialog.
     * 3. On YES: update the button, status label, counters, log, and cards.
     */
    private void processCheckIn(String guest, String room, String resId,
                                Button btn, Label statusLabel) {
        if (btn.isDisabled()) return;

        boolean confirmed = confirm(
                "Guest Check-In",
                "Confirm check-in for " + guest + " (" + room + ")?\nReservation: " + resId);
        if (!confirmed) return;

        btn.setText("Checked In ✓");
        btn.setStyle(BTN_DONE_STYLE);
        btn.setDisable(true);

        statusLabel.setText("CHECKED IN");
        statusLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #2e8b57; " +
                "-fx-font-weight: bold; -fx-letter-spacing: 0.5;");

        doneCheckIns++;
        occupiedRooms = Math.min(totalRooms, occupiedRooms + 1);
        refreshStatCards();
        logActivity("✅ Check-in: " + guest + " → " + room + " (" + resId + ")");
        info("Check-In Successful",
                guest + " has been checked into " + room + ".\nKey card is now active.");
    }

    // =========================================================================
    //  CHECK-OUT ACTIONS
    // =========================================================================

    @FXML private void onCheckOutDavid(ActionEvent e) {
        processCheckOut("David Chen", "Room 102", btnCheckOutDavid, false);
    }

    @FXML private void onCheckOutAmanda(ActionEvent e) {
        // Amanda has an overdue balance – redirect to a resolution dialog
        resolveOverdueCheckout("Amanda Smith", "Room 305", btnCheckOutAmanda);
    }

    @FXML private void onCheckOutRobert(ActionEvent e) {
        processCheckOut("Robert Fox", "Room 612", btnCheckOutRobert, false);
    }

    /**
     * Shared normal check-out workflow.
     */
    private void processCheckOut(String guest, String room, Button btn, boolean overdue) {
        if (btn.isDisabled()) return;

        boolean confirmed = confirm(
                "Guest Check-Out",
                "Confirm check-out for " + guest + " (" + room + ")?");
        if (!confirmed) return;

        btn.setText("Checked Out ✓");
        btn.setStyle("-fx-background-color: #2e8b57; -fx-text-fill: white; " +
                "-fx-font-size: 12px; -fx-background-radius: 6; -fx-padding: 7 14 7 14;");
        btn.setDisable(true);

        doneCheckOuts++;
        occupiedRooms = Math.max(0, occupiedRooms - 1);
        refreshStatCards();
        logActivity("🚪 Check-out: " + guest + " ← " + room);
        info("Check-Out Successful",
                guest + " has been checked out of " + room + ".\nRoom status updated to available.");
    }

    /**
     * Overdue / outstanding-balance check-out flow.
     */
    private void resolveOverdueCheckout(String guest, String room, Button btn) {
        // Step 1: prompt to resolve balance
        Alert balanceAlert = new Alert(Alert.AlertType.CONFIRMATION);
        balanceAlert.setTitle("Outstanding Balance");
        balanceAlert.setHeaderText("⚠ Overdue – " + guest + " (" + room + ")");
        balanceAlert.setContentText(
                "This guest has an outstanding balance.\n\n" +
                        "Select an action:\n" +
                        "  • YES  – Mark balance as settled and proceed with checkout.\n" +
                        "  • NO   – Cancel and follow up with guest.");
        balanceAlert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = balanceAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            lblAmandaStatus.setText("SETTLED");
            lblAmandaStatus.setStyle(
                    "-fx-background-color: #2e8b57; -fx-text-fill: white; " +
                            "-fx-font-size: 10px; -fx-background-radius: 10; " +
                            "-fx-padding: 3 8 3 8; -fx-font-weight: bold;");

            // Now do the actual checkout
            processCheckOut(guest, room, btn, true);
            logActivity("💰 Balance resolved for " + guest + " (" + room + ")");
        }
    }

    // =========================================================================
    //  PENDING REQUESTS
    // =========================================================================

    @FXML
    private void onResolveAllPending(ActionEvent e) {
        if (pendingCount == 0) {
            info("Pending Requests", "No pending requests at this time.");
            return;
        }
        boolean confirmed = confirm("Resolve All Pending",
                "Mark all " + pendingCount + " pending requests as resolved?");
        if (confirmed) {
            logActivity("✔ Resolved " + pendingCount + " pending requests.");
            pendingCount = 0;
            refreshStatCards();
            info("Done", "All pending requests have been resolved.");
        }
    }

    // =========================================================================
    //  VIEW-ALL HYPERLINKS
    // =========================================================================

    @FXML
    private void onViewAllCheckIns(ActionEvent e) {
        info("All Check-Ins Today",
                "Expected arrivals: " + totalCheckIns + "\n" +
                        "Completed: " + doneCheckIns + "\n" +
                        "Remaining: " + Math.max(0, totalCheckIns - doneCheckIns) + "\n\n" +
                        "Full list available in the Reservations view.");
    }

    @FXML
    private void onViewAllCheckOuts(ActionEvent e) {
        info("All Check-Outs Today",
                "Expected departures: " + totalCheckOuts + "\n" +
                        "Completed: " + doneCheckOuts + "\n" +
                        "Remaining: " + Math.max(0, totalCheckOuts - doneCheckOuts) + "\n\n" +
                        "Full list available in the Reservations view.");
    }

    // =========================================================================
    //  ACTIVITY LOG
    // =========================================================================

    private void logActivity(String message) {
        // Remove placeholder label if it exists
        activityLog.getChildren().removeIf(n ->
                n instanceof Label lbl && lbl.getText().contains("No activity yet"));

        String timestamp = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));

        HBox entry = new HBox(10);
        entry.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        Label timeLabel = new Label(timestamp);
        timeLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #aaa; -fx-min-width: 64;");

        Label msgLabel = new Label(message);
        msgLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #444;");
        msgLabel.setWrapText(true);
        HBox.setHgrow(msgLabel, Priority.ALWAYS);

        entry.getChildren().addAll(timeLabel, msgLabel);

        // Insert newest at top
        activityLog.getChildren().add(0, entry);

        // Cap log at 20 entries
        while (activityLog.getChildren().size() > 20) {
            activityLog.getChildren().remove(activityLog.getChildren().size() - 1);
        }
    }

    @FXML
    private void onClearLog(ActionEvent e) {
        activityLog.getChildren().clear();
        Label placeholder = new Label("No activity yet today.");
        placeholder.setStyle("-fx-font-size: 12px; -fx-text-fill: #aaa; -fx-font-style: italic;");
        activityLog.getChildren().add(placeholder);
        logActivity("Log cleared.");
    }

    // =========================================================================
    //  NAVIGATION
    // =========================================================================

    @FXML private void onNavConcierge()     { setActiveNav(navConcierge);     /* already here */ }
    @FXML private void onNavReservations()  { setActiveNav(navReservations);  navigateTo("Reservations"); }
    @FXML private void onNavRoomMap()       { setActiveNav(navRoomMap);       navigateTo("RoomMap"); }
    @FXML private void onNavGuestProfiles() { setActiveNav(navGuestProfiles); navigateTo("GuestProfiles"); }
    @FXML private void onNavAnalytics()     { setActiveNav(navAnalytics);     navigateTo("Analytics"); }
    @FXML private void onNavBilling()       { setActiveNav(navBilling);       navigateTo("Billing"); }
    @FXML private void onNavHousekeeping()  { setActiveNav(navHousekeeping);  navigateTo("Housekeeping"); }

    /** Highlights the active nav item and clears the rest. */
    private void setActiveNav(HBox active) {
        List<HBox> all = Arrays.asList(
                navConcierge, navReservations, navRoomMap,
                navGuestProfiles, navAnalytics, navBilling, navHousekeeping);
        for (HBox nav : all) {
            nav.setStyle(nav == active ? ACTIVE_NAV : INACTIVE_NAV);
            // Keep correct label colour
            nav.getChildren().stream()
                    .filter(n -> n instanceof Label)
                    .forEach(n -> {
                        Label lbl = (Label) n;
                        boolean isActive = nav == active;
                        if (!lbl.getText().matches("[⊞📅🛏👥📊💳🧹]")) { // text labels only
                            lbl.setStyle(isActive
                                    ? "-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: bold;"
                                    : "-fx-font-size: 14px; -fx-text-fill: #8fbbaa;");
                        } else {
                            lbl.setStyle(isActive
                                    ? "-fx-font-size: 15px; -fx-text-fill: white;"
                                    : "-fx-font-size: 15px; -fx-text-fill: #8fbbaa;");
                        }
                    });
        }
    }

    /**
     * Attempts to load the corresponding FXML for the given view name.
     * Falls back gracefully with an info dialog if the file is not yet present.
     */
    private void navigateTo(String viewName) {
        logActivity("🔀 Navigated to: " + viewName);
        String fxmlPath = "/hotel/GUI/views/" + viewName + ".fxml";
        URL resource = getClass().getResource(fxmlPath);
        if (resource != null) {
            try {
                Parent root = FXMLLoader.load(resource);
                Stage stage = (Stage) navConcierge.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex) {
                showError("Navigation Error",
                        "Failed to load " + viewName + " view.\n" + ex.getMessage());
            }
        } else {
            info("Coming Soon",
                    "The " + viewName + " view is not yet available.\n" +
                            "It will be linked once the FXML is added to:\n" + fxmlPath);
        }
    }

    // =========================================================================
    //  TOOLBAR ACTIONS
    // =========================================================================

    @FXML
    private void onNotifications(ActionEvent e) {
        info("Notifications",
                pendingCount > 0
                        ? "You have " + pendingCount + " pending requests that need attention.\n\n" +
                        "• 3 × housekeeping requests\n" +
                        "• 2 × room service orders\n" +
                        "• 2 × maintenance tickets"
                        : "No new notifications.");
    }

    @FXML
    private void onSettings(ActionEvent e) {
        info("Settings",
                "Receptionist Dashboard Settings\n\n" +
                        "• Clock format: 12-hour\n" +
                        "• Notification sound: Enabled\n" +
                        "• Auto-refresh interval: 60 s\n\n" +
                        "Full settings panel coming soon.");
    }

    @FXML
    private void onHelp(ActionEvent e) {
        info("Help – Receptionist Dashboard",
                "Grand Heritage Hotel & Spa\n\n" +
                        "• Check-in: Click 'Check In' for arriving guests.\n" +
                        "• Check-out: Click 'Check Out' for departing guests.\n" +
                        "• Overdue: Click 'Resolve' to handle outstanding balances.\n" +
                        "• Search: Type a guest name or room number to filter.\n" +
                        "• New Booking: Opens the reservation form.\n" +
                        "• Pending Requests: Resolve via the card button.\n" +
                        "• Activity Log: All actions are recorded below the tables.");
    }

    // =========================================================================
    //  NEW BOOKING
    // =========================================================================

    @FXML
    private void onNewBooking(ActionEvent e) {
        logActivity("📋 New booking form opened.");
        String fxmlPath = "/hotel/GUI/views/NewBooking.fxml";
        URL resource = getClass().getResource(fxmlPath);
        if (resource != null) {
            try {
                Parent root = FXMLLoader.load(resource);
                Stage dialog = new Stage();
                dialog.initModality(Modality.APPLICATION_MODAL);
                dialog.setTitle("New Booking – Grand Heritage");
                dialog.setScene(new Scene(root));
                dialog.showAndWait();
            } catch (IOException ex) {
                showError("Error", "Could not open New Booking form.\n" + ex.getMessage());
            }
        } else {
            info("New Booking",
                    "The New Booking form (NewBooking.fxml) is not yet available.\n" +
                            "It will open as a modal dialog once implemented.");
        }
    }

    // =========================================================================
    //  LOGOUT
    // =========================================================================

    @FXML
    private void onLogout(ActionEvent e) {
        boolean confirmed = confirm("Logout", "Are you sure you want to log out?");
        if (!confirmed) return;

        logActivity("🔓 User logged out.");
        String loginFxml = "/hotel/GUI/views/Login.fxml";
        URL resource = getClass().getResource(loginFxml);
        if (resource != null) {
            try {
                Parent root = FXMLLoader.load(resource);
                Stage stage = (Stage) btnLogout.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException ex) {
                showError("Logout Error", "Could not load Login screen.\n" + ex.getMessage());
            }
        } else {
            // Fallback: close the window
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.close();
        }
    }

    // =========================================================================
    //  DIALOG HELPERS
    // =========================================================================

    /** Shows a confirmation dialog. Returns true if the user clicked YES. */
    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message,
                ButtonType.YES, ButtonType.NO);
        alert.setTitle(title);
        alert.setHeaderText(null);
        return alert.showAndWait()
                .filter(r -> r == ButtonType.YES)
                .isPresent();
    }

    /** Shows an information dialog. */
    private void info(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    /** Shows an error dialog. */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}