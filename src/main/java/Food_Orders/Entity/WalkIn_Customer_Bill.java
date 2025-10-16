package Food_Orders.Entity;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
@Table(name = "bills_of_walk_in_customers")
public class WalkIn_Customer_Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String billNumber;
    private String itemName; // can repeat for multiple items
    private int quantity;
    private double amount;
    private double totalAmount;
}
