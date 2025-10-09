package Food_Orders.Service;

import Food_Orders.Entity.OtpEntity;
import Food_Orders.Repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;

    // OTP expiry time (in minutes)
    private static final int OTP_EXPIRY_MINUTES = 5;

    /**
     * Sends OTP to the given email.
     */
    public void sendOtp(String email) {
        // Generate random 6-digit OTP
        String otp = String.valueOf(100000 + new Random().nextInt(900000));

        // Save OTP record to DB
        OtpEntity entity = new OtpEntity(email, otp, LocalDateTime.now(), false);
        otpRepository.save(entity);

        // Send email with OTP included
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Verification");
        message.setText("""
                Hello,

                Your One-Time Password (OTP) is: %s

                ✅ It is valid for only %d minutes.
                ⚠️ Do not share this OTP with anyone for security reasons.

                Please use this OTP to verify your email in the app or website.

                Regards,
                Food Orders Team
                """.formatted(otp, OTP_EXPIRY_MINUTES));

        mailSender.send(message);

        System.out.println("✅ OTP generated and emailed to " + email + ": " + otp);
    }

    /**
     * Verifies the provided OTP for the given email.
     */
    public boolean verifyOtp(String email, String otp) {
        System.out.println("Verifying OTP for: " + email + " with OTP: " + otp);

        Optional<OtpEntity> record = otpRepository.findTopByEmailOrderByGeneratedAtDesc(email);
        if (record.isEmpty()) {
            System.out.println("❌ No OTP record found for email");
            return false;
        }

        OtpEntity entity = record.get();
        boolean notExpired = entity.getGeneratedAt()
                .isAfter(LocalDateTime.now().minusMinutes(OTP_EXPIRY_MINUTES));
        boolean match = entity.getOtp().equals(otp);

        System.out.println("Match: " + match + ", Not expired: " + notExpired);

        if (match && notExpired) {
            entity.setVerified(true);
            otpRepository.save(entity);
            System.out.println("✅ OTP verified successfully for " + email);
            return true;
        } else {
            System.out.println("❌ OTP expired or invalid");
            return false;
        }
    }
}
