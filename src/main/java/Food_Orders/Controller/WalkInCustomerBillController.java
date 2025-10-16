package Food_Orders.Controller;

import Food_Orders.Entity.WalkIn_Customer_Bill;
import Food_Orders.Service.WalkInCustomerBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/walkin")
@CrossOrigin(origins = "http://localhost:3000")
public class WalkInCustomerBillController {

    @Autowired
    private WalkInCustomerBillService billService;

    @PostMapping("/add")
    public WalkIn_Customer_Bill addBill(@RequestBody WalkIn_Customer_Bill bill) {
        return billService.addBill(bill);
    }

    // Add multiple bills at once
//    @PostMapping("/addAll")
//    public List<WalkIn_Customer_Bill> addAllBills(@RequestBody List<WalkIn_Customer_Bill> bills) {
//        return billService.addAllBills(bills);
//    }

    @GetMapping("/all")
    public List<WalkIn_Customer_Bill> getAllBills() {
        return billService.getAllBills();
    }

    @GetMapping("/{id}")
    public WalkIn_Customer_Bill getBillById(@PathVariable Long id) {
        return billService.getBillById(id);
    }

    @DeleteMapping("/{id}")
    public String deleteBill(@PathVariable Long id) {
        return billService.deleteBill(id);
    }
}
