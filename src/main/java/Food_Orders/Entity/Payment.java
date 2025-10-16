package Food_Orders.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String transactionId;

    private Double amount;
    private Double totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatusEnum status;

    private String customerEmail;
    private String customerName;
    private String paymentMethod;
    private String upiTransactionId;
    private String remarks;

    private LocalDateTime timestamp;
    private LocalDateTime paymentDate;

    // Constructors
    public Payment() {}

    public Payment(String transactionId, Double amount, PaymentStatusEnum status,
                   String customerEmail, String customerName, String paymentMethod) {
        this.transactionId = transactionId;
        this.amount = amount;
        this.totalAmount = amount;
        this.status = status;
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.paymentMethod = paymentMethod;
        this.timestamp = LocalDateTime.now();
    }

    // Getters and Setters
    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Double getAmount() { return amount; }
    public void setAmount(Double amount) {
        this.amount = amount;
        this.totalAmount = amount;
    }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public PaymentStatusEnum getStatus() { return status; }
    public void setStatus(PaymentStatusEnum status) { this.status = status; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getUpiTransactionId() { return upiTransactionId; }
    public void setUpiTransactionId(String upiTransactionId) { this.upiTransactionId = upiTransactionId; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }
}