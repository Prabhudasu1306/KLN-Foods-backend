package Food_Orders.Repository;

import Food_Orders.Entity.PaymentDone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentDoneRepository extends JpaRepository<PaymentDone, Long> {

    // Add this method to find orders by customer email
    List<PaymentDone> findByCustomerEmailOrderByPaymentDateDesc(String customerEmail);

    // Add this method to find order by transaction ID
    Optional<PaymentDone> findByTransactionId(String transactionId);
}