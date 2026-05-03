package hotel.model.staff;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import hotel.core.Database;
import hotel.model.entities.Review;
import hotel.model.enums.*;
import hotel.model.bookings.Invoice;
import hotel.model.bookings.Reservation;


public class Receptionist extends Staff {
    private List<Reservation> draftReservations;

    public Receptionist() {
        this.draftReservations = new ArrayList<>();
    }

    public Receptionist(String userName, String password, UserType typeofuser, Gender theGender, String newpassword, int failedLoginAttempts, AccountStatus accountStatus, LocalDate dateOfbirth, String phoneNumber, String address, int workingHours ) {
        super(userName, password, typeofuser, theGender, newpassword, failedLoginAttempts, accountStatus, dateOfbirth, phoneNumber, address, workingHours);
        this.draftReservations = new ArrayList<>();
    }

    public List<Reservation> getDraftReservations() {
        return draftReservations;
    }

    public void addDraftReservation(Reservation reservation) {
        this.draftReservations.add(reservation);
    }
    public void manageCheckIn(int reservationID, Scanner sc)
    {
        List<Reservation> reservations = Database.getReservations();
        List<Invoice> invoiceList = Database.getInvoices();
        for (Reservation res : reservations) {
            if (res.getReservationID() == reservationID)
            {
                if (res.getStatus() != ReservationStatus.PENDING) {
                    System.out.println("Check-in failed: Reservation is " + res.getStatus() + ", not PENDING.");
                    return;
                }
                for(int j=0;j<Database.getInvoices().size();j++) {
                    if (invoiceList.get(j).getReservation().getReservationID() == reservationID) {
                        if (invoiceList.get(j).getPaymentMethod() == PaymentMethod.CASH) {
                            System.out.println(invoiceList.get(j).getTotalAmount() + " $ must be payed cash");
                            System.out.println("1.confirm ,2. cancel reservation");
                            int choice= sc.nextInt();
                            while(choice !=1 && choice!=2){
                                System.out.println("Invalid input ,please try again");
                                choice=sc.nextInt();
                            }
                            if(choice ==2){
                                res.setStatus(ReservationStatus.CANCELLED);
                                Database.saveData();
                                return;
                            }else{
                                res.confirmreservation();
                                Database.saveData();
                                System.out.println("Reservation #" + reservationID + " confirmed. Guest checked in.");
                                return;


                            }

                        }else if (!invoiceList.get(j).isPaid()) {
                        System.out.println("No amount of money is paid, reservation will be cancelled");
                        res.setStatus(ReservationStatus.CANCELLED);
                        return;
                        }else{
                            res.confirmreservation();
                            Database.saveData();
                            System.out.println("Reservation #" + reservationID + " confirmed. Guest checked in.");
                            return;
                        }
                    }
                }
                System.out.println("This reservation doesn't has an invoice");
                return;
            }
        }
        System.out.println("Reservation ID not found.");
    }

    public void manageCheckOut(int reservationID , Review review) throws Exception {
        List<Invoice> invoices = Database.getInvoices();
        Invoice targetInvoice= null;
        
        for (Invoice inv : invoices) {
            if (inv.getReservation()!=null&&inv.getReservation().getReservationID() == reservationID ) {
                targetInvoice = inv;
                break;
            }
        }
        if (review != null) {

        }
        if (targetInvoice == null) {
            throw new Exception("Checkout Failed: No invoice found for ID " + reservationID);
        }
        else if(targetInvoice.isPaid()==false)
        {
            throw new Exception("Checkout Denied: Invoice must be settled before checkout.");
        }

            targetInvoice.getReservation().setStatus(ReservationStatus.COMPLETED);
            targetInvoice.getReservation().setCheckoutDate(LocalDate.now());
            targetInvoice.getReservation().getRoom().addReview(review);
            System.out.println("Check-out complete..... ");

            Database.saveData();
    }
}