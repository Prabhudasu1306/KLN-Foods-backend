package Food_Orders.Repository;

import Food_Orders.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByCustomerEmail(String customerEmail);
    boolean existsByTransactionId(String transactionId);
    List<Payment> findByStatus(String status);
}