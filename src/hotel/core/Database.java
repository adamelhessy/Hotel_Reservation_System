package hotel.core;

import hotel.model.bookings.*;
import hotel.model.entities.*;
import hotel.model.enums.*;
import hotel.model.staff.*;
import hotel.model.users.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.*; 
import java.time.LocalDate;

public class Database
{
    private static List<Guest> guests = new ArrayList<>();
    private static List<Room> rooms = new ArrayList<>();
    private static List<Reservation> reservations = new ArrayList<>();
    private static List<Invoice> invoices = new ArrayList<>();
    private static List<RoomType> roomTypes = new ArrayList<>();
    private static List<Amenity> amenities = new ArrayList<>();
    private static List<Admin> admins = new ArrayList<>();
    private static List<Receptionist> receptionists = new ArrayList<>();
    private static List<PromoCode> promoCodes = new ArrayList<>();

    public static List<PromoCode> getPromoCodes() { return promoCodes; }
    public static void setPromoCodes(List<PromoCode> promoCodes) { Database.promoCodes = promoCodes; }

    private Database() {}

    // --- Basic Getters ---
    public static List<Guest> getGuests() { return guests; }
    public static List<Room> getRooms() { return rooms; }
    public static List<Reservation> getReservations() { return reservations; }
    public static List<Invoice> getInvoices() { return invoices; }
    public static List<RoomType> getRoomTypes() { return roomTypes; }
    public static List<Amenity> getAmenities() { return amenities; }
    public static List<Admin> getAdmins(){ return admins; }
    public static List<Receptionist> getReceptionists(){ return receptionists; }

    public static void setGuests(List<Guest> guests) { Database.guests = guests; }
    public static void setRooms(List<Room> rooms) { Database.rooms = rooms; }
    public static void setReservations(List<Reservation> reservations) { Database.reservations = reservations; }
    public static void setInvoices(List<Invoice> invoices) { Database.invoices = invoices; }
    public static void setRoomTypes(List<RoomType> roomTypes) { Database.roomTypes = roomTypes; }
    public static void setAmenities(List<Amenity> amenities) { Database.amenities = amenities; }
    public static void setAdmins(List<Admin> admins) { Database.admins = admins; }
    public static void setReceptionists(List<Receptionist> receptionists) { Database.receptionists = receptionists; }

    private static final String FILE_NAME = "hotel_data.dat";

    public static void saveData() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            Object[] allData = {guests, rooms, reservations, invoices, roomTypes, amenities, admins, receptionists, promoCodes};
            oos.writeObject(allData);
            System.out.println("Data saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadData() {
        File file = new File(FILE_NAME);

        if (!file.exists() || file.length() == 0) {
            System.out.println("No data found, starting with fresh lists.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object obj = ois.readObject();

            if (obj instanceof Object[]) {
                Object[] allData = (Object[]) obj;
                guests = (List<Guest>) allData[0];
                rooms = (List<Room>) allData[1];
                reservations = (List<Reservation>) allData[2];
                invoices = (List<Invoice>) allData[3];
                roomTypes = (List<RoomType>) allData[4];
                amenities = (List<Amenity>) allData[5];
                admins = (List<Admin>) allData[6];
                receptionists = (List<Receptionist>) allData[7];
                promoCodes = (List<PromoCode>) allData[8];
                System.out.println("Data loaded successfully.");
            }
        } catch (Exception e) {
            System.err.println("Load failed. Data might be corrupted. Starting fresh.");
            e.printStackTrace();
        }
    }

    public static void initializeHotelData() {
        System.out.println("[SEEDER] Initializing Hurghada Beach Resort data...");
        Random rand = new Random();

        // 1. Clear existing lists
        Database.getAmenities().clear();
        Database.getRoomTypes().clear();
        Database.getRooms().clear();
        Database.getGuests().clear();
        Database.getAdmins().clear();
        Database.getReceptionists().clear();
        Database.getPromoCodes().clear();
        Database.getReservations().clear();
        Database.getInvoices().clear();

        // 2. Generate Hurghada-Specific Amenities
        Amenity scuba = new Amenity("Scuba Diving Excursion", "Guided dive in the Giftun Island coral reefs", 1500.0);
        Amenity kiteSurf = new Amenity("Kitesurfing Lesson", "2-hour beginner lesson on the private beach", 1200.0);
        Amenity safari = new Amenity("Desert Quad Safari", "Sunset quad biking in the Eastern Desert with Bedouin tea", 900.0);
        Amenity spa = new Amenity("Red Sea Spa", "Full body massage with Dead Sea minerals", 1800.0);
        Amenity transfer = new Amenity("Airport Transfer", "Private VIP transfer to/from Hurghada International Airport", 600.0);
        Amenity aquaPark = new Amenity("Aqua Park VIP Pass", "Skip-the-line access to the giant water slides", 300.0);
        Amenity wifi = new Amenity("Premium WiFi", "High-speed internet across the resort and beach", 150.0);
        // In-room amenities
        Amenity extraBed        = new Amenity("Extra Bed",           "Foldable single bed, ideal for an extra child or guest",     350.0);
        Amenity babyСot         = new Amenity("Baby Cot",            "Compact crib with bedding for infants",                      200.0);
        Amenity coffeeMachine   = new Amenity("Coffee Machine",      "Nespresso machine with a daily capsule refill",              150.0);
        Amenity minibar         = new Amenity("Stocked Minibar",     "Fridge stocked daily with soft drinks, water, and snacks",   400.0);
        Amenity breakfastInBed  = new Amenity("Breakfast in Bed",    "Full breakfast delivered to your room each morning",         500.0);
        Amenity lateCheckout    = new Amenity("Late Check-Out",      "Extend check-out to 4:00 PM at no extra hassle",             300.0);
        Amenity earlyCheckin    = new Amenity("Early Check-In",      "Room guaranteed ready from 9:00 AM",                         300.0);
        Amenity airportPickup   = new Amenity("Airport Pickup",      "Private car pickup from Hurghada International Airport",     700.0);
        Amenity romanticSetup   = new Amenity("Romantic Room Setup", "Rose petals, candles, and a chilled bottle of sparkling juice", 800.0);
        Amenity petFriendly     = new Amenity("Pet-Friendly Package","Extra cleaning service and a pet welcome kit",               450.0);
        Amenity ironingBoard    = new Amenity("Ironing Board & Iron","Full-size board with steam iron delivered on request",        100.0);
        Amenity safeBox         = new Amenity("In-Room Safe Box",    "Programmable digital safe for valuables",                     80.0);
                
        Database.getAmenities().addAll(Arrays.asList(
            scuba, kiteSurf, safari, spa, transfer, aquaPark, wifi, extraBed, babyСot, coffeeMachine, minibar, breakfastInBed, lateCheckout, earlyCheckin, airportPickup, romanticSetup,petFriendly, ironingBoard, safeBox));

        // 3. Generate Realistic Room Types
        RoomType gardenView = new RoomType("Standard Garden Oasis", 2500.0, RoomView.GARDEN, "Comfortable room overlooking the lush resort gardens and palm trees.", 1.0, 2500.0, 2);
        RoomType poolView = new RoomType("Deluxe Pool View", 3500.0, RoomView.POOL, "Spacious room with a balcony directly facing the main heated pool.", 1.1, 3181.8, 3);
        RoomType seaView = new RoomType("Red Sea Premium", 4800.0, RoomView.SEA_VIEW, "Stunning panoramic views of the Red Sea crystal clear waters.", 1.3, 3692.3, 3);
        RoomType royalSuite = new RoomType("Hurghada Royal Suite", 12000.0, RoomView.SEA_VIEW, "Luxurious top-floor suite with a private jacuzzi overlooking the sea.", 1.5, 8000.0, 5);

        Database.getRoomTypes().addAll(Arrays.asList(gardenView, poolView, seaView, royalSuite));

            // 4. Generate 50 Rooms (5 Floors, 10 Rooms per floor)
        for (int floor = 1; floor <= 5; floor++) {
            for (int r = 1; r <= 10; r++) {
                int roomNumber = (floor * 100) + r;
                RoomType type;
                if (r <= 4)      type = gardenView;
                else if (r <= 7) type = poolView;
                else if (r <= 9) type = seaView;
                else             type = royalSuite;

                Room room = new Room(roomNumber, floor, type);

                // --- UNIVERSAL (every room gets these) ---
                room.addAmenity(wifi);
                room.addAmenity(safeBox);

                // --- ROYAL SUITE --- full luxury package
                if (type == royalSuite) {
                    room.addAmenity(spa);
                    room.addAmenity(transfer);
                    room.addAmenity(aquaPark);
                    room.addAmenity(minibar);
                    room.addAmenity(coffeeMachine);
                    room.addAmenity(breakfastInBed);
                    room.addAmenity(romanticSetup);
                    room.addAmenity(lateCheckout);
                    room.addAmenity(earlyCheckin);
                    if (rand.nextBoolean()) room.addAmenity(extraBed);
                }

                // --- RED SEA PREMIUM (sea view) ---
                else if (type == seaView) {
                    room.addAmenity(aquaPark);
                    room.addAmenity(coffeeMachine);
                    room.addAmenity(minibar);
                    if (rand.nextBoolean()) room.addAmenity(spa);
                    if (rand.nextBoolean()) room.addAmenity(breakfastInBed);
                    if (rand.nextBoolean()) room.addAmenity(lateCheckout);
                    if (rand.nextDouble() > 0.6) room.addAmenity(romanticSetup);
                    if (rand.nextDouble() > 0.7) room.addAmenity(extraBed);
                }

                // --- DELUXE POOL VIEW ---
                else if (type == poolView) {
                    room.addAmenity(aquaPark);
                    room.addAmenity(coffeeMachine);
                    if (rand.nextBoolean()) room.addAmenity(minibar);
                    if (rand.nextBoolean()) room.addAmenity(kiteSurf);
                    if (rand.nextDouble() > 0.6) room.addAmenity(extraBed);
                    if (rand.nextDouble() > 0.7) room.addAmenity(lateCheckout);
                    if (rand.nextDouble() > 0.8) room.addAmenity(breakfastInBed);
                }

                // --- STANDARD GARDEN OASIS ---
                else {
                    if (rand.nextBoolean()) room.addAmenity(coffeeMachine);
                    if (rand.nextBoolean()) room.addAmenity(extraBed);
                    if (rand.nextDouble() > 0.5) room.addAmenity(safari);
                    if (rand.nextDouble() > 0.6) room.addAmenity(scuba);
                    if (rand.nextDouble() > 0.7) room.addAmenity(ironingBoard);
                    if (rand.nextDouble() > 0.8) room.addAmenity(minibar);
                }

                // --- FAMILY TOUCHES (floors 3-5 skew family-friendly) ---
                if (floor >= 3) {
                    if (rand.nextDouble() > 0.6) room.addAmenity(babyСot);
                    if (rand.nextDouble() > 0.7) room.addAmenity(petFriendly);
                }

                // --- BUSINESS TOUCHES (floor 1-2 skew solo/business travellers) ---
                if (floor <= 2) {
                    if (rand.nextDouble() > 0.6) room.addAmenity(ironingBoard);
                    if (rand.nextDouble() > 0.7) room.addAmenity(airportPickup);
                }

                Database.getRooms().add(room);
            }
        }

        // 5. Generate Realistic Reviews (20 reviews)
        String[] reviewTexts = {
            "The coral reefs right off the hotel beach are unbelievable!",
            "Great stay, but the WiFi on the beach was a bit weak.",
            "Loved the Aqua Park! The kids had an amazing time.",
            "The sea view from the balcony made the entire trip worth it.",
            "Kitesurfing instructors were very professional.",
            "Food at the open buffet was excellent, lots of variety.",
            "Perfect weather and the heated pool was a nice touch.",
            "The desert quad safari booked through the hotel was thrilling!",
            "Room was spotless. Housekeeping does a fantastic job.",
            "The Bedouin tea during the safari was delicious.",
            "A bit crowded near the main pool, but the beach was quiet.",
            "The Red Sea Spa massage really helped me relax.",
            "Absolutely beautiful resort. We will be back next year!",
            "Great value for money. The All-Inclusive package is totally worth it.",
            "The scuba diving excursion changed my life. Beautiful marine life.",
            "Front desk staff were very welcoming and helpful.",
            "Loved waking up to the sunrise over the Red Sea.",
            "Drinks at the beach bar were fantastic.",
            "Could have more vegetarian options at dinner, but overall good.",
            "Our royal suite was beyond luxurious. Perfect honeymoon destination!"
        };

        for (int i = 0; i < 20; i++) {
            Review review = new Review((rand.nextInt(3) + 3), reviewTexts[i]); // Scores between 3 and 5
            Room randomRoom = Database.getRooms().get(rand.nextInt(Database.getRooms().size()));
            randomRoom.addReview(review);
        }

        // 6. Generate Promo Codes
        Database.getPromoCodes().add(new PromoCode("HURGHADA2026", 0.15, LocalDate.now().plusMonths(6)));
        Database.getPromoCodes().add(new PromoCode("SUMMER_SUN", 0.10, LocalDate.now().plusMonths(3)));
        Database.getPromoCodes().add(new PromoCode("REDSEA_VIBES", 0.20, LocalDate.now().plusMonths(12)));

        // 7. Generate 50 Realistic Guests (Mix of Egyptian and International)
        String[] fNames = {"Ahmed", "Elena", "John", "Klaus", "Mona", "Youssef", "Olga", "Mark", "Aya", "Dmitry", "Nour", "Sarah", "Omar", "Sophie", "Tarek"};
        String[] lNames = {"Hassan", "Ivanova", "Smith", "Muller", "Ali", "El-Sayed", "Smirnova", "Taylor", "Mansour", "Volkov", "Gaber", "Williams", "Fawzy", "Davies", "Ibrahim"};
        String[] cities = {"Cairo, Egypt", "Moscow, Russia", "London, UK", "Berlin, Germany", "Alexandria, Egypt", "Kyiv, Russia", "Manchester, UK", "Munich, Germany", "Giza, Egypt"};

        int guestIdCounter = 1000;
        for (int i = 0; i < 50; i++) {
            String fName = fNames[rand.nextInt(fNames.length)];
            String lName = lNames[rand.nextInt(lNames.length)];
            String username = (fName.toLowerCase() + "_" + lName.toLowerCase() + i);
            String phone = "+201" + (rand.nextInt(3)) + String.format("%08d", rand.nextInt(100000000));
            String city = cities[rand.nextInt(cities.length)];
            LocalDate dob = LocalDate.of(1960 + rand.nextInt(40), 1 + rand.nextInt(12), 1 + rand.nextInt(28));
            
            Guest guest = new Guest();
            guest.setUserName(username);
            guest.setPassword("Guest@123");
            guest.setTypeofuser(UserType.GUEST);
            guest.setTheGender(rand.nextBoolean() ? Gender.MALE : Gender.FEMALE);
            guest.setAccountStatus(AccountStatus.ACTIVE);
            guest.setDateOfbirth(dob);
            guest.setPhoneNumber(phone);
            guest.setAddress(city);
            guest.setBalance(20000.0 + (rand.nextDouble() * 80000)); // Balance between 20k and 100k
            guest.setUniqueId(guestIdCounter++);
            
            // Hardcode one for easy testing
            if (i == 0) {
                guest.setUserName("guest_test");
                guest.setPassword("Test@123");
            }
            Database.getGuests().add(guest);
        }

        // 8. Generate 3 Admins & 3 Receptionists
        Admin a1 = new Admin(); a1.setUserName("admin_ahmed"); a1.setPassword("Admin@2026"); a1.setTypeofuser(UserType.ADMIN); a1.setAccountStatus(AccountStatus.ACTIVE);
        Admin a2 = new Admin(); a2.setUserName("admin_sara"); a2.setPassword("Admin@2026"); a2.setTypeofuser(UserType.ADMIN); a2.setAccountStatus(AccountStatus.ACTIVE);
        Admin a3 = new Admin(); a3.setUserName("admin_tarek"); a3.setPassword("Admin@2026"); a3.setTypeofuser(UserType.ADMIN); a3.setAccountStatus(AccountStatus.ACTIVE);
        Database.getAdmins().addAll(Arrays.asList(a1, a2, a3));

        Receptionist r1 = new Receptionist(); r1.setUserName("rec_mahmoud"); r1.setPassword("Desk@2026"); r1.setTypeofuser(UserType.RECEPTIONIST); r1.setAccountStatus(AccountStatus.ACTIVE);
        Receptionist r2 = new Receptionist(); r2.setUserName("rec_yasmine"); r2.setPassword("Desk@2026"); r2.setTypeofuser(UserType.RECEPTIONIST); r2.setAccountStatus(AccountStatus.ACTIVE);
        Receptionist r3 = new Receptionist(); r3.setUserName("rec_omar"); r3.setPassword("Desk@2026"); r3.setTypeofuser(UserType.RECEPTIONIST); r3.setAccountStatus(AccountStatus.ACTIVE);
        Database.getReceptionists().addAll(Arrays.asList(r1, r2, r3));

        // 9. Generate 50 Reservations & 50 Invoices
        BookingEngine engine = new BookingEngine();
        int resId = 1;
        
        for (int i = 0; i < 50; i++) {
            Guest guest = Database.getGuests().get(rand.nextInt(Database.getGuests().size()));
            Room room = Database.getRooms().get(rand.nextInt(Database.getRooms().size()));
            
            // Dates from 30 days ago to 30 days in the future
            LocalDate checkIn = LocalDate.now().plusDays(rand.nextInt(60) - 30);
            LocalDate checkOut = checkIn.plusDays(1 + rand.nextInt(10)); // Stays of 1 to 10 nights
            
            // Hurghada resorts are mostly ALL_INCLUSIVE or HALF_BOARD
            DiningPackage[] dPackages = {DiningPackage.BREAKFAST_ONLY, DiningPackage.ALL_INCLUSIVE, DiningPackage.HALF_BOARD, DiningPackage.FULL_BOARD};
            DiningPackage dp = dPackages[rand.nextInt(dPackages.length)];
            
            Reservation res = new Reservation(resId++, guest, room, checkIn, checkOut, dp, rand.nextInt(3), 1 + rand.nextInt(2));
            
            // Add fun amenities randomly
            if (rand.nextBoolean()) res.getSelectedAmenities().add(aquaPark);
            if (rand.nextDouble() > 0.7) res.getSelectedAmenities().add(scuba);
            if (rand.nextDouble() > 0.8) res.getSelectedAmenities().add(safari);

            // Set logical status based on dates
            if (checkOut.isBefore(LocalDate.now())) {
                res.setStatus(ReservationStatus.COMPLETED);
            } else if (!checkIn.isAfter(LocalDate.now()) && !checkOut.isBefore(LocalDate.now())) {
                res.setStatus(ReservationStatus.CONFIRMED);
            } else {
                res.setStatus(rand.nextBoolean() ? ReservationStatus.CONFIRMED : ReservationStatus.PENDING);
            }
            
            Database.getReservations().add(res);

            // Generate Invoices for CONFIRMED or COMPLETED reservations
            if (res.getStatus() == ReservationStatus.COMPLETED || res.getStatus() == ReservationStatus.CONFIRMED) {
                Invoice inv = new Invoice();
                inv.setInvoiceID(Database.getInvoices().size() + 1);
                inv.setReservation(res);
                inv.setPaymentMethod(rand.nextBoolean() ? PaymentMethod.CREDIT_CARD : PaymentMethod.ONLINE);
                inv.setPaymentDate(checkIn.minusDays(rand.nextInt(5))); // Paid before arrival
                inv.setPaid(true);
                
                double rCost = engine.calculateRoomCost(room, checkIn, checkOut);
                double dCost = engine.calculateDiningCost(dp, res.calcnights());
                double aCost = engine.calculateAmenityCost(res.getSelectedAmenities());
                
                inv.setTotalAmount(rCost + dCost + aCost);
                inv.setAppliedPromoCode(rand.nextBoolean() ? "HURGHADA2026" : "NONE");
                inv.setDiscountAmount(inv.getAppliedPromoCode().equals("NONE") ? 0.0 : (inv.getTotalAmount() * 0.15));
                
                // Adjust total if promo applied
                inv.setTotalAmount(inv.getTotalAmount() - inv.getDiscountAmount());

                Database.getInvoices().add(inv);
            }
        }

        Database.saveData();
        System.out.println("[SEEDER] Initialization complete! Hurghada resort loaded with:");
        System.out.println("  -> " + Database.getRooms().size() + " Rooms");
        System.out.println("  -> " + Database.getGuests().size() + " Guests");
        System.out.println("  -> " + Database.getReservations().size() + " Reservations");
        System.out.println("  -> " + Database.getInvoices().size() + " Invoices");
        System.out.println("  -> 20 Reviews generated.");
    }
}