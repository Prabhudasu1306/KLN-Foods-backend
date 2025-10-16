package Food_Orders.Service;

import Food_Orders.Entity.OtpEntity;
import Food_Orders.Repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpRepository otpRepository;

    private static final int OTP_EXPIRY_MINUTES = 5;

    public void sendOtp(String firstName, String lastName, String mobileNumber,
                        String email, String password, String confirmPassword, String role) {

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        Optional<OtpEntity> existing = otpRepository.findByEmail(email);
        if (existing.isPresent() && existing.get().isVerified()) {
            throw new IllegalArgumentException("Email already registered. Please login.");
        }

        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        OtpEntity entity = new OtpEntity(firstName, lastName, mobileNumber, email,
                password, otp, LocalDateTime.now(), false, role);

        try {
            otpRepository.save(entity);
        } catch (DataIntegrityViolationException e) {
            throw new IllegalArgumentException("Email already exists. Please login.");
        }

        // Send OTP Email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP for Email Verification");
        message.setText("""
                Hello %s %s,

                Your One-Time Password (OTP) is: %s

                It is valid for %d minutes.
                Do not share this OTP with anyone.

                Regards,
                Food Orders Team
                """.formatted(firstName, lastName, otp, OTP_EXPIRY_MINUTES));

        mailSender.send(message);
        System.out.println(" OTP sent to " + email + ": " + otp);
    }

    public boolean verifyOtp(String email, String otp) {
        Optional<OtpEntity> record = otpRepository.findTopByEmailOrderByGeneratedAtDesc(email);
        if (record.isEmpty()) return false;

        OtpEntity entity = record.get();
        boolean notExpired = entity.getGeneratedAt()
                .isAfter(LocalDateTime.now().minusMinutes(OTP_EXPIRY_MINUTES));
        boolean match = entity.getOtp().equals(otp);

        if (match && notExpired) {
            entity.setVerified(true);
            otpRepository.save(entity);
            return true;
        }
        return false;
    }

    public boolean checkLogin(String email, String password) {
        Optional<OtpEntity> user = otpRepository.findByEmail(email);
        return user.isPresent() && user.get().isVerified() && user.get().getPassword().equals(password);
    }

    public String getRoleByEmail(String email) {
        return otpRepository.findByEmail(email)
                .map(OtpEntity::getRole)
                .orElse("UNKNOWN");
    }

    public List<OtpEntity> getAllUsers() {
        return otpRepository.findAll();
    }

    public OtpEntity getUserByEmail(String email) {
        return otpRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }
}
