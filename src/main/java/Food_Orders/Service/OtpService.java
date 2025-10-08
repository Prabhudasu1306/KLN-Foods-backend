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
    @Autowired private OtpRepository otpRepository;

    public void sendOtp(String email) {
        String otp = String.valueOf(100000 + new Random().nextInt(900000));
        OtpEntity entity = new OtpEntity(email, otp, LocalDateTime.now(), false);
        otpRepository.save(entity);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code");
        message.setText("Your OTP is: " + otp);
        mailSender.send(message);
    }

    public boolean verifyOtp(String email, String otp) {
        System.out.println("Verifying OTP for: " + email + " with OTP: " + otp);

        Optional<OtpEntity> record = otpRepository.findTopByEmailOrderByGeneratedAtDesc(email);
        if (record.isEmpty()) {
            System.out.println("No OTP record found for email");
            return false;
        }

        OtpEntity entity = record.get();
        boolean notExpired = entity.getGeneratedAt().isAfter(LocalDateTime.now().minusMinutes(5));
        boolean match = entity.getOtp().equals(otp);

        System.out.println("Match: " + match + ", Not expired: " + notExpired);

        if (match && notExpired) {
            entity.setVerified(true);
            otpRepository.save(entity);
            return true;
        }
        return false;
    }

}
