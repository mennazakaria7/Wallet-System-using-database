import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

public class Controller {

    private UserDAO userDAO = new UserDAO();
    private Scanner input = new Scanner(System.in);
    private AdminDAO adminDAO = new AdminDAO();

    public void UserMenu(User loggedInUser) throws SQLException {

        String choice = "-1";

        while(true){
            System.out.println("-----------------------------User Menu-----------------------------");
            System.out.println("press 1 to View your balance");
            System.out.println("press 2 to Change your password");
            System.out.println("press 3 to request money from specific person:");
            System.out.println("press 4 to Send money to another person");
            System.out.println("press 5 to View incoming requests");
            System.out.println("press 6 to View sent requests");
            System.out.println("press 0 to logout");
            System.out.println("-----------------------------");

            choice = input.nextLine().trim();
            while(choice.isEmpty()){
                choice = input.nextLine().trim();
            }


            switch (choice){
                case "1":
                    BigDecimal balance = userDAO.GetUserBalance(loggedInUser.getUsername());
                    System.out.println("Your balance: " + balance);
                    break;

                case "2":
                    System.out.println("Enter New Password:");
                    String newpass=input.nextLine();

                  if(userDAO.ChangeUserPassword(loggedInUser,newpass)){

                      System.out.println("Password updated to: " + loggedInUser.getPassword());

                  }
                  else{
                      System.out.println("Password not changed");

                  }
                    break;

                case "3":
                    if(loggedInUser.getStatus().equalsIgnoreCase("Activated")){
                    System.out.println("Enter the sender username( who will send you monet) :");
                    String SendrUesrName=input.nextLine();


                    System.out.println("Enter the amount you want request :");
                    BigDecimal requested_amount=input.nextBigDecimal();

                 userDAO.RequestMoney(loggedInUser.getUsername(),SendrUesrName,requested_amount);
                    }
              else{
                        System.out.println("Your Account Not Activated yet");
                    }



                    break;

                case "4":

                    if(loggedInUser.getStatus().equalsIgnoreCase("Activated")) {
                        System.out.println("Enter the recipient username :");
                        String RecipientUesrName = input.nextLine();

                        System.out.println("Enter the amount you want send :");
                        BigDecimal amount = input.nextBigDecimal();

                        if (userDAO.SendMoneyFromOneToAnother(loggedInUser, RecipientUesrName, amount)) {
                            System.out.println("money was send successfully" + "and your current balance is " + loggedInUser.getBalance());

                        } else {
                            System.out.println("Error occurred try again later");
                        }
                    }
                    else{
                        System.out.println("Your Account Not Activated yet");
                    }

                    break;
                case "5":
                    if(loggedInUser.getStatus().equalsIgnoreCase("Activated")) {
                        userDAO.viewIncomingRequests(loggedInUser.getUsername());
                    }
                    else{
                        System.out.println("Your Account Not Activated Yet");
                    }
                    break;
                case "6":
                    if(loggedInUser.getStatus().equalsIgnoreCase("Activated")) {
                    userDAO.viewSentRequests(loggedInUser.getUsername());
                    }
                    else{
                        System.out.println("Your Account Not Activated Yet");
                    }
                    break;



                case "0":
                    return;
            }
        }
    }


    public void MainMenu() throws SQLException {

        String choice = "-1";

        while(true){
            System.out.println("-----------------------------Main Menu-----------------------------");

            System.out.println("YOUR TYPE ADMIN || USER CHOOSE");
            String type=input.nextLine();

            System.out.println("press 1 to register");
            System.out.println("press 2 to login into your account");
            System.out.println("press 0 to exit");


            System.out.println("-----------------------------");
            System.out.print("Enter Choice: ");
            choice = input.nextLine();

            switch (choice){
                case "1":

                if(type.equalsIgnoreCase("Admin")) {
                    adminDAO.AdminRegister();

                }
                   else{ userDAO.AddUser();}

                    break;

                case "2":

                        if(type.equalsIgnoreCase("Admin")) {
                            Admin loggedInAdmin = adminDAO.AdminLogin();
                            if (loggedInAdmin != null) {
                                AdminMenu(loggedInAdmin);

                    }}
                    else{

                    User loggedInUser = userDAO.UserLogin();
                    if(loggedInUser != null){
                        System.out.println("Login successful! Welcome, " + loggedInUser.getUsername());
                        UserMenu(loggedInUser);

                    } else {
                        System.out.println("Invalid username or password!");
                    }
                    }
                    break;

                case "0":
                    System.out.println("Exiting program...");
                    System.exit(0);
                    break;

            }
    }
}

    private void AdminMenu(Admin loggedInAdmin) throws SQLException {

        String choice = "-1";

        while (true) {
            System.out.println("-----------------------------Admin Menu-----------------------------");


            System.out.println("press 1 to  View All Registered Users");
            System.out.println("press 2 to  View All Transactions");
            System.out.println("press 3 to  Add Anew User To The System");
            System.out.println("press 4 to  Delete Specific Uesr From The System");
            System.out.println("press 5 to Update User Info");
            System.out.println("press 6 to Suspend user account (activate||disactivate)");
            System.out.println("press 0 to  logout");
            System.out.println("-----------------------------");
            System.out.print("Enter Choice: ");
            choice = input.nextLine();

            switch (choice) {
                case "1":

                    adminDAO.ViewAllRegisteredUsers();

                    break;

                case"2":
                    adminDAO.ViewAllTrnsactions();
                    break;


                case"3":
                    adminDAO.AddUser();

                    break;

                case "4":

                    System.out.println("Enter username of the account you want delete permanently:");
                    String name=input.nextLine();
                    adminDAO.DeleteUserFromSystem(name);
                    break;


                case"5":
                    System.out.println("Enter username of the account you want delete permanently:");
                    String username=input.nextLine();
                     adminDAO.UpdateUserAccount(username);
                    break;

                case"6":
                    System.out.println("Enter Username Of The Account You Want Change Their Activation Status");
                    String uname=input.nextLine();
                    System.out.println("Enter New Status");
                    String status=input.nextLine();
                    adminDAO.SuspendUserAccount(uname,status);

                    break;
                case"0":
                   return;


            }
        }

    }



    }