package Food_Orders.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "otp_records", uniqueConstraints = {@UniqueConstraint(columnNames = "email")})
public class OtpEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String email;
    private String password;
    private String otp;
    private LocalDateTime generatedAt;
    private boolean verified;

    private String role; // 👈 Added role field

    public OtpEntity(String firstName, String lastName, String mobileNumber,
                     String email, String password, String otp, LocalDateTime generatedAt,
                     boolean verified, String role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.mobileNumber = mobileNumber;
        this.email = email;
        this.password = password;
        this.otp = otp;
        this.generatedAt = generatedAt;
        this.verified = verified;
        this.role = role;
    }
}
