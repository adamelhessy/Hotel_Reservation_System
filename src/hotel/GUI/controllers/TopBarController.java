package hotel.GUI.controllers;

import java.util.ArrayList;
import java.util.List;

import hotel.GUI.utils.SessionManager;
import hotel.core.Database;
import hotel.model.bookings.Reservation;
import hotel.model.entities.Room;
import hotel.model.users.Guest;
import hotel.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;

/**
 * Controller for top-bar.fxml
 *
 * Responsibilities:
 *  - Display the logged-in user's username in the profile area
 *  - Live search across guests, rooms, and reservations as the user types
 *  - Bell icon: show notification count (unpaid invoices / pending check-ins)
 *  - Settings icon: navigate to settings screen
 *  - Help icon: navigate to help screen
 *  - Clicking the profile avatar: navigate to the profile screen
 *
 * Usage in a dashboard FXML:
 *   <fx:include fx:id="topBar" source="top-bar.fxml"/>
 * Then in the dashboard controller's initialize():
 *   topBarController.refresh();
 */
public class TopBarController {

    // ── FXML Bindings (must match fx:id in top-bar.fxml) ─────────────────────
    @FXML private Label     lblUsername;
    @FXML private TextField txtSearch;
    @FXML private ImageView imgBell;
    @FXML private ImageView imgSettings;
    @FXML private ImageView imgHelp;
    @FXML private ImageView imgAvatar;

    // Optional: a dropdown list shown below the search bar for live results
    // Add <ListView fx:id="lstSearchResults"> to top-bar.fxml if desired.
    // @FXML private ListView<String> lstSearchResults;

    // ── Page title labels (left side of top bar) ──────────────────────────────
    @FXML private Label lblPageTitle;
    @FXML private Label lblPageSubtitle;

    // ── Initializer ──────────────────────────────────────────────────────────

    @FXML
    public void initialize() {
        // 1. Populate username from SessionManager
        refresh();

        // 2. Wire icon click handlers
        if (imgBell     != null) imgBell.setOnMouseClicked(e     -> onBellClicked());
        if (imgSettings != null) imgSettings.setOnMouseClicked(e -> onSettingsClicked());
        if (imgHelp     != null) imgHelp.setOnMouseClicked(e     -> onHelpClicked());
        if (imgAvatar   != null) imgAvatar.setOnMouseClicked(e   -> onAvatarClicked());

        // 3. Live search listener
        if (txtSearch != null) {
            txtSearch.textProperty().addListener((obs, oldVal, newVal) -> onSearchChanged(newVal));
        }
    }

    // ── Public API called by parent dashboard controllers ────────────────────

    /**
     * Re-reads the current session and refreshes all dynamic labels.
     * Call this from any dashboard controller's initialize() to ensure
     * the top bar always reflects the correct logged-in user.
     */
    public void refresh() {
        User user = null;
        try {
            user = SessionManager.getLoggedInUser();
        } catch (Exception ignored) {
            // SessionManager not yet set up — silently skip
        }

        if (user != null && lblUsername != null) {
            lblUsername.setText(user.getUserName());
        } else if (lblUsername != null) {
            lblUsername.setText("Guest");
        }
    }

    /**
     * Sets the page title text shown on the left of the top bar.
     * E.g.  topBarController.setPageTitle("Reservations", "Manage Bookings");
     */
    public void setPageTitle(String title, String subtitle) {
        if (lblPageTitle    != null) lblPageTitle.setText(title);
        if (lblPageSubtitle != null) lblPageSubtitle.setText(subtitle);
    }

    // ── Search ───────────────────────────────────────────────────────────────

    /**
     * Called on every keystroke in the search box.
     * Searches guests (by username), rooms (by number), and
     * reservations (by ID) — all against the live Database lists.
     */
    private void onSearchChanged(String query) {
        if (query == null || query.isBlank()) {
            clearSearchResults();
            return;
        }

        String q = query.trim().toLowerCase();
        List<String> results = new ArrayList<>();

        // Search guests by username
        for (Guest g : Database.getGuests()) {
            if (g.getUserName() != null && g.getUserName().toLowerCase().contains(q)) {
                results.add("👤  " + g.getUserName() + "  (Guest)");
                if (results.size() >= 8) break; // cap results to keep the UI clean
            }
        }

        // Search rooms by number or type name
        for (Room r : Database.getRooms()) {
            String roomNum  = String.valueOf(r.getRoomNumber());
            String typeName = r.getRoomType().getTypeName().toLowerCase();
            if (roomNum.contains(q) || typeName.contains(q)) {
                results.add("🛏  Room " + r.getRoomNumber() + "  —  " + r.getRoomType().getTypeName());
                if (results.size() >= 8) break;
            }
        }

        // Search reservations by ID
        for (Reservation res : Database.getReservations()) {
            if (String.valueOf(res.getReservationID()).contains(q)) {
                results.add("📋  Reservation #" + res.getReservationID()
                        + "  (" + res.getGuest().getUserName() + ")  —  " + res.getStatus());
                if (results.size() >= 8) break;
            }
        }

        displaySearchResults(results);
    }

    /**
     * Pushes search results to the UI.
     *
     * Currently prints to console — replace the body of this method
     * with ListView population or a custom popup once you add
     * <ListView fx:id="lstSearchResults"> to top-bar.fxml.
     */
    private void displaySearchResults(List<String> results) {
        // ── Console output (always safe, no FXML dependency) ──────────────────
        if (results.isEmpty()) {
            System.out.println("[Search] No results found.");
        } else {
            System.out.println("[Search] Results:");
            results.forEach(r -> System.out.println("  " + r));
        }

        // ── Uncomment once lstSearchResults is added to top-bar.fxml ─────────
        // Platform.runLater(() -> {
        //     if (lstSearchResults != null) {
        //         lstSearchResults.getItems().setAll(results);
        //         lstSearchResults.setVisible(!results.isEmpty());
        //         lstSearchResults.setManaged(!results.isEmpty());
        //     }
        // });
    }

    private void clearSearchResults() {
        // lstSearchResults.setVisible(false);
        // lstSearchResults.getItems().clear();
    }

    // ── Icon Handlers ────────────────────────────────────────────────────────

    /**
     * Bell icon — shows a count of pending actions for the logged-in user.
     * Guests:        number of unpaid invoices.
     * Receptionist:  number of PENDING check-ins waiting.
     * Admin:         nothing specific yet.
     */
    private void onBellClicked() {
        User user = null;
        try { user = SessionManager.getLoggedInUser(); } catch (Exception ignored) {}

        if (user == null) return;

        int count = 0;
        String message;

        switch (user.getTypeofuser()) {
            case GUEST -> {
                String username = user.getUserName();
                count = (int) Database.getInvoices().stream()
                        .filter(inv -> !inv.isPaid()
                                && inv.getReservation().getGuest().getUserName().equals(username))
                        .count();
                message = count == 0
                        ? "No unpaid invoices."
                        : "You have " + count + " unpaid invoice(s). Go to 'View & Pay Invoices'.";
            }
            case RECEPTIONIST -> {
                count = (int) Database.getReservations().stream()
                        .filter(r -> r.getStatus().name().equals("PENDING"))
                        .count();
                message = count == 0
                        ? "No pending check-ins."
                        : count + " reservation(s) are awaiting check-in.";
            }
            case ADMIN -> {
                long cancelled = Database.getReservations().stream()
                        .filter(r -> r.getStatus().name().equals("CANCELLED"))
                        .count();
                message = "Total cancelled reservations: " + cancelled;
            }
            default -> message = "No notifications.";
        }

        // For now, print to console. Replace with a JavaFX Alert or custom popup.
        System.out.println("[Notifications] " + message);

        // Example with a simple Alert (uncomment when JavaFX Alert is appropriate):
        // javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
        //         javafx.scene.control.Alert.AlertType.INFORMATION);
        // alert.setTitle("Notifications");
        // alert.setHeaderText(null);
        // alert.setContentText(message);
        // alert.showAndWait();
    }

    private void onSettingsClicked() {
        System.out.println("[TopBar] Settings clicked — navigate to SettingsScreen.fxml");
        // SceneManager.navigate("SettingsScreen.fxml");
    }

    private void onHelpClicked() {
        System.out.println("[TopBar] Help clicked — navigate to HelpScreen.fxml");
        // SceneManager.navigate("HelpScreen.fxml");
    }

    /**
     * Avatar click — navigate to the profile/account screen.
     * The profile screen can double as a logout option.
     */
    private void onAvatarClicked() {
        System.out.println("[TopBar] Avatar clicked — navigating to ProfileScreen.fxml");
        // SceneManager.navigate("ProfileScreen.fxml");
    }
}
