package Food_Orders.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@Entity
@Table(name = "otp_records")
public class OtpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String otp;
    private LocalDateTime generatedAt;
    private boolean verified;

    public OtpEntity(String email, String otp, LocalDateTime generatedAt, boolean verified) {
        this.email = email;
        this.otp = otp;
        this.generatedAt = generatedAt;
        this.verified = verified;
    }
    }
