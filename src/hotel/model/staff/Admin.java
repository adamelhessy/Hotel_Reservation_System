package hotel.model.staff;
import java.util.List;
import hotel.core.Database;
import hotel.model.bookings.Reservation;
import hotel.model.entities.*;
import hotel.model.bookings.Invoice;
import java.time.LocalDate;
import hotel.interfaces.*;
import hotel.model.enums.AccountStatus;
import hotel.model.enums.Gender;
import hotel.model.enums.ReservationStatus;
import hotel.model.enums.UserType;

public class  Admin extends Staff implements Manageable
{



    public Admin() { }

    public Admin(String userName, String password, UserType typeofuser, Gender theGender, String newpassword, int failedLoginAttempts, AccountStatus accountStatus, LocalDate dateOfbirth, String phoneNumber, String address, int workingHours) {
        super(userName, password, typeofuser, theGender, newpassword, failedLoginAttempts, accountStatus, dateOfbirth, phoneNumber, address, workingHours);

    }

    //room functions
    public void createRoom(Room room) throws Exception
    {
        List <Room> roomList = Database.getRooms();
        if (room == null) {
            throw new Exception("System Error: Attempted to create a null room.");
        }

        // 2. Validate internal room data
        if (room.getRoomNumber() <= 0) {
            throw new Exception("Invalid Data: Room number must be greater than 0.");
        }
        for(Room r:roomList)
        {
            if(r.getRoomNumber()==room.getRoomNumber())
            {
                throw new Exception("Cannot add room: Number "+r.getRoomNumber()+" is already taken");
            }
        }

        roomList.add(room);
        Database.saveData();
    }

    public Room readRoom(int roomNumber) throws Exception
    {
        if(roomNumber<=0)
        {
            throw new Exception("Invalid Data: Room number must be greater than 0");
        }
        List <Room> roomList = Database.getRooms();
        for (int i=0;i<roomList.size();i++)
        {
            Room room = roomList.get(i);
            if (room.getRoomNumber() == roomNumber)
            {
                return room;
            }
        }
        throw new Exception ("Search Error: Room "+roomNumber+" wasn't found");
    }

    public void updateRoom(int roomNumber, Room updatedRoom) throws Exception
    {
        List <Room> roomList = Database.getRooms();
        if(roomNumber<=0)
        {
            throw new Exception("Invalid Data: Room number must be greater than 0");
        }
        if(updatedRoom==null)
        {
            throw new Exception("System Error: Attempted to create a null room.");
        }
        for (int i = 0; i < roomList.size(); i++)
        {
            if (roomList.get(i).getRoomNumber() == roomNumber)
            {
                roomList.set(i, updatedRoom);
                Database.saveData();
                System.out.println("Room " + roomNumber + " updated successfully.");
                return;
            }
        }
        throw new Exception("Update Failed: Room "+ roomNumber + " does not exist in the system.");
    }

    public void deleteRoom(int roomNumber) throws Exception
    {
        List <Room> roomList = Database.getRooms();
        List < Reservation> allReservations=Database.getReservations();
        if(roomNumber<=0)
        {
            throw new Exception("Invalid Data: Room number must be greater than 0");
        }
        for (int i = 0; i < roomList.size(); i++)
        {
            if (roomList.get(i).getRoomNumber() == roomNumber)
            {
                for (Reservation res : allReservations) {
                    if (res.getRoom().getRoomNumber() == roomNumber &&
                            (res.getStatus() == ReservationStatus.CONFIRMED || res.getStatus() == ReservationStatus.PENDING)) {
                        throw new Exception("Delete Failed: Room " + roomNumber + " has an active reservation.");
                    }
                }
                roomList.remove(i);
                Database.saveData();
                System.out.println("Room " + roomNumber + " deleted successfully.");
                //should check if room is occupied if it is then the room cannot be deleted
                return;
            }
        }
        throw new Exception("Delete Failed: Room "+ roomNumber + " does not exist in the system.");
    }


    //Amenity Functions
    // Amenity Functions
    public void createAmenity(Amenity amenity) throws Exception {
        List <Amenity> amenityList = Database.getAmenities();
        if (amenity == null) {
            throw new Exception("System Error: Attempted to create a null amenity.");
        }

        for (Amenity a : amenityList) {
            if (a.getAmenityName().equalsIgnoreCase(amenity.getAmenityName())) {
                throw new Exception("Cannot add amenity: " + a.getAmenityName() + " already exists.");
            }
        }
        amenityList.add(amenity);
        Database.saveData();
    }

    public Amenity readAmenity(String name) throws Exception {
        List <Amenity> amenityList = Database.getAmenities();
        for (Amenity a : amenityList) {
            if (a.getAmenityName().equalsIgnoreCase(name)) {
                return a;
            }
        }
        throw new Exception("Search Error: Amenity '" + name + "' not found.");
    }

    public void updateAmenity(String name, Amenity updatedAmenity) throws Exception {
        List <Amenity> amenityList = Database.getAmenities();
        for (int i = 0; i < amenityList.size(); i++) {
            if (amenityList.get(i).getAmenityName().equalsIgnoreCase(name)) {
                amenityList.set(i, updatedAmenity);
                Database.saveData();
                return;
            }
        }
        throw new Exception("Update Failed: Amenity '" + name + "' does not exist.");
    }

    public void deleteAmenity(String name) throws Exception {
        List <Amenity> amenityList = Database.getAmenities();
        for (int i = 0; i < amenityList.size(); i++) {
            if (amenityList.get(i).getAmenityName().equalsIgnoreCase(name)) {
                amenityList.remove(i);
                Database.saveData();
                return;
            }
        }
        throw new Exception("Delete Failed: Amenity '" + name + "' not found.");
    }


    //RoomType
// RoomType Functions
    public void createRoomType(RoomType roomtype) throws Exception {
        List <RoomType> roomTypeList = Database.getRoomTypes();

        if (roomtype == null) {
            throw new Exception("System Error: Attempted to create a null Room Type.");
        }

        // Guard: Prevent duplicate room type names
        for (RoomType type : roomTypeList) {
            if (type.getTypeName().equalsIgnoreCase(roomtype.getTypeName())) {
                throw new Exception("Creation Failed: Room Type '" + type.getTypeName() + "' already exists.");
            }
        }

        roomTypeList.add(roomtype);
        Database.saveData();
    }

    public RoomType readRoomType(String typeName) throws Exception {
        List <RoomType> roomTypeList = Database.getRoomTypes();

        for (RoomType type : roomTypeList) {
            if (type.getTypeName().equalsIgnoreCase(typeName)) {
                return type;
            }
        }

        // Throwing instead of returning null makes the GUI catch the error
        throw new Exception("Search Error: Room Type '" + typeName + "' not found.");
    }

    public void updateRoomType(String typeName, RoomType updatedType) throws Exception {
        List <RoomType> roomTypeList = Database.getRoomTypes();

        if (updatedType == null) {
            throw new Exception("System Error: Updated data is empty.");
        }

        for (int i = 0; i < roomTypeList.size(); i++) {
            if (roomTypeList.get(i).getTypeName().equalsIgnoreCase(typeName)) {
                roomTypeList.set(i, updatedType);
                Database.saveData();
                return;
            }
        }
        throw new Exception("Update Failed: Room Type '" + typeName + "' does not exist.");
    }

    public void deleteRoomType(String typeName) throws Exception {
        List <RoomType> roomTypeList = Database.getRoomTypes();

        for (int i = 0; i < roomTypeList.size(); i++) {
            if (roomTypeList.get(i).getTypeName().equalsIgnoreCase(typeName)) {

                // SAFETY CHECK: Ensure no existing rooms are using this type
                for (Room r : Database.getRooms()) {
                    if (r.getRoomType().getTypeName().equalsIgnoreCase(typeName)) {
                        throw new Exception("Delete Failed: Cannot delete '" + typeName + "' because rooms are still assigned to it.");
                    }
                }

                roomTypeList.remove(i);
                Database.saveData();
                return;
            }
        }
        throw new Exception("Delete Failed: Room Type '" + typeName + "' not found.");
    }


    public void setSeasonalMultiplier(String roomType, double multiplier) throws Exception
    {
        List <RoomType> roomTypeList = Database.getRoomTypes();

        if (multiplier < 0) {
            throw new Exception("Invalid Data: Multiplier cannot be negative.");
        }
        if (roomTypeList == null) {
            throw new Exception("System Error: Room type database is not initialized.");
        }
        for (RoomType type : roomTypeList)
        {
            if (type.getTypeName().equalsIgnoreCase(roomType))
            {
                type.setSeasonMultiplier(multiplier);// Fix: Actually update the multiplier
                Database.saveData();
                return;
            }
        }
        throw new Exception("Update Failed: Room Type '" + roomType + "' not found.");
    }

    public void generateFinancialReport(int days) throws Exception
    {
        int cancelationc=0;
        if (days < 0) {
            throw new Exception("Invalid Input: Days cannot be negative.");
        }

        List <Invoice> invoicesList = Database.getInvoices();
        double total = 0;
        LocalDate cutoffDate = LocalDate.now().minusDays(days);

        // If there's no data, we should let the user know via exception or return
        if (invoicesList.isEmpty()) {
            throw new Exception("Report Error: No invoices found in the database.");
        }

        System.out.println("\n--- Financial Report (Last " + days + " Days) ---");
        for (Invoice inv : invoicesList) {
            Reservation res = inv.getReservation();

            // Fix: Use Check-In Date for cancellations to avoid null paymentDate
            if (res.getStatus().equals(ReservationStatus.CANCELLED)) {
                if (res.getCheckinDate().isAfter(cutoffDate) || res.getCheckinDate().isEqual(cutoffDate)) {
                    cancelationc++;
                }
            }

            // Revenue logic stays the same (since paid invoices always have a date)
            if (inv.isPaid() && (inv.getPaymentDate().isAfter(cutoffDate) || inv.getPaymentDate().isEqual(cutoffDate))) {
                total += inv.getTotalAmount();
                System.out.println("ID: " + inv.getInvoiceID() + " | Date: " + inv.getPaymentDate() + " | Amount: $" + inv.getTotalAmount());
            }
        }

        System.out.println("TOTAL REVENUE: $" + total);
        System.out.println("TOTAL RESERVATIONS CANCELED: " + cancelationc);
    }
    public void DisplayRoomType() {
        List<RoomType> roomtype = Database.getRoomTypes();
        System.out.println("\n--- RoomTypes Dashboard ---");

            System.out.println("Available:");
            for (RoomType r : roomtype)
            {
                System.out.println(" • " + r.getTypeName());
            }

    }
    public void DisplayAmenity()
    {
        System.out.println("\n--- Amenities Dashboard ---");
        // 1. Fetch the list immediately before printing
        List<Amenity> currentAmenities = Database.getAmenities();
            System.out.println("Available:");
            for (Amenity a : currentAmenities)
            {
                System.out.println(" • " + a.getAmenityName());
            }
    }
}
