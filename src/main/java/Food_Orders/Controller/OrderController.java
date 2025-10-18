//package Food_Orders.Controller;
//
//import Food_Orders.Entity.Order;
//import Food_Orders.Service.OrderService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/api/orders")
//@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true") // frontend URL
//public class OrderController {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private PdfService pdfService;
//
//    // Get all orders for a customer
//    @GetMapping("/email/{email}")
//    public List<Order> getOrdersByEmail(@PathVariable String email) {
//        return orderService.getOrdersByEmail(email);
//    }
//
//    // Generate PDF invoice for a specific order
//    @GetMapping("/invoice/{orderId}")
//    public ResponseEntity<byte[]> generateInvoice(@PathVariable Long orderId) {
//        Optional<Order> order = Optional.ofNullable(orderService.getOrderById(orderId));
//
//        if (order == null) {
//            return ResponseEntity.notFound().build();
//        }
//
//        byte[] pdfBytes = pdfService.generateInvoicePdf(order);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_PDF);
//        headers.setContentDispositionFormData("attachment", "invoice-" + order.get() + ".pdf");
//
//        return ResponseEntity.ok().headers(headers).body(pdfBytes);
//    }
//}
