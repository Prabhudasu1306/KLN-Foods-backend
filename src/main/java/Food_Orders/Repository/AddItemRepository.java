package Food_Orders.Repository;

import Food_Orders.Entity.AddItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddItemRepository extends JpaRepository<AddItem,Long> {
}
