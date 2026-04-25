package hotel.model.users;
import hotel.core.Database;
import hotel.model.enums.AccountStatus;
import hotel.model.enums.Gender;
import hotel.model.enums.UserType;
import hotel.model.staff.Admin;
import hotel.model.staff.Receptionist;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;
public abstract class User implements Serializable {
    protected transient Scanner input = new Scanner(System.in);

    protected String UserName;
    protected String password;
    protected UserType Typeofuser = null;
    protected Gender theGender;
    protected String newpassword;
    protected int failedLoginAttempts = 0;
    protected AccountStatus accountStatus;
    protected LocalDate dateOfbirth;
    protected String phoneNumber;
    protected String address;

    public User() {
    }

    public User(String userName, String password, UserType typeofuser, Gender theGender, String newpassword, int failedLoginAttempts, AccountStatus accountStatus, LocalDate dateOfbirth, String phoneNumber, String address) {
        this.UserName = userName;
        this.password = password;
        this.Typeofuser = typeofuser;
        this.theGender = theGender;
        this.newpassword = newpassword;
        this.failedLoginAttempts = failedLoginAttempts;
        this.accountStatus = accountStatus;
        this.dateOfbirth = dateOfbirth;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getUserName() {
        return this.UserName;
    }

    public void setUserName(String userName) {
        this.UserName = userName;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserType getTypeofuser() {
        return this.Typeofuser;
    }

    public void setTypeofuser(UserType typeofuser) {
        this.Typeofuser = typeofuser;
    }

    public Gender getTheGender() {
        return this.theGender;
    }

    public void setTheGender(Gender theGender) {
        this.theGender = theGender;
    }

    public String getNewpassword() {
        return this.newpassword;
    }

    public void setNewpassword(String newpassword) {
        this.newpassword = newpassword;
    }

    public int getFailedLoginAttempts() {
        return this.failedLoginAttempts;
    }

    public void setFailedLoginAttempts(int failedLoginAttempts) {
        this.failedLoginAttempts = failedLoginAttempts;
    }

    public AccountStatus getAccountStatus() {
        return this.accountStatus;
    }

    public void setAccountStatus(AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }

    public LocalDate getDateOfbirth() {
        return this.dateOfbirth;
    }

    public void setDateOfbirth(LocalDate dateOfbirth) {
        this.dateOfbirth = dateOfbirth;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public static boolean Datechecker(String dateStr) {
        try {
            LocalDate.parse(dateStr); // Default YYYY-MM-DD
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }


    public boolean passwordcheck(String Password) {
        boolean uppercase = false;
        boolean number = false;
        boolean both = false;
        for (char x : Password.toCharArray()) {
            if (Character.isUpperCase(x)) uppercase = true;
            if (Character.isDigit(x)) number = true;

            if (uppercase && number) {
                both = true;
                break;
            }
        }
        if (both) {
            if (Password.length() >= 8) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }


    public User Login(UserType expectedType) 
    {
        this.Typeofuser = expectedType;

        while (failedLoginAttempts < 5) {
            if (accountStatus == AccountStatus.LOCKED) {
                System.out.println("This account is locked. Please contact an administrator.");
                return null;
            }

            System.out.print("Enter Username: ");
            String currentName = input.nextLine().trim();
            System.out.print("Enter Password: ");
            String currentPass = input.nextLine().trim();

            if (Typeofuser == UserType.GUEST) {
                for (Guest guest : Database.getGuests()) {
                    if (guest.getUserName() != null && guest.getUserName().equals(currentName) && guest.getPassword().equals(currentPass)) {
                        if (guest.getAccountStatus() == AccountStatus.ACTIVE) {
                            System.out.println("Access Granted. Welcome, " + currentName + "!");
                            return guest; // Return the actual matched database object
                        } else {
                            System.out.println("Access Denied: Account is locked.");
                            return null;
                        }
                    }
                }
            } else if (Typeofuser == UserType.RECEPTIONIST) {
                for (Receptionist rec : Database.getReceptionists()) {
                    if (rec.getUserName() != null && rec.getUserName().equals(currentName) && rec.getPassword().equals(currentPass)) {
                        if (rec.getAccountStatus() == AccountStatus.ACTIVE) {
                            System.out.println("Access Granted. Welcome, " + currentName + "!");
                            return rec;
                        } else {
                            System.out.println("Access Denied: Account is locked.");
                            return null;
                        }
                    }
                }
            } else if (Typeofuser == UserType.ADMIN) {
                for (Admin admin : Database.getAdmins()) {
                    if (admin.getUserName() != null && admin.getUserName().equals(currentName) && admin.getPassword().equals(currentPass)) {
                        if (admin.getAccountStatus() == AccountStatus.ACTIVE) {
                            System.out.println("Access Granted. Welcome, " + currentName + "!");
                            return admin;
                        } else {
                            System.out.println("Access Denied: Account is locked.");
                            return null;
                        }
                    }
                }
            }

            System.out.println("Access Denied. Please try again!");
            failedLoginAttempts++;
            if (failedLoginAttempts >= 5) {
                for (Guest g : Database.getGuests()) {
                    if (g.getUserName().equals(currentName)) {
                        g.setAccountStatus(AccountStatus.LOCKED);
                        break;
                    }
                }
                System.out.println("Too many attempts. Account is now locked.");
                Database.saveData();
                return null;
            }

            }
            return null;
        }

    public void passwordconfirmation(String Pass1,String Pass2)
    {
        while(!(Pass1.equals(Pass2))) 
        {
            System.out.println("Password don't match,Please re-enter your password");
            Pass1=input.nextLine();
            System.out.println("Please confirm Password");
            Pass2=input.nextLine();
        }
 
    }

    public void ResetPassword(String theUserName,UserType TheUserType)
    {
        String CurrentPassword;
        int counter=0;
        System.out.println("Please re-enter your current password");
        CurrentPassword=input.nextLine();
        if(TheUserType==UserType.ADMIN) {
                boolean found = false;
                List<Admin> guestList = Database.getAdmins();
                for (int i = 0; i < Database.getAdmins().size(); i++) {
                    if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                        found = true;
                        counter = i;
                        break;
                    }
                }
                if (found) {
                    int count=0;
                    do{
                        if(count>=1){
                            System.out.println("Password didn't satisfy the rules, 1 Capital letter , 1 number");
                        }
            System.out.println("Please enter new password");
            newpassword = input.nextLine();
            System.out.println("confirm password");
            String confirmpass = input.nextLine();
            passwordconfirmation(newpassword, confirmpass);
            count=count+1;
        }
        while(!passwordcheck(newpassword));
        Admin targetAdmin = guestList.get(counter);
        targetAdmin.setPassword(newpassword);
        Database.saveData();
                } else {
                    System.out.println("The Username or password is not found,Please re-enter your username");
                    String AUsername = input.nextLine();
                    ResetPassword(AUsername, TheUserType);
                }
            }  if(TheUserType==UserType.RECEPTIONIST) {
                    boolean found = false;
                    List<Receptionist> guestList = Database.getReceptionists();
                    for (int i = 0; i < Database.getReceptionists().size(); i++) {
                        if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                            found = true;
                            counter=i;
                            break;
                        }
                    }
                    if(found){
                        int count=0;
                        do {
                            if(count>=1){
                                System.out.println("Password didn't satisfy the rules, 1 Capital letter , 1 number");
                            }
                            System.out.println("Please enter new password");
                            newpassword = input.nextLine();
                            System.out.println("confirm password");
                            String confirmpass = input.nextLine();
                            passwordconfirmation(newpassword, confirmpass);
                            count=count+1;
                        }
                        while(!passwordcheck(newpassword));
                        Receptionist targetReceptionist = guestList.get(counter);
                        targetReceptionist.setPassword(newpassword);
                        Database.saveData();
                        }

                    else {
                        System.out.println("The Username or password is not found,Please re-enter your username");
                        String AUsername=input.nextLine();
                        ResetPassword(AUsername,TheUserType);
                    }}
                if(TheUserType==UserType.GUEST) {
                    boolean found = false;
                    List<Guest> guestList = Database.getGuests();
                    for (int i = 0; i < Database.getGuests().size(); i++) {
                        if (guestList.get(i).getUserName().equals(theUserName) && guestList.get(i).getPassword().equals(CurrentPassword)) {
                            found = true;
                            counter = i;
                            break;
                        }
                    }
                    if (found) {
                        int count=0;
                        do {
                            if(count>=1){
                                System.out.println("Password didn't satisfy the rules, 1 Capital letter , 1 number");
                            }
                            System.out.println("Please enter new password");
                            newpassword = input.nextLine();
                            System.out.println("confirm password");
                            String confirmpass = input.nextLine();
                            passwordconfirmation(newpassword, confirmpass);
                            count=count+1;
                        }
                        while(!passwordcheck(newpassword));
                        Guest targetGuest = guestList.get(counter);
                        targetGuest.setPassword(newpassword);
                        Database.saveData();
                    } else {
                        System.out.println("The Username or password is not found,Please re-enter your username");
                        String AUsername = input.nextLine();
                        ResetPassword(AUsername, TheUserType);
                    }
                }
    }

}

