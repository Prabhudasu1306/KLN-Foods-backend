package Food_Orders.Dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemDto {
    private Long id;
    private String name;
    private double price;
    private int quantity;
    private double totalGST;
    private Long cartId;
}
