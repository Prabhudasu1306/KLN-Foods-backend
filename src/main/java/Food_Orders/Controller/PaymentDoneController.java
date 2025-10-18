package Food_Orders.Controller;

import Food_Orders.Entity.PaymentDone;
import Food_Orders.Service.PaymentDoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/paymentdone")
@CrossOrigin(origins = "http://localhost:3000")
public class PaymentDoneController {

    @Autowired
    private PaymentDoneService paymentDoneService;

    @PostMapping("/add")
    public ResponseEntity<?> addPaymentDone(@RequestBody PaymentDone paymentDone) {
        try {
            PaymentDone saved = paymentDoneService.addPaymentDone(paymentDone);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error saving PaymentDone record: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PaymentDone>> getAllPaymentsDone() {
        return ResponseEntity.ok(paymentDoneService.getAllPaymentsDone());
    }

    // Add this new endpoint to get orders by customer email
    @GetMapping("/customer/{email}")
    public ResponseEntity<?> getOrdersByCustomerEmail(@PathVariable String email) {
        try {
            List<PaymentDone> orders = paymentDoneService.getOrdersByCustomerEmail(email);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error fetching orders: " + e.getMessage());
        }
    }

    // Add this endpoint to generate invoice
    @GetMapping("/invoice/{transactionId}")
    public ResponseEntity<?> generateInvoice(@PathVariable String transactionId) {
        try {
            PaymentDone order = paymentDoneService.getOrderByTransactionId(transactionId);
            if (order == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Error generating invoice: " + e.getMessage());
        }
    }
}