<div align="center">

# 🏨 Hotel Reservation System

**A fully object-oriented hotel management backend built in Java**

![Java](https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![OOP](https://img.shields.io/badge/Architecture-OOP-1B3A5C?style=for-the-badge)
![Status](https://img.shields.io/badge/Milestone-1%20%E2%80%94%20Backend-2E6DA4?style=for-the-badge)
![University](https://img.shields.io/badge/Ain%20Shams%20University-Spring%202026-green?style=for-the-badge)

*OOP Course Project · Spring 2026 · Ain Shams University / University of East London*

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Class Reference](#-class-reference)
  - [Enumerations](#enumerations)
  - [Entity Classes](#entity-classes)
  - [User Hierarchy](#user-hierarchy)
  - [Staff & Operations](#staff--operations)
  - [Booking & Finance](#booking--finance)
  - [Interfaces](#interfaces)
  - [Core Infrastructure](#core-infrastructure)
- [Amenity Catalogue](#-amenity-catalogue)
- [Room Amenity Assignment](#-room-amenity-assignment)
- [Team & Task Assignment](#-team--task-assignment)
- [Dependency Map](#-dependency-map)
- [Getting Started](#-getting-started)
- [Running the Project](#-running-the-project)
- [Sample Flow](#-sample-flow)
- [Milestone Plan](#-milestone-plan)
- [Contributing](#-contributing)

---

## 🌐 Overview

The **Hotel Reservation System** is a backend-only Java application developed as a Milestone 1 deliverable for the Object-Oriented Programming course. It models the complete lifecycle of a hotel — from room inventory management and guest registration through to booking, check-in, payment, and check-out — using clean OOP principles throughout.

### Key Design Goals

| Goal | Implementation |
|------|----------------|
| **Separation of Concerns** | Entities hold data only; logic lives in `BookingEngine` |
| **Centralised State** | Single static `Database` class acts as the in-memory hub |
| **Loose Coupling** | `Manageable` and `Payable` interfaces decouple layers |
| **Testability** | `Main` runs a full end-to-end console simulation with no GUI |
| **Extensibility** | Modular package structure ready for GUI layer in Milestone 2 |

### What Milestone 1 Covers

- ✅ Complete enum library — `AccountStatus`, `DiningPackage`, `PaymentMethod`, `ReservationStatus`, `Gender`, `RoomView` **(Omar)**
- ✅ Physical entity classes — `Amenity`, `RoomType`, `Room`, `Review` **(Omar)**
- ✅ Interface contracts — `Manageable`, `Payable` **(Omar)**
- ✅ Centralised in-memory `Database` with file persistence **(Omar)**
- ✅ Folder structure, file pathing, and UML diagram **(Omar)**
- ✅ User inheritance tree — `User` → `Staff` **(Belal)**
- ✅ Administration — `Admin` **(Belal)**
- ✅ Transactional model — `Reservation`, `Invoice` **(Mostafa)**
- ✅ Staff operations — `Admin` CRUD methods, `Receptionist` **(Adam)**
- ✅ `BookingEngine` — fully implemented with contributions from all members **(see BookingEngine section)**
- ✅ System-wide debugging **(Omar, Belal, Adam)**
- ✅ `Main` console test runner **(Basel, Mostafa)**

---

## 🏗 Architecture

The system is organised into four strictly ordered tiers. Dependencies always flow **downward** — upper tiers depend on lower ones, never the reverse.

```
┌─────────────────────────────────────────────────────────────┐
│  TIER 4 — Integration & Logic                               │
│  Database  ·  BookingEngine  ·  Main                        │
├─────────────────────────────────────────────────────────────┤
│  TIER 3 — Contracts                                         │
│  <<interface>> Manageable  ·  <<interface>> Payable         │
├─────────────────────────────────────────────────────────────┤
│  TIER 2 — Domain Models                                     │
│  Guest  ·  Staff  ·  Admin  ·  Receptionist                 │
│  Reservation  ·  Invoice                                    │
├─────────────────────────────────────────────────────────────┤
│  TIER 1 — Foundation                                        │
│  Enums  ·  Amenity  ·  RoomType  ·  Room  ·  Review        │
└─────────────────────────────────────────────────────────────┘
```

### UML Diagram Highlights

- `Room` aggregates `RoomType` (1) and `List<Amenity>` (0..*) and `List<Review>` (0..*)
- `Guest` and `Staff` both extend the abstract `User`
- `Admin` extends `Staff` and **implements** `Manageable`
- `Invoice` **implements** `Payable`
- `Reservation` references `Guest`, `Room`, `DiningPackage`, and `List<Amenity>`
- `Database` holds static collections of every entity type

---

## 📁 Project Structure

```
hotel-reservation-system/
│
├── src/
│   └── hotel/
│       ├── model/
│       │   ├── enums/
│       │   │   ├── AccountStatus.java
│       │   │   ├── DiningPackage.java
│       │   │   ├── Gender.java
│       │   │   ├── PaymentMethod.java
│       │   │   ├── ReservationStatus.java
│       │   │   ├── RoomView.java
│       │   │   └── UserType.java
│       │   │
│       │   ├── entities/
│       │   │   ├── Amenity.java
│       │   │   ├── Room.java
│       │   │   ├── RoomType.java
│       │   │   └── Review.java
│       │   │
│       │   ├── users/
│       │   │   ├── User.java           ← abstract
│       │   │   ├── Guest.java
│       │   │   └── Staff.java          ← abstract
│       │   │
│       │   ├── staff/
│       │   │   ├── Admin.java
│       │   │   └── Receptionist.java
│       │   │
│       │   └── bookings/
│       │       ├── Reservation.java
│       │       ├── Invoice.java
│       │       └── PromoCode.java
│       │
│       ├── interfaces/
│       │   ├── Manageable.java
│       │   └── Payable.java
│       │
│       └── core/
│           ├── Database.java
│           ├── BookingEngine.java
│           └── Main.java
│
├── README.md
└── .gitignore
```

---

## 📚 Class Reference

### Enumerations

All enums live in `hotel.model.enums`. Defined by **Omar**.

#### `AccountStatus`
```java
public enum AccountStatus {
    ACTIVE, LOCKED
}
```
Tracks guest account state. Accounts are `LOCKED` after exceeding the failed login attempt threshold.

---

#### `DiningPackage`
```java
public enum DiningPackage {
    BREAKFAST_ONLY,
    HALF_BOARD,
    FULL_BOARD,
    ALL_INCLUSIVE;
}
```
Each value carries a per-night surcharge applied to the reservation total.

---

#### `PaymentMethod`
```java
public enum PaymentMethod {
    CASH, CREDIT_CARD, ONLINE
}
```

---

#### `ReservationStatus`
```java
public enum ReservationStatus {
    PENDING, CONFIRMED, CANCELLED, COMPLETED
}
```
Status transitions: `PENDING → CONFIRMED → COMPLETED`, or `PENDING/CONFIRMED → CANCELLED`.

---

#### `Gender`
```java
public enum Gender {
    MALE, FEMALE
}
```

---

#### `RoomView`
```java
public enum RoomView {
    SEA_VIEW, GARDEN, POOL
}
```

---

### Entity Classes

Defined by **Omar**.

#### `Amenity`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `amenityName : String` | Display name of the amenity |
| `-` | `description : String` | Short description |
| `-` | `amenityPrice : double` | Per-stay surcharge (EGP) |

Represents either a bookable facility or an in-room comfort item. There are two categories in the system — **activity amenities** (e.g. scuba diving, desert safari) that a guest adds to a reservation during booking, and **room-level amenities** (e.g. minibar, extra bed) that are pre-assigned to specific rooms by the `Database` seeder. Both are stored as `Amenity` objects; the distinction is purely contextual.

---

#### `RoomType`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `typeName : String` | e.g. "Standard Garden Oasis", "Hurghada Royal Suite" |
| `-` | `basePrice : double` | Nightly base rate (EGP) |
| `-` | `description : String` | Type description |
| `-` | `seasonMultiplier : double` | Pricing multiplier (set by Admin) |
| `-` | `roomView : RoomView` | View type for this room category |
| `-` | `maxCapacity : int` | Maximum guest occupancy |
| `+` | `getEffectivePrice() : double` | Returns `basePrice × seasonMultiplier` |

---

#### `Room`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `roomNumber : int` | Unique room identifier |
| `-` | `floor : int` | Floor number |
| `-` | `roomType : RoomType` | Associated room type |
| `-` | `amenities : List<Amenity>` | Pre-assigned room-level amenities |
| `-` | `reviews : List<Review>` | Guest reviews for this room |
| `+` | `addAmenity(Amenity) : void` | Add an amenity to this room |
| `+` | `removeAmenity(Amenity) : void` | Remove an amenity |
| `+` | `addReview(Review) : void` | Attach a guest review |
| `+` | `calculateAverageRating() : double` | Average score across all reviews |

---

#### `Review`

| Modifier | Member | Description |
|----------|--------|-------------|
| `-` | `score : int` | Numeric rating (1–5) |
| `-` | `comment : String` | Guest comment text |

---

### User Hierarchy

Defined by **Belal**.

#### `User` *(abstract)*

Base class for all system users. Cannot be instantiated directly.

| Modifier | Member |
|----------|--------|
| `-` | `userName : String` |
| `-` | `password : String` |
| `-` | `typeOfUser : UserType` |
| `-` | `theGender : Gender` |
| `+` | `Login() : void` |
| `+` | `ResetPassword(String, UserType) : void` |
| `+` | `passwordconfirmation(String, String) : void` |
| `+` | `Datechecker(String) : boolean` |

---

#### `Guest` *(extends User)*

| Modifier | Member | Notes |
|----------|--------|-------|
| `-` | `balance : double` | Debited on invoice payment |
| `-` | `address : String` | |
| `-` | `phoneNumber : String` | |
| `-` | `dateOfBirth : LocalDate` | |
| `-` | `roomPreferences : List<String>` | Preference tags |
| `-` | `failedLoginAttempts : int` | Triggers account lock |
| `-` | `accountStatus : AccountStatus` | `ACTIVE` or `LOCKED` |
| `+` | `register() : void` | Collects guest-specific fields |
| `+` | `ViewReservation(String, int) : void` | View reservations by username and ID |
| `+` | `ViewReservationbyId() : void` | Fallback lookup by reservation ID |

---

#### `Staff` *(abstract, extends User)*

| Modifier | Member |
|----------|--------|
| `-` | `workingHours : int` |
| `+` | `viewAllGuests() : void` |
| `+` | `viewAllReservations() : void` |

---

### Staff & Operations

Defined by **Adam**.

#### `Admin` *(extends Staff, implements Manageable)*

Full CRUD access over Rooms, RoomTypes, and Amenities, plus seasonal pricing.

**Room CRUD**
```java
createRoom(Room room) : void
readRoom(int roomNumber) : Room
updateRoom(int roomNumber, Room updatedRoom) : void
deleteRoom(int roomNumber) : void
```

**Amenity CRUD**
```java
createAmenity(Amenity amenity) : void
readAmenity(String name) : Amenity
updateAmenity(String name, Amenity updatedAmenity) : void
deleteAmenity(String name) : void
```

**RoomType CRUD**
```java
createRoomType(RoomType type) : void
readRoomType(String typeName) : RoomType
updateRoomType(String typeName, RoomType updatedType) : void
deleteRoomType(String typeName) : void
```

**Additional Methods**
```java
setSeasonalMultiplier(String roomType, double multiplier) : void
generateFinancialReport(int days) : void
DisplayRoomType() : void
DisplayAmenity() : void
```

---

#### `Receptionist` *(extends Staff)*

```java
manageCheckIn(int reservationId) : void       // Confirms reservation (PENDING → CONFIRMED)
manageCheckOut(int reservationId, Review) : void  // Completes reservation after payment verified
addDraftReservation(Reservation) : void
getDraftReservations() : List<Reservation>
```

---

### Booking & Finance

Defined by **Mostafa**.

#### `Reservation`

| Modifier | Member |
|----------|--------|
| `-` | `reservationID : int` |
| `-` | `guest : Guest` |
| `-` | `room : Room` |
| `-` | `checkInDate : LocalDate` |
| `-` | `checkOutDate : LocalDate` |
| `-` | `status : ReservationStatus` |
| `-` | `diningPackage : DiningPackage` |
| `-` | `requestedAmenities : List<Amenity>` |
| `-` | `cancellationPenalty : double` |
| `-` | `numChildren : int` |
| `-` | `numAdults : int` |
| `+` | `confirmreservation() : void` |
| `+` | `cancelreservation() : void` |
| `+` | `completereservation() : void` |
| `+` | `calcnights() : int` |
| `+` | `calcamenitytotal() : double` |
| `+` | `updatediningpack(DiningPackage) : void` |

> `calcnights()` uses `ChronoUnit.DAYS.between(CheckinDate, CheckoutDate)`

---

#### `Invoice` *(implements Payable)*

| Modifier | Member |
|----------|--------|
| `-` | `invoiceID : int` |
| `-` | `reservation : Reservation` |
| `-` | `isPaid : boolean` |
| `-` | `totalAmount : double` |
| `-` | `paymentMethod : PaymentMethod` |
| `-` | `paymentDate : LocalDate` |
| `-` | `appliedPromoCode : String` |
| `-` | `discountAmount : double` |
| `+` | `pay(Guest, PaymentMethod) : void` |
| `+` | `getTotal() : double` |
| `+` | `generateItemizedSummary() : String` |

> `getTotal()` returns `totalAmount`
> `pay()` deducts from `guest.balance`, sets `isPaid = true`, records method and date

---

#### `PromoCode`

| Modifier | Member |
|----------|--------|
| `-` | `code : String` |
| `-` | `discountPercentage : double` |
| `-` | `expiryDate : LocalDate` |
| `-` | `isActive : boolean` |
| `+` | `isActive() : boolean` | Returns `true` only if active and not expired |

Active promo codes seeded by the database: `HURGHADA2026` (15%), `SUMMER_SUN` (10%), `REDSEA_VIBES` (20%).

---

### Interfaces

Defined by **Omar**.

#### `Manageable`

```java
public interface Manageable {
    // Room
    void createRoom(Room room) throws Exception;
    Room readRoom(int roomNumber) throws Exception;
    void updateRoom(int roomNumber, Room updatedRoom) throws Exception;
    void deleteRoom(int roomNumber) throws Exception;

    // Amenity
    void createAmenity(Amenity amenity) throws Exception;
    Amenity readAmenity(String name) throws Exception;
    void updateAmenity(String name, Amenity updatedAmenity) throws Exception;
    void deleteAmenity(String name) throws Exception;

    // RoomType
    void createRoomType(RoomType type) throws Exception;
    RoomType readRoomType(String typeName) throws Exception;
    void updateRoomType(String typeName, RoomType updatedType) throws Exception;
    void deleteRoomType(String typeName) throws Exception;
}
```

Implemented by: `Admin`

---

#### `Payable`

```java
public interface Payable {
    void pay(Guest guest, PaymentMethod method);
    double getTotal();
}
```

Implemented by: `Invoice`

---

### Core Infrastructure

#### `Database`

Defined by **Omar**. Centralised static in-memory storage hub with file persistence. All collections are static and shared across the entire application.

```java
public class Database {
    private static List<Guest>        guests        = new ArrayList<>();
    private static List<Room>         rooms         = new ArrayList<>();
    private static List<Reservation>  reservations  = new ArrayList<>();
    private static List<Invoice>      invoices      = new ArrayList<>();
    private static List<RoomType>     roomTypes     = new ArrayList<>();
    private static List<Amenity>      amenities     = new ArrayList<>();
    private static List<Admin>        admins        = new ArrayList<>();
    private static List<Receptionist> receptionists = new ArrayList<>();
    private static List<PromoCode>    promoCodes    = new ArrayList<>();

    public static void saveData()          { ... }  // Serializes all lists to hotel_data.dat
    public static void loadData()          { ... }  // Deserializes from hotel_data.dat on startup
    public static void initializeHotelData() { ... } // Seeds the Hurghada Beach Resort demo dataset
}
```

> ⚠️ `Database` is the **only** class in the system that uses static members.
> `initializeHotelData()` runs automatically on first launch when the database file is empty.

---

#### `BookingEngine`

The `BookingEngine` contains all complex calculation and workflow logic. Each method was implemented by the team member indicated below.

```java
public class BookingEngine {

    // Room availability & filtering
    List<Room>   getAvailableRooms(LocalDate checkIn, LocalDate checkOut)          // Basel
    List<Room>   filterRooms(String roomType, RoomView roomView, double maxPrice)  // Basel
    void         viewAllRooms()                                                    // Basel
    List<Room>   sortRooms(List<Room> rooms, boolean ascending)                   // Basel
    List<String> viewAllDiningPackages()                                           // Basel
    List<String> suggestPackages(Guest guest)                                      // Basel

    // Cost calculations
    double calculateRoomCost(Room room, LocalDate in, LocalDate out)              // Basel
    double calculateDiningCost(DiningPackage packageType, int nights)             // Omar
    double calculateAmenityCost(List<Amenity> selectedAmenities)                  // Adam
    double calculateTotalReservationCost(Reservation reservation)                 // Mostafa

    // Booking & payment workflow
    double      validatePromoCode(String code)                                    // Belal
    Reservation createDraftReservation(Guest, Room, LocalDate, LocalDate,
                    DiningPackage, int, int)                                       // Omar
    boolean     confirmReservation(int reservationId, PaymentMethod method)       // Omar
    void        processCancellation(int reservationId, LocalDate cancelDate)      // Mostafa
    double      calculateCancellationPenalty(int reservationId,
                    LocalDate cancelDate)                                          // Belal
    Invoice     generateInvoice(Reservation reservation, String promoCode)        // Omar

    // Guest helpers
    List<Reservation> getReservationsForGuest(Guest guest)                        // Omar
    static void       viewAndPayInvoices(Guest guest, Scanner sc)                 // Omar

    // Financial reporting
    double calculateTotalRevenue()                                                 // Adam
    double calcualteTotalRevenue(LocalDate startDate, LocalDate endDate)          // Adam
    double calculateOccupancyPercentage()                                          // Mostafa
}
```

---

## 🛎 Amenity Catalogue

All amenities are seeded by `Database.initializeHotelData()` and stored in `Database.getAmenities()`. They are split into two conceptual categories, though both are plain `Amenity` objects at the code level.

### Activity & Experience Amenities
*(Available for guests to add during Step 4 of the booking flow)*

| Amenity | Description | Price (EGP) |
|---------|-------------|-------------|
| Scuba Diving Excursion | Guided dive in the Giftun Island coral reefs | 1,500 |
| Kitesurfing Lesson | 2-hour beginner lesson on the private beach | 1,200 |
| Desert Quad Safari | Sunset quad biking in the Eastern Desert with Bedouin tea | 900 |
| Red Sea Spa | Full body massage with Dead Sea minerals | 1,800 |
| Airport Transfer | Private VIP transfer to/from Hurghada International Airport | 600 |
| Aqua Park VIP Pass | Skip-the-line access to the giant water slides | 300 |
| Premium WiFi | High-speed internet across the resort and beach | 150 |

### In-Room Comfort Amenities
*(Pre-assigned to rooms by room tier; also selectable during booking)*

| Amenity | Description | Price (EGP) |
|---------|-------------|-------------|
| Extra Bed | Foldable single bed, ideal for an extra child or guest | 350 |
| Baby Cot | Compact crib with bedding for infants | 200 |
| Coffee Machine | Nespresso machine with a daily capsule refill | 150 |
| Stocked Minibar | Fridge stocked daily with soft drinks, water, and snacks | 400 |
| Breakfast in Bed | Full breakfast delivered to your room each morning | 500 |
| Late Check-Out | Extend check-out to 4:00 PM | 300 |
| Early Check-In | Room guaranteed ready from 9:00 AM | 300 |
| Airport Pickup | Private car pickup from Hurghada International Airport | 700 |
| Romantic Room Setup | Rose petals, candles, and a chilled bottle of sparkling juice | 800 |
| Pet-Friendly Package | Extra cleaning service and a pet welcome kit | 450 |
| Ironing Board & Iron | Full-size board with steam iron delivered on request | 100 |
| In-Room Safe Box | Programmable digital safe for valuables | 80 |

> All prices are in Egyptian Pounds (EGP) to match the resort's Hurghada setting.

---

## 🛏 Room Amenity Assignment

Rooms are seeded with amenities based on their type tier and floor. The table below summarises the assignment logic in `Database.initializeHotelData()`.

| Amenity | Garden Oasis | Deluxe Pool | Red Sea Premium | Royal Suite |
|---------|:---:|:---:|:---:|:---:|
| Premium WiFi | ✅ | ✅ | ✅ | ✅ |
| In-Room Safe Box | ✅ | ✅ | ✅ | ✅ |
| Coffee Machine | 50% chance | ✅ | ✅ | ✅ |
| Stocked Minibar | 20% chance | 50% chance | ✅ | ✅ |
| Aqua Park VIP Pass | — | ✅ | ✅ | ✅ |
| Red Sea Spa | — | — | 50% chance | ✅ |
| Breakfast in Bed | — | 20% chance | 50% chance | ✅ |
| Late Check-Out | — | 30% chance | 50% chance | ✅ |
| Early Check-In | — | — | — | ✅ |
| Romantic Room Setup | — | — | 40% chance | ✅ |
| Extra Bed | 50% chance | 40% chance | 30% chance | 50% chance |
| Kitesurfing Lesson | — | 40% chance | — | — |
| Desert Quad Safari | 50% chance | — | — | — |
| Scuba Diving Excursion | 40% chance | — | — | — |
| Ironing Board & Iron | 30% chance | — | — | — |

**Floor-based overlay (applied on top of room-type tier):**

| Floor | Extra amenity | Chance |
|-------|---------------|--------|
| Floors 3 – 5 | Baby Cot | 40% |
| Floors 3 – 5 | Pet-Friendly Package | 30% |
| Floors 1 – 2 | Ironing Board & Iron | 40% |
| Floors 1 – 2 | Airport Pickup | 30% |

> The randomness means no two floors are identical, which makes the room browser feel realistic during the demo.

---

## 👥 Team & Task Assignment

| # | Member | Domain | Responsibilities |
|---|--------|--------|-----------------|
| 01 | **Omar** | Foundation, Infrastructure & Debugging | `AccountStatus`, `DiningPackage`, `PaymentMethod`, `ReservationStatus`, `Gender`, `RoomView`, `UserType`, `Amenity`, `RoomType`, `Room`, `Review`, `Manageable`, `Payable`, `Database`, folder structure, file pathing, UML diagram · `BookingEngine`: `calculateDiningCost`, `createDraftReservation`, `confirmReservation`, `generateInvoice`, `getReservationsForGuest`, `viewAndPayInvoices` · **System-wide debugging** |
| 02 | **Belal** | User Hierarchy, Authentication & Debugging | `User` *(abstract)*, `Guest`, `Staff` *(abstract)* · `BookingEngine`: `validatePromoCode`, `calculateCancellationPenalty` · **System-wide debugging** |
| 03 | **Adam** | Administration, Operations & Debugging | `Admin` CRUD methods, `Receptionist` · `BookingEngine`: `calculateAmenityCost`, `calculateTotalRevenue` (both overloads) · **System-wide debugging** |
| 04 | **Mostafa** | Booking, Finance & Console Testing | `Reservation`, `Invoice` · `BookingEngine`: `calculateTotalReservationCost`, `calculateOccupancyPercentage`, `processCancellation` · **`Main` console test runner** |
| 05 | **Basel** | Room Logic & Console Testing | `BookingEngine`: `getAvailableRooms`, `filterRooms`, `viewAllRooms`, `sortRooms`, `suggestPackages`, `calculateRoomCost` · **`Main` console test runner** |

---

## 🔗 Dependency Map

```
Omar   ──────────────────────────────────────────────────────► (no external dependencies)
Belal  ──── depends on ──► Omar   (enums: Gender, AccountStatus, UserType, RoomType entity)
Adam   ──── depends on ──► Omar   (Room, RoomType, Amenity, Database, Manageable interface)
             depends on ──► Belal  (Staff abstract class)
Mostafa ─── depends on ──► Omar   (Room, Amenity, Database, Payable interface)
             depends on ──► Belal  (Guest class)
Basel  ──── depends on ──► Omar   (Room, RoomType, Database)
             depends on ──► Mostafa (Reservation)
```

**Recommended build order:**
1. Omar delivers all enums, entity classes, interfaces, and empty `Database`
2. Belal delivers user hierarchy (`User`, `Guest`, `Staff`)
3. Adam and Mostafa work in parallel on their entity/staff classes
4. Basel implements `BookingEngine` room-side methods
5. Omar completes remaining `BookingEngine` methods and integration
6. Basel and Mostafa collaborate on `Main` console test runner
7. Omar, Belal, and Adam perform full system debugging

---

## ⚙️ Getting Started

### Prerequisites

- **Java 17+** (uses `java.time.LocalDate`, `ArrayList`, `HashMap`)
- **IDE**: IntelliJ IDEA or Eclipse recommended

### Clone the Repository

```bash
git clone https://github.com/<your-org>/hotel-reservation-system.git
cd hotel-reservation-system
```

### Compile

```bash
# From the project root
javac -d out -sourcepath src src/hotel/core/Main.java
```

### Run

```bash
java -cp out hotel.core.Main
```

---

## ▶️ Running the Project

The `Main` class is the entry point for Milestone 1. It runs a full end-to-end simulation in the console — no GUI required. The console test runner was implemented by **Basel** and **Mostafa**.

On first launch, if `hotel_data.dat` does not exist or is empty, `Database.initializeHotelData()` seeds the full **Hurghada Beach Resort** dataset automatically:

```
[SYSTEM] Database is empty. Seeding default hotel data...
[SEEDER] Initialization complete! Hurghada resort loaded with:
  -> 50 Rooms
  -> 50 Guests
  -> 50 Reservations
  -> ~35 Invoices
  -> 20 Reviews generated.
```

**Default test credentials:**

| Role | Username | Password |
|------|----------|----------|
| Admin | `admin_ahmed` | `Admin@2026` |
| Receptionist | `rec_mahmoud` | `Desk@2026` |
| Guest | `guest_test` | `Test@123` |

---

## 🔄 Sample Flow

The full guest lifecycle covered by the console test runner:

```
1. Admin creates RoomTypes and Rooms
            │
            ▼
2. Guest registers and logs in
            │
            ▼
3. BookingEngine finds available rooms and calculates cost
            │
            ▼
4. Guest selects room, dining package, and add-on amenities
            │
            ▼
5. Guest applies promo code (optional) and confirms booking
            │
            ▼
6. Reservation created (status: PENDING), Invoice generated
            │
            ▼
7. Receptionist manages check-in (status: CONFIRMED)
            │
            ▼
8. Guest pays Invoice (balance debited, isPaid = true)
            │
            ▼
9. Receptionist manages check-out with review (status: COMPLETED)
            │
            ▼
10. Admin generates financial report
```

---

## 🤝 Contributing

### Git Workflow

```
main
 └── feature/<member>/<classname>
       e.g. feature/omar/RoomType
            feature/omar/Database
            feature/mostafa/Reservation
            feature/adam/Receptionist
            feature/basel/BookingEngine
```

1. **Branch** off `main` using the naming convention above
2. Implement your assigned class(es)
3. Ensure your code compiles with the shared interfaces/stubs from `main`
4. Open a **Pull Request** — at least one other team member must review before merging
5. Do **not** push directly to `main`

### Code Conventions

| Rule | Detail |
|------|--------|
| All fields are `private` | Use getters/setters for access |
| No `null` returns | Use `Optional<T>` or empty collections |
| Dates use `java.time.LocalDate` | Never `java.util.Date` or Strings |
| Collections use `ArrayList<T>` | Initialised in constructor, never null |
| Only `Database` uses `static` | All other classes are instance-based |
| Packages match folder structure | `hotel.model.enums`, `hotel.core`, etc. |
| All entity classes implement `Serializable` | Required for `Database` file persistence |
| All prices in EGP | Consistent with Hurghada resort context |

---

<div align="center">

*Hotel Reservation System — OOP Course Project, Spring 2026*
*Ain Shams University · University of East London*

</div>