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

        User newUser = new User(username, password, phonenumber, balance);

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

                    return new User(id, user_name, password, phone_number, balance);
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
