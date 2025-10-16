package Food_Orders.Repository;

import Food_Orders.Entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByEmail(String email);
    List<Address> findByEmailAndHouseNumber(String email, String houseNumber);

    List<Address> findByCartId(int orderId);
}