package Food_Orders.Dto;

import Food_Orders.Repository.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter


public class CartDto {
    private Long id;
    private double totalAmount;
    private String phoneNumber;
    private String email;
    private Date date;
    private double totalPrice;
    private  double sumAllGST;
    private AddressDto address;
    private List<CartItemDto> cartItems;
}
