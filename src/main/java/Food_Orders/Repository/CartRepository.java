package Food_Orders.Repository;

import Food_Orders.Entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findByEmail(String email);

    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.address LEFT JOIN FETCH c.cartItems WHERE c.id = :cartId")
    Cart findCartWithAddressAndItems(@Param("cartId") Long cartId);

    List<Cart> findByEmailOrderByDateDesc(String email);
}
