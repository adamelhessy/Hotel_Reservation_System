package hotel.GUI.controllers;

import hotel.GUI.utils.SceneManager;
import hotel.GUI.utils.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

/**
 * Controller for side-bar.fxml
 *
 * Responsibilities:
 *  - Highlight the active nav item when a screen is selected
 *  - Navigate to the correct FXML screen via SceneManager
 *  - Show "New Booking" shortcut (guest flow only)
 *
 * The sidebar is included as a component inside every dashboard FXML
 * via <fx:include source="side-bar.fxml"/>  — the parent controller
 * injects the role after include so we can hide/show role-specific items.
 */
public class SideBarController {

    // ── Nav Buttons (must match fx:id in side-bar.fxml) ──────────────────────
    @FXML private Button btnConcierge;
    @FXML private Button btnReservations;
    @FXML private Button btnRoomMap;
    @FXML private Button btnGuestProfiles;
    @FXML private Button btnAnalytics;
    @FXML private Button btnNewBooking;

    // ── Current role injected by the parent dashboard controller ─────────────
    private String currentRole = "GUEST"; // default; overridden via setRole()

    // ── Style class constants ────────────────────────────────────────────────
    private static final String ACTIVE   = "sidebar-nav-item-active";
    private static final String INACTIVE = "sidebar-nav-item";

    @FXML
    public void initialize() {
        // Wire each button's action
        btnConcierge.setOnAction(e      -> navigateTo("concierge",       btnConcierge));
        btnReservations.setOnAction(e   -> navigateTo("reservations",    btnReservations));
        btnRoomMap.setOnAction(e        -> navigateTo("room-map",        btnRoomMap));
        btnGuestProfiles.setOnAction(e  -> navigateTo("guest-profiles",  btnGuestProfiles));
        btnAnalytics.setOnAction(e      -> navigateTo("analytics",       btnAnalytics));
        btnNewBooking.setOnAction(e     -> onNewBooking());

        // Concierge / Dashboard is active by default
        setActive(btnConcierge);
    }

    // ── Called by parent dashboard controller after fx:include ───────────────

    /**
     * Sets the role so the sidebar can show/hide role-specific items.
     * Call this from the parent dashboard's initialize() method:
     *   sideBarController.setRole("ADMIN");
     */
    public void setRole(String role) {
        this.currentRole = role.toUpperCase();
        applyRoleVisibility();
    }

    /**
     * Highlights a specific nav button from outside (e.g. when a tab
     * inside a dashboard page changes the active section).
     */
    public void setActiveSection(String section) {
        switch (section.toLowerCase()) {
            case "concierge"      -> setActive(btnConcierge);
            case "reservations"   -> setActive(btnReservations);
            case "room-map"       -> setActive(btnRoomMap);
            case "guest-profiles" -> setActive(btnGuestProfiles);
            case "analytics"      -> setActive(btnAnalytics);
        }
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private void navigateTo(String section, Button clicked) {
        setActive(clicked);

        // Map logical section names to FXML file names.
        // Add new screen mappings here as you build them.
        String fxml = switch (section) {
            case "concierge"      -> resolveDashboardFxml();
            case "reservations"   -> "ReservationsScreen.fxml";
            case "room-map"       -> "RoomMapScreen.fxml";
            case "guest-profiles" -> "GuestProfilesScreen.fxml";
            case "analytics"      -> "AnalyticsScreen.fxml";
            default               -> resolveDashboardFxml();
        };

        SceneManager.navigate(fxml);
    }

    /**
     * Returns the correct dashboard FXML based on the current user's role.
     * Reads the logged-in user from SessionManager so the sidebar always
     * sends the right role back to the correct dashboard.
     */
    private String resolveDashboardFxml() {
        // If SessionManager is available, use it; otherwise fall back to stored role.
        String role = currentRole;

        try {
            if (SessionManager.getLoggedInUser() != null) {
                role = SessionManager.getLoggedInUser()
                        .getTypeofuser()
                        .name(); // "ADMIN", "RECEPTIONIST", "GUEST"
            }
        } catch (Exception ignored) {
            // SessionManager not yet wired — use stored role
        }

        return switch (role) {
            case "ADMIN"        -> "AdminDashboard.fxml";
            case "RECEPTIONIST" -> "ReceptionistDashboard.fxml";
            default             -> "GuestDashboard.fxml";
        };
    }

    /** Show/hide nav items that are irrelevant for the current role. */
    private void applyRoleVisibility() {
        switch (currentRole) {
            case "GUEST" -> {
                // Guests cannot see Analytics or Guest Profiles list
                btnAnalytics.setVisible(false);
                btnAnalytics.setManaged(false);
                btnGuestProfiles.setVisible(false);
                btnGuestProfiles.setManaged(false);
            }
            case "RECEPTIONIST" -> {
                // Receptionists cannot see Analytics
                btnAnalytics.setVisible(false);
                btnAnalytics.setManaged(false);
            }
            case "ADMIN" -> {
                // Admins see everything — no changes needed
            }
        }
    }

    /** Clears active style from all buttons, then applies it to the clicked one. */
    private void setActive(Button target) {
        Button[] all = { btnConcierge, btnReservations, btnRoomMap,
                         btnGuestProfiles, btnAnalytics };

        for (Button btn : all) {
            btn.getStyleClass().remove(ACTIVE);
            if (!btn.getStyleClass().contains(INACTIVE)) {
                btn.getStyleClass().add(INACTIVE);
            }
        }

        if (!target.getStyleClass().contains(ACTIVE)) {
            target.getStyleClass().add(ACTIVE);
        }
    }

    private void onNewBooking() {
        // Navigate to the booking flow — available for all roles
        SceneManager.navigate("BookingScreen.fxml");
    }
}
