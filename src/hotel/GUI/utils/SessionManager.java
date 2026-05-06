package hotel.GUI.utils;

import hotel.model.users.User;

/**
 * SessionManager — a simple static holder for the currently logged-in User.
 *
 * Why this exists:
 *   JavaFX screens (FXMLs) are loaded fresh on every SceneManager.navigate() call,
 *   which means controller instances are created anew each time. There is no
 *   built-in "session" between screens. SessionManager fills that gap by holding
 *   a single static reference to the User object that was returned by User.Login().
 *
 * Usage:
 *   // After a successful login (in LoginController):
 *   SessionManager.setLoggedInUser(loggedInUser);
 *
 *   // In any controller that needs to know who is logged in:
 *   User me = SessionManager.getLoggedInUser();
 *
 *   // On logout:
 *   SessionManager.clearSession();
 *   SceneManager.navigate("login-page.fxml");
 */
public class SessionManager {

    // The single logged-in user shared across all screens
    private static User loggedInUser = null;

    // Private constructor — this is a utility class, never instantiated
    private SessionManager() {}

    /**
     * Stores the user returned by User.Login() so every screen can read it.
     * Call this immediately after a successful login.
     */
    public static void setLoggedInUser(User user) {
        loggedInUser = user;
    }

    /**
     * Returns the currently logged-in User, or null if nobody is logged in.
     * Callers should always null-check the return value.
     */
    public static User getLoggedInUser() {
        return loggedInUser;
    }

    /**
     * Clears the session on logout.
     * Always call this before navigating back to the login screen.
     */
    public static void clearSession() {
        loggedInUser = null;
    }

    /**
     * Convenience check — returns true if a user is currently logged in.
     */
    public static boolean isLoggedIn() {
        return loggedInUser != null;
    }
}
