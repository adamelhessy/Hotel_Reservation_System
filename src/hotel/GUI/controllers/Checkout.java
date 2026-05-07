package hotel.GUI.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

/**
 * Controller for the Grand Azure Hotel – Booking Checkout (3-step flow).
 *
 * Steps:
 *   Screen 1 → Review Invoice   (screenReview)
 *   Screen 2 → Payment          (screenPayment)
 *   Screen 3 → Confirmation     (screenConfirmation)
 *
 * How screens are swapped:
 *   Each VBox has visible + managed toggled. Only one is active at a time.
 */
public class Checkout implements Initializable {

    // ─── SCREEN 1 – REVIEW INVOICE ──────────────────────────────────────────
    @FXML private VBox   screenReview;
    @FXML private Button btnCancelReview;

    // Guest & room labels
    @FXML private Label lblGuestName;
    @FXML private Label lblGuestEmail;
    @FXML private Label lblRoomName;
    @FXML private Label lblRoomMeta;

    // Cost breakdown labels
    @FXML private Label lblRoomRateDesc;
    @FXML private Label lblRoomRateAmt;
    @FXML private Label lblTaxDesc;
    @FXML private Label lblTaxAmt;
    @FXML private Label lblResortFeeDesc;
    @FXML private Label lblResortFeeAmt;
    @FXML private Label lblTotalDue;

    // Booking summary labels
    @FXML private Label  lblCheckInDate;
    @FXML private Label  lblCheckInTime;
    @FXML private Label  lblCheckOutDate;
    @FXML private Label  lblCheckOutTime;
    @FXML private Button btnContinueToPayment;

    // ─── SCREEN 2 – PAYMENT ─────────────────────────────────────────────────
    @FXML private VBox   screenPayment;
    @FXML private Label  lblPaymentSubtitle;
    @FXML private Label  lblAvailableBalance;
    @FXML private Button btnPayFromBalance;
    @FXML private Label  lblAmountToPay;
    @FXML private Button btnPayWithCard;
    @FXML private VBox   panelCreditCard;
    @FXML private HBox   panelPayAtHotel;

    @FXML private RadioButton rbCreditCard;
    @FXML private RadioButton rbPayAtHotel;

    @FXML private Button btnBackToReview;
    @FXML private Button btnContinueToConfirm;

    // ─── SCREEN 3 – CONFIRMATION ─────────────────────────────────────────────
    @FXML private VBox   screenConfirmation;
    @FXML private Label  lblReservationId;
    @FXML private Label  lblConfirmGuestName;
    @FXML private Label  lblConfirmRoom;
    @FXML private Label  lblConfirmCheckIn;
    @FXML private Label  lblConfirmTotal;
    @FXML private Button btnDownloadReceipt;
    @FXML private Button btnGoToDashboard;

    // ─── Internal booking model ──────────────────────────────────────────────
    private final BookingData booking = new BookingData();

    // ─── Payment method state ────────────────────────────────────────────────
    private enum PaymentMethod { CREDIT_CARD, PAY_AT_HOTEL, ACCOUNT_BALANCE }
    private PaymentMethod selectedPayment = PaymentMethod.CREDIT_CARD;

    private static final NumberFormat CURRENCY =
            NumberFormat.getCurrencyInstance(Locale.US);

    // =========================================================================
    //  INITIALIZE
    // =========================================================================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateReviewScreen();
        populatePaymentScreen();
        showScreen(1); // start on Review Invoice
    }

    // ─── Populate Screen 1 ───────────────────────────────────────────────────
    private void populateReviewScreen() {
        lblGuestName.setText(booking.guestName);
        lblGuestEmail.setText(booking.guestEmail);
        lblRoomName.setText(booking.roomName);
        lblRoomMeta.setText(booking.nights + " Nights • " + booking.adults + " Adults");

        lblRoomRateDesc.setText("Room Rate (" + booking.nights + " nights @ "
                + CURRENCY.format(booking.nightlyRate) + "/night)");
        lblRoomRateAmt.setText(CURRENCY.format(booking.roomRateTotal()));

        lblTaxDesc.setText("State & Local Taxes (" + (int)(booking.taxRate * 100) + "%)");
        lblTaxAmt.setText(CURRENCY.format(booking.taxAmount()));

        lblResortFeeDesc.setText("Resort Fee (" + CURRENCY.format(booking.resortFeePerNight) + "/night)");
        lblResortFeeAmt.setText(CURRENCY.format(booking.resortFeeTotal()));

        lblTotalDue.setText(CURRENCY.format(booking.grandTotal()));

        lblCheckInDate.setText(booking.checkInDate);
        lblCheckInTime.setText(booking.checkInTime);
        lblCheckOutDate.setText(booking.checkOutDate);
        lblCheckOutTime.setText(booking.checkOutTime);
    }

    // ─── Populate Screen 2 ───────────────────────────────────────────────────
    private void populatePaymentScreen() {
        lblPaymentSubtitle.setText(
                "Choose how you would like to settle your balance of "
                        + CURRENCY.format(booking.grandTotal()) + ".");
        lblAvailableBalance.setText(CURRENCY.format(booking.accountBalance));
        lblAmountToPay.setText(
                String.format("%.2f", booking.grandTotal()).replace("-", ""));
    }

    // =========================================================================
    //  SCREEN NAVIGATION HELPERS
    // =========================================================================

    /** Shows exactly one of the three screens. */
    private void showScreen(int step) {
        setScreen(screenReview,       step == 1);
        setScreen(screenPayment,      step == 2);
        setScreen(screenConfirmation, step == 3);
    }

    private void setScreen(VBox screen, boolean active) {
        screen.setVisible(active);
        screen.setManaged(active);
    }

    // =========================================================================
    //  SCREEN 1 – ACTIONS
    // =========================================================================

    /** "CONTINUE TO PAYMENT →" button */
    @FXML
    private void onContinueToPayment(ActionEvent event) {
        showScreen(2);
    }

    /** "✕  CANCEL" button */
    @FXML
    private void onCancel(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "Are you sure you want to cancel this booking?",
                ButtonType.YES, ButtonType.NO);
        alert.setTitle("Cancel Booking");
        alert.setHeaderText("Cancel Booking Checkout");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                showInfoAlert("Cancelled", "Your booking process has been cancelled.");
                // TODO: close window or navigate back to dashboard
            }
        });
    }

    // =========================================================================
    //  SCREEN 2 – PAYMENT ACTIONS
    // =========================================================================

    /** "← Back" button on the Payment screen */
    @FXML
    private void onBackToReview(ActionEvent event) {
        showScreen(1);
    }

    /** Radio: Pay Now (Credit Card) selected */
    @FXML
    private void onSelectCreditCard(ActionEvent event) {
        selectedPayment = PaymentMethod.CREDIT_CARD;
        rbCreditCard.setSelected(true);
        rbPayAtHotel.setSelected(false);
        highlightCreditCardPanel(true);
    }

    /** Radio: Pay at Hotel selected */
    @FXML
    private void onSelectPayAtHotel(ActionEvent event) {
        selectedPayment = PaymentMethod.PAY_AT_HOTEL;
        rbPayAtHotel.setSelected(true);
        rbCreditCard.setSelected(false);
        highlightCreditCardPanel(false);
    }

    /** Highlights the credit-card panel border when selected */
    private void highlightCreditCardPanel(boolean selected) {
        String borderStyle = selected
                ? "-fx-border-color: #2e8b57; -fx-border-width: 2; -fx-border-radius: 10;"
                : "-fx-border-color: transparent; -fx-border-width: 2; -fx-border-radius: 10;";
        panelCreditCard.setStyle(
                "-fx-background-color: #f7f5f0; -fx-background-radius: 10; " +
                        "-fx-padding: 16 20 16 20; " + borderStyle);
    }

    /** "Pay from Balance" button */
    @FXML
    private void onPayFromBalance(ActionEvent event) {
        double total = booking.grandTotal();
        if (booking.accountBalance < total) {
            showInfoAlert("Insufficient Balance",
                    "Your account balance of " + CURRENCY.format(booking.accountBalance)
                            + " is less than the amount due of " + CURRENCY.format(total) + ".");
            return;
        }
        selectedPayment = PaymentMethod.ACCOUNT_BALANCE;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Pay " + CURRENCY.format(total) + " from your account balance of "
                        + CURRENCY.format(booking.accountBalance) + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Payment");
        confirm.setHeaderText("Pay from Account Balance");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                booking.accountBalance -= total;
                finalizeBookingAndShowConfirmation();
            }
        });
    }

    /** "Pay with Card" button inside the credit-card panel */
    @FXML
    private void onPayWithCard(ActionEvent event) {
        double total = booking.grandTotal();
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Charge " + CURRENCY.format(total) + " to your credit card on file?",
                ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirm Payment");
        confirm.setHeaderText("Credit Card Payment");
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                finalizeBookingAndShowConfirmation();
            }
        });
    }

    /** "Continue to Confirmation →" bottom button on the Payment screen */
    @FXML
    private void onContinueToConfirmation(ActionEvent event) {
        if (selectedPayment == PaymentMethod.PAY_AT_HOTEL) {
            finalizeBookingAndShowConfirmation();
        } else {
            showInfoAlert("Payment Required",
                    "Please complete payment before continuing to confirmation.");
        }
    }

    // ─── Finalize and transition to confirmation ──────────────────────────────
    private void finalizeBookingAndShowConfirmation() {
        String reservationId = "#RES-" + (8000 + (int)(Math.random() * 1999));
        booking.reservationId = reservationId;
        populateConfirmationScreen();
        showScreen(3);
    }

    // =========================================================================
    //  SCREEN 3 – CONFIRMATION ACTIONS
    // =========================================================================

    private void populateConfirmationScreen() {
        lblReservationId.setText(booking.reservationId);
        lblConfirmGuestName.setText(booking.guestName);
        lblConfirmRoom.setText(booking.roomShortName);
        lblConfirmCheckIn.setText(booking.checkInDate);
        lblConfirmTotal.setText(CURRENCY.format(booking.grandTotal()));
    }

    /** "⬇ Download Receipt" button */
    @FXML
    private void onDownloadReceipt(ActionEvent event) {
        showInfoAlert("Download Receipt",
                "Receipt for reservation " + booking.reservationId
                        + " is being prepared for download.\n\n"
                        + "Guest:     " + booking.guestName + "\n"
                        + "Room:      " + booking.roomShortName + "\n"
                        + "Check-in:  " + booking.checkInDate + "\n"
                        + "Total Paid: " + CURRENCY.format(booking.grandTotal()));
        // TODO: generate PDF and trigger file-save dialog
    }

    /** "Go to Dashboard" button */
    @FXML
    private void onGoToDashboard(ActionEvent event) {
        showInfoAlert("Dashboard", "Navigating back to the Receptionist Dashboard…");
        // TODO: load ReceptionistDashboard.fxml or close this window
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

    // =========================================================================
    //  INNER DATA MODEL
    // =========================================================================

    /**
     * Simple data-holder for the booking being checked out.
     * In a real app this would be injected or fetched from a service/DAO.
     */
    public static class BookingData {
        // Guest
        public String guestName     = "Alice Smith";
        public String guestEmail    = "alice.smith@example.com";

        // Room
        public String roomName      = "Deluxe Suite - Room 204";
        public String roomShortName = "Deluxe Suite 204";
        public int    nights        = 5;
        public int    adults        = 2;

        // Pricing
        public double nightlyRate        = 750.00;
        public double taxRate            = 0.12;
        public double resortFeePerNight  = 60.00;

        // Account
        public double accountBalance = 3200.00;

        // Dates
        public String checkInDate  = "Oct 12, 2023";
        public String checkInTime  = "3:00 PM";
        public String checkOutDate = "Oct 17, 2023";
        public String checkOutTime = "11:00 AM";

        // Set after payment
        public String reservationId = "";

        // ── Derived amounts ──────────────────────────────────────────────────
        public double roomRateTotal()   { return nightlyRate * nights; }
        public double taxAmount()       { return roomRateTotal() * taxRate; }
        public double resortFeeTotal()  { return resortFeePerNight * nights; }
        public double grandTotal()      { return roomRateTotal() + taxAmount() + resortFeeTotal(); }
    }
}
