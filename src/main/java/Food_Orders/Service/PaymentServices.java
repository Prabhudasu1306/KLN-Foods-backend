package Food_Orders.Service;

import Food_Orders.Entity.Payment;
import Food_Orders.Entity.PaymentStatusEnum;
import Food_Orders.Repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServices {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmailService emailService;

    public Payment addPayment(Payment payment) {
        if (payment.getTransactionId() == null) {
            payment.setTransactionId(generateTransactionId());
        }

        if (paymentRepository.existsByTransactionId(payment.getTransactionId())) {
            Payment existing = paymentRepository.findByTransactionId(payment.getTransactionId()).get();
            payment.setCustomerEmail(existing.getCustomerEmail());
            payment.setCustomerName(existing.getCustomerName());
        }

        if (payment.getTimestamp() == null) {
            payment.setTimestamp(LocalDateTime.now());
        }
        if (payment.getPaymentDate() == null) {
            payment.setPaymentDate(LocalDateTime.now());
        }

        return paymentRepository.save(payment);
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Payment updatePaymentStatus(String transactionId, PaymentStatusEnum status) {
        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transaction ID: " + transactionId));

        payment.setStatus(status);
        Payment updatedPayment = paymentRepository.save(payment);

        if (status == PaymentStatusEnum.SUCCESS && payment.getCustomerEmail() != null) {
            try {
                emailService.sendPaymentSuccessEmail(updatedPayment);
                emailService.sendInvoice(updatedPayment);
            } catch (Exception e) {
                System.err.println("Failed to send email: " + e.getMessage());
            }
        }

        return updatedPayment;
    }

    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + (int) (Math.random() * 1000);
    }

    public Payment getPaymentByTransactionId(String transactionId) {
        return paymentRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Payment not found with transaction ID: " + transactionId));
    }

    public List<Payment> getPaymentsByCustomerEmail(String customerEmail) {
        return paymentRepository.findByCustomerEmail(customerEmail);
    }
}
