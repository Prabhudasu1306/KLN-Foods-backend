package Food_Orders.Repository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressDto {
    private String houseNumber;
    private String landMark;
    private String street;
    private String city;
    private String state;
    private String zipCode;
}