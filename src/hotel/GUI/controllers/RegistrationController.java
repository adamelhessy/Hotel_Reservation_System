package hotel.GUI.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.event.ActionEvent;

import hotel.GUI.utils.SceneManager;
import hotel.core.Database;
import hotel.model.enums.AccountStatus;
import hotel.model.enums.Gender;
import hotel.model.enums.UserType;
import hotel.model.users.Guest;

import java.time.LocalDate;

public class RegistrationController {

    // --- Input Fields ---
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> genderComboBox; 
    @FXML private DatePicker dobPicker;
    @FXML private TextField phoneField;
    @FXML private TextField addressField;

    // --- Validation Icons (Labels used as icons in FXML) ---
    @FXML private Label charCountIcon;
    @FXML private Label numberIcon;
    @FXML private Label capitalIcon;

    // --- Action Controls ---
    @FXML private Button createAccountButton;
    @FXML private Hyperlink signInLink;

    @FXML
    public void initialize() {
        // Populate the gender dropdown to match the Gender enum
        genderComboBox.getItems().addAll("Male", "Female");

        // Add a live listener to update password requirement visuals dynamically 
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validatePasswordLive(newValue);
        });

        // Bind the button action
        createAccountButton.setOnAction(this::handleCreateAccount);
    }

    /**
     * Dynamically updates the color of the password requirement labels as the user types.
     */
    private void validatePasswordLive(String password) {
        // Check Minimum 8 Characters
        if (password.length() >= 8) {
            charCountIcon.setStyle("-fx-text-fill: #3fb68b;"); // Success Teal
        } else {
            charCountIcon.setStyle("-fx-text-fill: #73777f;"); // Default Gray
        }

        // Check for at least 1 number
        if (password.matches(".*\\d.*")) {
            numberIcon.setStyle("-fx-text-fill: #3fb68b;");
        } else {
            numberIcon.setStyle("-fx-text-fill: #73777f;");
        }

        // Check for at least 1 capital letter
        if (password.matches(".*[A-Z].*")) {
            capitalIcon.setStyle("-fx-text-fill: #3fb68b;");
        } else {
            capitalIcon.setStyle("-fx-text-fill: #73777f;");
        }
    }

    @FXML
    void handleCreateAccount(ActionEvent event) {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();
        String genderStr = genderComboBox.getValue();
        LocalDate dob = dobPicker.getValue();
        String phone = phoneField.getText().trim();
        String address = addressField.getText().trim();

        // 1. Basic Empty Field Validation
        if (username.isEmpty() || password.isEmpty() || genderStr == null || dob == null || phone.isEmpty() || address.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Please fill in all the fields.");
            return;
        }

        // 2. Duplicate Username Check[cite: 1]
        for (Guest guest : Database.getGuests()) {
            if (guest.getUserName().equalsIgnoreCase(username)) {
                showAlert(Alert.AlertType.ERROR, "Registration Failed", "Username is already taken. Please try another one.");
                return;
            }
        }

        // 3. Enforce Password Rules[cite: 1]
        if (password.length() < 8 || !password.matches(".*\\d.*") || !password.matches(".*[A-Z].*")) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Your password does not meet all the security requirements.");
            return;
        }

        // 4. Phone Number Validation (Mimicking phonecheck logic)[cite: 1]
        if (phone.length() != 11 || !phone.matches("\\d+")) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Invalid phone number! Please enter exactly 11 numbers.");
            return;
        }

        // 5. Build the Guest Object
        Guest newGuest = new Guest();
        newGuest.setUserName(username);
        newGuest.setPassword(password);
        newGuest.setTypeofuser(UserType.GUEST);
        newGuest.setTheGender(genderStr.equalsIgnoreCase("Male") ? Gender.MALE : Gender.FEMALE);
        newGuest.setAccountStatus(AccountStatus.ACTIVE);
        newGuest.setDateOfbirth(dob);
        newGuest.setPhoneNumber(phone);
        newGuest.setAddress(address);
        newGuest.setBalance(0.0);
        newGuest.setFailedLoginAttempts(0);

        // 6. Generate Unique ID[cite: 1]
        if (Database.getGuests().isEmpty()) {
            newGuest.setUniqueId(1000);
        } else {
            int lastGuestIndex = Database.getGuests().size() - 1;
            newGuest.setUniqueId(Database.getGuests().get(lastGuestIndex).getUniqueId() + 1);
        }

        // 7. Save to Database[cite: 1]
        Database.getGuests().add(newGuest);
        Database.saveData();

        // 8. Success & Redirection[cite: 1]
        showAlert(Alert.AlertType.INFORMATION, "Registration Complete", "Welcome, " + username + "!\nYour Guest ID is: " + newGuest.getUniqueId());
        SceneManager.navigate("login-page.fxml");
    }

    @FXML
    void handleSignIn(ActionEvent event) {
        SceneManager.navigate("login-page.fxml");
    }

    /**
     * Helper method to display UI alerts.
     */
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}