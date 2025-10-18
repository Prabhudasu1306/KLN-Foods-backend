package Food_Orders.Service;

import Food_Orders.Entity.PaymentDone;
import Food_Orders.Repository.PaymentDoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PaymentDoneService {

    @Autowired
    private PaymentDoneRepository paymentDoneRepository;

    public PaymentDone addPaymentDone(PaymentDone paymentDone) {
        return paymentDoneRepository.save(paymentDone);
    }

    public List<PaymentDone> getAllPaymentsDone() {
        return paymentDoneRepository.findAll();
    }

    // Add these new methods
    public List<PaymentDone> getOrdersByCustomerEmail(String email) {
        return paymentDoneRepository.findByCustomerEmailOrderByPaymentDateDesc(email);
    }

    public PaymentDone getOrderByTransactionId(String transactionId) {
        Optional<PaymentDone> order = paymentDoneRepository.findByTransactionId(transactionId);
        return order.orElse(null);
    }
}