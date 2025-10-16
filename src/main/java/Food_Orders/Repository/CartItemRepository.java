package Food_Orders.Repository;

import Food_Orders.Entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByCart_Id(Long cartId);
    List<CartItem> findByCartId(int cartId);
}
