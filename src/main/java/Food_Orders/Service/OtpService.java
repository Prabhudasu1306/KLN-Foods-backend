package Food_Orders.Service;

import Food_Orders.Entity.OtpEntity;
import Food_Orders.Repository.OtpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
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

    // Forgot Password Methods
    public void sendPasswordResetOtp(String email) {
        try {
            Optional<OtpEntity> userOpt = otpRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("User with this email does not exist");
            }

            OtpEntity user = userOpt.get();

            // Generate OTP
            String otp = String.valueOf(100000 + new Random().nextInt(900000));

            // Save OTP to user
            user.setOtp(otp);
            user.setGeneratedAt(LocalDateTime.now());
            otpRepository.save(user);

            // Send OTP via email
            sendPasswordResetEmail(email, otp, user.getFirstName());

        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to send reset OTP: " + e.getMessage());
        }
    }

    public boolean resetPasswordWithOtp(String email, String otp, String newPassword) {
        try {
            Optional<OtpEntity> userOpt = otpRepository.findByEmail(email);
            if (userOpt.isEmpty()) {
                throw new IllegalArgumentException("User not found");
            }

            OtpEntity user = userOpt.get();

            // Verify OTP
            if (!verifyOtpForPasswordReset(email, otp)) {
                throw new IllegalArgumentException("Invalid or expired OTP");
            }

            // Validate new password
            if (newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("Password must be at least 6 characters long");
            }

            // Update password
            user.setPassword(newPassword);
            user.setOtp(null); // Clear OTP after successful reset
            user.setGeneratedAt(null);
            otpRepository.save(user);

            // Send confirmation email
            sendPasswordResetConfirmation(email, user.getFirstName());

            return true;

        } catch (Exception e) {
            throw new IllegalArgumentException("Password reset failed: " + e.getMessage());
        }
    }

    private boolean verifyOtpForPasswordReset(String email, String otp) {
        Optional<OtpEntity> userOpt = otpRepository.findByEmail(email);
        if (userOpt.isEmpty()) return false;

        OtpEntity user = userOpt.get();

        // Check if OTP exists and matches
        if (user.getOtp() == null || !user.getOtp().equals(otp)) {
            return false;
        }

        // Check if OTP is not expired (10 minutes for password reset)
        LocalDateTime generatedTime = user.getGeneratedAt();
        if (generatedTime == null) return false;

        LocalDateTime currentTime = LocalDateTime.now();
        return generatedTime.plusMinutes(10).isAfter(currentTime);
    }

    private void sendPasswordResetEmail(String email, String otp, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("prabhudasuparusu1306@gmail.com");
            helper.setTo(email);
            helper.setSubject("Password Reset OTP - KLN Food Court");

            String emailContent = buildPasswordResetEmail(firstName, otp);
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println(" Password reset OTP sent to: " + email);

        } catch (Exception e) {
            System.err.println(" Failed to send password reset email: " + e.getMessage());
            throw new RuntimeException("Failed to send password reset email");
        }
    }

    private void sendPasswordResetConfirmation(String email, String firstName) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("prabhudasuparusu1306@gmail.com");
            helper.setTo(email);
            helper.setSubject("Password Reset Successful - KLN Food Court");

            String emailContent = buildPasswordResetConfirmationEmail(firstName);
            helper.setText(emailContent, true);

            mailSender.send(message);
            System.out.println("Password reset confirmation sent to: " + email);

        } catch (Exception e) {
            System.err.println("Failed to send password reset confirmation: " + e.getMessage());
        }
    }

    private String buildPasswordResetEmail(String firstName, String otp) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); padding: 20px; color: white; border-radius: 10px; }" +
                ".content { margin: 20px 0; padding: 20px; background: #f9f9f9; border-radius: 10px; }" +
                ".otp-box { background: #fff; padding: 15px; border: 2px dashed #667eea; border-radius: 8px; text-align: center; margin: 20px 0; }" +
                ".otp-code { font-size: 32px; font-weight: bold; color: #667eea; letter-spacing: 5px; }" +
                ".footer { margin-top: 20px; padding: 15px; background: #eee; border-radius: 5px; font-size: 12px; color: #666; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h2>Password Reset Request</h2>" +
                "<p>KLN Food Court - Account Security</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Hello <strong>" + firstName + "</strong>,</p>" +
                "<p>We received a request to reset your password. Use the OTP below to proceed:</p>" +
                "<div class='otp-box'>" +
                "<p style='margin: 0; color: #666;'>Your One-Time Password</p>" +
                "<div class='otp-code'>" + otp + "</div>" +
                "<p style='margin: 10px 0 0 0; color: #ff6b6b; font-size: 12px;'>Expires in 10 minutes</p>" +
                "</div>" +
                "<p>If you didn't request this reset, please ignore this email or contact support.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p><strong>KLN Food Court</strong><br>" +
                "Email: klnfoodcourt@gmail.com<br>" +
                "Phone: 9989123680<br>" +
                "This is an automated security email.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }

    private String buildPasswordResetConfirmationEmail(String firstName) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; margin: 20px; color: #333; }" +
                ".header { background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%); padding: 20px; color: white; border-radius: 10px; }" +
                ".content { margin: 20px 0; padding: 20px; background: #f9f9f9; border-radius: 10px; }" +
                ".success-icon { font-size: 48px; text-align: center; margin: 20px 0; }" +
                ".footer { margin-top: 20px; padding: 15px; background: #eee; border-radius: 5px; font-size: 12px; color: #666; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='header'>" +
                "<h2>Password Reset Successful</h2>" +
                "<p>KLN Food Court - Account Security</p>" +
                "</div>" +
                "<div class='content'>" +
                "<div class='success-icon'></div>" +
                "<p>Hello <strong>" + firstName + "</strong>,</p>" +
                "<p>Your password has been successfully reset!</p>" +
                "<p>You can now login to your account using your new password.</p>" +
                "<p>If you did not make this change, please contact our support team immediately.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p><strong>KLN Food Court</strong><br>" +
                "Email: klnfoodcourt@gmail.com<br>" +
                "Phone: 9989123680<br>" +
                "This is an automated security email.</p>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}