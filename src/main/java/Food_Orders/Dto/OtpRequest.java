package Food_Orders.Dto;

import lombok.Getter;

@Getter
public class OtpRequest {
    // Getters and setters
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String password;
    private String confirmPassword;
    private String otp;
    private String role; // 👈 Added role

    public void setFirstName(String firstName) { this.firstName = firstName; }

    public void setLastName(String lastName) { this.lastName = lastName; }

    public void setMobileNumber(String mobileNumber) { this.mobileNumber = mobileNumber; }

    public void setEmail(String email) { this.email = email; }

    public void setPassword(String password) { this.password = password; }

    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public void setOtp(String otp) { this.otp = otp; }

    public void setRole(String role) { this.role = role; }
}
