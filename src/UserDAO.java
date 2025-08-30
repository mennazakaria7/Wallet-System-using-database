
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import static java.sql.DriverManager.getConnection;


public class UserDAO {

    Scanner input=new Scanner(System.in);
    public int insert_in_db(User newuser) throws SQLException {

        String sql = "INSERT INTO users (user_name, password, phone_number, balance, creation_date) VALUES (?, ?, ?, ?, NOW())";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, newuser.getUsername());
            ps.setString(2, newuser.getPassword());
            ps.setString(3, newuser.getPhoneNumber());
            ps.setBigDecimal(4, newuser.getBalance() == null ? new BigDecimal("0.00") : newuser.getBalance());

            int rows = ps.executeUpdate();
            if (rows == 0) throw new SQLException("Insert failed, no rows affected.");

            int id = -1;
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    id = keys.getInt(1);
                    newuser.setId(id);
                } else {
                    throw new SQLException("Insert succeeded but no ID returned.");
                }
            }

            return id;

        }
    }
public boolean username_exist(String username) throws SQLException {

    String sql = "SELECT COUNT(*) FROM users WHERE user_name=?";

    try (Connection connection = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");

         PreparedStatement ps = connection.prepareStatement(sql)) {

        ps.setString(1, username);

        try(ResultSet rs =ps.executeQuery()) {

if(rs.next()){
    return rs.getInt(1)>0;

}
        }
    }
    return false;
}

    public void AddUser() throws SQLException {
        String username;
        while(true){
        System.out.print("Enter username:");
        username= input.nextLine();

        if (username_exist(username)){
            System.out.println("Username already exists, please choose another one");
        }
else{
    break;
        }
    }
        System.out.print("Enter password: ");
        String password= input.nextLine();

        System.out.print("Enter phone number: ");
        String phonenumber= input.nextLine();

        System.out.print("Enter initial balance: ");
        BigDecimal balance=input.nextBigDecimal();

        User newuser=new User(username,password,phonenumber,balance);


        int id = this.insert_in_db(newuser);
        System.out.println("User added successfully with ID: " + id);
    }


    }
