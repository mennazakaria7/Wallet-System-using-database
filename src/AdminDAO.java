import java.sql.*;
import java.util.Scanner;

public class AdminDAO {
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
}
