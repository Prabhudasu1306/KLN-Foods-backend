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
}