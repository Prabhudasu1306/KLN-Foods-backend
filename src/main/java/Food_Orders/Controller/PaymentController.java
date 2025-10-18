package Food_Orders.Controller;

import Food_Orders.Entity.Payment;
import Food_Orders.Entity.PaymentStatusEnum;
import Food_Orders.Service.EmailService;
import Food_Orders.Service.PaymentServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentController {

    @Autowired
    private PaymentServices paymentServices;

    @Autowired
    private EmailService emailService;

    @GetMapping("/all")
    public List<Payment> getAllPayments() {
        return paymentServices.getAllPayments();
    }

    @GetMapping("/transaction/{transactionId}")
    public Payment getPaymentByTransactionId(@PathVariable String transactionId) {
        return paymentServices.getPaymentByTransactionId(transactionId);
    }

    @PostMapping("/add")
    public Payment addPayment(@RequestBody Payment payment) {
        return paymentServices.addPayment(payment);
    }

    @PutMapping("/transaction/{transactionId}/status")
    public Payment updateStatus(@PathVariable String transactionId, @RequestBody Map<String,String> request) {
        PaymentStatusEnum status = PaymentStatusEnum.valueOf(request.get("status"));
        return paymentServices.updatePaymentStatus(transactionId, status);
    }

    @PostMapping("/send-invoice")
    public Map<String,Object> sendInvoice(@RequestBody Payment payment) {
        Map<String,Object> response = new HashMap<>();
        try {
            emailService.sendInvoice(payment);
            response.put("success", true);
            response.put("message", "Invoice sent successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        return response;
    }
    @PostMapping("/export-csv")
    public Map<String, Object> exportCSV(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        try {
            String csvData = request.get("csvData");
            String fileName = request.get("fileName");
            String subject = request.get("subject");
            String message = request.get("message");

            // Get admin email from request or use a default
            String adminEmail = request.get("adminEmail") != null ?
                    request.get("adminEmail") : "prabhudasuparusu1306@gmail.com"; // Your admin email

            // Validate required fields
            if (csvData == null || csvData.trim().isEmpty()) {
                throw new RuntimeException("CSV data is empty");
            }

            // Send CSV via email
            emailService.sendCSVAttachment(adminEmail, subject, message, csvData, fileName);

            response.put("success", true);
            response.put("message", "CSV file sent via email successfully");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to send CSV: " + e.getMessage());
        }
        return response;
    }
}