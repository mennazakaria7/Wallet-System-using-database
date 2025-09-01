import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

public class Controller {

    private UserDAO userDAO = new UserDAO();
    private Scanner input = new Scanner(System.in);


    public void UserMenu(User loggedInUser) throws SQLException {

        String choice = "-1";

        while(true){
            System.out.println("-----------------------------User Menu-----------------------------");
            System.out.println("press 1 to View your balance");
            System.out.println("press 2 to Change your password");
            System.out.println("press 4 to Send money to another person");
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

                case "4":
                    System.out.println("Enter the recipient username :");
                    String RecipientUesrName=input.nextLine();

                    System.out.println("Enter the amount you want send :");
                    BigDecimal amount=input.nextBigDecimal();

                    if( userDAO.SendMoneyFromOneToAnother(loggedInUser,RecipientUesrName,amount)){
                        System.out.println("money was send successfully"+ "and your current balance is "+loggedInUser.getBalance());

                    }
                    else{
                        System.out.println("Error occurred try again later");
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
            System.out.println("press 1 to register");
            System.out.println("press 2 to login into your account");
            System.out.println("press 3 to exit");
            System.out.println("-----------------------------");
            System.out.print("Enter Choice: ");
            choice = input.nextLine();

            switch (choice){
                case "1":
                    userDAO.AddUser();
                    break;

                case "2":
                    User loggedInUser = userDAO.UserLogin();
                    if(loggedInUser != null){
                        System.out.println("Login successful! Welcome, " + loggedInUser.getUsername());
                        UserMenu(loggedInUser);

                    } else {
                        System.out.println("Invalid username or password!");
                    }
                    break;

                case "3":
                    System.out.println("Exiting program...");
                    return;
            }
        }
    }
}
