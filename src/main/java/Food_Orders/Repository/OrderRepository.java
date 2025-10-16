package Food_Orders.Repository;

import Food_Orders.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByCustomerEmail(String customerEmail);

    @Query("SELECT o FROM Order o WHERE o.transactionId = :transactionId")
    Order findByTransactionId(@Param("transactionId") String transactionId);

    List<Order> findByPaymentStatus(String paymentStatus);

    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllByOrderByOrderDateDesc();
}