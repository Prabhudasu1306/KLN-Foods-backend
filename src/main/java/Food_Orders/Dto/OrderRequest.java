package Food_Orders.Dto;

import Food_Orders.Repository.AddressDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequest {


    private Double totalAmount;
    private List<CartItemDto> items;
    private AddressDto address;
    private String phoneNumber;
    private String email;
    private Date date;
}
