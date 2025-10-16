package Food_Orders.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CartItemResponse {
    private String name;
    private double price;
    private int quantity;
    private double totalPrice;

    public CartItemResponse(String name, double price, int quantity, double totalPrice) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
    }

    // Getters and setters
}

