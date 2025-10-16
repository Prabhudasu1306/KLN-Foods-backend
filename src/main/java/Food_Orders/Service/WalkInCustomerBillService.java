package Food_Orders.Service;

import Food_Orders.Entity.WalkIn_Customer_Bill;
import Food_Orders.Repository.WalkInCustomerBillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalkInCustomerBillService {

    @Autowired
    private WalkInCustomerBillRepository repository;

    public WalkIn_Customer_Bill addBill(WalkIn_Customer_Bill bill) {
        return repository.save(bill);
    }

    public List<WalkIn_Customer_Bill> addAllBills(List<WalkIn_Customer_Bill> bills) {
        return repository.saveAll(bills);
    }

    public List<WalkIn_Customer_Bill> getAllBills() {
        return repository.findAll();
    }

    public WalkIn_Customer_Bill getBillById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public String deleteBill(Long id) {
        repository.deleteById(id);
        return "Bill with id " + id + " deleted successfully!";
    }
}
