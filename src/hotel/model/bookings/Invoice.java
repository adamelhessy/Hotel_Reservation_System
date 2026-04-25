package hotel.model.bookings;
import hotel.interfaces.*;
import hotel.model.enums.PaymentMethod;
import hotel.model.users.Guest;
import java.time.LocalDate;
import java.io.Serializable;

public class Invoice implements Payable , Serializable
{
    private int invoiceID;
    private Reservation reservation;
    private boolean isPaid;
    private double totalAmount;
    private PaymentMethod paymentMethod;
    private LocalDate paymentDate;
    private String appliedPromoCode;
    private double discountAmount;

    public Invoice() {
    }

    public Invoice(int invoiceID, Reservation reservation, boolean isPaid, double totalAmount, PaymentMethod paymentMethod, LocalDate paymentDate, String appliedPromoCode, double discountAmount) {
        this.invoiceID = invoiceID;
        this.reservation = reservation;
        this.isPaid = isPaid;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.paymentDate = paymentDate;
        this.appliedPromoCode = appliedPromoCode;
        this.discountAmount = discountAmount;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public Reservation getReservation() {
        return reservation;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public String getAppliedPromoCode() {
        return appliedPromoCode;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public void setReservation(Reservation reservation) {
        this.reservation = reservation;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public void setAppliedPromoCode(String appliedPromoCode) {
        this.appliedPromoCode = appliedPromoCode;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    @Override
    public double getTotal() {
        return totalAmount; 
    }

    @Override
    public void pay(Guest guest, PaymentMethod method)
    {
        if (guest.getBalance() >= getTotal()) 
        {
            guest.setBalance(guest.getBalance() - getTotal());
            this.isPaid = true;
            this.paymentMethod = method;
            this.paymentDate = LocalDate.now();
            System.out.println("Payment successful.");
        } 
        else 
        {
            System.out.println("Insufficient guest balance.");
        }
    }

    public String generateItemizedSummary() {
        if (this.reservation == null) {
            return "Error: No reservation linked to this invoice.";
        }

        StringBuilder summary = new StringBuilder();
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                reservation.getCheckinDate(),
                reservation.getCheckoutDate()
        );
        if (nights <= 0) nights = 1;

        summary.append("\n--- ITEMIZATION FOR INVOICE #").append(invoiceID).append(" ---\n");

        double roomPrice = reservation.getRoom().getRoomType().getPricePerNight();
        summary.append(String.format("Room Cost (%d nights @ $%.2f/night): $%.2f\n",
                nights, roomPrice, (roomPrice * nights)));

        if (reservation.getDiningpackage() != null) {
            summary.append("Dining Package (").append(reservation.getDiningpackage()).append("): Included in Total\n");
        }

        if (!reservation.getSelectedAmenities().isEmpty()) {
            summary.append("Amenities:\n");
            for (hotel.model.entities.Amenity a : reservation.getSelectedAmenities()) {
                summary.append(String.format("  - %-15s : $%.2f\n", a.getAmenityName(), a.getAmenityPrice()));
            }
        }

        if (this.discountAmount > 0 && this.appliedPromoCode != null) 
        {
            summary.append(String.format("Promo Discount (%s): -$%.2f\n",
            appliedPromoCode, this.discountAmount));
        }

        summary.append("------------------------------------------\n");
        summary.append(String.format("GRAND TOTAL: $%.2f\n", this.totalAmount));
        summary.append("------------------------------------------\n");

        return summary.toString();
    }
}
