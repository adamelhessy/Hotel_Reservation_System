package hotel.GUI.controllers;

import hotel.GUI.utils.SessionManager;
import hotel.core.BookingEngine;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.Reservation;
import hotel.model.enums.ReservationStatus;
import hotel.model.users.Guest;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GuestDashboard {

    // Nested Controllers injected by JavaFX <fx:include>
    @FXML private SideBarController sideBarController;
    @FXML private TopBarController topBarController;

    // Stats row labels
    @FXML private Label lblGreeting;
    @FXML private Label lblActiveReservations;
    @FXML private Label lblBalance;
    @FXML private Label lblNextCheckIn;

    // Latest Reservation card
    @FXML private HBox latestResBox;
    @FXML private Label lblRoomType;
    @FXML private Label lblCheckInDate;
    @FXML private Label lblCheckOutDate;

    // Room image
    @FXML private ImageView roomImageView;

    // Invoice panel
    @FXML private VBox invoicePanel;
    @FXML private Label lblInvoiceTitle;
    @FXML private Label lblInvoiceStatus;
    @FXML private Label lblInvRoom;
    @FXML private Label lblInvCheckIn;
    @FXML private Label lblInvCheckOut;
    @FXML private Label lblInvNights;
    @FXML private Label lblInvDining;
    @FXML private Label lblInvGuests;
    @FXML private Label lblInvPayment;
    @FXML private Label lblInvPaymentDate;
    @FXML private Label lblInvRoomCost;
    @FXML private Label lblInvDiningCost;
    @FXML private Label lblInvAmenityCost;
    @FXML private Label lblInvPromo;
    @FXML private Label lblInvDiscount;
    @FXML private Label lblInvTotal;

    private BookingEngine engine;
    private Reservation latestReservation;

    private static final String ROOMS_ASSET_PATH = "/hotel/GUI/assets/rooms/";
    private static final String DEFAULT_IMAGE     = ROOMS_ASSET_PATH + "default.jpg";

    @FXML
    public void initialize() {
        engine = new BookingEngine();

        if (sideBarController != null) {
            sideBarController.setRole("GUEST");
            sideBarController.setActiveSection("concierge");
        }

        if (topBarController != null) {
            topBarController.setPageTitle("The Digital Concierge", "Dashboard");
            topBarController.refresh();
        }

        Guest activeGuest = (Guest) SessionManager.getLoggedInUser();
        if (activeGuest != null) {
            populateDashboard(activeGuest);
        }
    }

    // -------------------------------------------------------------------------
    // Data population
    // -------------------------------------------------------------------------

    private void populateDashboard(Guest guest) {
        // Time-aware greeting
        int hour = LocalTime.now().getHour();
        String timeGreeting = (hour < 12) ? "Good Morning" : (hour < 17) ? "Good Afternoon" : "Good Evening";
        lblGreeting.setText(timeGreeting + ", " + guest.getUserName());

        lblBalance.setText(String.format("%,.2f", guest.getBalance()));

        // Filter to only this guest's active reservations
        List<Reservation> guestRes = engine.getReservationsForGuest(guest)
                .stream()
                .filter(r -> r.getGuest().equals(guest))
                .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED
                        || r.getStatus() == ReservationStatus.PENDING)
                .collect(Collectors.toList());

        lblActiveReservations.setText(String.valueOf(guestRes.size()));

        if (!guestRes.isEmpty()) {
            latestReservation = guestRes.get(guestRes.size() - 1);

            lblRoomType.setText(latestReservation.getRoom().getRoomType().getTypeName());
            lblCheckInDate.setText(latestReservation.getCheckinDate().toString());
            lblCheckOutDate.setText(latestReservation.getCheckoutDate().toString());

            guestRes.stream()
                    .filter(r -> r.getCheckinDate().isAfter(LocalDate.now()))
                    .min((a, b) -> a.getCheckinDate().compareTo(b.getCheckinDate()))
                    .ifPresentOrElse(
                            r -> lblNextCheckIn.setText(r.getCheckinDate().toString()),
                            () -> lblNextCheckIn.setText("Currently Checked In")
                    );

            loadRoomImage(latestReservation.getRoom().getRoomType().getTypeName());

            latestResBox.setDisable(false);
            latestResBox.setOpacity(1.0);

        } else {
            latestReservation = null;

            lblRoomType.setText("No Active Bookings");
            lblCheckInDate.setText("-");
            lblCheckOutDate.setText("-");
            lblNextCheckIn.setText("None");

            loadRoomImage(null);

            latestResBox.setDisable(true);
            latestResBox.setOpacity(0.5);
        }

        // Always hide the invoice panel when the dashboard refreshes
        hideInvoicePanel();
    }

    // -------------------------------------------------------------------------
    // Image loading
    // -------------------------------------------------------------------------

    private void loadRoomImage(String typeName) {
        if (roomImageView == null) return;

        try {
            java.io.InputStream imageStream = null;

            if (typeName != null && !typeName.isBlank()) {
                String imagePath = ROOMS_ASSET_PATH + typeName + ".jpg";
                imageStream = getClass().getResourceAsStream(imagePath);
                if (imageStream == null) {
                    System.out.println("GuestDashboard: no image found for type \"" + typeName + "\", using default.");
                }
            }

            if (imageStream == null) {
                imageStream = getClass().getResourceAsStream(DEFAULT_IMAGE);
            }

            if (imageStream != null) {
                roomImageView.setImage(new Image(imageStream));
            } else {
                System.err.println("GuestDashboard: default room image also missing — check assets/rooms/default.jpg");
            }

        } catch (Exception e) {
            System.err.println("GuestDashboard: failed to load room image — " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Invoice panel helpers
    // -------------------------------------------------------------------------

    private void showInvoicePanel(Invoice invoice) {
        if (invoicePanel == null || invoice == null) return;

        Reservation res = latestReservation;

        // Header
        lblInvoiceTitle.setText("Invoice  #" + invoice.getInvoiceID()
                + "   —   Reservation #" + res.getReservationID());
        lblInvoiceStatus.setText(invoice.isPaid() ? "✔  PAID" : "⚠  UNPAID");
        lblInvoiceStatus.setStyle(invoice.isPaid()
                ? "-fx-text-fill: #90EE90;"
                : "-fx-text-fill: #FFD700;");

        // Left column — reservation details
        lblInvRoom.setText(res.getRoom().getRoomType().getTypeName()
                + "  (Room " + res.getRoom().getRoomNumber() + ")");
        lblInvCheckIn.setText(res.getCheckinDate().toString());
        lblInvCheckOut.setText(res.getCheckoutDate().toString());
        lblInvNights.setText(res.calcnights() + " night(s)");
        lblInvDining.setText(res.getDiningpackage() != null
                ? res.getDiningpackage().toString() : "None");
        lblInvGuests.setText(res.getNumAdults() + " adult(s)"
                + (res.getNumChildren() > 0 ? ", " + res.getNumChildren() + " child(ren)" : ""));
        lblInvPayment.setText(invoice.getPaymentMethod() != null
                ? invoice.getPaymentMethod().toString() : "—");
        lblInvPaymentDate.setText(invoice.getPaymentDate() != null
                ? invoice.getPaymentDate().toString() : "—");

        // Right column — cost breakdown
        double roomCost  = engine.calculateRoomCost(res.getRoom(), res.getCheckinDate(), res.getCheckoutDate());
        double diningCost = engine.calculateDiningCost(res.getDiningpackage(), res.calcnights());
        double amenityCost = engine.calculateAmenityCost(res.getSelectedAmenities());

        lblInvRoomCost.setText(String.format("EGP %,.2f", roomCost));
        lblInvDiningCost.setText(String.format("EGP %,.2f", diningCost));
        lblInvAmenityCost.setText(String.format("EGP %,.2f", amenityCost));
        lblInvPromo.setText(invoice.getAppliedPromoCode() != null
                && !invoice.getAppliedPromoCode().equals("NONE")
                ? invoice.getAppliedPromoCode() : "No promo applied");
        lblInvDiscount.setText(String.format("- EGP %,.2f", invoice.getDiscountAmount()));
        lblInvTotal.setText(String.format("EGP %,.2f", invoice.getTotalAmount()));

        // Show the panel
        invoicePanel.setVisible(true);
        invoicePanel.setManaged(true);
    }

    private void hideInvoicePanel() {
        if (invoicePanel == null) return;
        invoicePanel.setVisible(false);
        invoicePanel.setManaged(false);
    }

    // -------------------------------------------------------------------------
    // FXML action handlers
    // -------------------------------------------------------------------------

    @FXML
    private void onViewInvoice() {
        if (latestReservation == null) return;

        // If invoice panel is already open, toggle it closed
        if (invoicePanel.isVisible()) {
            hideInvoicePanel();
            return;
        }

        Invoice invoice = engine.generateInvoice(latestReservation, null);

        if (invoice == null) {
            showError("Invoice Not Found", "No invoice was found for this reservation.");
            return;
        }

        showInvoicePanel(invoice);
    }

    @FXML
    private void onCloseInvoice() {
        hideInvoicePanel();
    }

    @FXML
    private void onCancelReservation() {
        if (latestReservation == null) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancel Reservation");
        confirm.setHeaderText("Are you sure you want to cancel?");
        confirm.setContentText(
                "Reservation #" + latestReservation.getReservationID()
                        + " — " + latestReservation.getRoom().getRoomType().getTypeName()
                        + "\nCheck-in:  " + latestReservation.getCheckinDate()
                        + "\nCheck-out: " + latestReservation.getCheckoutDate()
        );

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            engine.processCancellation(
                    latestReservation.getReservationID(),
                    LocalDate.now()
            );

            showInfo("Reservation Cancelled",
                    "Your reservation has been successfully cancelled.");

            Guest activeGuest = (Guest) SessionManager.getLoggedInUser();
            if (activeGuest != null) {
                populateDashboard(activeGuest);
            }
        }
    }

    @FXML
    private void onModifyStay() {
        showInfo("Modify Stay",
                "Please contact reception to modify your reservation,\n"
                        + "or use the Book Room screen to make a new booking.");
    }

    // -------------------------------------------------------------------------
    // Alert helpers
    // -------------------------------------------------------------------------

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
