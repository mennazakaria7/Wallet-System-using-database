import java.math.BigDecimal;

public class MoneyRequest {
    private int id;
    private String senderUsername;
    private String recipientUsername;
    private BigDecimal amount;
    private String status;
    private String requestDate;


    public MoneyRequest(int id, String senderUsername, String recipientUsername,
                        BigDecimal amount, String status, String requestDate) {
        this.id = id;
        this.senderUsername = senderUsername;
        this.recipientUsername = recipientUsername;
        this.amount = amount;
        this.status = status;
        this.requestDate = requestDate;
    }


    public int getId() { return id; }
    public String getSenderUsername() { return senderUsername; }
    public String getRecipientUsername() { return recipientUsername; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getRequestDate() { return requestDate; }

    @Override
    public String toString() {
        return "ID: " + id +
                " | From: " + senderUsername +
                " | To: " + recipientUsername +
                " | Amount: " + amount +
                " | Status: " + status +
                " | Date: " + requestDate;
    }
}
