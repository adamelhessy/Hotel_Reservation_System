package hotel.core;

import hotel.model.entities.*;
import hotel.model.enums.*;
import hotel.model.staff.*;
import hotel.model.users.*;
import hotel.model.bookings.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner sc = new Scanner(System.in);
    private static BookingEngine engine = new BookingEngine();

    public static void main(String[] args) {
        Database.loadData();
        
        // Auto-seed dummy data if the database is completely empty
        if (Database.getAdmins().isEmpty() && Database.getReceptionists().isEmpty()) {
            System.out.println("[SYSTEM] Database is empty. Seeding default hotel data...");
            Database.initializeHotelData();
        }

        try {
            showMainMenu();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // --- 1. MAIN MENU ---
    public static void showMainMenu() {
        while (true) {
            System.out.println("\n========== AIN SHAMS HOTEL SYSTEM ==========");
            System.out.println("1. Admin Portal      2. Receptionist Portal");
            System.out.println("3. Guest Portal      4. Global Occupancy Stats");
            System.out.println("5. Save and Exit");
            System.out.print("Selection: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1":
                    System.out.println("\n--- ADMIN LOGIN ---");
                    Admin tempAdmin = new Admin();
                    User loggedInAdmin = tempAdmin.Login(UserType.ADMIN);
                    if (loggedInAdmin != null) showAdminMenu((Admin) loggedInAdmin);
                    break;
                    
                case "2":
                    System.out.println("\n--- RECEPTIONIST LOGIN ---");
                    Receptionist tempRec = new Receptionist();
                    User loggedInRec = tempRec.Login(UserType.RECEPTIONIST);
                    if (loggedInRec != null) {
                        try {
                            showReceptionistMenu((Receptionist) loggedInRec);
                        } catch (Exception e) {
                            System.out.println("Menu Error: " + e.getMessage());
                        }
                    }
                    break;
                    
                case "3":
                    System.out.println("\n--- GUEST PORTAL ---");
                    System.out.println("1. Login   2. Register");
                    System.out.print("Selection: ");
                    String gChoice = sc.nextLine();
                    Guest tempGuest = new Guest();
                    
                    if (gChoice.equals("1")) {
                        User loggedInGuest = tempGuest.Login(UserType.GUEST);
                        if (loggedInGuest != null) {
                            ((Guest) loggedInGuest).setInput(sc); // Bind main scanner to guest
                            showGuestMenu((Guest) loggedInGuest);
                        }
                    } else if (gChoice.equals("2")) {
                        tempGuest.setInput(sc);
                        try {
                            tempGuest.register();
                            System.out.println("Registration complete! You can now log in.");
                        } catch (Exception e) {
                            System.out.println("Registration error: " + e.getMessage());
                        }
                    } else {
                        System.out.println("Invalid selection.");
                    }
                    break;
                    
                case "4":
                    System.out.println("Current Occupancy: " + engine.calculateOccupancyPercentage() + "%");
                    break;
                    
                case "5":
                    Database.saveData();
                    System.out.println("Data saved successfully. Exiting...");
                    System.exit(0);
                    break;
                    
                default:
                    System.out.println("Invalid selection.");
                    break;
            }
        }
    }

    // --- 2. ADMIN MENU ---
    private static void showAdminMenu(Admin admin) {
        while (true) {
            System.out.println("\n--- ADMIN MANAGEMENT PANEL ---");
            System.out.println("Logged in as: " + admin.getUserName());
            System.out.println("1. Create/Read/Update/Delete Room");
            System.out.println("2. Create/Read/Update/Delete Room Type");
            System.out.println("3. Create/Read/Update/Delete Amenity");
            System.out.println("4. Set Seasonal Multiplier    5. Financial Report");
            System.out.println("6. View All Guests            7. View All Reservations");
            System.out.println("8. View All Rooms");
            System.out.println("9. Logout");
            System.out.print("Action: ");

            try {
                String choice = sc.nextLine();
                switch (choice) {
                    case "1":  // --- MANAGE ROOMS ---
                        List <RoomType> roomtype=Database.getRoomTypes();
                        if (roomtype.isEmpty()) {
                            System.out.println("[No Room Types available]");
                        }
                        else
                        {
                            admin.DisplayRoomType();
                            System.out.print("Rooms: 1.Create 2.Read 3.Update 4.Delete: ");
                            String op = sc.nextLine();

                        try {
                            if (op.equals("1")) {
                                System.out.print("Enter new Room Number: ");
                                int roomNum = Integer.parseInt(sc.nextLine()); 

                                System.out.print("Enter Floor Number: ");
                                int floorNum = Integer.parseInt(sc.nextLine());

                                System.out.print("Enter Room Type Name (From Available List): ");
                                String typeName = sc.nextLine();

                                // Fetch the actual RoomType from the database before assigning it
                                RoomType assignedType = admin.readRoomType(typeName);

                                admin.createRoom(new Room(roomNum, floorNum, assignedType));
                                System.out.println("Success! Room created.");
                            }
                            else if (op.equals("2"))
                            {
                                try
                                {
                                    System.out.print("Enter Room Number to read: ");
                                    int roomNum = Integer.parseInt(sc.nextLine());
                                    Room r = admin.readRoom(roomNum);
                                    System.out.println("Found Room: " + r.getRoomNumber() + " on floor " + r.getFloor() + "\nRoomType: "+r.getRoomType().getTypeName());
                                    System.out.println("Amenties Included: ");
                                    List<Amenity> list = r.getAmenities();
                                    if (list == null || list.isEmpty())
                                    {
                                        System.out.println("  [No amenities assigned to this room]");
                                    } else
                                    {
                                        // Loop through the list to actually print the names
                                        for (Amenity a : list)
                                        {
                                            System.out.println("  • " + a.getAmenityName() + " ($" + a.getAmenityPrice() + ")");
                                        }
                                    }
                                }

                                 catch (Exception e)
                                {
                                    // This will catch all those custom Exception messages we wrote earlier!
                                    System.out.println("❌ ERROR: " + e.getMessage());
                                }
                                break;
                            }
                            else if (op.equals("3"))
                            {
                                System.out.print("Enter Room Number to update: ");
                                int Name = Integer.parseInt(sc.nextLine());

                                // To update, we first read the existing one, then modify it
                                Room existing = admin.readRoom(Name);
                                printDivider(" ADD-ON AMENITIES (Optional)");
                                List<Amenity> allAmenities = Database.getAmenities();
                                List<Amenity> chosenAmenities = new ArrayList<>();

                                if (allAmenities.isEmpty()) {
                                    System.out.println("  No amenities currently available.");
                                } else {
                                    System.out.println("  Select amenities to add to your stay (one per line, 0 when done):\n");
                                    for (int i = 0; i < allAmenities.size(); i++) {
                                        Amenity a = allAmenities.get(i);
                                        System.out.printf("  [%d] %-35s $%.0f%n", i + 1, a.getAmenityName(), a.getAmenityPrice());
                                    }

                                    System.out.println("\n  Enter amenity number(s) to add (0 to skip):");
                                    while (true) {
                                        System.out.print("  > ");
                                        String raw = sc.nextLine().trim();
                                        if (raw.equals("0") || raw.isEmpty()) break;
                                        try {
                                            int idx = Integer.parseInt(raw);
                                            if (idx >= 1 && idx <= allAmenities.size()) {
                                                Amenity picked = allAmenities.get(idx - 1);
                                                if (!chosenAmenities.contains(picked)) {
                                                    chosenAmenities.add(picked);

                                                    System.out.println("  ✔ Added: " + picked.getAmenityName());
                                                } else {
                                                    System.out.println("  Already added.");
                                                }
                                            } else {
                                                System.out.println("  Invalid selection.");
                                            }
                                        } catch (NumberFormatException e) {
                                            System.out.println("  Please enter a number.");
                                        }
                                    }
                                    existing.setAmenities(chosenAmenities);//this part doesnt work still
                                    admin.updateRoom(Name, existing);
                                    Database.saveData();
                                }
                            }
                            else if (op.equals("4")) {
                                System.out.print("Enter Room Number to delete: ");
                                int roomNum = Integer.parseInt(sc.nextLine());
                                admin.deleteRoom(roomNum);
                            }
                        }
                        catch (Exception e)
                        {
                            // This will catch all those custom Exception messages we wrote earlier!
                            System.out.println(" ERROR: " + e.getMessage());
                        }
                        break;
                    }
                    case "2": { // --- MANAGE ROOM TYPES ---

                        List<RoomType> list = Database.getRoomTypes();
                        if (list.isEmpty())
                        {
                            System.out.println("[No Room Types available]");
                        }
                            // Only printing the TypeName field
                        admin.DisplayRoomType();
                        System.out.print("Room Types: 1.Create 2.Read 3.Update 4.Delete: \n");
                        String op = sc.nextLine();

                        try {
                            if (op.equals("1")) {
                                System.out.print("Enter Type Name: ");
                                String typeName = sc.nextLine();

                                System.out.print("Enter Base Price: ");
                                double basePrice = Double.parseDouble(sc.nextLine());

                                // Handling the Enum input
                                System.out.print("Enter Room View (SEA_VIEW, GARDEN_VIEW, CITY_VIEW): ");
                                String viewInput = sc.nextLine().toUpperCase();
                                RoomView view = RoomView.valueOf(viewInput);

                                System.out.print("Enter Description: ");
                                String description = sc.nextLine();

                                System.out.print("Enter Max Guests: ");
                                int maxGuests = Integer.parseInt(sc.nextLine());

                                // Note: Multiplier defaults to 1.0 for new types
                                admin.createRoomType(new RoomType(typeName, basePrice, view, description, 1.0, basePrice, maxGuests));
                                System.out.println("Success! Room Type '" + typeName + "' added.");
                            }
                            else if (op.equals("2")) {
                                System.out.print("Enter Room Type Name to search: ");
                                String typeName = sc.nextLine();
                                RoomType rt = admin.readRoomType(typeName);
                                System.out.println("Found: " + rt.getTypeName() + " | Price: $" + rt.getEffectivePrice() + " | View: " + rt.getRoomView());
                            }
                            else if (op.equals("3"))
                            {
                                System.out.print("Enter Room Type Name to update: ");
                                String typeName = sc.nextLine();

                                // To update, we first read the existing one, then modify it
                                RoomType existing = admin.readRoomType(typeName);
                                if(existing!=null)
                                {
                                    System.out.print("Enter New Base Price (was " + existing.getBasePrice() + "): ");
                                    existing.setBasePrice(Double.parseDouble(sc.nextLine()));
                                    System.out.print("Enter New Room View (was " + existing.getRoomView() + "): ");
                                    existing.setRoomView(RoomView.valueOf(sc.nextLine()));
                                    System.out.print("Enter New Room Description (was " + existing.getDescription() + "): ");
                                    existing.setDescription(sc.nextLine());
                                    System.out.print("Enter New Room's Max Capacity (was " + existing.getMaxCapacity() + "): ");
                                    existing.setMaxCapacity(Integer.parseInt(sc.nextLine()));
                                    admin.updateRoomType(typeName, existing);
                                    System.out.println("Update successful.");
                                }
                                else
                                    throw new Exception("Error: Room type not found.");
                            }
                            else if (op.equals("4")) {
                                System.out.print("Enter Room Type Name to delete: ");
                                String typeName = sc.nextLine();
                                admin.deleteRoomType(typeName);
                                System.out.println("Room Type deleted.");
                            }
                        } catch (IllegalArgumentException e) {
                            System.out.println(" ERROR: Invalid Enum value. Please use SEA_VIEW, GARDEN_VIEW, or CITY_VIEW.");
                        } catch (Exception e) {
                            System.out.println(" ERROR: " + e.getMessage());
                        }
                        break;
                    }
                    case "3": { // --- MANAGE AMENITIES ---
                        // 1. Fetch the list immediately before printing
                        List<Amenity> currentAmenities = Database.getAmenities();
                        // 2. The Check & Loop
                        if (currentAmenities == null || currentAmenities.isEmpty()) {
                            System.out.println("[No amenities available in database]");
                        }
                        admin.DisplayAmenity();
                        System.out.print("Amenities: 1.Create 2.Read 3.Update 4.Delete:");
                        String op = sc.nextLine();
                        try {
                            if (op.equals("1")) {
                                System.out.print("Enter Amenity Name: ");
                                String name = sc.nextLine();

                                System.out.print("Enter Description: ");
                                String desc = sc.nextLine();

                                System.out.print("Enter Price (0 if free): ");
                                double price = Double.parseDouble(sc.nextLine());

                                admin.createAmenity(new Amenity(name, desc, price));
                                System.out.println("Success! Amenity created.");
                            }
                            else if (op.equals("2"))
                            { // --- READ ---
                                System.out.print("Enter Amenity Name to search: ");
                                String name = sc.nextLine();

                                Amenity a = admin.readAmenity(name);
                                System.out.println("Found: " + a.getAmenityName() + " | Price: $" + a.getAmenityPrice() + " | Info: " + a.getDescription());
                            }
                            else if (op.equals("3"))
                            { // --- UPDATE ---
                                System.out.print("Enter Amenity Name to update: ");
                                String name = sc.nextLine();

                                // Fetch existing to show current values
                                Amenity existing = admin.readAmenity(name);

                                System.out.print("Enter New Description (was: " + existing.getDescription() + "): ");
                                existing.setDescription(sc.nextLine());

                                System.out.print("Enter New Price (was: " + existing.getAmenityPrice() + "): ");
                                existing.setAmenityPrice(Double.parseDouble(sc.nextLine()));

                                admin.updateAmenity(name, existing);
                                System.out.println("Update successful.");
                            }
                            else if (op.equals("4"))
                            {
                                System.out.print("Enter Amenity Name to delete: ");
                                String name = sc.nextLine();
                                admin.deleteAmenity(name);
                            }
                        } catch (Exception e) {
                            System.out.println(" ERROR: " + e.getMessage());
                        }
                        break;
                    }
                    case "4": {
                        admin.DisplayRoomType();
                        System.out.print("Type: "); String t = sc.nextLine();
                        System.out.print("Multiplier: "); double m = Double.parseDouble(sc.nextLine());
                        admin.setSeasonalMultiplier(t, m);
                        break;
                    }
                    case "5": {
                        System.out.print("Report days: ");
                        admin.generateFinancialReport(Integer.parseInt(sc.nextLine()));
                        break;
                    }
                    case "6": admin.viewAllGuests(); break;
                    case "7": admin.viewAllReservations(); break;
                    case "8" :BookingEngine.viewAllRooms(); break;
                    case "9": System.out.println("Logging out..."); return;
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    // --- 3. RECEPTIONIST MENU ---
    private static void showReceptionistMenu(Receptionist rec) throws Exception {
        while (true) {
            System.out.println("\n--- RECEPTIONIST FRONT DESK ---");
            System.out.println("Logged in as: " + rec.getUserName());
            System.out.println("1. Manage Check-In       2. Manage Check-Out");
            System.out.println("3. View Drafts           4. View Guests");
            System.out.println("5. View Reservations     6. Logout");
            System.out.print("Action: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1": System.out.print("ID: "); rec.manageCheckIn(Integer.parseInt(sc.nextLine())); break;
                case "2": {
                    Scanner input = new Scanner(System.in);
                    System.out.print("Add a Comment: ");
                    String comment = input.nextLine();
                    System.out.print("Rating: ");
                    int rating = Integer.parseInt(sc.nextLine());
                    Review review = new Review(rating, comment);
                    System.out.print("ID: ");
                    rec.manageCheckOut(Integer.parseInt(sc.nextLine()), review);
                    input.close();
                    break;  
                }
                case "3": rec.getDraftReservations().forEach(System.out::println); break;
                case "4": rec.viewAllGuests(); break;
                case "5": rec.viewAllReservations(); break;
                case "6": System.out.println("Logging out..."); return;
                default:
                    throw new IllegalStateException("Unexpected value: " + choice);
                
            }
        }
    }

    // --- 4. GUEST MENU ---
    private static void showGuestMenu(Guest guest) {
        while (true) {
            System.out.println("\n--- GUEST PORTAL ---");
            System.out.println("Logged in as: " + guest.getUserName()+"  ID : "+guest.getUniqueId());
            System.out.println("1. Search & Book Room (Engine)");
            System.out.println("2. View My Reservations");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View & Pay Invoices");
            System.out.println("5. Reset Password");
            System.out.println("6.Top up your balance");
            System.out.println("7. Logout");
            System.out.print("Action: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1": {
                    runBookingFlow(guest);
                    break;
                }
                case "2" : 
                {
                    guest.ViewReservation(guest.getUserName(), guest.getUniqueId());
                    break;
                }
                case "3": {
                    List<Reservation> myActiveBookings = engine.getReservationsForGuest(guest);

                    if (myActiveBookings.isEmpty()) {
                        System.out.println("You have no active reservations to cancel.");
                    } 
                    else 
                    {
                        System.out.println("\n--- Your Active Bookings ---");
                        for (int i = 0; i < myActiveBookings.size(); i++) {
                            Reservation r = myActiveBookings.get(i);
                            System.out.println((i + 1) + ". Room " + r.getRoom().getRoomNumber() +
                                    " [" + r.getCheckinDate() + " to " + r.getCheckoutDate() + "]");
                        }

                        System.out.print("Enter the number of the booking to cancel (or 0 to exit): ");
                        try {
                            int choice1 = Integer.parseInt(sc.nextLine());
                            if (choice1 > 0 && choice1 <= myActiveBookings.size()) {
                                Reservation selected = myActiveBookings.get(choice1 - 1);
                                engine.processCancellation(selected.getReservationID(), LocalDate.now());
                                System.out.println("Cancellation request submitted successfully.");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Please enter a number.");
                        }
                    }
                    break;
                }
                case "4" : {
                    BookingEngine.viewAndPayInvoices(guest, sc);
                    break;
                }
                case "5": {
                    guest.ResetPassword(guest.getUserName(), UserType.GUEST); 
                    break;
                }
                case "6": {
                    engine.addbalance(guest,sc);
                    break;
                }
                case "7": {
                    System.out.println("Logging out..."); 
                    return;
                }
            }
        }
    }



    private static void runBookingFlow(Guest guest) 
    {
        final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        printDivider("BOOK A ROOM - STEP 1 OF 5 : SELECT DATES");
    
        //STEP 1 : Dates 
        LocalDate checkIn  = promptDate("Enter Check-In  Date (yyyy-MM-dd): ", DATE_FMT);
        if (checkIn == null || checkIn.isBefore(LocalDate.now())) { System.out.println("Booking cancelled."); return; }
    
        LocalDate checkOut = promptDate("Enter Check-Out Date (yyyy-MM-dd): ", DATE_FMT);
        if (checkOut == null || checkOut.isBefore(checkIn) || checkOut.isBefore(LocalDate.now())) { System.out.println("Booking cancelled."); return; }
    
        if (!checkOut.isAfter(checkIn)) {
            System.out.println("Check-out must be at least one day after check-in. Returning to menu.");
            return;
        }
    
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        System.out.println("    " + nights + " night(s): " + checkIn + "  to  " + checkOut);
    
        //STEP 2 : Choose Room
        printDivider("STEP 2 OF 5 : CHOOSE A ROOM");
        List<Room> available = engine.getAvailableRooms(checkIn, checkOut);
        if (available.isEmpty()) {
            System.out.println("No rooms available for those dates. Try different dates.");
            return;
        }
    
        System.out.printf("%-6s %-30s %-12s %-10s %-8s %-8s%n",
                "Room#", "Type", "View", "Price/Night", "Capacity", "Rating");
        for (Room r : available) {
            RoomType rt = r.getRoomType();
            double avgRating = r.calculateAverageRating();
            String stars = avgRating == 0 ? "No reviews"
                    : String.format("%.1f ★", avgRating);
            System.out.printf("%-6d %-30s %-12s $%-9.0f %-8d %s%n",
                    r.getRoomNumber(),
                    rt.getTypeName(),
                    rt.getRoomView(),
                    rt.getEffectivePrice(),
                    rt.getMaxCapacity(),
                    stars);
        }
    
        Room selectedRoom = promptRoomChoice(available);
        if (selectedRoom == null) { System.out.println("Booking cancelled."); return; }
    
        // Show selected room details
        RoomType srt = selectedRoom.getRoomType();
        System.out.println("\n  Selected 2Room " + selectedRoom.getRoomNumber()
                + "  |  Floor " + selectedRoom.getFloor()
                + "  |  " + srt.getTypeName()
                + "  |  " + srt.getRoomView()
                + "  |  Max " + srt.getMaxCapacity() + " guests");
        System.out.println("  " + srt.getDescription());
    
        //STEP 3 : Guests & Dining 
        printDivider("STEP 3 OF 5 : GUESTS & DINING PACKAGE");
    
        int adults   = promptPositiveInt("Number of Adults   : ", 1, srt.getMaxCapacity());
        int children = promptPositiveInt("Number of Children : ", 0, srt.getMaxCapacity() - adults);
    
    
        System.out.println("\n  Dining packages available:");
        System.out.println("  [1] BREAKFAST_ONLY  - Complimentary breakfast");
        System.out.println("  [2] HALF_BOARD      - Breakfast + Dinner  (+$300/night)");
        System.out.println("  [3] FULL_BOARD      - All 3 meals          (+$500/night)");
        System.out.println("  [4] ALL_INCLUSIVE   - Ultimate experience  (+$700/night)");
    
        
        List<String> suggestions = engine.suggestPackages(guest);
        if (!suggestions.isEmpty()) {
            System.out.println("\n   Suggested for you:");
            suggestions.forEach(s -> System.out.println("    -2 " + s));
        }
    
        DiningPackage selectedDining = promptDiningPackage();
        if (selectedDining == null) { System.out.println("Booking cancelled."); return; }
    
        //STEP 4 : Amenities 
        printDivider("STEP 4 OF 5 : ADD-ON AMENITIES (Optional)");
        List<Amenity> allAmenities = Database.getAmenities();
        List<Amenity> chosenAmenities = new ArrayList<>();
    
        if (allAmenities.isEmpty()) {
            System.out.println("  No amenities currently available.");
        } else {
            System.out.println("  Select amenities to add to your stay (one per line, 0 when done):\n");
            for (int i = 0; i < allAmenities.size(); i++) {
                Amenity a = allAmenities.get(i);
                System.out.printf("  [%d] %-35s $%.0f%n", i + 1, a.getAmenityName(), a.getAmenityPrice());
            }
    
            System.out.println("\n  Enter amenity number(s) to add (0 to skip):");
            while (true) {
                System.out.print("  > ");
                String raw = sc.nextLine().trim();
                if (raw.equals("0") || raw.isEmpty()) break;
                try {
                    int idx = Integer.parseInt(raw);
                    if (idx >= 1 && idx <= allAmenities.size()) {
                        Amenity picked = allAmenities.get(idx - 1);
                        if (!chosenAmenities.contains(picked)) {
                            chosenAmenities.add(picked);
                            System.out.println("Added: " + picked.getAmenityName());
                        } else {
                            System.out.println("Already added.");
                        }
                    } else {
                        System.out.println("Invalid selection.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println(" Please enter a number.");
                }
            }
        }
    
        //STEP 5 : Promo Code & Confirmation 
        printDivider("STEP 5 OF 5 : PROMO CODE & CONFIRM");
    
        System.out.print("Enter a promo code (or press Enter to skip): ");
        String promoInput = sc.nextLine().trim();
        String promoCode = promoInput.isEmpty() ? null : promoInput.toUpperCase();
    
        //Calculate costs for preview BEFORE creating anything 
        double roomCost    = engine.calculateRoomCost(selectedRoom, checkIn, checkOut);
        double diningCost  = engine.calculateDiningCost(selectedDining, (int) nights);
        double amenityCost = engine.calculateAmenityCost(chosenAmenities);
        double subtotal    = roomCost + diningCost + amenityCost;
        double discount    = 0.0;
        double total       = subtotal;
    
        if (promoCode != null) {
            double multiplier = engine.validatePromocode(promoCode);
            if (multiplier < 1.0) {
                discount = subtotal * (1.0 - multiplier);
                total    = subtotal * multiplier;
                System.out.println("Promo code applied! You save $" + String.format("%.2f", discount));
            } else {
                System.out.println(" Promo code not valid or expired — no discount applied.");
                promoCode = null;
            }
        }
    
        // Print itemised breakdown
        System.out.println("\n----------------------------------------------------------");
        System.out.println("                    BOOKING SUMMARY                         ");
        System.out.println("\n----------------------------------------------------------");

        System.out.printf ("  > Room %-10s x %d night(s)%13s$%,-8.2f%n", selectedRoom.getRoomNumber(), nights, "", roomCost);
        System.out.printf ("  > Dining (%s)%-25s$%,-8.2f%n",selectedDining, "", diningCost);

        if (!chosenAmenities.isEmpty()) {
            for (Amenity a : chosenAmenities) {
                System.out.printf("   + %-37s$%,-8.2f%n",
                        a.getAmenityName(), a.getAmenityPrice());
            }
        }
        if (discount > 0) {
            System.out.printf("  > Promo Discount (%s)%-21s-$%,-7.2f%n",
                    promoInput, "", discount);
        }
        System.out.println("\n");
        System.out.printf ("  > TOTAL DUE%-35s$%,-8.2f%n", "", total);
        System.out.printf ("  > Your Balance%-31s$%,-8.2f%n", "", guest.getBalance());
        System.out.println("\n");
    
        System.out.print("\n Confirm this booking? (Y to confirm / N to cancel): ");
        String confirm = sc.nextLine().trim().toUpperCase();
        if (!confirm.equals("Y")) {
            System.out.println("Booking cancelled. No charges made.");
            return;
        }
    
        // CREATE RESERVATION & INVOICE
        try {
            Reservation draft = engine.createDraftReservation(
                    guest, selectedRoom, checkIn, checkOut,
                    selectedDining, children, adults);
    
            draft.setSelectedAmenities(chosenAmenities);
    
            Invoice invoice = engine.generateInvoice(draft, promoCode);
    
            System.out.println("\n Reservation #" + draft.getReservationID() + " created (PENDING).");
    
            //Offer to pay now 
            System.out.print("   Pay now from your balance to confirm immediately? (Y/N): ");
            String payNow = sc.nextLine().trim().toUpperCase();
    
            if (payNow.equals("Y")) 
            {
                if (guest.getBalance() >= invoice.getTotalAmount()) 
                {
                    boolean confirmed = engine.confirmReservation(
                            draft.getReservationID(), PaymentMethod.ONLINE);
    
                    if (confirmed) {
                        invoice.pay(guest, PaymentMethod.ONLINE);
                        Database.saveData();
    
                        System.out.println("\n  ══════════════════════════════════════════");
                        System.out.println("    BOOKING CONFIRMED!");
                        System.out.println("   Reservation ID : " + draft.getReservationID());
                        System.out.println("   Room           : " + selectedRoom.getRoomNumber() + "  (Floor " + selectedRoom.getFloor() + ")");
                        System.out.println("   Check-In       : " + checkIn);
                        System.out.println("   Check-Out      : " + checkOut);
                        System.out.println("   Total Paid     : $" + String.format("%.2f", invoice.getTotalAmount()));
                        System.out.println("   Remaining Bal  : $" + String.format("%.2f", guest.getBalance()));
                        System.out.println("  ══════════════════════════════════════════");
                    }
                } else {
                    System.out.println("   Insufficient balance ($"
                            + String.format("%.2f", guest.getBalance())
                            + "). Reservation saved as PENDING. Pay at check-in.");
                    Database.saveData();
                }
            } else {
                System.out.println("   Reservation saved as PENDING. Invoice #"
                        + invoice.getInvoiceID() + " will be due at check-in.");
                Database.saveData();
            }
    
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Booking failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println(" Unexpected error: " + e.getMessage());
        }
    }
     
    //  UI / INPUT HELPERS
    //-------------------------------------------------

    // Prints a styled section divider with a title
    private static void printDivider(String title) 
    {
        int count = Math.max(0, 55 - title.length());
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < count; i++) line.append('=');
        System.out.println("\n== " + title + " " + line.toString());
    }
    
    // Prompts for a LocalDate, retries on bad format, returns null if user
    // types "0" to abort.

    private static LocalDate promptDate(String prompt, DateTimeFormatter fmt) 
    {
        while (true) {
            System.out.print("  " + prompt);
            String raw = sc.nextLine().trim();
            if (raw.equals("0")) return null;
            if (User.Datechecker(raw)) {
                return LocalDate.parse(raw, fmt);
            }
            System.out.println("   Invalid format. Use yyyy-MM-dd  (or 0 to cancel).");
        }
    }
    
    // Prompts the user to pick a room from the available list.
    // Retries on invalid input; returns null if user enters 0.
    private static Room promptRoomChoice(List<Room> available) 
    {
        while (true) {
            System.out.print("\n  Enter Room Number to book (0 to cancel): ");
            String raw = sc.nextLine().trim();
            if (raw.equals("0")) return null;
            try {
                int num = Integer.parseInt(raw);
                for (Room r : available) {
                    if (r.getRoomNumber() == num) return r;
                }
                System.out.println("   Room " + num + " is not in the available list above.");
            } catch (NumberFormatException e) {
                System.out.println("   Please enter a valid room number.");
            }
        }
    }
    
    //Prompts for an integer in [min, max].  Retries until valid
    private static int promptPositiveInt(String prompt, int min, int max) {
        while (true) {
            System.out.print("  " + prompt);
            String raw = sc.nextLine().trim();
            try {
                int val = Integer.parseInt(raw);
                if (val >= min && val <= max) return val;
                System.out.println("   Please enter a value between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("   Numbers only.");
            }
        }
    }
    

    //Prompts for a DiningPackage by number.  Retries on bad input.
    //Returns null if user enters 0 to cancel.
    private static DiningPackage promptDiningPackage() {
        while (true) {
            System.out.print("\n  Your choice (1-4, 0 to cancel): ");
            String raw = sc.nextLine().trim();
            switch (raw) {
                case "0": return null;
                case "1": return DiningPackage.BREAKFAST_ONLY;
                case "2": return DiningPackage.HALF_BOARD;
                case "3": return DiningPackage.FULL_BOARD;
                case "4": return DiningPackage.ALL_INCLUSIVE;
                default: System.out.println("   Please enter 1, 2, 3, 4, or 0 to cancel.");
            }
        }
    }



}