package hotel.GUI.controllers;

import hotel.GUI.utils.SceneManager;
import hotel.core.Database;
import hotel.model.enums.UserType;
import hotel.model.staff.Admin;
import hotel.model.staff.Receptionist;
import hotel.model.users.Guest;
import hotel.model.users.User;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private Button btnGuest;
    @FXML private Button btnReceptionist;
    @FXML private Button btnAdmin;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnSignIn;

    private String selectedRole = "Guest";

    @FXML
    public void initialize() {
        btnGuest.setOnAction(event -> switchRole("Guest", btnGuest));
        btnReceptionist.setOnAction(event -> switchRole("Receptionist", btnReceptionist));
        btnAdmin.setOnAction(event -> switchRole("Admin", btnAdmin));
        
        btnSignIn.setOnAction(event -> onSignIn());

        switchRole("Guest", btnGuest);
        
        // Ensure the database is seeded if launching directly to GUI
        if (Database.getGuests().isEmpty() && Database.getAdmins().isEmpty()) {
            Database.loadData();
            if (Database.getGuests().isEmpty()) {
                Database.initializeHotelData();
            }
        }
    }

    private void switchRole(String role, Button clickedButton) {
        this.selectedRole = role;

        btnGuest.getStyleClass().remove("role-tab-active");
        btnReceptionist.getStyleClass().remove("role-tab-active");
        btnAdmin.getStyleClass().remove("role-tab-active");

        if (!clickedButton.getStyleClass().contains("role-tab-active")) {
            clickedButton.getStyleClass().add("role-tab-active");
        }
    }

    private void onSignIn() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            System.out.println("Error: Please enter both username and password.");
            return;
        }

        User loggedInUser = null;

        // Use your backend User classes and the overloaded Login method!
        if (selectedRole.equals("Guest")) {
            Guest tempGuest = new Guest();
            loggedInUser = tempGuest.Login(username, password, UserType.GUEST);
            
            if (loggedInUser != null) {
                System.out.println("-> Navigating to Guest Dashboard...");
                SceneManager.navigate("GuestDashboard.fxml");
            }
            
        } else if (selectedRole.equals("Receptionist")) {
            Receptionist tempRec = new Receptionist();
            loggedInUser = tempRec.Login(username, password, UserType.RECEPTIONIST);
            
            if (loggedInUser != null) {
                System.out.println("-> Navigating to Receptionist Dashboard...");
                SceneManager.navigate("ReceptionistDashboard.fxml");
            }
            
        } else if (selectedRole.equals("Admin")) {
            Admin tempAdmin = new Admin();
            loggedInUser = tempAdmin.Login(username, password, UserType.ADMIN);
            
            if (loggedInUser != null) {
                System.out.println("-> Navigating to Admin Dashboard...");
                SceneManager.navigate("AdminDashboard.fxml");
            }
        }

        if (loggedInUser == null) {
            // The User.Login method already printed the failure reason to the console.
            // Later, we can show a red error label on the GUI here.
            System.out.println("GUI: Login sequence failed.");
        }
    }
}