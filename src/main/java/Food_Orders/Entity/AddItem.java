package Food_Orders.Entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
@Table(name = "Walk_in_Food_Items")
public class AddItem {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double price;
    private double stateGST;
    private double centralGST;
    private double totalGST;
    private String description;
    private double totalPrice;
    private String imageUrl;


}
