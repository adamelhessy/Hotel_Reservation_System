package hotel.model.users;
import hotel.core.Database;
import hotel.model.bookings.Reservation;
import hotel.model.enums.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import hotel.model.entities.*;
public class Guest extends User
{
    private double balance;
    protected static int GuestId=100;
    protected int UniqueId;
    private List<String> roomPreferences;

    public Guest(String userName, String password, UserType typeofuser, Gender theGender, String newpassword, int failedLoginAttempts, AccountStatus accountStatus, LocalDate dateOfbirth, String phoneNumber, String address, double balance, int uniqueId, Scanner input) {
        super(userName, password, typeofuser, theGender, newpassword, failedLoginAttempts, accountStatus, dateOfbirth, phoneNumber, address);
        this.balance = balance;
        this.UniqueId = uniqueId;
        this.input = input;
        this.roomPreferences = new ArrayList<>();
    }

    public Guest() {
        this.roomPreferences = new ArrayList<>();
    }

    public List<String> getRoomPreferences() {
        return roomPreferences;
    }

    public void setRoomPreferences(List<String> roomPreferences) {
        this.roomPreferences = roomPreferences;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
    public void Newbalance(double newbalance){balance=balance+newbalance;}

    public static int getGuestId() {
        return GuestId;
    }


    public static void setGuestId(int guestId) {
        GuestId = guestId;
    }

    public int getUniqueId() {
        return UniqueId;
    }

    public void setUniqueId(int uniqueId) {
        UniqueId = uniqueId;
    }

    public Scanner getInput() {
        return input;
    }

    public void setInput(Scanner input) {
        this.input = input;
    }

    private boolean phonecheck(String s) 
    {
        if (s == null || s.length() != 11) return false;

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private void printreservationdetails(int idcounter, List<Reservation> reservations) 
    {
       
        Reservation res = reservations.get(idcounter);

        System.out.println("\n=================================================");
        System.out.println("               RESERVATION DETAILS               ");
        System.out.println("=================================================");
        
        System.out.printf("%-20s : %s%n", "Guest Username", res.getGuest().getUserName());
        System.out.printf("%-20s : %s%n", "Reservation Status", res.getStatus());
        
        System.out.println("-------------------------------------------------");
        System.out.println("ROOM INFORMATION");
        System.out.printf("%-20s : %s%n", "Room Type", res.getRoom().getRoomType().getTypeName());
        System.out.printf("%-20s : %d%n", "Room Number", res.getRoom().getRoomNumber());
        System.out.printf("%-20s : %d%n", "Floor", res.getRoom().getFloor());
        
        System.out.println("-------------------------------------------------");
        System.out.println("STAY DETAILS");
        System.out.printf("%-20s : %s%n", "Check-in Date", res.getCheckinDate());
        System.out.printf("%-20s : %s%n", "Check-out Date", res.getCheckoutDate());
        
        System.out.println("-------------------------------------------------");
        System.out.println("PACKAGES & ADD-ONS");
        System.out.printf("%-20s : %s%n", "Dining Package", res.getDiningpackage());
        
        System.out.println("\n* Room Amenities:");
        
        List<Amenity> amenities = res.getRoom().getAmenities();
        if (amenities == null || amenities.isEmpty()) {
            System.out.println("  - No special amenities");
        } else {
            for (Amenity amenity : amenities) {
                System.out.println("  - " + amenity.getAmenityName());
            }
        }
        System.out.println("=================================================\n");
    }

    public void ViewReservationbyId() 
    {
        int limit =0;
        List<Reservation> reservations = Database.getReservations();

        while(limit<2) 
        {
            System.out.println("Can't find your reservation? Enter your ReservationID or 0 to exit");
            if (!input.hasNextInt()) { 
                input.next();
                System.out.println("Invalid input! Please enter a numeric ID.");
                continue;
            }

            String phone;
            int id = input.nextInt();
            input.nextLine();
            if (id == 0) return;

            while (true) 
            {
                System.out.print("Enter your Phone Number : ");
                phone = input.nextLine();

                if ((phonecheck(phone))) {
                    break;
                } else {
                    System.out.println("Invalid input! Please enter (numbers only).");
                }
            }


            for (int i = 0; i < reservations.size(); i++) 
            {
                if (reservations.get(i).getReservationID() == id && reservations.get(i).getGuest().getPhoneNumber().equals(phone)) {
                    printreservationdetails(i, reservations);
                    return;
                }
            }
                limit = limit + 1;

            if (limit < 2) {
                System.out.println("Data not found. You have " + (2 - limit) + " attempts left.");
            } 
            else 
            {
                System.out.println("Too many failed attempts. Returning to main menu.");
            }
        }
    }


    public void register()
    {
        System.out.println("\n=================================================");
        System.out.println("             GUEST REGISTRATION FORM             ");
        System.out.println("=================================================");

        List<Guest> Guestlist = Database.getGuests();
        boolean found = false;
        do{
            System.out.println("> Please enter a username :");
            UserName = input.nextLine().trim();
            for (int i = 0; i < Database.getGuests().size(); i++) {
                if (Guestlist.get(i).getUserName().equals(UserName)) {
                    System.out.println("User name is taken please try another one");
                    found = true;
                }
            }
        }while (found);

        System.out.println("\n[ Password Requirements ]");
        System.out.println("  * Minimum 8 characters");
        System.out.println("  * At least 1 number");
        System.out.println("  * At least 1 capital letter");

        System.out.print("> Enter a Password: ");
        password = input.nextLine();

        while(!passwordcheck(password))
        {
            System.out.println("\n[!] Password does not meet the requirements.");
            System.out.print("> Please try again: ");
            password = input.nextLine().trim();
            passwordcheck(password);
        }

        System.out.println("[SUCCESS] Password created successfully!\n");

        this.Typeofuser = UserType.GUEST;
        this.accountStatus = AccountStatus.ACTIVE;

        if (Database.getGuests().isEmpty())
        {
            this.UniqueId = 1000;
        }
        else
        {
            int lastGuestIndex = Database.getGuests().size() - 1;
            this.UniqueId = Database.getGuests().get(lastGuestIndex).getUniqueId() + 1;
        }

        this.failedLoginAttempts = 0;

        System.out.println("-------------------------------------------------");
        System.out.println("                 PERSONAL DETAILS                ");
        System.out.println("-------------------------------------------------");

        while (true) {
            System.out.print("> Enter Gender (Male/Female): ");
            String genderInput = input.nextLine().trim().toUpperCase();

            if (genderInput.equals("MALE") || genderInput.equals("M")) {
                this.theGender = Gender.MALE;
                break;
            } else if (genderInput.equals("FEMALE") || genderInput.equals("F")) {
                this.theGender = Gender.FEMALE;
                break;
            } else {
                System.out.println("[!] Invalid input. Please type 'Male' or 'Female'.");
            }
        }

        System.out.print("> Enter Date of Birth (YYYY-MM-DD): ");
        String userInput = input.nextLine().trim();

        if(!Datechecker(userInput)){
            System.out.println("[!] Invalid date format.");
            System.out.print("> Please re-enter (YYYY-MM-DD): ");
            userInput = input.nextLine().trim();
        }
        dateOfbirth = LocalDate.parse(userInput);

        System.out.print("> Enter Phone Number: ");
        phoneNumber = input.nextLine().trim();

        System.out.print("> Enter Home Address: ");
        address = input.nextLine().trim();

        // Save to database
        Database.getGuests().add(this);
        Database.saveData();

        System.out.println("\n=================================================");
        System.out.println("*** Registration Complete! Welcome, " + UserName + "!");
        System.out.println("*** Your Guest ID is: " + getUniqueId());
        System.out.println("=================================================\n");
    }


    public void ViewReservation(String UserName, int UserId) 
    {
        List<Reservation> allReservations = Database.getReservations();
        boolean foundAny = false;
        for (int i = 0; i < allReservations.size(); i++) 
        {
            Reservation res = allReservations.get(i);
            // Check if both Username and ID match
            if (res.getGuest().getUserName().equals(UserName) && res.getGuest().getUniqueId() == UserId) {
                printreservationdetails(i, allReservations);
                foundAny = true;
            }
        }

        if (!foundAny) 
        {
            System.out.println("No reservations found for this Guest.");
            ViewReservationbyId();
        }
    }

}





