package hotel.model.bookings;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import hotel.model.users.*;
import hotel.model.entities.*;
import hotel.model.enums.*;
public class Reservation implements Serializable
{
    private int reservationID;
    private Guest guest;
    private Room room;
    private LocalDate CheckinDate;
    private LocalDate CheckoutDate;
    private ReservationStatus status;
    private DiningPackage diningpackage;
    private List<Amenity> requestedAmenities;
    private double cancellationPenalty;
    private int numChildren;
    private int numAdults;

    public Reservation(int id, Guest guest, Room room,LocalDate in , LocalDate out , DiningPackage diningpackage, int numChildren, int numAdults) {
        this.reservationID = id;
        this.guest = guest;
        this.room = room;
        this.CheckinDate = in;
        this.CheckoutDate = out;
        this.status = ReservationStatus.PENDING;
        this.requestedAmenities = new ArrayList<Amenity>();
        this.diningpackage = diningpackage;
        this.numChildren = numChildren;
        this.numAdults = numAdults;
    }

    public void confirmreservation() { status = ReservationStatus.CONFIRMED; }
    public void cancelreservation() { status = ReservationStatus.CANCELLED; }
    public void completereservation() { status = ReservationStatus.COMPLETED;}

    public int calcnights() {
        return (int) ChronoUnit.DAYS.between(CheckinDate , CheckoutDate);

    }

    public double calcamenitytotal() 
    {
        double total = 0;
        if (requestedAmenities != null) 
        {
            for (Amenity amenity : requestedAmenities) 
            {
                total += amenity.getAmenityPrice();
            }
        }
        return total;
    }

    public void addAmenity (Amenity amenity)
    {
        if( amenity != null && !requestedAmenities.contains(amenity))
        {
            this.requestedAmenities.add(amenity);
        }
    }

    public void updatediningpack(DiningPackage diningpackage)
    {
        this.diningpackage = diningpackage;
    }

    public int getReservationID() {
        return reservationID;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckinDate() {
        return CheckinDate;
    }

    public LocalDate getCheckoutDate() {
        return CheckoutDate;
    }

    public ReservationStatus getStatus() {
        return status;
    }

    public DiningPackage getDiningpackage() {
        return diningpackage;
    }

    public List<Amenity> getSelectedAmenities() {
        return requestedAmenities;
    }

    public void setReservationID(int reservationID) {
        this.reservationID = reservationID;
    }

    public void setGuest(Guest guest) {
        this.guest = guest;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public void setCheckinDate(LocalDate checkinDate) {
        CheckinDate = checkinDate;
    }

    public void setCheckoutDate(LocalDate checkoutDate) {
        CheckoutDate = checkoutDate;
    }

    public void setStatus(ReservationStatus status) {
        this.status = status;
    }

    public void setDiningpackage(DiningPackage diningpackage) {
        this.diningpackage = diningpackage;
    }

    public void setSelectedAmenities(List<Amenity> selectedAmenities) {
        this.requestedAmenities = selectedAmenities;
    }

    public double getCancellationPenalty() {
        return cancellationPenalty;
    }

    public void setCancellationPenalty(double cancellationPenalty) {
        this.cancellationPenalty = cancellationPenalty;
    }

    public int getNumChildren() {
        return numChildren;
    }
    
    public void setNumChildren(int numChildren) {
        this.numChildren = numChildren;
    }

    public int getNumAdults() {
        return numAdults;
    }

    public void setNumAdults(int numAdults) {
        this.numAdults = numAdults;
    }

}