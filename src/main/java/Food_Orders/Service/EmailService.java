package Food_Orders.Service;

import Food_Orders.Entity.Payment;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendInvoice(Payment payment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("prabhudasuparusu1306@gmail.com");
            helper.setTo(payment.getCustomerEmail());
            helper.setSubject("Invoice for Your Order - KLN Food Court");

            String emailContent = buildInvoiceEmail(payment);
            helper.setText(emailContent, false);

            mailSender.send(message);
            System.out.println("✅ Invoice email sent to: " + payment.getCustomerEmail());

        } catch (Exception e) {
            System.err.println("❌ Failed to send invoice email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendPaymentSuccessEmail(Payment payment) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("prabhudasuparusu1306@gmail.com");
            helper.setTo(payment.getCustomerEmail());
            helper.setSubject("Payment Confirmed - KLN Food Court");

            String emailContent = buildPaymentSuccessEmail(payment);
            helper.setText(emailContent, false);

            mailSender.send(message);
            System.out.println("✅ Payment success email sent to: " + payment.getCustomerEmail());

        } catch (Exception e) {
            System.err.println("❌ Failed to send payment success email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Enhanced CSV attachment method for payments, categories, foods, and feedbacks
    public void sendCSVAttachment(String toEmail, String subject, String messageText, String csvData, String fileName, String type) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("prabhudasuparusu1306@gmail.com");
            helper.setTo(toEmail);
            helper.setSubject(subject);

            // Email body based on type
            String emailContent;
            if ("categories".equalsIgnoreCase(type)) {
                emailContent = buildCategoriesCSVEmailContent(messageText);
            } else if ("foods".equalsIgnoreCase(type)) {
                emailContent = buildFoodsCSVEmailContent(messageText);
            } else if ("feedbacks".equalsIgnoreCase(type)) {
                emailContent = buildFeedbacksCSVEmailContent(messageText);
            } else {
                emailContent = buildPaymentsCSVEmailContent(messageText);
            }
            helper.setText(emailContent, true);

            // Convert CSV string to bytes and attach
            byte[] csvBytes = csvData.getBytes();
            ByteArrayResource csvAttachment = new ByteArrayResource(csvBytes) {
                @Override
                public String getFilename() {
                    return fileName;
                }
            };

            helper.addAttachment(fileName, csvAttachment, "text/csv");

            mailSender.send(message);
            System.out.println("✅ CSV email sent successfully to: " + toEmail);
            System.out.println("✅ File: " + fileName + " | Type: " + type);

        } catch (Exception e) {
            System.err.println("❌ Failed to send CSV email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to send CSV email: " + e.getMessage());
        }
    }

    // Overloaded method for backward compatibility
    public void sendCSVAttachment(String toEmail, String subject, String messageText, String csvData, String fileName) {
        sendCSVAttachment(toEmail, subject, messageText, csvData, fileName, "payments");
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

    private String buildPaymentsCSVEmailContent(String customMessage) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; color: white; border-radius: 10px; }" +
                ".content { margin: 20px 0; padding: 20px; background: #f9f9f9; border-radius: 10px; }" +
                ".footer { margin-top: 20px; padding: 15px; background: #eee; border-radius: 5px; font-size: 12px; color: #666; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h2>📊 Payments Data Export</h2>" +
                "<p>KLN Food Court - Admin Report</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>" + (customMessage != null ? customMessage : "Please find attached the payments data export CSV file.") + "</p>" +
                "<p>The CSV file contains all payment transactions with details including transaction IDs, customer information, amounts, and status.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p><strong>KLN Food Court</strong><br>" +
                "Email: klnfoodcourt@gmail.com<br>" +
                "Phone: 9989123680<br>" +
                "This is an automated email from the Food Orders System.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildCategoriesCSVEmailContent(String customMessage) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; color: white; border-radius: 10px; }" +
                ".content { margin: 20px 0; padding: 20px; background: #f9f9f9; border-radius: 10px; }" +
                ".footer { margin-top: 20px; padding: 15px; background: #eee; border-radius: 5px; font-size: 12px; color: #666; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h2>📊 Categories Data Export</h2>" +
                "<p>KLN Food Court - Admin Report</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>" + (customMessage != null ? customMessage : "Please find attached the categories data export CSV file.") + "</p>" +
                "<p>The CSV file contains all categories with details including category names, descriptions, and image URLs.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p><strong>KLN Food Court</strong><br>" +
                "Email: klnfoodcourt@gmail.com<br>" +
                "Phone: 9989123680<br>" +
                "This is an automated email from the Food Orders System.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildFoodsCSVEmailContent(String customMessage) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); padding: 20px; color: white; border-radius: 10px; }" +
                ".content { margin: 20px 0; padding: 20px; background: #f9f9f9; border-radius: 10px; }" +
                ".footer { margin-top: 20px; padding: 15px; background: #eee; border-radius: 5px; font-size: 12px; color: #666; }" +
                ".features { margin: 15px 0; }" +
                ".feature-item { margin: 8px 0; padding-left: 20px; position: relative; }" +
                ".feature-item:before { content: ''; position: absolute; left: 0; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h2> Food Items Data Export</h2>" +
                "<p>KLN Food Court - Menu Management Report</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>" + (customMessage != null ? customMessage : "Please find attached the food items data export CSV file.") + "</p>" +
                "<div class='features'>" +
                "<p><strong>The CSV file contains:</strong></p>" +
                "<div class='feature-item'>Complete food items inventory</div>" +
                "<div class='feature-item'>Pricing information with GST breakdown</div>" +
                "<div class='feature-item'>Category-wise organization</div>" +
                "<div class='feature-item'>Product descriptions and images</div>" +
                "</div>" +
                "<p>You can use this data for inventory management, pricing analysis, or menu planning.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p><strong>KLN Food Court</strong><br>" +
                "Email: klnfoodcourt@gmail.com<br>" +
                "Phone: 9989123680<br>" +
                "This is an automated email from the Food Orders System.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    // NEW METHOD FOR FEEDBACKS CSV EMAIL CONTENT
    private String buildFeedbacksCSVEmailContent(String customMessage) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".header { background: linear-gradient(135deg, #FF6B6B 0%, #FF8E53 100%); padding: 20px; color: white; border-radius: 10px; }" +
                ".content { margin: 20px 0; padding: 20px; background: #f9f9f9; border-radius: 10px; }" +
                ".footer { margin-top: 20px; padding: 15px; background: #eee; border-radius: 5px; font-size: 12px; color: #666; }" +
                ".stats { background: white; padding: 15px; border-radius: 8px; margin: 15px 0; border-left: 4px solid #FF6B6B; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h2>Feedbacks Data Export</h2>" +
                "<p>KLN Food Court - Customer Feedback Report</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>" + (customMessage != null ? customMessage : "Please find attached the customer feedbacks data export CSV file.") + "</p>" +
                "<div class='stats'>" +
                "<p><strong>Report Includes:</strong></p>" +
                "<ul>" +
                "<li>All customer feedback submissions</li>" +
                "<li>Customer email addresses</li>" +
                "<li>Detailed feedback text</li>" +
                "<li>Submission timeline</li>" +
                "</ul>" +
                "</div>" +
                "<p>Use this data to analyze customer satisfaction and improve your services.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p><strong>KLN Food Court</strong><br>" +
                "Email: klnfoodcourt@gmail.com<br>" +
                "Phone: 9989123680<br>" +
                "This is an automated email from the Food Orders System.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}