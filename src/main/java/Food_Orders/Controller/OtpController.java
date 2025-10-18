package Food_Orders.Controller;

import Food_Orders.Dto.ForgotPasswordRequest;
import Food_Orders.Dto.OtpRequest;
import Food_Orders.Dto.ResetPasswordRequest;
import Food_Orders.Entity.OtpEntity;
import Food_Orders.Service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendOtp(@RequestBody OtpRequest request) {
        try {
            otpService.sendOtp(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getMobileNumber(),
                    request.getEmail(),
                    request.getPassword(),
                    request.getConfirmPassword(),
                    request.getRole()
            );
            return ResponseEntity.ok(Map.of("message", "OTP sent to " + request.getEmail()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyOtp(@RequestBody OtpRequest request) {
        boolean verified = otpService.verifyOtp(request.getEmail(), request.getOtp());
        if (verified) {
            return ResponseEntity.ok(Map.of("status", "success", "message", "OTP verified successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Invalid or expired OTP"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String password = body.get("password");

        if (otpService.checkLogin(email, password)) {
            String role = otpService.getRoleByEmail(email);
            OtpEntity user = otpService.getUserByEmail(email);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Login successful",
                    "role", role,
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "email", user.getEmail()
            ));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Invalid credentials or email not verified"));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(otpService.getAllUsers());
    }

    @GetMapping("/user/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        try {
            OtpEntity user = otpService.getUserByEmail(email);
            return ResponseEntity.ok(Map.of(
                    "firstName", user.getFirstName(),
                    "lastName", user.getLastName(),
                    "email", user.getEmail(),
                    "role", user.getRole()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", "User not found"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        try {
            otpService.sendPasswordResetOtp(request.getEmail());
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Password reset OTP sent to " + request.getEmail()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Server error occurred"));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            boolean reset = otpService.resetPasswordWithOtp(
                    request.getEmail(),
                    request.getOtp(),
                    request.getNewPassword()
            );

            if (reset) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "message", "Password reset successfully"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("status", "error", "message", "Password reset failed"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Server error occurred"));
        }
    }
}