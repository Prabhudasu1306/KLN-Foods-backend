package Food_Orders.Controller;

import Food_Orders.Entity.AddItem;
import Food_Orders.Service.AddItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "http://localhost:3000")
public class AddItemController {

    @Autowired
    private AddItemService addItemService;

    @PostMapping("/add")
    public AddItem addItem(@RequestBody AddItem item) {
        return addItemService.addItem(item);
    }

    @GetMapping("/all")
    public List<AddItem> getAllItems() {
        return addItemService.getAllItems();
    }

    @GetMapping("/{id}")
    public Optional<AddItem> getItemById(@PathVariable Long id) {
        return addItemService.getItemById(id);
    }

    @PutMapping("/update/{id}")
    public AddItem updateItem(@PathVariable Long id, @RequestBody AddItem item) {
        return addItemService.updateItem(id, item);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteItem(@PathVariable Long id) {
        addItemService.deleteItem(id);
        return "Item with ID " + id + " deleted successfully!";
    }
}
