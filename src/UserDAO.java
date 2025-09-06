import java.math.BigDecimal;
import java.sql.*;
import java.util.Scanner;

public class UserDAO {

    private Scanner input = new Scanner(System.in);


    public void AddUser() throws SQLException {
        String username;
        while(true){
            System.out.print("Enter username: ");
            username = input.nextLine();

            if (usernameExists(username)){
                System.out.println("Username already exists, please choose another one");
            } else {
                break;
            }
        }

        System.out.print("Enter password: ");
        String password = input.nextLine();

        System.out.print("Enter phone number: ");
        String phonenumber = input.nextLine();

        System.out.print("Enter initial balance: ");
        BigDecimal balance = input.nextBigDecimal();
        input.nextLine();

        User newUser = new User(username, password, phonenumber, balance,"Activated");

        int id = insertUser(newUser);
        System.out.println("User added successfully with ID: " + id);
    }


    public boolean usernameExists(String username) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE user_name=?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }


    public int insertUser(User newUser) throws SQLException {
        String sql = "INSERT INTO users (user_name, password, phone_number, balance, creation_date) VALUES (?, ?, ?, ?, NOW())";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newUser.getUsername());
            ps.setString(2, newUser.getPassword());
            ps.setString(3, newUser.getPhonenumber());
            ps.setBigDecimal(4, newUser.getBalance() == null ? new BigDecimal("0.00") : newUser.getBalance());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insert failed, no rows affected.");

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int id = keys.getInt(1);
                    newUser.setId(id);
                    return id;
                } else {
                    throw new SQLException("Insert succeeded but no ID returned.");
                }
            }
        }
    }


    public User getUserByUsername(String username) throws SQLException {
        String sql = "SELECT * FROM users WHERE user_name = ?";

        try (Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {

                    int id = rs.getInt("user_id");
                    String user_name = rs.getString("user_name");
                    String password = rs.getString("password");
                    String phone_number = rs.getString("phone_number");
                    BigDecimal balance = rs.getBigDecimal("balance");
                    String status = rs.getString("status");
                    return new User(id, user_name, password, phone_number, balance,status);
                } else {
                    return null;
                }
            }
        }
    }


    public BigDecimal GetUserBalance(String username) throws SQLException {
        String sql = "SELECT balance FROM users WHERE user_name=?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if(rs.next()){
                    return rs.getBigDecimal("balance");
                } else {
                    return null;
                }
            }
        }
    }


    public boolean ChangeUserPassword(User user,String newpass)throws SQLException{

        String sql="UPDATE users SET password=? WHERE user_name=?";


        try(Connection connection=DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
            PreparedStatement ps=connection.prepareStatement(sql)) {

            ps.setString(1, newpass);
            ps.setString(2, user.getUsername());

            if(ps.executeUpdate()>0){
                user.setPassword(newpass);
                return true;
            }
            else{
                return false;
            }

        }

    }




    public void viewSentRequests(String sender_username) throws SQLException {
        String sql = "SELECT idmoney_requests, recipient_username, amount, status, request_date " +
                "FROM money_requests WHERE sender_username = ?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, sender_username);
            ResultSet rs = ps.executeQuery();

            System.out.println("    ---- Sent Money Requests ----");
            boolean hasRequests = false;

            while (rs.next()) {
                hasRequests = true;
                int id = rs.getInt("idmoney_requests");
                String recipient = rs.getString("recipient_username");
                BigDecimal amount = rs.getBigDecimal("amount");
                String status = rs.getString("status");
                Timestamp date = rs.getTimestamp("request_date");
                String formattedDate = date.toLocalDateTime().toString().substring(0, 16);

                System.out.printf("%-5d %-10s %-10s %-10s %-20s%n",
                        id, recipient, amount, status, formattedDate);
            }

            if (!hasRequests) {
                System.out.println("    (No requests sent)");
            }
        }
    }


    public void viewIncomingRequests(String recipient_username) {
        String sql = "SELECT idmoney_requests, sender_username, amount, status, request_date " +
                "FROM money_requests WHERE recipient_username = ?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, recipient_username);
            ResultSet rs = ps.executeQuery();

            System.out.println("    ---- Incoming Money Requests ----");
            boolean hasRequests = false;

            while (rs.next()) {
                hasRequests = true;
                int id = rs.getInt("idmoney_requests");
                String sender = rs.getString("sender_username");
                BigDecimal amount = rs.getBigDecimal("amount");
                String status = rs.getString("status");
                Timestamp date = rs.getTimestamp("request_date");
                String formattedDate = date.toLocalDateTime().toString().substring(0, 16);

                System.out.printf("%-5d %-10s %-10s %-10s %-20s%n",
                        id, sender, amount, status, formattedDate);
            }

            if (!hasRequests) {
                System.out.println("    (No incoming requests)");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }




    public boolean SendMoneyFromOneToAnother(User sender, String recipient_username, BigDecimal amount) throws SQLException {
        String updateBalanceSQL = "UPDATE users SET balance = ? WHERE user_name = ?";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root")) {


            if (sender.getBalance().compareTo(amount) < 0) {
                System.out.println(" Insufficient balance. Transaction cancelled.");
                return false;
            }


            if (sender.getUsername().equalsIgnoreCase(recipient_username)) {
                System.out.println(" You cannot send money to yourself.");
                return false;
            }


            connection.setAutoCommit(false);

            try (
                    PreparedStatement psSender = connection.prepareStatement(updateBalanceSQL);
                    PreparedStatement psRecipient = connection.prepareStatement(updateBalanceSQL)
            ) {

                BigDecimal newSenderBalance = sender.getBalance().subtract(amount);
                psSender.setBigDecimal(1, newSenderBalance);
                psSender.setString(2, sender.getUsername());
                int rowSender = psSender.executeUpdate();


                BigDecimal recipientOldBalance = GetUserBalance(recipient_username);
                BigDecimal newRecipientBalance = recipientOldBalance.add(amount);
                psRecipient.setBigDecimal(1, newRecipientBalance);
                psRecipient.setString(2, recipient_username);
                int rowRecipient = psRecipient.executeUpdate();


                sender.setBalance(newSenderBalance);


                if (rowSender > 0 && rowRecipient > 0) {
                    connection.commit();
                    System.out.println(" Money sent successfully.");
                    return true;
                } else {
                    connection.rollback();
                    System.out.println(" Transaction failed. Rolled back.");
                    return false;
                }

            } catch (SQLException ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }


    public User UserLogin() throws SQLException {
        System.out.print("Enter username: ");
        String username = input.nextLine();

        System.out.print("Enter password: ");
        String password = input.nextLine();

        User user = getUserByUsername(username);

        if(user != null && user.getPassword().equals(password)){
            return user;
        } else {
            return null;
        }
    }
}
