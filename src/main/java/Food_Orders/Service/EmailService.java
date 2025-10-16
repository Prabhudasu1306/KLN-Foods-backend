package Food_Orders.Service;

import Food_Orders.Entity.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendInvoice(Payment payment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("prabhudasuparusu1306@gmail.com");
            message.setTo(payment.getCustomerEmail());
            message.setSubject("Invoice for Your Order - KLN Food Court");

            String emailContent = buildInvoiceEmail(payment);
            message.setText(emailContent);

            mailSender.send(message);
            System.out.println(" Invoice email sent to: " + payment.getCustomerEmail());

        } catch (Exception e) {
            System.err.println(" Failed to send invoice email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPaymentSuccessEmail(Payment payment) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("prabhudasuparusu1306@gmail.com");
            message.setTo(payment.getCustomerEmail());
            message.setSubject("Payment Confirmed - KLN Food Court");

            String emailContent = buildPaymentSuccessEmail(payment);
            message.setText(emailContent);

            mailSender.send(message);
            System.out.println(" Payment success email sent to: " + payment.getCustomerEmail());

        } catch (Exception e) {
            System.err.println("Failed to send payment success email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildInvoiceEmail(Payment payment) {
        return String.format(
                "Dear %s,\n\n" +
                        "Thank you for your order at KLN Food Court!\n\n" +
                        "INVOICE DETAILS:\n" +
                        "================\n" +
                        "Transaction ID: %s\n" +
                        "Order Date: %s\n" +
                        "Total Amount: ₹%.2f\n" +
                        "Payment Method: %s\n" +
                        "Status: PAYMENT CONFIRMED \n\n" +
                        "Your payment has been successfully processed.\n\n" +
                        "We hope you enjoy your meal! 🍽️\n\n" +
                        "Best regards,\n" +
                        "KLN Food Court\n" +
                        "Email: klnfoodcourt@gmail.com\n" +
                        "Phone: 9989123680",
                payment.getCustomerName() != null ? payment.getCustomerName() : "Valued Customer",
                payment.getTransactionId(),
                payment.getPaymentDate() != null ? payment.getPaymentDate().toString() : "Recent",
                payment.getTotalAmount(),
                payment.getPaymentMethod()
        );
    }

    private String buildPaymentSuccessEmail(Payment payment) {
        return String.format(
                "Dear %s,\n\n" +
                        "Your payment has been successfully processed!\n\n" +
                        "PAYMENT DETAILS:\n" +
                        "================\n" +
                        "Transaction ID: %s\n" +
                        "Amount: ₹%.2f\n" +
                        "Payment Method: %s\n" +
                        "Status: APPROVED BY ADMIN \n\n" +
                        "Thank you for choosing KLN Food Court!\n\n" +
                        "Best regards,\n" +
                        "KLN Food Court Team",
                payment.getCustomerName() != null ? payment.getCustomerName() : "Valued Customer",
                payment.getTransactionId(),
                payment.getTotalAmount(),
                payment.getPaymentMethod()
        );
    }
}