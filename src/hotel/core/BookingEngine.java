package hotel.core;
import hotel.model.entities.Amenity;
import hotel.model.entities.Room;
import hotel.model.entities.RoomType;
import hotel.model.enums.DiningPackage;
import hotel.model.enums.PaymentMethod;
import hotel.model.enums.ReservationStatus;
import hotel.model.enums.RoomView;
import hotel.model.staff.Receptionist;
import hotel.model.users.Guest;
import hotel.model.bookings.*;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
public class BookingEngine 
{
    Database database;

    public List<Room> filterRooms(String roomType, RoomView roomview,double maxPrice)
    {
        if (roomview == null) {
            System.out.println("Search Blocked: RoomView is required.");
            return new ArrayList<>(); // Return an empty list so the GUI doesn't crash
        }
        ArrayList<Room> results=new ArrayList<>();
        List<Room> Rooms=Database.getRooms();

        for (int i=0;i<Rooms.size();i++)
        {
            Room room=Rooms.get(i);
            RoomType type=room.getRoomType();
            if(type.getTypeName().equalsIgnoreCase(roomType)&&type.getRoomView().equals(roomview)&&type.getPricePerNight()<=maxPrice)
            {
                results.add(room);
            }
        }
        return results;
    }

    public double validatePromocode(String code) 
    {
        if (code == null) 
        {
            return 1.0; 
        }
    
        List<PromoCode> promos = Database.getPromoCodes();

        for (int i = 0; i < Database.getPromoCodes().size(); i++) 
        {
            if (promos.get(i).getCode().equals(code)) 
            {
                if (promos.get(i).isActive()) 
                {
                    return 1 - (promos.get(i).getDiscountPercentage());
                } 
                else 
                {
                    System.out.println("Promo code is expired");
                    return 1;
                }
            }
        }
        System.out.println("Promo code is not found");
        return 1; 
    }

    public static List<Room> getAvailableRooms(LocalDate checkIn, LocalDate checkOut)
    {    
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (checkIn.isAfter(checkOut)) 
        {
            throw new IllegalArgumentException("Check-in date cannot be after check-out date.");
        }  
        List<Room> available = new ArrayList<>();

        if (Database.getRooms() != null) 
        {
            for (Room room : Database.getRooms()) 
            {
                boolean isBooked = false;
                if (Database.getReservations() != null) 
                {
                    for (Reservation res : Database.getReservations()) 
                    {
                        if (res.getRoom().getRoomNumber() == room.getRoomNumber()) 
                        {    
                            if (!(checkOut.isBefore(res.getCheckinDate()) || checkOut.isEqual(res.getCheckinDate()) || 
                                checkIn.isAfter(res.getCheckoutDate()) || checkIn.isEqual(res.getCheckoutDate()))) 
                            {
                                isBooked = true;
                                break;
                            }
                        }
                    }
                }
                if (!isBooked) 
                {
                    available.add(room);
                }
            }
        }
        return available;
    }

    public static void viewAllRooms()
    {
        System.out.println("--- All Hotel Rooms ---");

        if (Database.getRooms() == null || Database.getRooms().isEmpty())
        {
            System.out.println("No rooms are currently in the system.");
            return;
        }

        //Define the dates to check current availability (Today to Tomorrow)
        LocalDate today = LocalDate.now();
        LocalDate tomorrow = today.plusDays(1);
        
        //Get the list of rooms available for these dates
        List<Room> availableRoomsList = getAvailableRooms(today, tomorrow);

        for (Room room : Database.getRooms()) {
            // 3. Check if the current room is inside the available list
            boolean isCurrentlyAvailable = availableRoomsList.contains(room);

            System.out.println("Room " + room.getRoomNumber()
                    + " | Type: " + room.getRoomType().getTypeName()
                    + " | Available: " + (isCurrentlyAvailable ? "Yes" : "No")); 
        }
    }

    public List<Room> sortRooms(List<Room> rooms, boolean ascending) {
        if (rooms == null || rooms.isEmpty()) {
            return new ArrayList<>();
        }

        rooms.sort((r1, r2) -> {
            double price1 = r1.getRoomType().getEffectivePrice();
            double price2 = r2.getRoomType().getEffectivePrice();

            if (ascending) {
                return Double.compare(price1, price2);
            } else {
                return Double.compare(price2, price1);
            }
        });

        return rooms;
    }


    public List<String> viewAllDiningPackages() 
    {
        List<String> packages = new ArrayList<>();
        packages.add("Breakfast Only - Start your day right!");
        packages.add("Half Board - Breakfast and Dinner included.");
        packages.add("Full Board - All three meals covered.");
        packages.add("All Inclusive - Ultimate dining experience.");
        return packages;
    }


    public double calculateRoomCost(Room room, LocalDate checkIn, LocalDate checkOut) 
    {
        
        if (room == null) {
            throw new IllegalArgumentException("Room cannot be null.");
        }
        if (checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Dates cannot be null.");
        }
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be at least one day after check-in.");
        }
    
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        double pricePerNight = room.getRoomType().getEffectivePrice();
        
        RoomView view = room.getRoomType().getRoomView(); 
        double viewSurcharge = 0.0;
        
        if (view != null) 
        {
            switch (view) 
            {
                case SEA_VIEW:
                    viewSurcharge = 1000; 
                    break;
                case POOL:
                    viewSurcharge = 500; 
                    break;
                case GARDEN:
                    viewSurcharge = 0; 
                    break;
            }
        }
        
        return nights * (pricePerNight + viewSurcharge);
    }

    //NOT COMPLETE - MUST PRINT PRICES FOR PACKAGES SUGGESTED TOO
    public List<String> suggestPackages(Guest guest) 
    {
        List<String> suggestions = new ArrayList<>();

        
        if (guest == null || guest.getRoomPreferences() == null || guest.getRoomPreferences().isEmpty()) 
        {
            suggestions.add("Special Offer: Stay in our hotel for 3 nights with a standard BREAKFAST_ONLY package.");
            return suggestions;
        }
 
        boolean lovesFood = false;
        boolean wantsRelaxation = false;

        for (String pref : guest.getRoomPreferences()) {
            String lowerPref = pref.toLowerCase(); 
            
            
            if (lowerPref.contains("food") || lowerPref.contains("dining") || lowerPref.contains("eat")) {
                lovesFood = true;
            }
            
            if (lowerPref.contains("relax") || lowerPref.contains("sea") || lowerPref.contains("quiet")) {
                wantsRelaxation = true;
            }
        }

    
        if (lovesFood && wantsRelaxation) {
            suggestions.add("Perfect Match: Stay in our hotel for 5 nights in a SEA_VIEW room with an ALL_INCLUSIVE dining package!");
        } else if (lovesFood) {
            suggestions.add("Perfect Match: Stay in our hotel for 3 nights and enjoy unlimited meals with a FULL_BOARD dining package.");
        } else if (wantsRelaxation) {
            suggestions.add("Perfect Match: Stay in our hotel for 4 nights to relax with a HALF_BOARD dining package.");
        } else {
            suggestions.add("Perfect Match: Stay in our hotel for 3 nights with our BREAKFAST_ONLY package.");
        }

        return suggestions;
    }

    
    public Reservation createDraftReservation( Guest guest , Room room , LocalDate checkIn , LocalDate checkOut , DiningPackage diningPackage, int numChildren, int numAdults ) throws IllegalArgumentException
    {
        List <Receptionist> allReceptionists = Database.getReceptionists();

        if (allReceptionists == null || allReceptionists.isEmpty()) 
        {
            throw new IllegalStateException("No receptionists available to handle the reservation.");
        }
        Random random = new Random();
        int randomIndex = random.nextInt( allReceptionists.size() );
        Receptionist allocatedReceptionist = allReceptionists.get(randomIndex);

        if (guest == null || room == null || checkIn == null || checkOut == null || diningPackage == null || numChildren < 0 || numAdults < 0) {
            throw new IllegalArgumentException("All reservation details must be provided.");
        }
        if (checkIn.isAfter(checkOut) || checkIn.isEqual(checkOut)) {
            throw new IllegalArgumentException("Check-out date must be at least one day after check-in.");
        }

         if (!getAvailableRooms(checkIn, checkOut).contains(room)) {
            throw new IllegalArgumentException("Selected room is not available for the given dates.");
        }

        if ( (numChildren + numAdults) > room.getRoomType().getMaxCapacity()) {
            throw new IllegalArgumentException("Number of guests exceeds room capacity.");
        }

        int reservationID = Database.getReservations().size() + 1;
        Reservation reservation = new Reservation(reservationID, guest, room, checkIn, checkOut, diningPackage, numChildren, numAdults);
        Database.getReservations().add(reservation);
        Database.saveData();
        allocatedReceptionist.addDraftReservation(reservation); 
        
        return reservation;
       
    }


    public double calculateDiningCost(DiningPackage packageType, int nights) 
    {
        if (packageType == null || nights <= 0) 
        {
            return 0.0; 
        }

        double packagePricePerNight = 0.0;

    
        switch (packageType) 
        {
            case BREAKFAST_ONLY:
                packagePricePerNight = 0;
                break;
            case HALF_BOARD:
                packagePricePerNight = 300;
                break;
            case FULL_BOARD:
                packagePricePerNight = 500;
                break;
            case ALL_INCLUSIVE:
                packagePricePerNight = 700;
                break;
        }

        return packagePricePerNight * nights;
    }

    public boolean confirmReservation( int reservationID , PaymentMethod paymentMethod)
    {
        List<Reservation> reservations = Database.getReservations();
        for (Reservation res : reservations)
        {
            if (res.getReservationID() == reservationID)
            {
                if (paymentMethod == null )
                {
                    System.out.println("Payment method is required to confirm reservation.");
                    return false;
                }
                if (res.getStatus() != ReservationStatus.PENDING)
                {
                    System.out.println("Only pending reservations can be confirmed.");
                    return false;
                }
                res.confirmreservation();
                Database.saveData();
                System.out.println("Reservation " + reservationID + " has been confirmed with payment method: " + paymentMethod);
                return true;
            }
        }
        return false;
    }

    public Invoice generateInvoice(Reservation reservation , String promoCode)
    {

        Invoice invoice = new Invoice();
        invoice.setInvoiceID(Database.getInvoices().size() + 1);
        invoice.setReservation(reservation);
        invoice.setPaid(false);

        double roomCost = calculateRoomCost(reservation.getRoom(), reservation.getCheckinDate(), reservation.getCheckoutDate());
        double diningCost = calculateDiningCost(reservation.getDiningpackage(), (int)ChronoUnit.DAYS.between(reservation.getCheckinDate(), reservation.getCheckoutDate()));
        double amenityCost = calculateAmenityCost( reservation.getSelectedAmenities());
        
        double subtotal = roomCost + diningCost + amenityCost;
        double discountMultiplier = validatePromocode(promoCode); 
        double totalCost = subtotal * discountMultiplier;

        invoice.setTotalAmount(totalCost);
        invoice.setPaymentDate(LocalDate.now());
        invoice.setAppliedPromoCode(promoCode);
        invoice.setDiscountAmount((roomCost + diningCost + amenityCost) - totalCost);
        Database.getInvoices().add(invoice);
        Database.saveData();                   
        return invoice;
    }

    public double calculateCancellationPenalty(int ReservationId, LocalDate cancelDate) {
        List<Reservation> reservations = Database.getReservations();
        List<Invoice> invoices = Database.getInvoices();

        for (Reservation res : reservations) {
            if (res.getReservationID() == ReservationId) {
                long daysBetween = ChronoUnit.DAYS.between(cancelDate, res.getCheckinDate());

                // Penalty Logic: 3 days or less
                if (daysBetween >= 0 && daysBetween <= 3) {
                    for (Invoice inv : invoices) {
                        if (inv.getReservation().getReservationID() == ReservationId) {
                            double penalty = inv.getTotalAmount() * 0.30;
                            System.out.println("Late cancellation (within 3 days). Penalty: " + penalty);
                            return penalty;
                        }
                    }
                    // If no invoice is found, we can't calculate 30%, so we return 0 or a flat fee
                    System.out.println("No invoice found for this reservation. No penalty applied.");
                    return 0.0;
                } else {
                    System.out.println("Cancelled in advance. No penalty applied.");
                    return 0.0;
                }
            }
        }
        System.out.println("Error: Reservation ID " + ReservationId + " not found in database.");
        return -1.0;
    }


    public double calculateAmenityCost(List<Amenity> selectedAmenities)
    {
        double total=0.0;
        if (selectedAmenities==null||selectedAmenities.isEmpty())
        {
            return  total;
        }
        for (Amenity a :selectedAmenities)
        {
            total+=a.getAmenityPrice();
        }
        return total;
    }

    public double calculateTotalRevenue() 
    {
        double total = 0.0;
        for (Invoice inv : Database.getInvoices()) {
            if (inv.isPaid()) {
                total += inv.getTotalAmount();
            }
        }
        return total;
    }

    public double calcualteTotalRevenue(LocalDate startDate,LocalDate endDate) throws Exception
    {
        if(endDate.isBefore(startDate))
        {
            throw new Exception("Invalid Range: End date cannot be before Start date");
        }

        List <Invoice> invoices=Database.getInvoices();
        double revenue=0;
        for(Invoice i:invoices)
        {
            if (i.isPaid()) {
                LocalDate paymentDate=i.getPaymentDate();
                if(paymentDate.isEqual(startDate)|| paymentDate.isEqual(endDate)||(paymentDate.isBefore(endDate)&& paymentDate.isAfter(startDate)))
                {
                    revenue+=i.getTotalAmount();
                }
            }
        }
        return revenue;
    }

    public double calculateTotalReservationCost(Reservation reservation) {
        if (reservation == null || reservation.getRoom() == null) {
            System.err.println("Error: Invalid reservation data for cost calculation.");
            return 0.0;
        }
        // We use the dates directly from the reservation object
        long nights = java.time.temporal.ChronoUnit.DAYS.between(
                reservation.getCheckinDate(),
                reservation.getCheckoutDate()
        );

        // Standard hotel policy: a stay of 0 days (same-day checkout) is charged as 1 night
        if (nights <= 0) {
            nights = 1;
        }
        double roomPricePerNight = reservation.getRoom().getRoomType().getPricePerNight();
        double totalRoomCost = roomPricePerNight * nights;
        double totalDiningCost = calculateDiningCost(reservation.getDiningpackage(), (int) nights);


        double totalAmenitiesCost = 0;
        if (reservation.getSelectedAmenities() != null) {
            for (hotel.model.entities.Amenity amenity : reservation.getSelectedAmenities()) {
                totalAmenitiesCost += amenity.getAmenityPrice();
            }
        }
        double grandTotal = totalRoomCost + totalDiningCost + totalAmenitiesCost;
        return grandTotal;
    }

    public double calculateOccupancyPercentage() 
    {
        List<Room> allRooms = Database.getRooms();
        List<Reservation> allReservations = Database.getReservations();

        if (allRooms == null || allRooms.isEmpty()) {
            return 0.0;
        }

        LocalDate today = LocalDate.now();
        int occupiedCount = 0;

        // Loop through every physical room in the hotel
        for (Room room : allRooms) {
            
            // For each room, check if there is a matching, active reservation
            for (Reservation res : allReservations) {
                boolean isThisRoom = (res.getRoom().getRoomNumber() == room.getRoomNumber());
                boolean isConfirmed = (res.getStatus() == ReservationStatus.CONFIRMED);
                boolean isCurrentlyStaying = !today.isBefore(res.getCheckinDate()) && today.isBefore(res.getCheckoutDate());

                // If we find an active reservation for this specific room
                if (isThisRoom && isConfirmed && isCurrentlyStaying) {
                    occupiedCount++;
                    break; // Stop checking reservations for this room and move to the next room
                }
            }
        }

        double totalRooms = allRooms.size();
        return (occupiedCount / totalRooms) * 100.0;
    }

    public void processCancellation(int reservationId, LocalDate cancelDate) {
        Reservation reservation = null;
        for (Reservation r : Database.getReservations()) {
            if (r.getReservationID() == reservationId) {
                reservation = r;
                break;
            }
        }

        if (reservation == null) {
            System.out.println("System Error: Reservation object lost.");
            return;
        }

        // This calls the function above which now has more System.out.println messages
        double penalty = calculateCancellationPenalty(reservationId, cancelDate);

        if (penalty >= 0) {
            reservation.setCancellationPenalty(penalty);
            reservation.setStatus(ReservationStatus.CANCELLED);
            Database.saveData(); // Ensure the "CANCELLED" status is actually written to the file
            System.out.println("Successfully cancelled Reservation #" + reservationId);
        }
    }

    public List<Reservation> getReservationsForGuest(Guest guest) 
    {
        List<Reservation> guestReservations = new ArrayList<>();

        for (Reservation res : Database.getReservations()) {
            // Match the reservation to the specific guest object or unique ID
            if (res.getGuest().getUserName().equals(guest.getUserName())) {
                // Only include active/pending reservations that CAN be cancelled
                if (res.getStatus() == ReservationStatus.PENDING || res.getStatus() == ReservationStatus.CONFIRMED) {
                    guestReservations.add(res);
                }
            }
        }
        return guestReservations;
    }

    public static void viewAndPayInvoices(Guest guest , Scanner sc) 
    {
        System.out.println("\n--- Your Unpaid Invoices ---");
        List<Invoice> myUnpaidInvoices = new ArrayList<>();
        
        for (Invoice inv : Database.getInvoices()) {
            if (!inv.isPaid() && inv.getReservation().getGuest().getUserName().equals(guest.getUserName())) {
                myUnpaidInvoices.add(inv);
            }
        }

        // Handle case where there's nothing to pay
        if (myUnpaidInvoices.isEmpty()) {
            System.out.println("You have no unpaid invoices.");
            return;
        }

        for (int i = 0; i < myUnpaidInvoices.size(); i++) {
            Invoice inv = myUnpaidInvoices.get(i);
            System.out.println((i + 1) + ". Invoice ID: " + inv.getInvoiceID() + 
                               " | Amount: $" + String.format("%.2f", inv.getTotalAmount()) + 
                               " | Reservation ID: " + inv.getReservation().getReservationID());
        }

        System.out.print("Enter the number of the invoice to pay (or 0 to exit): ");
        try {
            int invChoice = Integer.parseInt(sc.nextLine());
            
            // Fixed the bounds check to include the last item (<=)
            if (invChoice <= myUnpaidInvoices.size() && invChoice > 0) {
                Invoice selectedInv = myUnpaidInvoices.get(invChoice - 1);

                System.out.println(selectedInv.generateItemizedSummary());
                System.out.println("Your Current Balance: $" + String.format("%.2f", guest.getBalance()));
                
                System.out.println("\nSelect Payment Method:");
                System.out.println("1. Credit Card   2. Cash   3. Online   0. Cancel");
                System.out.print("Choice: ");
                String payChoice = sc.nextLine();

                PaymentMethod method = null;
                switch(payChoice) {
                    case "1": method = PaymentMethod.CREDIT_CARD; break;
                    case "2": method = PaymentMethod.CASH; break;
                    case "3": method = PaymentMethod.ONLINE; break;
                    case "0": System.out.println("Payment cancelled."); break;
                    default: System.out.println("Invalid selection."); break;      
                }
                
                if (method != null) {
                    selectedInv.pay(guest, method);
                        
                    if (selectedInv.isPaid()) {
                        selectedInv.getReservation().setStatus(ReservationStatus.CONFIRMED);
                        System.out.println("Reservation updated to CONFIRMED.");
                        Database.saveData(); 
                    }
                }
            } else if (invChoice != 0) {
                System.out.println("Invalid invoice number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    public void addbalance( Guest guest, Scanner sc){
        System.out.println("Your current balance : "+guest.getBalance());
        System.out.println("Please enter the amount to be added to your balance, 0 to cancel");
        double amount = sc.nextDouble();
        while(amount<=0){
            if(amount == 0){
                return;
            }
            else{
                System.out.println("Invalid value, Please re-enter the amount");
                amount=sc.nextDouble();
            }

        }
        guest.Newbalance(amount);
        System.out.println(amount+"EGP has been added to balance, current balance :"+guest.getBalance());
        Database.saveData();
    }
}


