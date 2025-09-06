import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Scanner;

public class User {
    private int id;
    private String username;
    private String password;
    private String phonenumber;
    private BigDecimal balance;
    private String status;

    // Constructors
    public User(String username, String password, String phonenumber, BigDecimal balance,String status) {
        this.username = username;
        this.password = password;
        this.phonenumber = phonenumber;
        this.balance = balance;
this.status=status;

    }
    public User(int id, String username, String password, String phonenumber, BigDecimal balance,String status) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.phonenumber = phonenumber;
        this.balance = balance;
        this.status=status;
    }

    public User() {

    }

    // Getters and setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public void setUsername(String username) {
        this.username=username;
    }

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String newpass) {
        this.password = newpass;
    }
    public void setStatus( String status) {
        this.status = status;
    }
    public String getStatus() {
        return status;
    }
    public String getPhoneNumber() {
        return phonenumber;
    }
    public BigDecimal getBalance() {
        return balance;
    }


    public String getPhonenumber() {
        return phonenumber;
    }
}
