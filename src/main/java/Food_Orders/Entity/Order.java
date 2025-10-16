package Food_Orders.Entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;
    private String customerName;
    private String phoneNumber;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private String paymentStatus;
    private String transactionId;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    private List<CartItem> cartItems = new ArrayList<>();

    public Order() {
        this.orderDate = LocalDateTime.now();
    }

    public Order(String customerEmail, String customerName, String phoneNumber, Double totalAmount) {
        this();
        this.customerEmail = customerEmail;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.totalAmount = totalAmount;
        this.paymentStatus = "PENDING";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public Double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }

    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public List<CartItem> getCartItems() { return cartItems; }
    public void setCartItems(List<CartItem> cartItems) { this.cartItems = cartItems; }

    public void addCartItem(CartItem item) {
        this.cartItems.add(item);
    }
}