import java.math.BigDecimal;
import java.sql.*;

public class MoneyRequestDAO {
    public MoneyRequest getRequestById(int id) throws SQLException {
        String sql = "SELECT * FROM money_requests WHERE idmoney_requests=?";
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new MoneyRequest(
                        rs.getInt("idmoney_requests"),
                        rs.getString("sender_username"),
                        rs.getString("recipient_username"),
                        rs.getBigDecimal("amount"),
                        rs.getString("status"),
                        rs.getString("request_date")
                );
            }
        }
        return null;
    }

    public boolean RequestMoney(String sender_username, String recipient_username, BigDecimal amount) throws SQLException {
        String sql = "INSERT INTO money_requests (sender_username, recipient_username, amount, status, request_date) " +
                "VALUES (?, ?, ?, 'PENDING', NOW())";

        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            if (sender_username.equals(recipient_username)) {
                System.out.println("You cannot request money from yourself.");
                return false;
            }

            ps.setString(1, sender_username);
            ps.setString(2, recipient_username);
            ps.setBigDecimal(3, amount);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public void respondToRequest(MoneyRequest req, String newStatus) throws SQLException {
        try (Connection connection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/wallet_schema", "root", "root")) {

            connection.setAutoCommit(false);

            if ("ACCEPTED".equalsIgnoreCase(newStatus)) {

                String checkBalanceSql = "SELECT balance FROM users WHERE user_name = ?";
                try (PreparedStatement checkStmt = connection.prepareStatement(checkBalanceSql)) {
                    checkStmt.setString(1, req.getRecipientUsername());
                    ResultSet rs = checkStmt.executeQuery();

                    if (rs.next()) {
                        BigDecimal balance = rs.getBigDecimal("balance");
                        if (balance.compareTo(req.getAmount()) < 0) {
                            System.out.println(" Insufficient balance. Cannot accept the request.");
                            return;
                        }
                    } else {
                        System.out.println(" Recipient not found.");
                        return;
                    }
                }


                String updateReqSql = "UPDATE money_requests SET status=? WHERE idmoney_requests=?";
                try (PreparedStatement ps = connection.prepareStatement(updateReqSql)) {
                    ps.setString(1, newStatus);
                    ps.setInt(2, req.getId());
                    ps.executeUpdate();
                }


                String deductSql = "UPDATE users SET balance = balance - ? WHERE user_name=?";
                try (PreparedStatement ps = connection.prepareStatement(deductSql)) {
                    ps.setBigDecimal(1, req.getAmount());
                    ps.setString(2, req.getRecipientUsername());
                    ps.executeUpdate();
                }


                String addSql = "UPDATE users SET balance = balance + ? WHERE user_name=?";
                try (PreparedStatement ps = connection.prepareStatement(addSql)) {
                    ps.setBigDecimal(1, req.getAmount());
                    ps.setString(2, req.getSenderUsername());
                    ps.executeUpdate();
                }

                connection.commit();
                System.out.println(" Request " + req.getId() + " accepted. Money transferred!");
            } else {

                String updateReqSql = "UPDATE money_requests SET status=? WHERE idmoney_requests=?";
                try (PreparedStatement ps = connection.prepareStatement(updateReqSql)) {
                    ps.setString(1, newStatus);
                    ps.setInt(2, req.getId());
                    ps.executeUpdate();
                }
                System.out.println(" Request " + req.getId() + " updated to " + newStatus);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
