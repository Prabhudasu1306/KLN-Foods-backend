package Food_Orders.Service;

import Food_Orders.Entity.CartItem;
import Food_Orders.Entity.Order;
import Food_Orders.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    // Create new order
    public Order createOrder(Order order) {
        order.setOrderDate(LocalDateTime.now());
        if (order.getPaymentStatus() == null) {
            order.setPaymentStatus("PENDING");
        }
        return orderRepository.save(order);
    }

    // Get all orders
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByOrderDateDesc();
    }

    // Get order by ID
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    // Get orders by customer email
    public List<Order> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }

    // Get order by transaction ID
    public Order getOrderByTransactionId(String transactionId) {
        return orderRepository.findByTransactionId(transactionId);
    }

    // Update order
    public Order updateOrder(Long id, Order orderDetails) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setCustomerName(orderDetails.getCustomerName());
            order.setCustomerEmail(orderDetails.getCustomerEmail());
            order.setPhoneNumber(orderDetails.getPhoneNumber());
            order.setTotalAmount(orderDetails.getTotalAmount());
            order.setPaymentStatus(orderDetails.getPaymentStatus());
            order.setCartItems(orderDetails.getCartItems());
            return orderRepository.save(order);
        }
        return null;
    }

    // Update payment status
    public Order updatePaymentStatus(String transactionId, String paymentStatus) {
        Order order = orderRepository.findByTransactionId(transactionId);
        if (order != null) {
            order.setPaymentStatus(paymentStatus);
            return orderRepository.save(order);
        }
        return null;
    }

    // Delete order
    public boolean deleteOrder(Long id) {
        if (orderRepository.existsById(id)) {
            orderRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // Create order from cart items and payment
    public Order createOrderFromPayment(String customerEmail, String customerName, String phoneNumber,
                                        Double totalAmount, String transactionId, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerEmail(customerEmail);
        order.setCustomerName(customerName);
        order.setPhoneNumber(phoneNumber);
        order.setTotalAmount(totalAmount);
        order.setTransactionId(transactionId);
        order.setPaymentStatus("SUCCESS");
        order.setCartItems(cartItems);

        // Set the order reference for each cart item
//        for (CartItem item : cartItems) {
//            item.setOrder(order);
//        }

        return orderRepository.save(order);
    }
}