package Food_Orders.Repository;

import Food_Orders.Entity.WalkIn_Customer_Bill;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalkInCustomerBillRepository extends JpaRepository<WalkIn_Customer_Bill,Long> {
}
