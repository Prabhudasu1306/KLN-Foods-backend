package Food_Orders.Service;

import Food_Orders.Entity.AddItem;
import Food_Orders.Repository.AddItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AddItemService {

    @Autowired
    private AddItemRepository addItemRepository;

    public AddItem addItem(AddItem item) {
        return addItemRepository.save(item);
    }

    public List<AddItem> getAllItems() {
        return addItemRepository.findAll();
    }

    public Optional<AddItem> getItemById(Long id) {
        return addItemRepository.findById(id);
    }

    public AddItem updateItem(Long id, AddItem updatedItem) {
        return addItemRepository.findById(id).map(item -> {
            item.setName(updatedItem.getName());
            item.setPrice(updatedItem.getPrice());
            item.setStateGST(updatedItem.getStateGST());
            item.setCentralGST(updatedItem.getCentralGST());
            item.setTotalGST(updatedItem.getTotalGST());
            item.setDescription(updatedItem.getDescription());
            item.setTotalPrice(updatedItem.getTotalPrice());
            item.setImageUrl(updatedItem.getImageUrl());
            return addItemRepository.save(item);
        }).orElse(null);
    }

    public void deleteItem(Long id) {
        addItemRepository.deleteById(id);
    }
}
