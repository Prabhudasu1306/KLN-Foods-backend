package Food_Orders.Dto;

public class AddressRequest {
    private String email;
    private String houseNumber;
    private String landMark;
    private String street;
    private String city;
    private String state;
    private String zipCode;

    public AddressRequest() {}

    // Getters and setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getHouseNumber() { return houseNumber; }
    public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }
    public String getLandMark() { return landMark; }
    public void setLandMark(String landMark) { this.landMark = landMark; }
    public String getStreet() { return street; }
    public void setStreet(String street) { this.street = street; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getZipCode() { return zipCode; }
    public void setZipCode(String zipCode) { this.zipCode = zipCode; }
}