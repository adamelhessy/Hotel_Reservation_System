package hotel.model.bookings;

import java.io.Serializable;
import java.time.LocalDate;

public class PromoCode implements Serializable {
    private static final long serialVersionUID = 1L; 

    private String code;
    private double discountPercentage; 
    private LocalDate expiryDate;
    private boolean isActive;

    public PromoCode(String code, double discountPercentage, LocalDate expiryDate) {
        this.code = code.toUpperCase();
        this.discountPercentage = discountPercentage;
        this.expiryDate = expiryDate;
        this.isActive = true;
    }

    // Getters and Setters
    public String getCode() { return code; }
    public double getDiscountPercentage() { return discountPercentage; }
    public LocalDate getExpiryDate() { return expiryDate; }
    public boolean isActive() {
        return isActive && LocalDate.now().isBefore(expiryDate);
    }
    public void setisActive(boolean active){this.isActive = active;}
    public void setCode(String code) { this.code = code.toUpperCase(); }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return "PromoCode{" + "code='" + code + '\'' + ", discount=" + (discountPercentage * 100) + "%}";
    }
}
