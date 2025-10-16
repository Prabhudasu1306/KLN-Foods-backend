package Food_Orders.Controller;

import Food_Orders.Entity.Order;
import Food_Orders.Service.OrderService;
import Food_Orders.Service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:3000")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private PdfService pdfService;


    @GetMapping("/all")
    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    @GetMapping("/byEmail")
    public ResponseEntity<?> getOrdersByEmail(@RequestParam String email) {
        try {
            List<Order> orders = orderService.getOrdersByCustomerEmail(email);
            if (orders.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No orders found for email: " + email);
            }
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error fetching orders: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/create")
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order orderDetails) {
        Order updatedOrder = orderService.updateOrder(id, orderDetails);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long id) {
        boolean deleted = orderService.deleteOrder(id);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/invoice")
    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long id) {
        try {
            Optional<Order> orderOpt = orderService.getOrderById(id);
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                byte[] pdfBytes = pdfService.generateInvoicePdf(order);

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_PDF);
                String filename = "invoice-order-" + order.getId() + ".pdf";
                headers.setContentDispositionFormData("attachment", filename);
                headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

                return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(("Error generating PDF: " + e.getMessage()).getBytes());
        }
    }

    @PutMapping("/update-payment-status")
    public ResponseEntity<Order> updatePaymentStatus(@RequestParam String transactionId,
                                                     @RequestParam String paymentStatus) {
        Order updatedOrder = orderService.updatePaymentStatus(transactionId, paymentStatus);
        return updatedOrder != null ? ResponseEntity.ok(updatedOrder) : ResponseEntity.notFound().build();
    }
}