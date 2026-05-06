package hotel.GUI.controllers;

import hotel.GUI.utils.SceneManager;
import hotel.GUI.utils.SessionManager;
import hotel.core.Database;
import hotel.model.enums.UserType;
import hotel.model.staff.Admin;
import hotel.model.staff.Receptionist;
import hotel.model.users.Guest;
import hotel.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for login-page.fxml
 *
 * Changes from original:
 *  1. Calls SessionManager.setLoggedInUser() before navigating — this makes the
 *     logged-in User available to every subsequent screen's controller via
 *     SessionManager.getLoggedInUser().
 *  2. Shows an error label (lblError) for bad credentials instead of
 *     only printing to the console.
 *  3. Clears any previous session on load (handles "Back to Login" flows).
 */
public class LoginController {

    @FXML private Button        btnGuest;
    @FXML private Button        btnReceptionist;
    @FXML private Button        btnAdmin;
    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button        btnSignIn;

    // Optional — add <Label fx:id="lblError"> to login-page.fxml to show
    // inline error messages. Safe to leave out; null-checked below.
    @FXML private Label lblError;

    private String selectedRole = "Guest";

    @FXML
    public void initialize() {
        // Clear any previous session when returning to login
        SessionManager.clearSession();

        // Role selector
        btnGuest.setOnAction(event         -> switchRole("Guest",        btnGuest));
        btnReceptionist.setOnAction(event  -> switchRole("Receptionist", btnReceptionist));
        btnAdmin.setOnAction(event         -> switchRole("Admin",         btnAdmin));

        btnSignIn.setOnAction(event -> onSignIn());

        // Default tab
        switchRole("Guest", btnGuest);

        // Seed data if the database is empty
        if (Database.getGuests().isEmpty() && Database.getAdmins().isEmpty()) {
            Database.loadData();
            if (Database.getGuests().isEmpty()) {
                Database.initializeHotelData();
            }
        }
    }

    // ── Role Tab Switching ────────────────────────────────────────────────────

    private void switchRole(String role, Button clickedButton) {
        this.selectedRole = role;
        clearError();

        btnGuest.getStyleClass().remove("role-tab-active");
        btnReceptionist.getStyleClass().remove("role-tab-active");
        btnAdmin.getStyleClass().remove("role-tab-active");

        if (!clickedButton.getStyleClass().contains("role-tab-active")) {
            clickedButton.getStyleClass().add("role-tab-active");
        }
    }

    // ── Sign In Logic ─────────────────────────────────────────────────────────

    private void onSignIn() {
        clearError();

        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }

        User loggedInUser = null;

        switch (selectedRole) {
            case "Guest" -> {
                Guest tempGuest = new Guest();
                loggedInUser = tempGuest.Login(username, password, UserType.GUEST);
                if (loggedInUser != null) {
                    SessionManager.setLoggedInUser(loggedInUser);
                    SceneManager.navigate("GuestDashboard.fxml");
                }
            }
            case "Receptionist" -> {
                Receptionist tempRec = new Receptionist();
                loggedInUser = tempRec.Login(username, password, UserType.RECEPTIONIST);
                if (loggedInUser != null) {
                    SessionManager.setLoggedInUser(loggedInUser);
                    SceneManager.navigate("ReceptionistDashboard.fxml");
                }
            }
            case "Admin" -> {
                Admin tempAdmin = new Admin();
                loggedInUser = tempAdmin.Login(username, password, UserType.ADMIN);
                if (loggedInUser != null) {
                    SessionManager.setLoggedInUser(loggedInUser);
                    SceneManager.navigate("AdminDashboard.fxml");
                }
            }
        }

        if (loggedInUser == null) {
            showError("Invalid username or password. Please try again.");
        }
    }

    // ── Error Label Helpers ───────────────────────────────────────────────────

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
            lblError.setManaged(true);
        }
        System.err.println("[LoginController] " + message);
    }

    private void clearError() {
        if (lblError != null) {
            lblError.setText("");
            lblError.setVisible(false);
            lblError.setManaged(false);
        }
    }
}
