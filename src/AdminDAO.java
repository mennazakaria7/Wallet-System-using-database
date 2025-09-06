import java.math.BigDecimal;
import java.sql.*;
import java.util.Date;
import java.util.Scanner;

public class AdminDAO extends UserDAO {
    private Scanner input = new Scanner(System.in);


    public void AdminRegister() throws SQLException {
        String name;

        while (true) {
            System.out.println("Enter Admin Name (unique):");
            name = input.nextLine();

            if (IsAdminNameExists(name)) {
                System.out.println("AdminName already exists, please choose another one");
            } else {
                break;
            }
        }

        System.out.println("Enter Admin Password:");
        String password = input.nextLine();

        Admin newadmin = new Admin(name, password);
        InsertAdminInDB(newadmin);
        System.out.println("Admin registered successfully!");
    }


    public boolean IsAdminNameExists(String Admin_name) throws SQLException {
        String sql = "SELECT COUNT(*) FROM admins WHERE AdminName=?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, Admin_name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                } else {
                    return false;
                }
            }
        }
    }


    public int InsertAdminInDB(Admin new_admin) throws SQLException {
        String sql = "INSERT INTO admins (AdminName,password) VALUES (?,?)";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, new_admin.GetAdminUserName());
            ps.setString(2, new_admin.GetAdminPassword());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insert failed, no rows affected.");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    new_admin.SetAdminId(id);
                    return id;
                } else {
                    throw new SQLException("Insert succeeded but no ID returned.");
                }
            }
        }
    }


    public Admin getAdminByAdminName(String name) throws SQLException {
        String sql = "SELECT * FROM admins WHERE AdminName = ?";

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, name);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("Admin_id");
                    String user_name = rs.getString("AdminName");
                    String password = rs.getString("password");

                    return new Admin(id, user_name, password);
                } else {
                    return null;
                }
            }
        }
    }


    public Admin AdminLogin() throws SQLException {
        System.out.print("Enter AdminName: ");
        String AdminName = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        Admin admin = getAdminByAdminName(AdminName);

        if (admin != null && admin.GetAdminPassword().equals(password)) {
            System.out.println("Admin login successful! Welcome, " + admin.GetAdminUserName());
            return admin;
        } else {
            System.out.println("Invalid AdminName or password!");
            return null;
        }
    }


    public void ViewAllTrnsactions()throws SQLException {


        String sql = "SELECT idmoney_requests,sender_username,recipient_username,request_date, amount,status From money_requests ";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            System.out.println("----------------------------- All Transactions -----------------------------");
            System.out.printf("%-5s  %-15s %-15s %-15s %-15s %-20s%n", "ID", "Sender", "Recipient","Amount","Status", "RequestDate");

            System.out.println("-------------------------------------------------------------------------------");
            while (rs.next()) {
                int id = rs.getInt("idmoney_requests");
                String sender = rs.getString("sender_username");
                String recipient = rs.getString("recipient_username");
                BigDecimal amount=rs.getBigDecimal("amount");
                String status = rs.getString("status");
                Date date = rs.getDate("request_date");


                System.out.printf("%-5d  %-15s %-15s %-15s %-15s %-20s%n", id, sender, recipient,amount,status, date.toString());


            }
        }
        }
        public void ViewAllRegisteredUsers () throws SQLException {
            String sql = "SELECT user_id, user_name, phone_number, balance FROM users";

            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
                 PreparedStatement ps = connection.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                System.out.println("----------------------------- All Registered Users -----------------------------");
                System.out.printf("%-5s %-15s %-15s %-15s%n", "ID", "Username", "Phone", "Balance");
                System.out.println("-------------------------------------------------------------------------------");

                while (rs.next()) {
                    int id = rs.getInt("user_id");
                    String username = rs.getString("user_name");
                    String phone = rs.getString("phone_number");
                    BigDecimal balance = rs.getBigDecimal("balance");


                    System.out.printf("%-5d %-15s %-15s %-15s%n", id, username, phone, balance);


                    viewSentRequests(username);


                    viewIncomingRequests(username);

                    System.out.println("-------------------------------------------------------------------------------");
                }
            }
        }

        public void UpdateUserAccount (String name)throws SQLException {

            System.out.println("press 1 to Update User Password");
            System.out.println("press 2 to Update User Phone Number");
            System.out.println("press 1 to Update User Balance");
            System.out.println("Enter Choice");
            String choice = input.nextLine();


            String sql = "";
            switch (choice) {

                case "1":
                    sql = "UPDATE users SET password=? WHERE user_name=?";
                    break;

                case "2":
                    sql = "UPDATE users SET phone_number=? WHERE user_name=?";
                    break;

                case "3":
                    sql = "UPDATE users SET balance=? WHERE user_name=?";
                    break;
                default:
                    System.out.println("Invalid choice.");
                    return;

            }
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                if (choice.equals("1")) {
                    System.out.print("Enter new password: ");
                    String newPassword = input.nextLine();
                    ps.setString(1, newPassword);
                } else if (choice.equals("2")) {
                    System.out.print("Enter new phone number: ");
                    String newPhone = input.nextLine();
                    ps.setString(1, newPhone);
                } else if (choice.equals("3")) {
                    System.out.print("Enter new balance: ");
                    BigDecimal newBalance = input.nextBigDecimal();
                    ps.setBigDecimal(1, newBalance);
                }

                ps.setString(2, name);
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    System.out.println("User '" + name + "' updated successfully.");
                } else {
                    System.out.println("No user found with username: " + name);
                }
            }


        }

        public void DeleteUserFromSystem (String name)throws SQLException {
            String sql = "DELETE FROM users WHERE user_name=?";
            try (Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
                 PreparedStatement ps = connection.prepareStatement(sql)) {

                ps.setString(1, name);

                int rows_affected = ps.executeUpdate();
                if (rows_affected > 0) {
                    System.out.println("User '" + name + "' deleted successfully.");
                } else {
                    System.out.println("No user found with username: " + name);
                }
            }


        }

        public String SuspendUserAccount(String name , String new_status)throws SQLException{
        String sql="UPDATE users SET status=? WHERE user_name=?";

        try(Connection connection=DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
            PreparedStatement ps=connection.prepareStatement(sql)){

            ps.setString(1,new_status);
            ps.setString(2,name);

            int rows_affected=ps.executeUpdate();

            if(rows_affected>0){
                System.out.println("Account of "+name+"became "+new_status);

                return new_status;
            }else{
                System.out.println("User Not Found ,Try Again with True value");

            }
        }
return "Activated";
        }}




