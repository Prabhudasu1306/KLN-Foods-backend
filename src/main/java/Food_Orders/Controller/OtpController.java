package Food_Orders.Controller;

import Food_Orders.Dto.OtpRequest;
import Food_Orders.Service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<String> sendOtp(@RequestParam String email) {
        otpService.sendOtp(email);
        return ResponseEntity.ok("OTP sent to " + email);
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody OtpRequest request) {
        System.out.println("✅ Email: " + request.getEmail());
        System.out.println("✅ OTP: " + request.getOtp());

        boolean verified = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (verified) {
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "OTP verified successfully"
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                    "status", "error",
                    "message", "Invalid or expired OTP"
            ));
        }
    }

}
